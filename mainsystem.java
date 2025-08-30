import javax.swing.SwingUtilities;

public class mainsystem {

    public static void main(String[] args) {
        database.UserDatabase();
        database.MenuTables();
        database.OrdersTable();
        database.CategoriesTable();
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
