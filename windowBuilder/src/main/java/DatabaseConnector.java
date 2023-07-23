import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	//root, root, jdbc:mysql://localhost:3309/workshop?useSSL=false&serverTimezone=UTC
    private String userName;
    private String userPassword;
    private String dbUrl;

    // Constructor
    public DatabaseConnector(String userName, String userPassword, String dbUrl) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.dbUrl = dbUrl;
    }
	
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, userName, userPassword);
            System.out.println("Successful: Veritabanına bağlantı kuruldu");
        } catch (SQLException sqlException) {
            connection = null; // Bağlantı başarısız olduysa null olarak ayarlayalım
            showErrorMessage(sqlException);
        }
        return connection;
    }
	
    public void showErrorMessage(SQLException sqlException) {
        System.err.println("Unsuccessful: Veritabanı işlemi başarısız oldu.");
        System.err.println("Error: " + sqlException.getMessage());
        System.err.println("Error code: " + sqlException.getErrorCode());
    }
}