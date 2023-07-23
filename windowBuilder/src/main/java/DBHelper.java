import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBHelper {
    private DatabaseConnector databaseConnector;


    public DBHelper(DatabaseConnector connector) {
        this.databaseConnector = connector;
    }
    
    public ArrayList<StockCard> getStockCard() {
        ArrayList<StockCard> stockCard = new ArrayList<>();
        Connection connection = null;
        try {
            connection = databaseConnector.getConnection();
            if (connection != null) {
                String query = "SELECT * FROM stockcard";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                     ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        StockCard stock = new StockCard(
                                resultSet.getString("stock_code"),
                                resultSet.getString("stock_name"),
                                resultSet.getInt("stock_type"),
                                resultSet.getString("unit"),
                                resultSet.getString("barcode"),
                                resultSet.getDouble("kdv_type"),
                                resultSet.getString("detail"),
                                resultSet.getString("create_date")
                        );
                        stockCard.add(stock);
                    }
                }
            } else {
                System.err.println("Unsuccessful: Veritabanýna baðlantý kurulamadý.");
            }
        } catch (SQLException sqlException) {
            showErrorMessage(sqlException);
        } finally {
            closeConnection(connection);
        }
        return stockCard;
    }
    
    public boolean insertStock(String stockCode, String stockName, int stockType, String unit, String barcode, double kdvType, String detail, String createDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnector.getConnection();
            if (connection != null) {
                String query = "INSERT INTO stockcard (stock_code, stock_name, stock_type, unit, barcode, kdv_type, detail, create_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, stockCode);
                preparedStatement.setString(2, stockName);
                preparedStatement.setInt(3, stockType);
                preparedStatement.setString(4, unit);
                preparedStatement.setString(5, barcode);
                preparedStatement.setDouble(6, kdvType);
                preparedStatement.setString(7, detail);
                preparedStatement.setString(8, createDate);

                int result = preparedStatement.executeUpdate();
                return result > 0; // Returns true if at least one row was affected (insertion successful)
            } else {
                System.err.println("Unsuccessful: Veritabanýna baðlantý kurulamadý.");
            }
        } catch (SQLException sqlException) {
            showErrorMessage(sqlException);
        } finally {
            closeConnection(connection);
        }
        return false;
    }
    
    public boolean updateStock(String stockCode, String stockName, int stockType, String unit, String barcode, double kdvType, String detail, String createDate) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = databaseConnector.getConnection();
            String query = "UPDATE stockcard SET stock_name=?, stock_type=?, unit=?, barcode=?, kdv_type=?, detail=?, create_date=? WHERE stock_code=?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, stockName);
            preparedStatement.setInt(2, stockType);
            preparedStatement.setString(3, unit);
            preparedStatement.setString(4, barcode);
            preparedStatement.setDouble(5, kdvType);
            preparedStatement.setString(6, detail);
            preparedStatement.setString(7, createDate);
            preparedStatement.setString(8, stockCode);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException sqlException) {
            showErrorMessage(sqlException);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        return false;
    }
    
    public boolean copyStock(StockCard newStockCard) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = databaseConnector.getConnection();
            String query = "INSERT INTO stockcard (stock_code, stock_name, stock_type, unit, barcode, kdv_type, detail, create_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

            // Set the parameters
            statement.setString(1, newStockCard.getStockCode());
            statement.setString(2, newStockCard.getStockName());
            statement.setInt(3, newStockCard.getStockType());
            statement.setString(4, newStockCard.getUnit());
            statement.setString(5, newStockCard.getBarcode());
            statement.setDouble(6, newStockCard.getKdvType());
            statement.setString(7, newStockCard.getDetail());
            statement.setString(8, newStockCard.getCreateDate());

            // Execute the query
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException sqlException) {
            databaseConnector.showErrorMessage(sqlException);
        } finally {
            try {
                if (statement != null) { statement.close(); }
                if (connection != null) { connection.close(); }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        return false;
    }
    
    public void deleteStock(String stockCode) {
        Connection connection = null;
        try {
            connection = databaseConnector.getConnection();
            if (connection != null) {
                String query = "DELETE FROM stockcard WHERE stock_code = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, stockCode);
                preparedStatement.executeUpdate();
                System.out.println("Successful: Stok kaydý veritabanýndan silindi.");
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } else {
                System.err.println("Unsuccessful: Veritabanýna baðlantý kurulamadý.");
            }
        } catch (SQLException sqlException) {
            showErrorMessage(sqlException);
        } finally {
            closeConnection(connection);
        }
    }
    
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqlException) {
                System.err.println("Error: Baðlantý kapatýlamadý: " + sqlException.getMessage());
            }
        }
    }

    public void showErrorMessage(SQLException sqlException) {
        System.err.println("Unsuccessful: Veritabaný iþlemi baþarýsýz oldu.");
        System.err.println("Error: " + sqlException.getMessage());
        System.err.println("Error code: " + sqlException.getErrorCode());
    }
}