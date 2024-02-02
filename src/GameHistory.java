import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GameHistory extends JFrame {
    private JPanel panel1;
    private JTable gameHistory;
    private JButton btnBack;
    private JButton btnClearHistory;
    private DefaultTableModel tableModel;
    public User user;

    public GameHistory(User user) {
        this.user = user;
        initializeUI();
        loadGameHistoryFromDatabase();
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                GameMenu gameMenu = new GameMenu(user);
                gameMenu.setVisible(true);
            }
        });
    }

    private void initializeUI() {
        setTitle("Historia Gier");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        panel1 = new JPanel(new BorderLayout());
        add(panel1);

        tableModel = new DefaultTableModel(new Object[]{"Data Gry", "Wynik"}, 0);
        gameHistory = new JTable(tableModel);
        panel1.add(new JScrollPane(gameHistory), BorderLayout.CENTER);

        btnBack = new JButton("Wróć");
        btnClearHistory = new JButton("Wyczyść Historię");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBack);
        buttonPanel.add(btnClearHistory);
        panel1.add(buttonPanel, BorderLayout.SOUTH);


        btnClearHistory.addActionListener(e -> clearGameHistory());

        setLocationRelativeTo(null);
    }

    private void loadGameHistoryFromDatabase() {
        tableModel.setRowCount(0);
        final String dbUrl = "jdbc:mysql://localhost/tictactoe?serverTimezone=UTC";
        final String username = "root";
        final String password = "";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT game_date, result FROM game_results WHERE user_id = ? ORDER BY game_date DESC")) {
            stmt.setInt(1, this.user.id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String gameDate = rs.getString("game_date");
                    String result = rs.getString("result");
                    tableModel.addRow(new Object[]{gameDate, result});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas ładowania historii gier", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearGameHistory() {
        final String dbUrl = "jdbc:mysql://localhost/tictactoe?serverTimezone=UTC";
        final String username = "root";
        final String password = "";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM game_results WHERE user_id = ?")) {
            stmt.setInt(1, this.user.id);

            stmt.executeUpdate();
            loadGameHistoryFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas czyszczenia historii gier", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
