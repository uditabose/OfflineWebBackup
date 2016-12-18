package offlineweb.manager.crawler.youtube;

/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Print a list of videos matching a search term.
 *
 * @author Jeremy Walker
 */
public class YoutubeSearch {

    /**
     * Define a global variable that identifies the name of a file that contains
     * the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    private static final String DUMP_DIR = "/usr/local/great/workspace/DataPort/OfflineWebData/youtubedump";

    private static final String ID_FILE = DUMP_DIR + File.separator + "youtube-id";
    private static final String DESC_FILE = DUMP_DIR + File.separator + "youtube-desc-";

    /**
     * Define a global instance of a Youtube object, which will be used to make
     * YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then display
     * the name and thumbnail image of each video in the result set.
     *
     * @param searchTopic
     */
    public static void search(String searchTopic) {
        // Read the developer key from the properties file.
        Properties properties = new Properties();
        try {
            InputStream in = YoutubeSearch.class.getResourceAsStream(PROPERTIES_FILENAME);
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("YouScrap").build();

            // Prompt the user to enter a query term.
            String queryTerm = searchTopic;

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                saveSearchResult(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void saveSearchResult(Iterator<SearchResult> iteratorSearchResults, String query) {
        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
            return;
        }

        BufferedWriter videoIdWriter = null;
        BufferedWriter videoDescWriter = null;
        try {
            videoIdWriter = new BufferedWriter(new FileWriter(ID_FILE, true));
            while (iteratorSearchResults.hasNext()) {

                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();
                

                // Confirm that the result represents a video. Otherwise, the
                // item will not contain a video ID.
                if (rId.getKind().equals("youtube#video")) {
                    Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                    
                    videoIdWriter.append(rId.getVideoId())
                            .append("\n");
                    videoDescWriter = new BufferedWriter(new FileWriter(DESC_FILE + rId.getVideoId(), true));
                    videoDescWriter.append("title=");
                    videoDescWriter.append(singleVideo.getSnippet().getTitle());
                    videoDescWriter.append("\n");
                    videoDescWriter.append("description=");
                    videoDescWriter.append(singleVideo.getSnippet().getDescription());
                    videoDescWriter.append("\n");
                    videoDescWriter.append("thumb=");
                    videoDescWriter.append(thumbnail.getUrl());
                    videoDescWriter.append("\n");
                    
                    try {
                        videoDescWriter.flush();
                        videoDescWriter.close();
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                   
                    saveThumbImage(thumbnail.getUrl(), rId.getVideoId());

                    System.out.println(" Video Id" + rId.getVideoId());
                    System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                    System.out.println(" Description: " + singleVideo.getSnippet().getDescription());
                    System.out.println(" Thumbnail: " + thumbnail.getUrl());
                    System.out.println("\n-------------------------------------------------------------\n");
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                videoIdWriter.flush();
                videoIdWriter.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
            
            
        }

    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Description: " + singleVideo.getSnippet().getDescription());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

    private static void saveThumbImage(String thumbImgURL, String videoId) {
        try {
            String[] splitted = thumbImgURL.split("\\.");
            String imageType = splitted[splitted.length - 1];
            BufferedImage thumbImage = ImageIO.read(new URL(thumbImgURL));
            ImageIO.write(thumbImage, imageType, 
                    new File(DUMP_DIR + File.separator + videoId + "." + imageType));
        } catch (MalformedURLException ex) {
            System.err.println("Thumb can't be stored for : " + videoId);
        } catch (IOException ex) {
            System.err.println("Thumb can't be stored for : " + videoId);
        }
    }
}
