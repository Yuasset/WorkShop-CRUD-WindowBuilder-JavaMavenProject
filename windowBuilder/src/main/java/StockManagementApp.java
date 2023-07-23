import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class StockManagementApp extends JFrame {
    private DefaultTableModel model;
    private JTable tblStockCard;
    private JTextField txtSearch;
    private JTextField txtStockCode;
    private JTextField txtStockName;
    private JTextField txtBarcode;
    private JFormattedTextField txtfCreateDate;
    private JTextArea txtaDetail;
    private JComboBox<Integer> cbxStockType;
    private JComboBox<String> cbxUnit;
    private JComboBox<Double> cbxKDVType;
    private JButton btnSearch;
    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCopy;
    private JButton btnClear;
    private String userName;
    private String userPassword;
    private String dbUrl;
    private DatabaseConnector databaseConnector;

	public StockManagementApp() {
		this.userName = "root";
		this.userPassword = "root";
		this.dbUrl = "jdbc:mysql://localhost:3309/workshop?useSSL=false&serverTimezone=UTC";
		this.databaseConnector = new DatabaseConnector(userName, userPassword, dbUrl);
		initComponents();
		model = (DefaultTableModel) tblStockCard.getModel();
		modelDataList();
		clearInputForm();
		}
	
    public static void main(String[] args) {
    	EventQueue.invokeLater(new Runnable() {
    		public void run() {
    			try {
    				StockManagementApp stockManagementApp = new StockManagementApp();
    				stockManagementApp.setVisible(true);
    				} catch (Exception exception) {
    					exception.printStackTrace();
    					}}});
    	}
    
	public void modelDataList() {
		DBHelper dbHelper = new DBHelper(databaseConnector);
		ArrayList<StockCard> stockCard = dbHelper.getStockCard();
		if (stockCard != null) {
		for (StockCard stockCardList : stockCard) {
			Object[] row = {
					stockCardList.getStockCode(),
					stockCardList.getStockName(),
					stockCardList.getStockType(),
					stockCardList.getUnit(),
					stockCardList.getBarcode(),
					stockCardList.getKdvType(),
					stockCardList.getDetail(),
					getFormattedCreateDate(stockCardList.getCreateDate())};
			model.addRow(row);
			}}}
	
	public String getFormattedCreateDate(String createDateStr) {
		if (createDateStr == null || createDateStr.isEmpty()) { return ""; }
		try {
			SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date createDate = sourceFormat.parse(createDateStr);
			SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			return targetFormat.format(createDate);
			} catch (ParseException parseException) {
				parseException.printStackTrace();
				return "";
				}
		}

	public String getDeffaultFormatCreateDate(String createDateStr) {
		if (createDateStr == null || createDateStr.isEmpty()) { return ""; }
		try {
			SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date createDate = sourceFormat.parse(createDateStr);
			SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return targetFormat.format(createDate);
			} catch (ParseException parseException) {
				parseException.printStackTrace();
				return "";
				}
		}
	
	public void clearInputForm() {
		txtStockCode.setText("");
		txtStockName.setText("");
		cbxStockType.setSelectedIndex(-1);
		cbxUnit.setSelectedIndex(-1);
		txtBarcode.setText("");
		cbxKDVType.setSelectedIndex(-1);
		txtaDetail.setText("");
		txtfCreateDate.setText("");
		txtSearch.setText("");
		DefaultTableModel model = (DefaultTableModel) tblStockCard.getModel();
		model.setRowCount(0);
		DBHelper dbHelper = new DBHelper(databaseConnector);
		ArrayList<StockCard> stockCard = dbHelper.getStockCard();
		for (StockCard stockCardList : stockCard) {
			model.addRow(new Object[]{
					stockCardList.getStockCode(),
					stockCardList.getStockName(),
					stockCardList.getStockType(),
					stockCardList.getUnit(),
					stockCardList.getBarcode(),
					stockCardList.getKdvType(),
					stockCardList.getDetail(),
					getFormattedCreateDate((String) stockCardList.getCreateDate())});
			}
		TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<>(model);
		tblStockCard.setRowSorter(tableRowSorter);
		tableRowSorter.setRowFilter(null);
		tblStockCard.clearSelection();
		}
	
	public boolean isStockCodeExists(String stockCode) {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
	    try {
	        connection = databaseConnector.getConnection();
	        String query = "SELECT COUNT(*) FROM stockcard WHERE stock_code = ?";
	        statement = connection.prepareStatement(query);
	        statement.setString(1, stockCode);
	        resultSet = statement.executeQuery();
	        if (resultSet.next()) {
	        	int count = resultSet.getInt(1);
	            return count > 0;
	            }
	        } catch (SQLException sqlException) {
	        	databaseConnector.showErrorMessage(sqlException);
	        	} finally {
	        		try {
	        			if (resultSet != null) { resultSet.close(); }
						if (statement != null) { statement.close(); }
						if (connection != null) { connection.close(); }
						} catch (SQLException sqlException) { sqlException.printStackTrace(); }
	        		}
	    return false;
	    }
	
	private String findSmallestUniqueStockCode() {
		DBHelper dbHelper = new DBHelper(databaseConnector);
		ArrayList<StockCard> stockCardList = dbHelper.getStockCard();
		try {
			TreeSet<String> existingStockCodes = new TreeSet<>();
			for (StockCard stockCard : stockCardList) {
				existingStockCodes.add(stockCard.getStockCode());
				}
			TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<>(model);
			tblStockCard.setRowSorter(tableRowSorter);
			tableRowSorter.setRowFilter(null);
			String smallestUniqueStockCode = "STK-001";
			while (existingStockCodes.contains(smallestUniqueStockCode)) {
				int number = Integer.parseInt(smallestUniqueStockCode.substring(4)) + 1;
				smallestUniqueStockCode = String.format("STK-%03d", number);
				}
			return smallestUniqueStockCode;
			} catch (Exception sqlException) {
				sqlException.printStackTrace();
				return "STK-001";
				}
		}
	
	public void initComponents() {
		setAlwaysOnTop(true);
		setTitle("MySQL Veritabaný CRUL Temel Ýþlemler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(1280, 720);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);
        
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 1246, 375);
		getContentPane().add(scrollPane);
		
		txtSearch = new JTextField();
		this.txtSearch = txtSearch;
		getContentPane().add(txtSearch);
		txtSearch.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		txtSearch.setBounds(140, 389, 200, 30);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 427, 470, 246);
		getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblStockCode = new JLabel("Stok Kodu");
		panel.add(lblStockCode);
		lblStockCode.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		txtStockCode = new JTextField();
		this.txtStockCode = txtStockCode;
		panel.add(txtStockCode);
		txtStockCode.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		txtStockCode.setColumns(10);
		
		JLabel lblStockName = new JLabel("Stok Adý");
		panel.add(lblStockName);
		lblStockName.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		txtStockName = new JTextField();
		this.txtStockName = txtStockName;
		panel.add(txtStockName);
		txtStockName.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		txtStockName.setColumns(10);
		
		JLabel lblStockType = new JLabel("Stok Tipi");
		panel.add(lblStockType);
		lblStockType.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JComboBox<Integer> cbxStockType = new JComboBox<>();
		this.cbxStockType = cbxStockType;
		panel.add(cbxStockType);
		cbxStockType.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		for (int i = 0; i < 100; i++) { cbxStockType.addItem(i); }
		
		JLabel lblUnit = new JLabel("Birimi");
		panel.add(lblUnit);
		lblUnit.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JComboBox<String> cbxUnit = new JComboBox<>();
		this.cbxUnit = cbxUnit;
		panel.add(cbxUnit);
		cbxUnit.setModel(new DefaultComboBoxModel<String>(new String[] {"Hammadde", "Yarý Mamul", "Mamul", "YrdMalzeme"}));
		cbxUnit.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JLabel lblBarcode = new JLabel("Barkodu");
		panel.add(lblBarcode);
		lblBarcode.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		txtBarcode = new JTextField();
		this.txtBarcode = txtBarcode;
		panel.add(txtBarcode);
		txtBarcode.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		txtBarcode.setColumns(10);
		
		JLabel lblKDVType = new JLabel("KDV Tipi");
		panel.add(lblKDVType);
		lblKDVType.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JComboBox<Double> cbxKDVType = new JComboBox<>();
		this.cbxKDVType = cbxKDVType;
		panel.add(cbxKDVType);
		cbxKDVType.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		cbxKDVType.setModel(new DefaultComboBoxModel<Double>(new Double[] {1.0, 8.0, 18.0}));
		
		JLabel lblDetail = new JLabel("Açýklama");
		panel.add(lblDetail);
		lblDetail.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JTextArea txtaDetail = new JTextArea();
		txtaDetail.setBorder(UIManager.getBorder("CheckBoxMenuItem.border"));
		this.txtaDetail = txtaDetail;
		panel.add(txtaDetail);
		txtaDetail.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JLabel lblCreateDate = new JLabel("Oluþturma Tarihi");
		panel.add(lblCreateDate);
		lblCreateDate.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JFormattedTextField txtfCreateDate = new JFormattedTextField();
		this.txtfCreateDate = txtfCreateDate;
		panel.add(txtfCreateDate);
		txtfCreateDate.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		JLabel lblSearch = new JLabel("Stok Kodu Ara");
		getContentPane().add(lblSearch);
		lblSearch.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		lblSearch.setBounds(20, 389, 120, 30);
		
		JButton btnCopy = new JButton("Kayýt Kopyala");
		this.btnCopy = btnCopy;
		getContentPane().add(btnCopy);
		btnCopy.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btnCopy.setBounds(490, 511, 130, 30);
		btnCopy.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        int selectedRow = tblStockCard.getSelectedRow();
		        if (selectedRow == -1) {
		            JOptionPane.showMessageDialog(StockManagementApp.this, "Lütfen kopyalamak istediðiniz kaydý tabloda seçiniz.", "Seçim Yapýnýz", JOptionPane.WARNING_MESSAGE);
		            return;
		        }		      
		        String newStockCode = findSmallestUniqueStockCode();
		        if (newStockCode == null) {
		            JOptionPane.showMessageDialog(StockManagementApp.this, "Stok kodu oluï¿½turulurken bir hata oluþtu.", "Hata", JOptionPane.ERROR_MESSAGE);
		            return;
		        }
		        String stockName = (String) tblStockCard.getValueAt(selectedRow, 1);
		        int stockType = (int) tblStockCard.getValueAt(selectedRow, 2);
		        String unit = (String) tblStockCard.getValueAt(selectedRow, 3);
		        String barcode = (String) tblStockCard.getValueAt(selectedRow, 4);
		        double kdvType = (double) tblStockCard.getValueAt(selectedRow, 5);
		        String detail = (String) tblStockCard.getValueAt(selectedRow, 6);
		        String createDate = getDeffaultFormatCreateDate((String) tblStockCard.getValueAt(selectedRow, 7));
		        StockCard newStockCard = new StockCard(newStockCode, stockName, stockType, unit, barcode, kdvType, detail, createDate);
		        DBHelper dbHelper = new DBHelper(databaseConnector);
		        boolean isCopied = dbHelper.copyStock(newStockCard);
		        createDate = getFormattedCreateDate((String) createDate);
		        if (isCopied) {
		            model.addRow(new Object[]{
		                    newStockCode,
		                    stockName,
		                    stockType,
		                    unit,
		                    barcode,
		                    kdvType,
		                    detail,
		                    createDate
		            });
		            int lastRow = model.getRowCount() - 1;
		            if (lastRow >= 0) {
		                tblStockCard.setRowSelectionInterval(lastRow, lastRow);
		                tblStockCard.scrollRectToVisible(tblStockCard.getCellRect(lastRow, 0, true));
		            }
		            JOptionPane.showMessageDialog(StockManagementApp.this, "Kayýt baþarýlýyla kopyalandý ve veritabanýna eklendi.", "Baþarýlý", JOptionPane.INFORMATION_MESSAGE);
		            } else {
		        	JOptionPane.showMessageDialog(StockManagementApp.this, "Kopyalama iþlemi baþarýsýz.", "Hata", JOptionPane.ERROR_MESSAGE);
		        	}}});
				
		JButton btnDelete = new JButton("Kayýt Sil");
		this.btnDelete = btnDelete;
		getContentPane().add(btnDelete);
		btnDelete.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btnDelete.setBounds(490, 551, 130, 30);
		btnDelete.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent actionEvent) {
		        int selectedRow = tblStockCard.getSelectedRow();
		        if (selectedRow != -1) {
		            int option = JOptionPane.showConfirmDialog(StockManagementApp.this, "Seçili kaydý silmek istediðinizden emin misiniz?", "Kayýt Sil", JOptionPane.YES_NO_OPTION);
		            if (option == JOptionPane.YES_OPTION) {
		                String stockCode = tblStockCard.getValueAt(selectedRow, 0).toString();
		                DBHelper dbHelper = new DBHelper(databaseConnector);
		                try {
		                    dbHelper.deleteStock(stockCode);
		                    JOptionPane.showMessageDialog(StockManagementApp.this, "Kayýt baþarýlýyla silindi.", "Baþarýlý", JOptionPane.INFORMATION_MESSAGE);
		                    DefaultTableModel model = (DefaultTableModel) tblStockCard.getModel();
		                    model.setRowCount(0);
		                    clearInputForm();
		                    modelDataList();
		                    } catch (Exception exception) {
		                    	exception.printStackTrace();
		                    	JOptionPane.showMessageDialog(StockManagementApp.this, "Kayýt silinirken bir hata oluþtu.", "Hata", JOptionPane.ERROR_MESSAGE);
		                    	}
		                }
		            } else {
		            	JOptionPane.showMessageDialog(StockManagementApp.this, "Lütfen silmek istediðiniz kaydý tabloda seçiniz.", "Seçim Yapýnýz", JOptionPane.WARNING_MESSAGE);
		            	}}});

		JButton btnUpdate = new JButton("Kayýt Güncelle");
		this.btnUpdate = btnUpdate;
		getContentPane().add(btnUpdate);
		btnUpdate.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btnUpdate.setBounds(490, 471, 130, 30);
		btnUpdate.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        int selectedRow = tblStockCard.getSelectedRow();
		        if (selectedRow == -1) {
		            JOptionPane.showMessageDialog(StockManagementApp.this, "Lütfen güncellemek istediðiniz kaydý tabloda seçiniz.", "Seçim Yapýnýz", JOptionPane.WARNING_MESSAGE);
		            return;
		        }
		        String selectedStockCode = tblStockCard.getValueAt(selectedRow, 0).toString();
		        if (!selectedStockCode.equals(txtStockCode.getText().trim())) {
		            if (isStockCodeExists(txtStockCode.getText().trim())) {
		                JOptionPane.showMessageDialog(StockManagementApp.this, "Seçili kaydýn Stok Kodu deðiþtirilemez.", "Hata", JOptionPane.ERROR_MESSAGE);
		                txtStockCode.setText(selectedStockCode);
		                return;
		            }
		        }
		        String stockCode = txtStockCode.getText().trim();
		        String stockName = txtStockName.getText().trim();
		        int stockType = (int) cbxStockType.getSelectedItem();
		        String unit = (String) cbxUnit.getSelectedItem();
		        String barcode = txtBarcode.getText().trim();
		        double kdvType = (double) cbxKDVType.getSelectedItem();
		        String detail = txtaDetail.getText().trim();
		        String createDate = getDeffaultFormatCreateDate((String) txtfCreateDate.getText());
		        DBHelper dbHelper = new DBHelper(databaseConnector);
		        boolean isUpdated = dbHelper.updateStock(stockCode, stockName, stockType, unit, barcode, kdvType, detail, createDate);
		        if (isUpdated) {
		            JOptionPane.showMessageDialog(StockManagementApp.this, "Kayýt baþarýlya güncellendi.", "Baþarýlý", JOptionPane.INFORMATION_MESSAGE);
		            clearInputForm();
		            tblStockCard.setRowSelectionInterval(selectedRow, selectedRow);
		        } else {
		            JOptionPane.showMessageDialog(StockManagementApp.this, "Güncelleme iþlemi baþarýsýz oldu. \nSeçili kaydýn Stok Kodu deðiþtirilemez.", "Hata", JOptionPane.ERROR_MESSAGE);
		            }}});
		
		JButton btnInsert = new JButton("Yeni Kayýt");
		this.btnInsert = btnInsert;
		getContentPane().add(btnInsert);
		btnInsert.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btnInsert.setBounds(490, 429, 130, 30);
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String stockCode = txtStockCode.getText().trim();
				DBHelper dbHelper = new DBHelper(databaseConnector);
		        if (isStockCodeExists(stockCode)) {
		        	JOptionPane.showMessageDialog(StockManagementApp.this, "Stok kodu veritabanýnda zaten mevcut.", "Hata", JOptionPane.ERROR_MESSAGE);
		        	clearInputForm();
		        	return;
		        	}
		        if (txtStockCode.getText().isEmpty() || txtStockName.getText().isEmpty() || cbxStockType.getSelectedIndex() == -1 || 
		        		cbxUnit.getSelectedIndex() == -1 || txtBarcode.getText().isEmpty() || cbxKDVType.getSelectedIndex() == -1 || 
		        		txtaDetail.getText().isEmpty() || txtfCreateDate.getText().isEmpty()) {
		        	JOptionPane.showMessageDialog(StockManagementApp.this, "Lütfen boþ alanlarý doldurunuz.", "Uyarý", JOptionPane.WARNING_MESSAGE);
		        	} else {
		        		String stockName = txtStockName.getText().trim();
						int stockType = (int) cbxStockType.getSelectedItem();
						String unit = (String) cbxUnit.getSelectedItem();
						String barcode = txtBarcode.getText().trim();
						double kdvType = (double) cbxKDVType.getSelectedItem();
						String detail = txtaDetail.getText().trim();
						String createDate = getDeffaultFormatCreateDate((String) txtfCreateDate.getText());
						boolean isInserted = dbHelper.insertStock(stockCode, stockName, stockType, unit, barcode, kdvType, detail, createDate);
						if (isInserted) {
							model.addRow(new Object[]{
									stockCode,
									stockName,
									stockType,
									unit,
									barcode,
									kdvType,
									detail,
									createDate});
		                JOptionPane.showMessageDialog(StockManagementApp.this, "Kayýt baþarýlýyla oluþturuldu.", "Baþarýlý", JOptionPane.INFORMATION_MESSAGE);
		                clearInputForm();
		                int lastRow = model.getRowCount() - 1;
		                if (lastRow >= 0) {
		                	tblStockCard.setRowSelectionInterval(lastRow, lastRow);
		                    tblStockCard.scrollRectToVisible(tblStockCard.getCellRect(lastRow, 0, true));
		                }
		            } else {
		            	JOptionPane.showMessageDialog(StockManagementApp.this, "Kayýt eklenirken bir hata oluþtu.", "Hata", JOptionPane.ERROR_MESSAGE);
		            }
		        }
		    }
		});
		
		JButton btnClear = new JButton("Temizle");
		this.btnClear = btnClear;
		getContentPane().add(btnClear);
		btnClear.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btnClear.setBounds(490, 389, 130, 30);
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearInputForm();
				}});
		
		btnSearch = new JButton("Ara");
		getContentPane().add(btnSearch);
		btnSearch.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btnSearch.setBounds(350, 389, 130, 30);
		btnSearch.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String searchInput = txtSearch.getText().trim().toLowerCase();
		        TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<>(model);
		        tblStockCard.setRowSorter(tableRowSorter);
		        if (searchInput.isEmpty()) {
		        	clearInputForm();
		        } else {
		            tableRowSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
		                @Override
		                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
		                    String stockCode = entry.getStringValue(0).toLowerCase();
		                    boolean isMatching = stockCode.startsWith(searchInput);
		                    if (isMatching && stockCode.equals(searchInput)) {
		                        int rowIndex = entry.getIdentifier();
		                        tblStockCard.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
		                    }
		                    return isMatching;
		                }
		            });
		        }
		    }
		});
		
		tblStockCard = new JTable();
		this.tblStockCard = tblStockCard;
		scrollPane.setViewportView(tblStockCard);
		tblStockCard.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		tblStockCard.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {
						"Stok Kodu", "Stok Adeti", "Stok Tipi", "Birimi", "Barkodu", "KDV Tipi", "Açýklama", "Oluþturma Tarihi"
						}) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false, false };
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
				}
			});
		tblStockCard.getColumnModel().getColumn(0).setResizable(false);
		tblStockCard.getColumnModel().getColumn(1).setResizable(false);
		tblStockCard.getColumnModel().getColumn(2).setResizable(false);
		tblStockCard.getColumnModel().getColumn(3).setResizable(false);
		tblStockCard.getColumnModel().getColumn(4).setResizable(false);
		tblStockCard.getColumnModel().getColumn(5).setResizable(false);
		tblStockCard.getColumnModel().getColumn(6).setResizable(false);
		tblStockCard.getColumnModel().getColumn(7).setResizable(false);
		tblStockCard.getTableHeader().setReorderingAllowed(false);
		setVisible(true);
	    tblStockCard.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblStockCard.getSelectionModel().addListSelectionListener(e -> {
		    if (!e.getValueIsAdjusting()) {
		        int selectedRow = tblStockCard.getSelectedRow();
		        if (selectedRow != -1 && selectedRow < tblStockCard.getRowCount()) {
			        txtStockCode.setText(tblStockCard.getValueAt(selectedRow, 0).toString());
			        txtStockName.setText(tblStockCard.getValueAt(selectedRow, 1).toString());
			        cbxStockType.setSelectedItem(tblStockCard.getValueAt(selectedRow, 2));
			        cbxUnit.setSelectedItem(tblStockCard.getValueAt(selectedRow, 3).toString());
			        txtBarcode.setText(tblStockCard.getValueAt(selectedRow, 4).toString());
			        cbxKDVType.setSelectedItem(tblStockCard.getValueAt(selectedRow, 5));
			        txtaDetail.setText(tblStockCard.getValueAt(selectedRow, 6).toString());			      
			        txtfCreateDate.setText(tblStockCard.getValueAt(selectedRow, 7).toString());
			    }
		    }
		});
	}
}