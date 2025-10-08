import java.util.*;

public class SimpleEightPuzzle {

    // Goal state
    static int[][] goal = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    };

    // Convert matrix to string (for easy comparison and storing in visited)
    static String stateToString(int[][] state) {
        String s = "";
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                s += state[i][j];
        return s;
    }

    // Copy state (to avoid modifying original)
    static int[][] copy(int[][] state) {
        int[][] newState = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                newState[i][j] = state[i][j];
        return newState;
    }

    // Find blank (0) position
    static int[] findZero(int[][] state) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (state[i][j] == 0) return new int[]{i, j};
        return null;
    }

    // Generate next possible moves
    static List<int[][]> generateMoves(int[][] state) {
        List<int[][]> moves = new ArrayList<>();
        int[] zero = findZero(state);
        int x = zero[0], y = zero[1];

        // 4 directions
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && nx < 3 && ny >= 0 && ny < 3) {
                int[][] newState = copy(state);
                // swap blank with neighbor
                newState[x][y] = newState[nx][ny];
                newState[nx][ny] = 0;
                moves.add(newState);
            }
        }
        return moves;
    }

    // BFS
    static void bfs(int[][] start) {
        Queue<int[][]> q = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        q.add(start);
        visited.add(stateToString(start));
        parent.put(stateToString(start), null);

        while (!q.isEmpty()) {
            int[][] state = q.poll();
            if (stateToString(state).equals(stateToString(goal))) {
                printPath(state, parent);
                return;
            }
            for (int[][] next : generateMoves(state)) {
                String key = stateToString(next);
                if (!visited.contains(key)) {
                    visited.add(key);
                    parent.put(key, stateToString(state));
                    q.add(next);
                }
            }
        }
        System.out.println("No solution.");
    }

    // DFS with depth limit
    static void dfs(int[][] start, int limit) {
        Stack<int[][]> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> depth = new HashMap<>();

        stack.push(start);
        visited.add(stateToString(start));
        parent.put(stateToString(start), null);
        depth.put(stateToString(start), 0);

        while (!stack.isEmpty()) {
            int[][] state = stack.pop();
            if (stateToString(state).equals(stateToString(goal))) {
                printPath(state, parent);
                return;
            }
            if (depth.get(stateToString(state)) >= limit) continue;

            for (int[][] next : generateMoves(state)) {
                String key = stateToString(next);
                if (!visited.contains(key)) {
                    visited.add(key);
                    parent.put(key, stateToString(state));
                    depth.put(key, depth.get(stateToString(state)) + 1);
                    stack.push(next);
                }
            }
        }
        System.out.println("No solution within depth " + limit);
    }

    // Print path using parent map
    static void printPath(int[][] end, Map<String, String> parent) {
        List<String> path = new ArrayList<>();
        String key = stateToString(end);
        while (key != null) {
            path.add(key);
            key = parent.get(key);
        }
        Collections.reverse(path);

        int step = 0;
        for (String s : path) {
            System.out.println("\nStep " + step++);
            for (int i = 0; i < 9; i++) {
                System.out.print(s.charAt(i) + " ");
                if ((i+1)%3==0) System.out.println();
            }
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
        bfs(start);

        System.out.println("\nDFS Solution:");
        dfs(start, 20);
    }
}
