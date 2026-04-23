package Program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

public class DatabaseConnection {
    private static Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost/Project_BikiniBottom?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "99Rizky@@@";

    private DatabaseConnection() {} // Private constructor untuk mencegah instantiasi

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Koneksi database berhasil dibuat.");
                // Jangan set auto commit ke false di sini
                connection.setAutoCommit(true);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Gagal terkoneksi dengan database: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        // Hanya panggil method ini ketika aplikasi benar-benar akan ditutup
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}