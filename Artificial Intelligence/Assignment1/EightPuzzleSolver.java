import java.util.*;

public class EightPuzzleSolver {

    // Goal state
    private static final int[][] GOAL = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    };

    // Directions: up, down, left, right
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    // Check if the state is goal
    private static boolean isGoal(int[][] state) {
        return Arrays.deepEquals(state, GOAL);
    }

    // Find the blank (0)
    private static int[] findZero(int[][] state) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (state[i][j] == 0)
                    return new int[]{i, j};
        return null;
    }

    // Generate possible moves
    private static List<int[][]> generateMoves(int[][] state) {
        List<int[][]> moves = new ArrayList<>();
        int[] zero = findZero(state);
        int x = zero[0], y = zero[1];

        for (int[] d : DIRECTIONS) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && nx < 3 && ny >= 0 && ny < 3) {
                int[][] newState = copy(state);
                // Swap 0 with the neighbor
                newState[x][y] = newState[nx][ny];
                newState[nx][ny] = 0;
                moves.add(newState);
            }
        }
        return moves;
    }

    // BFS search
    public static List<int[][]> bfs(int[][] start) {
        Queue<Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(new Node(start, new ArrayList<>()));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            int[][] state = node.state;

            if (isGoal(state)) {
                node.path.add(state);
                return node.path;
            }

            visited.add(toString(state));

            for (int[][] move : generateMoves(state)) {
                if (!visited.contains(toString(move))) {
                    List<int[][]> newPath = new ArrayList<>(node.path);
                    newPath.add(state);
                    queue.add(new Node(move, newPath));
                }
            }
        }
        return null;
    }

    // DFS search with depth limit
    public static List<int[][]> dfs(int[][] start, int limit) {
        Stack<Node> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        stack.push(new Node(start, new ArrayList<>()));

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            int[][] state = node.state;

            if (isGoal(state)) {
                node.path.add(state);
                return node.path;
            }

            if (node.path.size() >= limit) continue;

            visited.add(toString(state));

            for (int[][] move : generateMoves(state)) {
                if (!visited.contains(toString(move))) {
                    List<int[][]> newPath = new ArrayList<>(node.path);
                    newPath.add(state);
                    stack.push(new Node(move, newPath));
                }
            }
        }
        return null;
    }

    // Helpers
    private static int[][] copy(int[][] state) {
        int[][] newState = new int[3][3];
        for (int i = 0; i < 3; i++)
            newState[i] = Arrays.copyOf(state[i], 3);
        return newState;
    }

    private static String toString(int[][] state) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : state)
            for (int val : row)
                sb.append(val);
        return sb.toString();
    }

    private static void printSolution(List<int[][]> path) {
        if (path == null) {
            System.out.println("No solution found.");
            return;
        }
        for (int step = 0; step < path.size(); step++) {
            System.out.println("\nStep " + step + ":");
            for (int[] row : path.get(step)) {
                System.out.println(Arrays.toString(row));
            }
        }
    }

    // Node class
    private static class Node {
        int[][] state;
        List<int[][]> path;

        Node(int[][] state, List<int[][]> path) {
            this.state = state;
            this.path = path;
        }
    }

    // Main
    public static void main(String[] args) {
        int[][] start = {
            {1, 2, 3},
            {4, 0, 6},
            {7, 5, 8}
        };

        System.out.println("BFS Solution:");
        printSolution(bfs(start));

        System.out.println("\nDFS Solution:");
        printSolution(dfs(start, 20));
    }
}
