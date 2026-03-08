import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCacheSystem {

    private static final int L1_CAPACITY = 10000;
    private static final int L2_CAPACITY = 100000;

    private LinkedHashMap<String, VideoData> L1 = new LinkedHashMap<>(16, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
            return size() > L1_CAPACITY;
        }
    };

    private LinkedHashMap<String, VideoData> L2 = new LinkedHashMap<>(16, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
            return size() > L2_CAPACITY;
        }
    };

    private Map<String, VideoData> L3 = new HashMap<>();

    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    public MultiLevelCacheSystem() {

        for (int i = 1; i <= 5; i++) {
            String id = "video_" + i;
            L3.put(id, new VideoData(id, "Content for " + id));
        }
    }

    public VideoData getVideo(String videoId) {

        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(videoId);
        }

        if (L2.containsKey(videoId)) {
            L2Hits++;
            System.out.println("L2 Cache HIT → Promoted to L1");
            VideoData v = L2.get(videoId);
            L1.put(videoId, v);
            return v;
        }

        if (L3.containsKey(videoId)) {
            L3Hits++;
            System.out.println("L3 Database HIT → Added to L2");
            VideoData v = L3.get(videoId);
            L2.put(videoId, v);
            return v;
        }

        System.out.println("Video not found");
        return null;
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double L1Rate = total == 0 ? 0 : (L1Hits * 100.0 / total);
        double L2Rate = total == 0 ? 0 : (L2Hits * 100.0 / total);
        double L3Rate = total == 0 ? 0 : (L3Hits * 100.0 / total);

        System.out.println("L1: Hit Rate " + L1Rate + "% Avg Time: 0.5ms");
        System.out.println("L2: Hit Rate " + L2Rate + "% Avg Time: 5ms");
        System.out.println("L3: Hit Rate " + L3Rate + "% Avg Time: 150ms");

        double overall = ((L1Hits + L2Hits) * 100.0) / total;
        System.out.println("Overall Hit Rate: " + overall + "%");
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.getVideo("video_1");
        cache.getVideo("video_1");
        cache.getVideo("video_2");
        cache.getVideo("video_3");
        cache.getVideo("video_1");

        cache.getStatistics();
    }
}