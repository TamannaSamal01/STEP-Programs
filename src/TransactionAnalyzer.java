import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    public List<int[]> findTwoSum(List<Transaction> transactions, int target) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public List<int[]> findTwoSumWithWindow(List<Transaction> transactions, int target, long windowMillis) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {

            Iterator<Map.Entry<Integer, Transaction>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Transaction old = it.next().getValue();
                if (t.time - old.time > windowMillis) {
                    it.remove();
                }
            }

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public List<List<Integer>> findKSum(List<Transaction> transactions, int k, int target) {

        List<List<Integer>> result = new ArrayList<>();
        backtrack(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(List<Transaction> transactions, int k, int target, int start,
                           List<Integer> current, List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || start >= transactions.size()) return;

        for (int i = start; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);

            current.add(t.id);
            backtrack(transactions, k - 1, target - t.amount, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public void detectDuplicates(List<Transaction> transactions) {

        Map<String, List<String>> map = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "-" + t.merchant;
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t.account);
        }

        for (String key : map.keySet()) {
            List<String> accounts = map.get(key);
            if (accounts.size() > 1) {
                System.out.println("Duplicate: " + key + " Accounts: " + accounts);
            }
        }
    }

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1, 500, "StoreA", "acc1", System.currentTimeMillis()));
        transactions.add(new Transaction(2, 300, "StoreB", "acc2", System.currentTimeMillis()));
        transactions.add(new Transaction(3, 200, "StoreC", "acc3", System.currentTimeMillis()));

        List<int[]> pairs = analyzer.findTwoSum(transactions, 500);

        for (int[] p : pairs) {
            System.out.println("TwoSum Pair: " + p[0] + ", " + p[1]);
        }

        analyzer.detectDuplicates(transactions);

        List<List<Integer>> ksum = analyzer.findKSum(transactions, 3, 1000);

        for (List<Integer> list : ksum) {
            System.out.println("KSum: " + list);
        }
    }
}