import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    private Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    public void processEvent(PageEvent event) {

        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        uniqueVisitors.putIfAbsent(event.url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);

        trafficSources.put(event.source, trafficSources.getOrDefault(event.source, 0) + 1);
    }

    public void getDashboard() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");

        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println((count + 1) + ". " + url + " - " + views + " views (" + unique + " unique)");
            count++;
        }

        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    public static void main(String[] args) throws Exception {

        RealTimeAnalyticsDashboard analytics = new RealTimeAnalyticsDashboard();

        analytics.processEvent(new PageEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_123", "google"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_999", "direct"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_777", "google"));

        analytics.getDashboard();
    }
}