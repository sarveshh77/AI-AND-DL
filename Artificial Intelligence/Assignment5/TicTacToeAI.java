
import java.util.*;

public class TicTacToeAI {
    static final char PLAYER = 'X';  // AI
    static final char HUMAN = 'O';
    static final char EMPTY = '_';
    static char[][] board = {
        { '_', '_', '_' },
        { '_', '_', '_' },
        { '_', '_', '_' }
    };

    // Print the board
    static void printBoard() {
        for (char[] row : board) {
            for (char c : row) System.out.print(c + " ");
            System.out.println();
        }
        System.out.println();
    }

    // Check for winner
    static int evaluate() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == PLAYER) return +10;
                if (board[i][0] == HUMAN) return -10;
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == PLAYER) return +10;
                if (board[0][i] == HUMAN) return -10;
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == PLAYER) return +10;
            if (board[0][0] == HUMAN) return -10;
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == PLAYER) return +10;
            if (board[0][2] == HUMAN) return -10;
        }
        return 0;
    }

    static boolean isMovesLeft() {
        for (char[] row : board)
            for (char c : row)
                if (c == EMPTY) return true;
        return false;
    }

    // Minimax with Alpha-Beta Pruning
    static int minimax(boolean isMax, int alpha, int beta, int depth) {
        int score = evaluate();
        if (score == 10) return score - depth;
        if (score == -10) return score + depth;
        if (!isMovesLeft()) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        best = Math.max(best, minimax(false, alpha, beta, depth + 1));
                        board[i][j] = EMPTY;
                        alpha = Math.max(alpha, best);
                        if (beta <= alpha) return best;
                    }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (board[i][j] == EMPTY) {
                        board[i][j] = HUMAN;
                        best = Math.min(best, minimax(true, alpha, beta, depth + 1));
                        board[i][j] = EMPTY;
                        beta = Math.min(beta, best);
                        if (beta <= alpha) return best;
                    }
            return best;
        }
    }

    // Find best move for AI
    static int[] bestMove() {
        int bestVal = Integer.MIN_VALUE;
        int[] move = {-1, -1};
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER;
                    int val = minimax(false, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                    board[i][j] = EMPTY;
                    if (val > bestVal) {
                        bestVal = val;
                        move = new int[]{i, j};
                    }
                }
        return move;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tic-Tac-Toe AI (AI=X, You=O)");
        printBoard();

        while (true) {
            // AI move
            int[] move = bestMove();
            if (move[0] == -1) { System.out.println("Draw!"); break; }
            board[move[0]][move[1]] = PLAYER;
            System.out.println("AI moves to: " + Arrays.toString(move));
            printBoard();
            if (evaluate() == 10) { System.out.println("AI Wins!"); break; }
            if (!isMovesLeft()) { System.out.println("Draw!"); break; }

            // Human move
            System.out.print("Enter your move (row col): ");
            int r = sc.nextInt(), c = sc.nextInt();
            if (r < 0 || r > 2 || c < 0 || c > 2 || board[r][c] != EMPTY) {
                System.out.println("Invalid move. Try again.");
                continue;
            }
            board[r][c] = HUMAN;
            printBoard();
            if (evaluate() == -10) { System.out.println("You Win!"); break; }
            if (!isMovesLeft()) { System.out.println("Draw!"); break; }
        }
        sc.close();
    }
}
