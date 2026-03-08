import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequencyMap = new HashMap<>();

    public void addQuery(String query) {
        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    private void collectQueries(TrieNode node, String prefix, List<String> results) {

        if (node.isEnd) {
            results.add(prefix);
        }

        for (char c : node.children.keySet()) {
            collectQueries(node.children.get(c), prefix + c, results);
        }
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        List<String> queries = new ArrayList<>();
        collectQueries(node, prefix, queries);

        PriorityQueue<String> pq = new PriorityQueue<>(
                (a, b) -> frequencyMap.get(b) - frequencyMap.get(a)
        );

        pq.addAll(queries);

        List<String> result = new ArrayList<>();
        int count = 0;

        while (!pq.isEmpty() && count < 10) {
            String q = pq.poll();
            result.add(q + " (" + frequencyMap.get(q) + " searches)");
            count++;
        }

        return result;
    }

    public void updateFrequency(String query) {
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");

        List<String> suggestions = system.search("jav");

        System.out.println("Suggestions:");
        int i = 1;
        for (String s : suggestions) {
            System.out.println(i + ". " + s);
            i++;
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nUpdated frequency for 'java 21 features': " +
                system.frequencyMap.get("java 21 features"));
    }
}