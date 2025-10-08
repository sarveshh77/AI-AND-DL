import java.util.*;

class Rule {
    List<String> conditions;
    String conclusion;

    Rule(List<String> conditions, String conclusion) {
        this.conditions = conditions;
        this.conclusion = conclusion;
    }
}

public class ForwardChainingSimple {

    // Forward chaining method
    static Set<String> forwardChaining(Set<String> facts, List<Rule> rules) {
        boolean newFactAdded = true;

        while (newFactAdded) {
            newFactAdded = false;

            for (Rule rule : rules) {
                boolean allTrue = true;

                // Check if all conditions are present in facts
                for (String cond : rule.conditions) {
                    if (!facts.contains(cond)) {
                        allTrue = false;
                        break;
                    }
                }

                // Add new conclusion if all conditions met
                if (allTrue && !facts.contains(rule.conclusion)) {
                    System.out.println("Inferred new fact: " + rule.conclusion);
                    facts.add(rule.conclusion);
                    newFactAdded = true;
                }
            }
        }
        return facts;
    }

    public static void main(String[] args) {
        // Initial known facts
        Set<String> facts = new HashSet<>(Arrays.asList("Fever", "Cough", "BodyAche"));

        // Rules: IF conditions THEN conclusion
        List<Rule> rules = Arrays.asList(
            new Rule(Arrays.asList("Fever", "Cough"), "Flu"),
            new Rule(Arrays.asList("Flu"), "RestNeeded"),
            new Rule(Arrays.asList("Flu"), "SeeDoctor")
        );

        System.out.println("Initial facts: " + facts);
        Set<String> inferred = forwardChaining(facts, rules);

        System.out.println("\nFinal inferred facts: " + inferred);
    }
}
