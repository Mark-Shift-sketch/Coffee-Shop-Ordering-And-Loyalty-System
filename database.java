
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class database {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/systemdatabase?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void UserDatabase() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/systemdatabase", USER, PASSWORD);
            var stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS systemdatabase");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active'
                )
            """);
        } catch (Exception e) {
            System.out.println("DB Setup Error: " + e.getMessage());
        }
    }

    public static void CategoriesTable() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50) UNIQUE NOT NULL
                )
            """;
            stmt.execute(sql);

            stmt.executeUpdate("""
                INSERT IGNORE INTO categories (name) VALUES
                ('Coffee'), ('Beans'), ('Pastries'), ('Drinks'), ('Others')
            """);

            System.out.println("Categories Table ready");
        } catch (Exception e) {
            System.out.println("DB Setup Error: " + e.getMessage());
        }
    }

    public static void MenuTables() {
        try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS menuitems (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    price DOUBLE NOT NULL,
                    imagepath VARCHAR(255) NOT NULL,
                    category_id INT,
                    availability ENUM('Available', 'Unavailable') DEFAULT 'Available',
                    FOREIGN KEY (category_id) REFERENCES categories(id)
                )
            """;

            statement.execute(sql);
            System.out.println("Menu Items Table ready");
        } catch (Exception e) {
            System.out.println("DB Setup Error: " + e.getMessage());
        }
    }

    public static void OrdersTable() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS orders (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    menu_item_id INT NOT NULL,
                    price DOUBLE NOT NULL,
                    quantity INT NOT NULL DEFAULT 1,
                    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (menu_item_id) REFERENCES menuitems(id)
                )
            """;
            stmt.execute(sql);
            System.out.println("Orders Table ready");

        } catch (Exception e) {
            System.out.println("DB Setup Error: " + e.getMessage());
        }
    }

}
