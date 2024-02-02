import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {

    private JButton btnOk;
    private JButton btnCancel;
    private JPasswordField tfPassword;
    private JTextField tfEmail;
    private JPanel loginPanel;
    private JButton registerButton;
    public User user;

    public LoginForm(JFrame parent){
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        int width = 450, height = 475;
        setMinimumSize(new Dimension(width,height));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String paasowrd = String.valueOf(tfPassword.getPassword());

                user =  getAutenticateUser(email,paasowrd);

                if(user != null) {
                    dispose();
                    GameMenu gameMenu = new GameMenu(user);
                    gameMenu.setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email or password invalied",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false); // Ukrywa LoginForm
                RegisterForm registerForm = new RegisterForm(LoginForm.this);
                setVisible(true); // Pokazuje LoginForm ponownie po zamknięciu RegisterForm
            }
        });
    }

    private User getAutenticateUser(String email, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/tictactoe?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.id = resultSet.getInt("id");
                    user.name = resultSet.getString("name");
                    user.email = resultSet.getString("email");
                    user.password = resultSet.getString("password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}
