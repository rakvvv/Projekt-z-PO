import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegisterForm extends JDialog {
    private JPanel registerPanel;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton SIGNUPButton;
    private JButton cancelButton;
    public RegisterForm(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        int width = 450, height = 475;
        setMinimumSize(new Dimension(width,height));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        SIGNUPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (registerUser()) {
                    dispose(); //
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }


    private boolean registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Confirm Password does not match", "Try again", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Nowy krok: sprawdzenie unikalności e-maila przed rejestracją
        if (emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email is already used. Please use a different email.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        user = addUserToDatabase(name, email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Registration successful. Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the RegisterForm
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register new user", "Try again", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public User user;
    private User addUserToDatabase(String name,String email,String password){
        User user = null;

        //spr połaczenai do DB
        final String DB_URL = "jdbc:mysql://localhost/tictactoe?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name,email,password) VALUES (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);

            //insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0){
                user = new User();
                user.name = name;
                user.email  = email;
                user.password = password;
            }
            //close connection
            stmt.close();
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return user;
    }

    private boolean emailExists(String email) {
        final String DB_URL = "jdbc:mysql://localhost/tictactoe?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";
        String sql = "SELECT COUNT(*) FROM users WHERE email=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
