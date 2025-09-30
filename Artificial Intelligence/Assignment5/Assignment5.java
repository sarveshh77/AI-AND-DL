import java.util.*;

public class Assignment5 {
    static final char PLAYER = 'X';   // AI (maximizer)
    static final char OPPONENT = 'O'; // Human (minimizer)
    static final char EMPTY = '_';

    // Print board
    static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char c : row) System.out.print(c + " ");
            System.out.println();
        }
        System.out.println();
    }

    // Check for win state
    static int evaluate(char[][] b) {
        // Rows
        for (int row = 0; row < 3; row++) {
            if (b[row][0] == b[row][1] && b[row][1] == b[row][2]) {
                if (b[row][0] == PLAYER) return +10;
                else if (b[row][0] == OPPONENT) return -10;
            }
        }
        // Columns
        for (int col = 0; col < 3; col++) {
            if (b[0][col] == b[1][col] && b[1][col] == b[2][col]) {
                if (b[0][col] == PLAYER) return +10;
                else if (b[0][col] == OPPONENT) return -10;
            }
        }
        // Diagonals
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            if (b[0][0] == PLAYER) return +10;
            else if (b[0][0] == OPPONENT) return -10;
        }
        if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            if (b[0][2] == PLAYER) return +10;
            else if (b[0][2] == OPPONENT) return -10;
        }
        return 0;
    }

    // Check if moves left
    static boolean isMovesLeft(char[][] board) {
        for (char[] row : board)
            for (char c : row)
                if (c == EMPTY) return true;
        return false;
    }

    // Minimax with alpha-beta pruning
    static int minimax(char[][] board, int depth, boolean isMax, int alpha, int beta) {
        int score = evaluate(board);

        if (score == 10) return score - depth; // prefer faster win
        if (score == -10) return score + depth; // prefer slower loss
        if (!isMovesLeft(board)) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        int val = minimax(board, depth + 1, false, alpha, beta);
                        board[i][j] = EMPTY;
                        best = Math.max(best, val);
                        alpha = Math.max(alpha, best);
                        if (beta <= alpha) return best; // Beta cut-off
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = OPPONENT;
                        int val = minimax(board, depth + 1, true, alpha, beta);
                        board[i][j] = EMPTY;
                        best = Math.min(best, val);
                        beta = Math.min(beta, best);
                        if (beta <= alpha) return best; // Alpha cut-off
                    }
                }
            }
            return best;
        }
    }

    // Find best move for AI
    static int[] findBestMove(char[][] board) {
        int bestVal = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER;
                    int moveVal = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j] = EMPTY;
                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }
        return bestMove;
    }

    // Main game loop
    public static void main(String[] args) {
        char[][] board = {
            { '_', '_', '_' },
            { '_', '_', '_' },
            { '_', '_', '_' }
        };

        Scanner sc = new Scanner(System.in);
        System.out.println("Tic-Tac-Toe Minimax (AI = X, Human = O)");
        printBoard(board);

        while (true) {
            // AI move
            int[] best = findBestMove(board);
            if (best[0] == -1) {
                System.out.println("No moves left. It's a draw!");
                break;
            }
            board[best[0]][best[1]] = PLAYER;
            System.out.println("AI moves to: (" + best[0] + ", " + best[1] + ")");
            printBoard(board);
            if (evaluate(board) == 10) { System.out.println("AI (X) wins!"); break; }
            if (!isMovesLeft(board)) { System.out.println("Draw!"); break; }

            // Human move
            System.out.print("Enter your move (row col 0-2): ");
            int r = sc.nextInt(), c = sc.nextInt();
            if (r < 0 || r > 2 || c < 0 || c > 2 || board[r][c] != EMPTY) {
                System.out.println("Invalid move. Try again.");
                continue;
            }
            board[r][c] = OPPONENT;
            printBoard(board);
            if (evaluate(board) == -10) { System.out.println("Human (O) wins!"); break; }
            if (!isMovesLeft(board)) { System.out.println("Draw!"); break; }
        }
        sc.close();
    }
}
