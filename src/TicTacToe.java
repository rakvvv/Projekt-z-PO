import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToe extends JFrame {
    private JPanel mainPanel;
    private JButton backButton;
    private JButton exitButton;
    private JPanel fieldPanel2;
    private JPanel fieldPanel1;
    private JPanel fieldPanel3;
    private JPanel fieldPanel4;
    private JPanel fieldPanel5;
    private JPanel fieldPanel6;
    private JPanel fieldPanel7;
    private JPanel fieldPanel8;
    private JPanel fieldPanel9;
    private JLabel resultLabel;
    private JLabel fieldPanelLabel1;
    private JLabel fieldPanelLabel2;
    private JLabel fieldPanelLabel3;
    private JLabel fieldPanelLabel4;
    private JLabel fieldPanelLabel5;
    private JLabel fieldPanelLabel6;
    private JLabel fieldPanelLabel7;
    private JLabel fieldPanelLabel8;
    private JLabel fieldPanelLabel9;
    private JButton resetButton;
    private JButton playerSymbolButton;
    private JButton playerStartButton;

    private final JPanel[][] fieldPanels = {
            {fieldPanel1, fieldPanel2, fieldPanel3},
            {fieldPanel4, fieldPanel5, fieldPanel6},
            {fieldPanel7, fieldPanel8, fieldPanel9}
    };

    private final JLabel[][] fieldPanelsLabels = {
            {fieldPanelLabel1, fieldPanelLabel2, fieldPanelLabel3},
            {fieldPanelLabel4, fieldPanelLabel5, fieldPanelLabel6},
            {fieldPanelLabel7, fieldPanelLabel8, fieldPanelLabel9}
    };

    private int[][] fieldStatus = new int[3][3];
    private int currentPlayer = 1; // 1 for X, 2 for O
    private boolean endGame = false;
    private char playerSymbol = 'X'; // Default player symbol
    private boolean playerStarts = true; // Default player starts first

    public static void main(String[] args) {
        new TicTacToe().setVisible(true);
    }

    public TicTacToe() {
        initializeUI();

        exitButton.addActionListener(e -> dispose());

        resetButton.addActionListener(e -> resetGame());

        playerSymbolButton.addActionListener(e -> {
            if (playerSymbol == 'X') {
                playerSymbol = 'O';
                playerSymbolButton.setText("Symbol: O");
            } else {
                playerSymbol = 'X';
                playerSymbolButton.setText("Symbol: X");
            }
            resetGame();
        });

        playerStartButton.addActionListener(e -> {
            playerStarts = !playerStarts;
            if (playerStarts) {
                playerStartButton.setText("Player Starts First");
            } else {
                playerStartButton.setText("Computer Starts First");
                makeComputerMove();
            }
            resetGame();
        });

        for (int i = 0; i < fieldPanels.length; i++) {
            for (int j = 0; j < fieldPanels[i].length; j++) {
                int finalI = i;
                int finalJ = j;
                fieldPanels[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        makeMove(finalI, finalJ);
                    }
                });
            }
        }

        resetGame();
    }

    private void initializeUI() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(mainPanel);
        pack();
    }

    private void makeMove(int rowId, int columnId) {
        if (!endGame) {
            if (fieldStatus[rowId][columnId] == 0) {
                fieldStatus[rowId][columnId] = currentPlayer;
                updateUI();

                int result = checkGameResult(fieldStatus); // Poprawiono przekazywanie argumentu
                if (result != -1) {
                    handleGameResult(result);
                } else {
                    if (!endGame) {
                        switchPlayer();
                        makeComputerMove();
                        updateUI();
                        result = checkGameResult(fieldStatus); // Poprawiono przekazywanie argumentu
                        if (result != -1) {
                            handleGameResult(result);
                        }
                    }
                }
            }
        }
    }


    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    private void makeComputerMove() {
        if (!endGame) {
            int[] bestMove = minimax(fieldStatus, 0, true);
            if (bestMove[1] != -1 && bestMove[2] != -1) {
                fieldStatus[bestMove[1]][bestMove[2]] = currentPlayer;
                switchPlayer();
                updateUI();
            }
        }
    }

    private int[] minimax(int[][] board, int depth, boolean isMaximizing) {
        if (isGameOver(board)) {
            int utility = calculateUtility(board);
            return new int[]{utility, -1, -1}; // Zwraca ocenę, a także współrzędne ruchu
        }

        int[] bestMove = {isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE, -1, -1};

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = isMaximizing ? 2 : 1;

                    int[] result = minimax(board, depth + 1, !isMaximizing);

                    board[i][j] = 0; // Przywróć stan planszy

                    if ((isMaximizing && result[0] > bestMove[0]) || (!isMaximizing && result[0] < bestMove[0])) {
                        bestMove[0] = result[0];
                        bestMove[1] = i;
                        bestMove[2] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int calculateUtility(int[][] board) {
        int result = checkGameResult(board);
        if (result == 1) {
            return -1; // Gracz X wygrywa
        } else if (result == 2) {
            return 1;  // Gracz O wygrywa
        } else {
            return 0;  // Remis
        }
    }

    private boolean isGameOver(int[][] board) {
        return checkGameResult(board) != -1 || isBoardFull(board);
    }

    private boolean isBoardFull(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int checkGameResult(int[][] board) {
        // Sprawdź wiersze i kolumny
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return board[i][0]; // Gracz X lub O wygrywa w wierszu
            }
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                return board[0][i]; // Gracz X lub O wygrywa w kolumnie
            }
        }

        // Sprawdź przekątne
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return board[0][0]; // Gracz X lub O wygrywa na przekątnej
        }
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            return board[0][2]; // Gracz X lub O wygrywa na przekątnej
        }

        // Sprawdź remis
        boolean isTie = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    isTie = false; // Plansza nie jest pełna, gra nie jest remisowa
                    break;
                }
            }
        }
        if (isTie) {
            return 0; // Remis
        }

        // Gra nie jest zakończona
        return -1;
    }


    private void updateUI() {
        for (int i = 0; i < fieldPanelsLabels.length; i++) {
            for (int j = 0; j < fieldPanelsLabels[i].length; j++) {
                fieldPanelsLabels[i][j].setText(getSymbolForPlayer(fieldStatus[i][j]));
            }
        }
    }

    private String getSymbolForPlayer(int player) {
        return (player == 1) ? "X" : (player == 2) ? "O" : "";
    }

    private void handleGameResult(int result) {
        endGame = true;
        if (result == 0) {
            resultLabel.setText("It's a Tie!");
        } else {
            resultLabel.setText("Player " + getSymbolForPlayer(result) + " wins!");
        }
    }

    private void resetGame() {
        for (int i = 0; i < fieldStatus.length; i++) {
            for (int j = 0; j < fieldStatus[i].length; j++) {
                fieldStatus[i][j] = 0;
            }
        }

        endGame = false;
        currentPlayer = 1;
        resultLabel.setText("Player " + getSymbolForPlayer(currentPlayer) + "'s Turn");
        updateUI();

        if (!playerStarts) {
            makeComputerMove();
            updateUI();
        }
    }
}
