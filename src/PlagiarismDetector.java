import java.util.*;

public class PlagiarismDetector {

    private static final int N = 5;

    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    private Map<String, List<String>> documentNgrams = new HashMap<>();

    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> grams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }
            grams.add(sb.toString().trim());
        }

        return grams;
    }

    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : ngrams) {
            Set<String> docs = ngramIndex.getOrDefault(gram, new HashSet<>());

            for (String otherDoc : docs) {
                if (!otherDoc.equals(docId)) {
                    matchCounts.put(otherDoc, matchCounts.getOrDefault(otherDoc, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches + " matching n-grams with \"" + otherDoc + "\"");
            System.out.println("Similarity: " + similarity + "%");

            if (similarity > 50) {
                System.out.println("PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "machine learning is a field of artificial intelligence that allows systems to learn automatically from data";
        String essay2 = "machine learning is a field of artificial intelligence that allows computers to learn automatically";
        String essay3 = "the history of ancient rome is full of wars emperors and political change";

        detector.addDocument("essay_123.txt", essay1);
        detector.addDocument("essay_089.txt", essay2);
        detector.addDocument("essay_092.txt", essay3);

        detector.analyzeDocument("essay_123.txt");
    }
}