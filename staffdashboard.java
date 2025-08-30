import java.awt.BorderLayout;
import  java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class staffdashboard extends JFrame {
    private JTextField name;
    private JTextField price;
    private JLabel image;
    private JPanel menuPanel;

    public staffdashboard(){
        setTitle("Staff Dashboard  Coffee Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Menu item display panel

        menuPanel = new JPanel(new GridLayout(0, 4, 10,10)); 
        JScrollPane scrollPane = new JScrollPane(menuPanel);


        add(scrollPane, BorderLayout.CENTER);

        loadmenuitems();
        setVisible(true);
    }

        private void loadmenuitems() {
            menuPanel.removeAll();
            try (Connection conn = database.getConnection()){
                String sql = "SELECT id, name, price, imagepath FROM menuitems";
                ResultSet result = conn.createStatement().executeQuery(sql);

        

                while (result.next()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    double price = result.getDouble("price");
                    String imagepath = result.getString("imagepath");
                    


                        JPanel card = new JPanel();
                        card.setLayout(new BorderLayout());

                        card.setBackground(Color.DARK_GRAY);
                        card.setPreferredSize(new Dimension(100, 150));
                        // Image
                    
                    try {
                        BufferedImage img = ImageIO.read(new File(imagepath));
                        ImageIcon icon = new ImageIcon(img.getScaledInstance(80,100, Image.SCALE_SMOOTH));
                        JLabel image = new JLabel(icon);
                        card.add(image, BorderLayout.CENTER);
                        
                    } 
                    catch (IOException e) {
                        card.add(new JLabel("No Image"), BorderLayout.CENTER);
                    }
                    JPanel bothbutton = new JPanel(new FlowLayout());
                    bothbutton.setBackground(Color.DARK_GRAY);
                

                    JPanel modify = new JPanel(new GridLayout(3, 1));
                    modify.setBackground(Color.DARK_GRAY);

                    JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
                    nameLabel.setForeground(Color.WHITE);
                    
                    JLabel prices = new JLabel("P "+ price, SwingConstants.CENTER);
                    prices.setForeground(Color.WHITE);

                    JButton buy = new JButton("Buy");
                    buy.setPreferredSize(new Dimension(20, 25));
                    int quantity = 0;
                    buy.addActionListener(e -> Buy(id, name, price, quantity));

                    JButton addcartButton = new JButton("Add to cart");
                    addcartButton.setPreferredSize(new Dimension(20, 25));
                    addcartButton.addActionListener(e -> addcart(name, price)); 


                
                    modify.add(nameLabel);
                    modify.add(prices);
                    modify.add(buy);
                    modify.add(addcartButton);
                    card.add(modify, BorderLayout.SOUTH);
                    menuPanel.add(card);
                }
            }
                catch (Exception e) {
                        e.printStackTrace();
                    }

            menuPanel.revalidate();
            menuPanel.repaint();
        }

        private void Buy(int menuItemId, String name, double price, int quantity) {
            JDialog dialog = new JDialog((JFrame) null, "Buy Item", true);
            dialog.setSize(320, 240);
            dialog.setLocationRelativeTo(null);

            JPanel buyPanel = new JPanel(new GridLayout(5, 1));
            buyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel title = new JLabel("Buy Item", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel nameLabel = new JLabel("Item: " + name, SwingConstants.CENTER);
            JLabel priceLabel = new JLabel("Price: P" + price, SwingConstants.CENTER);

            // Ask quantity
            JTextField qtyField = new JTextField("1");
            JPanel qtyPanel = new JPanel(new BorderLayout());
            qtyPanel.add(new JLabel("Quantity: "), BorderLayout.WEST);
            qtyPanel.add(qtyField, BorderLayout.CENTER);

            JButton confirmbuyButton = new JButton("Confirm Purchase");

            confirmbuyButton.addActionListener(e -> {
                int qty;
                try {
                    qty = Integer.parseInt(qtyField.getText().trim());
                    if (qty <= 0) throw new NumberFormatException("Quantity must be > 0");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Enter a valid quantity.", "Invalid quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection conn = database.getConnection()) {
                    // Insert into orders with proper placeholders and NOW() for order_date
                    String sql = "INSERT INTO orders (menu_item_id, price, quantity, order_date) VALUES (?, ?, ?, NOW())";
                    try (var ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, menuItemId);
                    ps.setDouble(2, price);
                    ps.setInt(3, qty);
                    ps.executeUpdate();

                    }
                    JOptionPane.showMessageDialog(dialog, "Purchase confirmed: " + qty + " x " + name);
                    dialog.dispose();


                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                            "Error saving purchase:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            buyPanel.add(title);
            buyPanel.add(nameLabel);
            buyPanel.add(priceLabel);
            buyPanel.add(qtyPanel);
            buyPanel.add(confirmbuyButton);

            dialog.add(buyPanel);
            dialog.setVisible(true);
        }

        private void addcart(String name, double price){
            JDialog dialogaddcart = new JDialog((JFrame) null, "Cart", true);
            dialogaddcart.setSize(500, 500);
            dialogaddcart.setLocationRelativeTo(null);
            JPanel addcartPanel = new JPanel();
            addcartPanel.setLayout(new GridLayout( 2, 4));
            addcartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel title = new JLabel("Cart", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 24));
            JLabel nameLabel = new JLabel("Item: " + name, SwingConstants.CENTER);
            JLabel priceLabel = new JLabel("Price: P" + price, SwingConstants.CENTER);


            JButton confirmbuyButton = new JButton("Confirm Purchase");
            confirmbuyButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(dialogaddcart, "To be Purchased " + name + " Price " + price);
                dialogaddcart.dispose();
            });
            addcartPanel.add(title);
            addcartPanel.add(nameLabel);
            addcartPanel.add(priceLabel);
            addcartPanel.add(confirmbuyButton);

            dialogaddcart.add(addcartPanel);
            dialogaddcart.setVisible(true);

        }
        
    
}

