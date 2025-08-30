
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class signup extends JFrame {

    private JTextField username;
    private JPasswordField password;
    private JPasswordField confirmpassword;
    private JButton signupButton;
    private JLabel backtologin;
    private static final int maxuser = 5;
    private int usercount = 0;

    public signup() {
        setTitle("Sign up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        setContentPane(new BackgroundPanel("loginbg.jpg"));
        getContentPane().setLayout(null);

        // sign Up
        JPanel signup = new JPanel();
        signup.setBounds(600, 200, 300, 400);
        signup.setBackground(new Color(255, 255, 255, 230));
        signup.setLayout(null);
        getContentPane().add(signup);

        // Title
        JLabel signuptext = new JLabel("Sign Up");
        signuptext.setFont(new Font("Arial", Font.BOLD, 20));
        signuptext.setBounds(120, 10, 100, 30);
        signup.add(signuptext);

        // Username
        username = new JTextField();
        username.setBounds(30, 50, 240, 40);
        username.setBorder(BorderFactory.createTitledBorder("Username"));
        signup.add(username);

        // Password
        password = new JPasswordField();
        password.setBounds(30, 100, 240, 40);
        password.setBorder(BorderFactory.createTitledBorder("Password"));
        signup.add(password);

        // confirm pass
        confirmpassword = new JPasswordField();
        confirmpassword.setBounds(30, 150, 240, 40);
        confirmpassword.setBorder(BorderFactory.createTitledBorder("Confirm Password"));
        signup.add(confirmpassword);

        // Sign Up Button
        signupButton = new JButton("Sign up");
        signupButton.setBounds(30, 220, 240, 30);
        signupButton.setBackground(new Color(0, 150, 50));
        signupButton.setForeground(Color.WHITE);
        signup.add(signupButton);

        // Back to login
        backtologin = new JLabel("Already Have an Account? Log in");
        backtologin.setBounds(65, 265, 200, 20);
        backtologin.setFont(new Font("Arial", Font.PLAIN, 11));
        signup.add(backtologin);

        // Sign up click
        signupButton.addActionListener(e -> {
            String user = username.getText();
            String pass = new String(password.getPassword());
            String confirmpass = new String(confirmpassword.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter Credentials to Sign up or you May log in if You have account");
                return;
            }
            if (!pass.equals(confirmpass)) {
                JOptionPane.showMessageDialog(this, "Password do not match");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/systemdatabase", "root", "");

                // Check how many users are already registered
                PreparedStatement countStatement = conn.prepareStatement("SELECT COUNT(*) FROM users");
                ResultSet countResult = countStatement.executeQuery();
                int totalUsers = 0;
                if (countResult.next()) {
                    totalUsers = countResult.getInt(1);
                }

                if (totalUsers >= maxuser) {
                    JOptionPane.showMessageDialog(this, "Maximum User Limit Reached (" + maxuser + "). Please ask Admin to remove one.");
                    conn.close();
                    return;
                }

                // Check if username already exists
                PreparedStatement checkstatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                checkstatement.setString(1, user);
                ResultSet result = checkstatement.executeQuery();

                if (result.next()) {
                    JOptionPane.showMessageDialog(this, "Username Already exist. Please Enter another username or you may log in");
                } else {
                    PreparedStatement insertuser = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                    insertuser.setString(1, user);
                    insertuser.setString(2, pass);
                    insertuser.executeUpdate();

                    dispose();
                    new staffdashboard().setVisible(true);
                }

                conn.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Back to login click (now works fine)
        backtologin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Login().setVisible(true);
            }
        });
    }
}
