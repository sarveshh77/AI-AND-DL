import java.util.*;

// A cell on the grid
class Cell {
    int parentI, parentJ;  // parent cell coordinates
    double f, g, h;        // f = g + h

    Cell() {
        parentI = -1;
        parentJ = -1;
        f = g = h = Double.MAX_VALUE;
    }
}

public class Assignment4
 {
    static final int ROW = 9;
    static final int COL = 10;

    // Check if cell is valid (within grid)
    static boolean isValid(int row, int col) {
        return (row >= 0 && row < ROW && col >= 0 && col < COL);
    }

    // Check if cell is unblocked (1 means free, 0 means blocked)
    static boolean isUnblocked(int[][] grid, int row, int col) {
        return grid[row][col] == 1;
    }

    // Check if cell is destination
    static boolean isDestination(int row, int col, int[] dest) {
        return row == dest[0] && col == dest[1];
    }

    // Heuristic (Euclidean distance)
    static double calculateHValue(int row, int col, int[] dest) {
        return Math.sqrt((row - dest[0]) * (row - dest[0]) +
                         (col - dest[1]) * (col - dest[1]));
    }

    // Trace the path from destination to source
    static void tracePath(Cell[][] cellDetails, int[] dest) {
        System.out.println("The Path is:");
        int row = dest[0];
        int col = dest[1];

        List<int[]> path = new ArrayList<>();

        while (!(cellDetails[row][col].parentI == row &&
                 cellDetails[row][col].parentJ == col)) {
            path.add(new int[]{row, col});
            int tempRow = cellDetails[row][col].parentI;
            int tempCol = cellDetails[row][col].parentJ;
            row = tempRow;
            col = tempCol;
        }

        path.add(new int[]{row, col});
        Collections.reverse(path);

        for (int[] p : path) {
            System.out.print("-> (" + p[0] + "," + p[1] + ") ");
        }
        System.out.println();
    }

    // A* Search Algorithm
    static void aStarSearch(int[][] grid, int[] src, int[] dest) {
        // Check validity
        if (!isValid(src[0], src[1]) || !isValid(dest[0], dest[1])) {
            System.out.println("Source or destination is invalid");
            return;
        }
        if (!isUnblocked(grid, src[0], src[1]) || !isUnblocked(grid, dest[0], dest[1])) {
            System.out.println("Source or destination is blocked");
            return;
        }
        if (isDestination(src[0], src[1], dest)) {
            System.out.println("We are already at the destination");
            return;
        }

        boolean[][] closedList = new boolean[ROW][COL];
        Cell[][] cellDetails = new Cell[ROW][COL];

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                cellDetails[i][j] = new Cell();
            }
        }

        int i = src[0], j = src[1];
        cellDetails[i][j].f = 0.0;
        cellDetails[i][j].g = 0.0;
        cellDetails[i][j].h = 0.0;
        cellDetails[i][j].parentI = i;
        cellDetails[i][j].parentJ = j;

        // PriorityQueue based on f value
        PriorityQueue<int[]> openList = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));
        openList.add(new int[]{0, i, j});

        boolean foundDest = false;

        // Directions (8 neighbors)
        int[][] directions = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        while (!openList.isEmpty()) {
            int[] p = openList.poll();
            i = p[1];
            j = p[2];
            closedList[i][j] = true;

            for (int[] dir : directions) {
                int newI = i + dir[0];
                int newJ = j + dir[1];

                if (isValid(newI, newJ) && isUnblocked(grid, newI, newJ) && !closedList[newI][newJ]) {
                    if (isDestination(newI, newJ, dest)) {
                        cellDetails[newI][newJ].parentI = i;
                        cellDetails[newI][newJ].parentJ = j;
                        System.out.println("Destination found!");
                        tracePath(cellDetails, dest);
                        foundDest = true;
                        return;
                    } else {
                        double gNew = cellDetails[i][j].g + 1.0;
                        double hNew = calculateHValue(newI, newJ, dest);
                        double fNew = gNew + hNew;

                        if (cellDetails[newI][newJ].f == Double.MAX_VALUE || cellDetails[newI][newJ].f > fNew) {
                            openList.add(new int[]{(int) fNew, newI, newJ});
                            cellDetails[newI][newJ].f = fNew;
                            cellDetails[newI][newJ].g = gNew;
                            cellDetails[newI][newJ].h = hNew;
                            cellDetails[newI][newJ].parentI = i;
                            cellDetails[newI][newJ].parentJ = j;
                        }
                    }
                }
            }
        }

        if (!foundDest) {
            System.out.println("Failed to find the destination cell");
        }
    }

    // Driver Code
    public static void main(String[] args) {
        int[][] grid = {
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 1},
            {1, 1, 1, 0, 1, 1, 0, 1, 0, 1},
            {0, 0, 1, 0, 1, 0, 0, 0, 0, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 0},
            {1, 0, 1, 1, 1, 1, 0, 1, 0, 0},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 0, 0, 1, 0, 0, 1}
        };

        int[] src = {8, 0};
        int[] dest = {0, 0};

        aStarSearch(grid, src, dest);
    }
}
