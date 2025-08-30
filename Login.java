
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame {

    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private JButton signUpButton;
    private JLabel signUpLabel;

    // hardcoded admin credentials
    private final String ADMINUSERNAME = "Admin";
    private final String ADMINPASSWORD = "Password";

    public Login() {
        setTitle("Admin Menu Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // background
        setContentPane(new BackgroundPanel("loginbg.jpg"));
        getContentPane().setLayout(null);

        // Login box white background
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds(600, 200, 300, 400);
        loginPanel.setBackground(new Color(0, 0, 0, 128));
        loginPanel.setLayout(null);
        add(loginPanel);

        // title
        JLabel loginTitle = new JLabel("Log in");
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 20));
        loginTitle.setBounds(110, 15, 100, 30);
        loginPanel.add(loginTitle);

        // Username
        username = new JTextField();
        username.setBounds(30, 50, 240, 40);
        username.setBorder(BorderFactory.createTitledBorder("Username"));
        loginPanel.add(username);

        // Password
        password = new JPasswordField();
        password.setBounds(30, 120, 240, 40);
        password.setBorder(BorderFactory.createTitledBorder("Password"));
        loginPanel.add(password);

        // Log in Button
        loginButton = new JButton("Log in");
        loginButton.setBounds(30, 250, 240, 30);
        loginButton.setBackground(new Color(0, 120, 255));
        loginButton.setForeground(Color.WHITE);
        loginPanel.add(loginButton);

        // sign up
        signUpLabel = new JLabel("Don't have account? Sign up");
        signUpLabel.setForeground(Color.WHITE);
        signUpLabel.setBounds(50, 290, 200, 20);
        signUpLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        loginPanel.add(signUpLabel);

        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                signup obj = new signup();
                obj.setVisible(true);
            }
        });

        // Log in
        loginButton.addActionListener(e -> {
            String user = username.getText();
            String pass = new String(password.getPassword());

            // Admin Login
            if (user.equals(ADMINUSERNAME) && pass.equals(ADMINPASSWORD)) {
                Adminmenudashboard obj = new Adminmenudashboard();
                obj.setVisible(true);
                dispose();
                return;
            }

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/systemdatabase", "root", "");

                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                statement.setString(1, user);
                statement.setString(2, pass);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    staffdashboard obj = new staffdashboard();
                    obj.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong Username and Password");
                }

                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });
    }
}
