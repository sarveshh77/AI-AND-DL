import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Assignment2 {
    private static Set<String> leftSide = new HashSet<>();
    private static Set<String> rightSide = new HashSet<>();
    private static String farmerSide = "left";

    // Initialize game state
    public static void initializeGame() {
        leftSide.add("farmer");
        leftSide.add("wolf");
        leftSide.add("goat");
        leftSide.add("cabbage");
    }

    // Check if a side is safe
    public static boolean isSafe(Set<String> side) {
        return !((side.contains("wolf") && side.contains("goat") && !side.contains("farmer")) ||
                 (side.contains("goat") && side.contains("cabbage") && !side.contains("farmer")));
    }

    // Move an item with the farmer
    public static String moveItem(String item) {
        if (farmerSide.equals("left")) {
            if (item != null) {
                if (!leftSide.contains(item)) {
                    return "Invalid move!";
                }
                leftSide.remove(item);
                rightSide.add(item);
            }
            farmerSide = "right";
            leftSide.remove("farmer");
            rightSide.add("farmer");
        } else {
            if (item != null) {
                if (!rightSide.contains(item)) {
                    return "Invalid move!";
                }
                rightSide.remove(item);
                leftSide.add(item);
            }
            farmerSide = "left";
            rightSide.remove("farmer");
            leftSide.add("farmer");
        }

        if (!isSafe(leftSide) || !isSafe(rightSide)) {
            return "Move not allowed! Items left alone unsafely.";
        }
        return "Move successful!\n";
    }

    // Display current game state
    public static void displayState() {
        System.out.println("Left Side: " + leftSide);
        System.out.println("Right Side: " + rightSide);
        System.out.println("Farmer is on the " + farmerSide + " side.");
    }

    // Check if player won
    public static boolean isGameWon() {
        return leftSide.isEmpty();
    }

    // Main game loop
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeGame();

        System.out.println("Welcome to the Wolf, Goat, and Cabbage game!\n");
        displayState();

        while (!isGameWon()) {
            System.out.print("What do you want to move? (wolf, goat, cabbage, none): ");
            String moveInput = scanner.nextLine().trim().toLowerCase();

            String item = null;
            if (moveInput.equals("none")) {
                item = null;
            } else if (moveInput.equals("wolf") || moveInput.equals("goat") || moveInput.equals("cabbage")) {
                item = moveInput;
            } else {
                System.out.println("Invalid item! Please choose wolf, goat, cabbage, or none.");
                continue;
            }

            String result = moveItem(item);
            System.out.println(result);
            displayState();

            if (result.startsWith("Move not allowed")) {
                break;
            }
        }

        if (isGameWon()) {
            System.out.println("Congratulations! You won the game!");
        } else {
            System.out.println("Game over! Try again.");
        }

        scanner.close();
    }
}
