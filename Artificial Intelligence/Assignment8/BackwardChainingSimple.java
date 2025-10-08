import java.util.*;

class Rule {
    List<String> conditions;
    String conclusion;

    Rule(List<String> conditions, String conclusion) {
        this.conditions = conditions;
        this.conclusion = conclusion;
    }
}

public class BackwardChainingSimple {

    // Backward chaining function
    static boolean backwardChaining(String goal, Set<String> facts, List<Rule> rules, Set<String> proven) {
        // If goal already known
        if (facts.contains(goal)) {
            System.out.println("Fact known: " + goal);
            proven.add(goal);
            return true;
        }

        // Try to prove goal using rules
        for (Rule rule : rules) {
            if (rule.conclusion.equals(goal)) {
                System.out.println("Checking rule for: " + goal);
                boolean allConditionsMet = true;

                for (String cond : rule.conditions) {
                    if (!proven.contains(cond)) {
                        if (!backwardChaining(cond, facts, rules, proven)) {
                            allConditionsMet = false;
                            break;
                        }
                    }
                }

                if (allConditionsMet) {
                    System.out.println("Proved: " + goal);
                    proven.add(goal);
                    return true;
                }
            }
        }

        System.out.println("Could not prove: " + goal);
        return false;
    }

    public static void main(String[] args) {
        // Known facts
        Set<String> facts = new HashSet<>(Arrays.asList("Fever", "Cough"));

        // Define rules
        List<Rule> rules = Arrays.asList(
            new Rule(Arrays.asList("Fever", "Cough"), "Flu"),
            new Rule(Arrays.asList("Flu"), "RestNeeded"),
            new Rule(Arrays.asList("Flu"), "SeeDoctor")
        );

        String goal = "SeeDoctor";
        Set<String> proven = new HashSet<>();

        System.out.println("Trying to prove: " + goal + "\n");
        boolean result = backwardChaining(goal, facts, rules, proven);

        System.out.println("\nFinal Result:");
        if (result)
            System.out.println(goal + " is TRUE (proven)");
        else
            System.out.println(goal + " cannot be proven");
    }
}
