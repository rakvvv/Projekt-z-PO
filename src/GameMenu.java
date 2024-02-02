import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameMenu extends JFrame {
    private JPanel gameMenuPanel;
    private JButton btnGame;
    private JButton btnLogout;
    private JButton btnGameHistory;
    private JLabel userName;
    private User user;

    public GameMenu(User user) {
        this.user = user;
        addActionListeners();
        initializeUI();
        if (user != null) {
            String username = user.getName();
            userName.setText("Zalogowany z konta: " + username);
        } else {
            userName.setText("Zalogowany z konta: null");
        }
        setVisible(true);
    }


    private void addActionListeners() {
        btnGame.addActionListener(e -> {
            TicTacToe ticTacToe = new TicTacToe(user);
            ticTacToe.setVisible(true);
            setVisible(false);
        });

        btnGameHistory.addActionListener(e -> {
            GameHistory historyFrame = new GameHistory(user);
            historyFrame.setVisible(true);
            setVisible(false);
        });

        btnLogout.addActionListener(e -> {
            this.dispose(); // Zamyka GameMenu
            LoginForm loginForm = new LoginForm(null); // Tworzy nową instancję LoginForm
            loginForm.setVisible(true); // Pokazuje LoginForm, umożliwiając ponowne logowanie
            dispose();
        });
    }

    private void initializeUI() {
        setTitle("Menu Gry");
        setContentPane(gameMenuPanel);
        int width = 450, height = 475;
        setMinimumSize(new Dimension(width,height));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Wyśrodkowanie okna

    }

}