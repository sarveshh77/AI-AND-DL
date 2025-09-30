import java.util.*;

class Person {
    String name;
    String gender; // "male" or "female"
    List<Person> parents = new ArrayList<>();
    List<Person> children = new ArrayList<>();

    Person(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
}

class FamilyTree {
    private Map<String, Person> people = new HashMap<>();

    // Add person if not exists
    void addPerson(String name, String gender) {
        if (!people.containsKey(name)) {
            people.put(name, new Person(name, gender));
        }
    }

    // Add parent-child relation
    void addParentChild(String parentName, String parentGender,
                        String childName, String childGender) {
        addPerson(parentName, parentGender);
        addPerson(childName, childGender);
        Person parent = people.get(parentName);
        Person child = people.get(childName);
        parent.children.add(child);
        child.parents.add(parent);
    }

    // Get parents
    List<String> getParents(String name) {
        List<String> result = new ArrayList<>();
        if (people.containsKey(name)) {
            for (Person p : people.get(name).parents) {
                result.add(p.name);
            }
        }
        return result;
    }

    // Get father
    List<String> getFather(String name) {
        List<String> fathers = new ArrayList<>();
        if (people.containsKey(name)) {
            for (Person p : people.get(name).parents) {
                if (p.gender.equals("male")) {
                    fathers.add(p.name);
                }
            }
        }
        return fathers;
    }

    // Get grandmother
    List<String> getGrandmother(String name) {
        Set<String> grandmothers = new HashSet<>();
        for (String parent : getParents(name)) {
            for (String gp : getParents(parent)) {
                if (people.get(gp).gender.equals("female")) {
                    grandmothers.add(gp);
                }
            }
        }
        return new ArrayList<>(grandmothers);
    }

    // Query relationship
    String queryRelationship(String query) {
        query = query.toLowerCase().trim();
        if (!query.contains(" of ")) {
            return "Invalid query format. Use 'relationship of person' (e.g., 'father of kate')";
        }

        String[] parts = query.split(" of ");
        String relationship = parts[0].trim();
        String person = parts[1].trim();
        person = Character.toUpperCase(person.charAt(0)) + person.substring(1);

        List<String> results;
        if (relationship.equals("father")) {
            results = getFather(person);
        } else if (relationship.equals("grandmother")) {
            results = getGrandmother(person);
        } else {
            return "Unsupported relationship: " + relationship;
        }

        if (results.isEmpty()) {
            results.add("No " + relationship + " found");
        }

        // Format output like a slide
        StringBuilder sb = new StringBuilder("\n");
        sb.append("            ").append(Character.toUpperCase(relationship.charAt(0)))
          .append(relationship.substring(1)).append(" of ").append(person).append("\n");
        sb.append("--------------------------------------------------\n");
        for (String res : results) {
            sb.append("| ").append(res);
            sb.append(" ".repeat(Math.max(0, 50 - res.length()))).append("|\n");
        }
        sb.append("--------------------------------------------------\n");

        return sb.toString();
    }
}

public class Assignment3 {
    public static void main(String[] args) {
        FamilyTree tree = new FamilyTree();

        // Add sample family tree
        tree.addParentChild("John", "male", "Bob", "male");
        tree.addParentChild("Mary", "female", "Bob", "male");
        tree.addParentChild("Bob", "male", "Kate", "female");
        tree.addParentChild("Alice", "female", "Kate", "female");
        tree.addParentChild("Bob", "male", "Frank", "male");
        tree.addParentChild("Alice", "female", "Frank", "male");

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter query (e.g., 'father of kate'): ");
        String query = sc.nextLine();

        System.out.println(tree.queryRelationship(query));

        sc.close();
    }
}
