
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Adminmenudashboard extends JFrame {

    private JTextField name;
    private JTextField price;
    private JLabel image;
    private String selectedimagepath = null;
    private JPanel menuPanel;
    private static Adminmenudashboard INSTANCE = null;
    private JDialog reportsDialog = null;
    private JPanel barGraphPanel = null;
    private JTextField newusername;
    private JTextField newuserpassword;

    private JComboBox<String> categoryBox;
    private JComboBox<String> filterBox;

    public Adminmenudashboard() {
        INSTANCE = this;
        setTitle("Admin Dashboard Coffee Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu panel FIRST
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 6, 15, 15));
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        //Top panel
        JPanel toppanel = new JPanel(new FlowLayout());
        toppanel.setPreferredSize(new Dimension(getWidth(), 120));

        try {
            BufferedImage logo = ImageIO.read(new File("logo.jpg"));
            int size = 100;
            BufferedImage circleshape = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleshape.createGraphics();
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(logo.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
            g2.dispose();
            toppanel.add(new JLabel(new ImageIcon(circleshape)));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error logo not found");
        }

        JButton addButton = new JButton("Add Menu Item");
        addButton.addActionListener(e -> addmenuitems());

        JButton addCategoryBtn = new JButton("Add Category");
        addCategoryBtn.addActionListener(e -> addCategory());

        JButton removeCategoryBtn = new JButton("Remove Category");
        removeCategoryBtn.addActionListener(e -> removeCategory());

        JButton reportsButton = new JButton("Show Report");
        reportsButton.addActionListener(e -> reports());

        JButton settings = new JButton("Settings");
        settings.addActionListener(e -> Settings());

        toppanel.add(new JLabel("Filter:"));
        filterBox = new JComboBox<>();
        filterBox.addActionListener(e -> loadmenuitems());
        loadCategoriesToFilterBox();

        toppanel.add(addButton);
        toppanel.add(addCategoryBtn);
        toppanel.add(removeCategoryBtn);
        toppanel.add(reportsButton);
        toppanel.add(settings);
        toppanel.add(filterBox);

        add(toppanel, BorderLayout.NORTH);

        loadmenuitems();
        setVisible(true);
    }

    private void addmenuitems() {
        JDialog addDialog = new JDialog((JFrame) null, "Add Menu Item", true);
        addDialog.setSize(400, 500);
        addDialog.setLocationRelativeTo(null);

        JPanel addmenu = new JPanel(new GridLayout(0, 1, 10, 10)); // auto-rows
        addmenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Add Menu Item", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        name = new JTextField();
        name.setBorder(BorderFactory.createTitledBorder("Name"));

        price = new JTextField();
        price.setBorder(BorderFactory.createTitledBorder("Price"));

        categoryBox = new JComboBox<>();
        categoryBox.setBorder(BorderFactory.createTitledBorder("Category"));
        loadCategories();

        image = new JLabel("No Image selected", SwingConstants.CENTER);
        JButton chooseImgBtn = new JButton("Choose Image");
        chooseImgBtn.addActionListener(e -> chooseImages());

        JButton addButton = new JButton("Add Menu Item");
        addButton.addActionListener(e -> {
            addMenuItem();
            addDialog.dispose();
        });

        addmenu.add(title);
        addmenu.add(name);
        addmenu.add(price);
        addmenu.add(categoryBox);
        addmenu.add(image);
        addmenu.add(chooseImgBtn);
        addmenu.add(addButton);
        addDialog.add(addmenu, BorderLayout.CENTER);
        addDialog.setVisible(true);
    }

    private void loadMenuItems() {
        menuPanel.removeAll();

        try (Connection conn = database.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT id, name, price, imagepath FROM menuitems")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("imagepath");

                JPanel itemPanel = createCardPanel(id, name, price, imagePath, "");
                menuPanel.add(itemPanel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + e.getMessage());
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private JPanel createCardPanel(int id, String name, double price, String imagePath, String category) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setPreferredSize(new Dimension(150, 200)); // Card size

        // Menu name
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Price
        JLabel priceLabel = new JLabel("₱" + price, SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Image (optional)
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                imageLabel.setText("[No Image]");
            }
        } else {
            imageLabel.setText("[No Image]");
        }

        // Layout
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(priceLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void addCategory() {
        String newCat = JOptionPane.showInputDialog(this, "Enter new category name:");
        if (newCat == null) {
            return;
        }
        newCat = newCat.trim();
        if (newCat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty.");
            return;
        }
        try (Connection conn = database.getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO categories (name) VALUES (?)")) {
            ps.setString(1, newCat);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Category added!");
            loadCategories();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCategories() {
        // ensure combo boxes exist
        if (categoryBox == null) {
            categoryBox = new JComboBox<>();
        }
        if (filterBox == null) {
            filterBox = new JComboBox<>();
        }

        categoryBox.removeAllItems();
        filterBox.removeAllItems();
        filterBox.addItem("All");

        try (Connection conn = database.getConnection()) {
            // ensure categories table exists (safe to call repeatedly)
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS categories ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "name VARCHAR(50) UNIQUE NOT NULL)"
                );
            } catch (Exception ignore) {
                // ignore, some DBs handle differently we'll continue
            }

            // seed some defaults if table is empty
            boolean needSeed = false;
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    needSeed = true;
                }
            } catch (Exception e) {
                needSeed = true; // if query fails, attempt to insert defaults anyway
            }

            if (needSeed) {
                String[] defaults = {"Coffee", "Beans", "Pastries", "Drinks", "Others"};
                try (PreparedStatement ins = conn.prepareStatement("INSERT IGNORE INTO categories (name) VALUES (?)")) {
                    for (String d : defaults) {
                        ins.setString(1, d);
                        try {
                            ins.executeUpdate();
                        } catch (Exception ex) {
                        }
                    }
                } catch (Exception e) {
                    // ignore seed failure, categories might already exist or DB permissions missing
                }
            }

            // load categories to both combo boxes
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM categories ORDER BY name ASC"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String catName = rs.getString("name");
                    categoryBox.addItem(catName);
                    filterBox.addItem(catName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void loadCategoriesToFilterBox() {
        filterBox.removeAllItems();
        filterBox.addItem("All");

        try (Connection conn = database.getConnection(); Statement stmt = conn.createStatement(); var rs = stmt.executeQuery("SELECT name FROM categories")) {

            while (rs.next()) {
                filterBox.addItem(rs.getString("name"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }

        filterBox.setSelectedItem("All");
    }

    private void removeCategory() {
        String selected = (String) categoryBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No category selected.");
            return;
        }
        if ("Uncategorized".equalsIgnoreCase(selected)) {
            JOptionPane.showMessageDialog(this, "You cannot remove 'Uncategorized'.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove category '" + selected + "'?\nAll menu items under this category will be moved to 'Uncategorized'.",
                "Confirm Remove Category",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Reassign items to 'Uncategorized'
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE menuitems SET category = 'Uncategorized' WHERE name = ?")) {
                    ps.setString(1, selected);
                    ps.executeUpdate();
                }
                // Delete the category
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM categories WHERE name = ?")) {
                    ps.setString(1, selected);
                    ps.executeUpdate();
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "Category removed and items reassigned to 'Uncategorized'.");
                loadCategories();
                loadmenuitems();
            } catch (Exception inner) {
                conn.rollback();
                throw inner;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error removing category: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void chooseImages() {
        JFileChooser fileChoose = new JFileChooser();
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png");
        fileChoose.setFileFilter(imageFilter);

        int option = fileChoose.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChoose.getSelectedFile();
            selectedimagepath = file.getAbsolutePath();
            image.setText(file.getName());
        }
    }

    private void addMenuItem() {
        String name = this.name.getText();
        String priceText = price.getText();
        String category = (String) categoryBox.getSelectedItem();

        if (name.isEmpty() || priceText.isEmpty() || selectedimagepath == null || category == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select image");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            File srcFile = new File(selectedimagepath);
            String imageDir = "images";
            new File(imageDir).mkdirs();

            String filename = System.currentTimeMillis() + "_" + srcFile.getName();
            Path desPath = Paths.get(imageDir, filename);
            Files.copy(srcFile.toPath(), desPath, StandardCopyOption.REPLACE_EXISTING);

            try (Connection conn = database.getConnection()) {
                String sql = "INSERT INTO menuitems (name, price, imagepath, category_id) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setString(3, desPath.toString());

                // look up category_id from categories table
                PreparedStatement catStmt = conn.prepareStatement("SELECT id FROM categories WHERE name = ?");
                catStmt.setString(1, category);
                ResultSet rs = catStmt.executeQuery();
                if (rs.next()) {
                    ps.setInt(4, rs.getInt("id"));
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER); // no category found
                }

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Menu item added");
                this.image.setText("No Image selected");
                selectedimagepath = null;

                // refresh GUI
                loadmenuitems();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid price format");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadmenuitems() {
        if (menuPanel == null) {
            return; 

                }menuPanel.removeAll();

        String selectedCategory = (filterBox != null) ? (String) filterBox.getSelectedItem() : "All";

        try (Connection conn = database.getConnection()) {
            String sql = "SELECT id, name, price, imagepath, availability FROM menuitems"; 
            PreparedStatement ps;

            if (selectedCategory != null && !"All".equals(selectedCategory)) {
                // Filter by category name
                sql += " WHERE category_id = (SELECT id FROM categories WHERE name = ?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, selectedCategory);
            } else {
                ps = conn.prepareStatement(sql);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("imagepath");
                String availability = rs.getString("availability");

                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                card.setPreferredSize(new Dimension(150, 250));

                // Image
                try {
                    Image img = ImageIO.read(new File(imagePath));
                    Image scaled = img.getScaledInstance(120, 150, Image.SCALE_SMOOTH);
                    JLabel imgLabel = new JLabel(new ImageIcon(scaled));
                    card.add(imgLabel, BorderLayout.CENTER);
                } catch (IOException e) {
                    JLabel imgLabel = new JLabel("No Image", SwingConstants.CENTER);
                    card.add(imgLabel, BorderLayout.CENTER);
                }

                // Info section (name, price, availability)
                JPanel infoPanel = new JPanel(new GridLayout(3, 1));
                JLabel nameLabel = new JLabel("Name: " + name, SwingConstants.CENTER);
                JLabel priceLabel = new JLabel("₱" + price, SwingConstants.CENTER);

                JLabel availabilityLabel = new JLabel(
                        "Available".equalsIgnoreCase(availability) ? "Available" : " Unavailable",
                        SwingConstants.CENTER
                );
                availabilityLabel.setForeground(
                        "Available".equalsIgnoreCase(availability) ? Color.GREEN.darker() : Color.RED
                );

                infoPanel.add(nameLabel);
                infoPanel.add(priceLabel);
                infoPanel.add(availabilityLabel);

                // Buttons
                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton editButton = new JButton("Edit");
                editButton.setPreferredSize(new Dimension(100, 30));
                editButton.addActionListener(e -> editMenuItem(id, name, price));
                buttonPanel.add(editButton);

                JButton removeButton = new JButton("Remove");
                removeButton.setPreferredSize(new Dimension(100, 30));
                removeButton.addActionListener(e -> removeMenuitem(id));
                buttonPanel.add(removeButton);

                // South Panel (info + buttons)
                JPanel southPanel = new JPanel(new BorderLayout());
                southPanel.add(infoPanel, BorderLayout.CENTER);
                southPanel.add(buttonPanel, BorderLayout.SOUTH);

                card.add(southPanel, BorderLayout.SOUTH);
                menuPanel.add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + e.getMessage());
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void editMenuItem(int id, String oldname, double oldprice) {
        JTextField name = new JTextField(oldname);
        JTextField price = new JTextField(String.valueOf(oldprice));

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("New Name: "));
        panel.add(name);
        panel.add(new JLabel("New Price: "));
        panel.add(price);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newname = name.getText();
            String newPrice = price.getText();
            try {
                double newPrices = Double.parseDouble(newPrice);
                try (Connection conn = database.getConnection()) {
                    String sql = "UPDATE menuitems SET name = ?, price = ? WHERE id=? ";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, newname);
                    ps.setDouble(2, newPrices);
                    ps.setInt(3, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Item updated successfully");
                    loadmenuitems();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Incorrect price format");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage());
            }
        }
    }

    private void removeMenuitem(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to remove item from menu?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = database.getConnection()) {
                String sql = "DELETE FROM menuitems WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Item deleted");
                loadmenuitems();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage());
            }
        }
    }

    private void reports() {
        if (reportsDialog != null && reportsDialog.isShowing()) {
            if (barGraphPanel != null) {
                barGraphPanel.repaint();
            }
            reportsDialog.toFront();
            return;
        }

        reportsDialog = new JDialog(this, "Sales Reports", true);
        reportsDialog.setSize(1300, 800);
        reportsDialog.setLocationRelativeTo(this);

        barGraphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                String[] labels = {"Today", "Yesterday", "1 Week", "Month", "3 Months", "1 Year"};
                int[] sales = new int[labels.length];

                try (Connection conn = database.getConnection()) {
                    sales[0] = getSalesCount(conn, "DATE(order_date) = CURDATE()");
                    sales[1] = getSalesCount(conn, "DATE(order_date) = CURDATE() - INTERVAL 1 DAY");
                    sales[2] = getSalesCount(conn, "order_date >= CURDATE() - INTERVAL 7 DAY AND DATE(order_date) < CURDATE() - INTERVAL 1 DAY");
                    sales[3] = getSalesCount(conn, "order_date >= CURDATE() - INTERVAL 1 MONTH AND order_date < CURDATE() - INTERVAL 7 DAY");
                    sales[4] = getSalesCount(conn, "order_date >= CURDATE() - INTERVAL 3 MONTH AND order_date < CURDATE() - INTERVAL 1 MONTH");
                    sales[5] = getSalesCount(conn, "order_date >= CURDATE() - INTERVAL 1 YEAR AND order_date < CURDATE() - INTERVAL 3 MONTH");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                int barWidth = 100;
                int spacing = 40;
                int maxHeight = getHeight() - 50;
                int maxSales = (sales.length > 0) ? java.util.Arrays.stream(sales).max().orElse(1) : 1;
                maxSales = Math.max(maxSales, 1);

                int totalWidth = (labels.length * barWidth) + ((labels.length - 1) * spacing);
                int startX = (getWidth() - totalWidth) / 2;

                for (int i = 0; i < sales.length; i++) {
                    int x = startX + i * (barWidth + spacing);
                    int height = (int) ((sales[i] / (float) maxSales) * maxHeight);
                    int y = getHeight() - height - 20;

                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, barWidth, height);
                    g.setColor(Color.BLACK);
                    g.drawString(labels[i] + " (" + sales[i] + ")", x, getHeight() - 5);
                }
            }
        };

        barGraphPanel.setPreferredSize(new Dimension(1000, 600));
        reportsDialog.add(barGraphPanel);
        reportsDialog.setVisible(true);
    }

    public static void refreshReports() {
        if (INSTANCE != null && INSTANCE.barGraphPanel != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    INSTANCE.barGraphPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private int getSalesCount(Connection conn, String whereClause) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total FROM orders WHERE " + whereClause;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void Settings() {
        JDialog SettingsDialog = new JDialog((JFrame) null, "Settings");
        SettingsDialog.setSize(400, 300);
        SettingsDialog.setLocationRelativeTo(null);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JButton usermanagement = new JButton("User Management");
        usermanagement.setPreferredSize(new Dimension(200, 40));
        usermanagement.setAlignmentX(CENTER_ALIGNMENT);
        usermanagement.addActionListener(e -> usermanagement());
 /*
        JButton MembershipManagement = new JButton("Membership Management");
        MembershipManagement.setPreferredSize(new Dimension(200, 40));
        MembershipManagement.setAlignmentX(CENTER_ALIGNMENT);
        MembershipManagement.addActionListener(e -> membershipmanagement());   */


        settingsPanel.add(title);
        settingsPanel.add(usermanagement);

        SettingsDialog.add(settingsPanel);
        SettingsDialog.setVisible(true);
    }

    private void usermanagement() {
        JDialog usermanage = new JDialog((JFrame) null, "User Management", true);
        usermanage.setSize(new Dimension(600, 500));
        usermanage.setLocationRelativeTo(null);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try (Connection conn = database.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT id, username, status FROM users"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("id");
                String uname = rs.getString("username");
                String status = rs.getString("status"); // active or banned

                JPanel rowPanel = new JPanel(new BorderLayout());
                rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JLabel userLabel = new JLabel("👤 " + uname + " (" + status + ")");
                userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                rowPanel.add(userLabel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

                // Ban/Unban button
                JButton banBtn = new JButton(status.equalsIgnoreCase("banned") ? "Unban" : "Ban");
                banBtn.addActionListener(ev -> {
                    try (PreparedStatement updatePs = conn.prepareStatement(
                            "UPDATE users SET status=? WHERE id=?")) {
                        updatePs.setString(1, status.equalsIgnoreCase("banned") ? "active" : "banned");
                        updatePs.setInt(2, userId);
                        updatePs.executeUpdate();
                        JOptionPane.showMessageDialog(usermanage,
                                uname + (status.equalsIgnoreCase("banned") ? " unbanned." : " banned."));
                        usermanage.dispose();
                        usermanagement(); // refresh window
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(usermanage, "Error updating user: " + ex.getMessage());
                    }
                });

                // Delete button
                JButton delBtn = new JButton("Delete");
                delBtn.addActionListener(ev -> {
                    int confirm = JOptionPane.showConfirmDialog(usermanage,
                            "Are you sure you want to delete " + uname + "?", "Confirm Delete",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try (PreparedStatement delPs = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
                            delPs.setInt(1, userId);
                            delPs.executeUpdate();
                            JOptionPane.showMessageDialog(usermanage, uname + " deleted.");
                            usermanage.dispose();
                            usermanagement(); // refresh window
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(usermanage, "Error deleting user: " + ex.getMessage());
                        }
                    }
                });

                buttonPanel.add(banBtn);
                buttonPanel.add(delBtn);

                rowPanel.add(buttonPanel, BorderLayout.EAST);

                userPanel.add(rowPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(userPanel);
        usermanage.add(scrollPane, BorderLayout.CENTER);

        usermanage.setVisible(true);
    }
 /* 
    private void membershipmanagement() {
        JDialog member = new JDialog((JFrame) null, "Member Management", true);
        member.setSize(new Dimension(600, 500));
        member.setLocationRelativeTo(null);
    }  */
}
