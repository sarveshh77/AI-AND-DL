import java.io.*;
import java.util.*;

public class SmartBot {

    // Normalize: lowercase, trim, remove non-alphanumeric (except space)
    private static String normalize(String str) {
        return str.toLowerCase().trim().replaceAll("[^a-z0-9 ]", "");
    }

    // Load knowledge base
    private static Map<String, String> loadKB(String filename) {
        Map<String, String> kb = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String question, answer;
            while ((question = br.readLine()) != null && (answer = br.readLine()) != null) {
                kb.put(normalize(question), answer.trim());
            }
        } catch (IOException e) {
            System.out.println("⚠️ Knowledge base not found, starting fresh.");
        }
        return kb;
    }

    // Save knowledge base
    private static void saveKB(Map<String, String> kb, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, String> entry : kb.entrySet()) {
                bw.write(entry.getKey());
                bw.newLine();
                bw.write(entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving KB: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String filename = "knowledge.txt";
        Map<String, String> kb = loadKB(filename);

        System.out.println("🤖 SmartBot: Hello! I am your assistant. Type 'exit' to quit.");

        while (true) {
            System.out.print("\nYou: ");
            String input = sc.nextLine();
            String key = normalize(input);

            if (key.equals("exit")) {
                System.out.println("🤖 SmartBot: Goodbye!");
                break;
            }

            if (kb.containsKey(key)) {
                System.out.println("🤖 SmartBot: " + kb.get(key));
            } else {
                System.out.println("🤖 SmartBot: I don't know the answer. Can you teach me? (yes/no)");
                String teach = sc.nextLine().trim().toLowerCase();
                if (teach.equals("yes")) {
                    System.out.println("Please provide the correct answer:");
                    String answer = sc.nextLine().trim();
                    kb.put(key, answer);
                    saveKB(kb, filename);
                    System.out.println("🤖 SmartBot: Thank you! I will remember this.");
                } else {
                    System.out.println("🤖 SmartBot: Okay, maybe next time.");
                }
            }
        }
        sc.close();
    }
}
