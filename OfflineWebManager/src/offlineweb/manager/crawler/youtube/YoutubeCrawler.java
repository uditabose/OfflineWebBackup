
package offlineweb.manager.crawler.youtube;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author papa2
 */
public class YoutubeCrawler {
    
    private static final String YUOTUBE_API = "";
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        List<String> topicList = new ArrayList<String>();
        topicList.add("java tutorial");
        topicList.add("c++ tutorial");
        topicList.add("python tutorial");
        topicList.add("MIT algorithm");
        topicList.add("Stanford algorithm");
        topicList.add("ruby on rails tutorial");
        topicList.add("new york city");
        topicList.add("ios tutorial");
        topicList.add("android tutorial");
        topicList.add("javascript tutorial");
        topicList.add("nodejs tutorial");
        topicList.add("perl tutorial");
        topicList.add("php tutorial");
        topicList.add("Caltech algorithm");
        topicList.add("Princeton algorithm");
        topicList.add("swift tutorial");
        topicList.add("london");
        topicList.add("boston");
        topicList.add("chicago");
        topicList.add("san francisco");
        
        for (String topic : topicList) {
            searchAndSaveVideoMetadataForTopic(topic);
        }
    }

    private static void searchAndSaveVideoMetadataForTopic(String topic) {
        System.out.println("SEARCHING : " + topic);
        System.out.println("=======================================");
        YoutubeSearch.search(topic);
        System.out.println("=======================================");
    }
    
    

}
