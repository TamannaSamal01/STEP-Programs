import java.util.*;

public class UsernameChecker {

    private HashMap<String, Integer> users = new HashMap<>();
    private HashMap<String, Integer> attempts = new HashMap<>();
    private int idCounter = 1;

    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    public void register(String username) {
        if (!users.containsKey(username)) {
            users.put(username, idCounter++);
        }
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int i = 1;

        while (suggestions.size() < 3) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
            i++;
        }

        String dotVersion = username.replace("_", ".");
        if (!users.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String maxUser = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser;
    }

    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();

        checker.register("john_doe");

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));
        System.out.println(checker.suggestAlternatives("john_doe"));
        System.out.println(checker.getMostAttempted());
    }
}