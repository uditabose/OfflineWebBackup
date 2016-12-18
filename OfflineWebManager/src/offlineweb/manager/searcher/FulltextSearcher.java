
package offlineweb.manager.searcher;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.store.NIOFSDirectory;

/**
 *
 * @author papa
 */
public class FulltextSearcher {
    public static void main(String[] args) throws IOException, ParseException {
        String idxDirPath = "/media/papa/offline/OfflineWebData/index/wiki-index";
        IndexReader fullTextReader = DirectoryReader.open(NIOFSDirectory.open(new File(idxDirPath)));
        
        IndexSearcher fullTextSearcher = new IndexSearcher(fullTextReader);
        
        System.out.println(fullTextSearcher.getIndexReader().numDocs());
        
        /*String queryString = "newyorkcity title:\"New York City\" "
                + "content:\"New York City\"  -title:Category* title:(New York City) content:(New York City)";/**/
        
        /*String queryString = "milliontourists title:\"million tourists\" content:\"million tourists\""
                + " title:(million tourists)^0.5 content:(million tourists)^0.5";*/

        String queryString = "wickquasgeck title:\"Wickquasgeck\" content:\"Wickquasgeck\"";
        
        //String queryString = "java title:\"java\" content:\"java\"";
        String defaultField = "titleKey";
        
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser(defaultField, analyzer);
        Query searchQuery = queryParser.parse(queryString);
        System.out.println("serchQuery :: " + searchQuery.toString("titleKey"));

        GroupingSearch groupSearch = new GroupingSearch(defaultField);
        groupSearch.setCachingInMB(10, true);

        int loopOn = 0;
        while (loopOn++ < 10) {
             long startTime = System.currentTimeMillis();
             doSearch(groupSearch, fullTextSearcher, searchQuery, loopOn);
             long endTime = System.currentTimeMillis();
             System.out.println("*****************************");
             System.out.println("Thime taken :: " + (endTime - startTime) / 1000);
             System.out.println("*****************************");
        }

    }

    private static void doSearch(GroupingSearch groupSearch, IndexSearcher fullTextSearcher,  
            Query searchQuery, int loopOn) throws IOException {
        TopGroups searchResult = groupSearch.search(fullTextSearcher,
                searchQuery, loopOn * 10, (loopOn + 1) * 10);

        Document document = null;

        if (searchResult == null) {
            System.out.println("No search result");
            return;
        }

        for (GroupDocs groupDoc : searchResult.groups) {
            
            for (ScoreDoc scoredDoc : groupDoc.scoreDocs) {
                try {
                    document = fullTextSearcher.doc(scoredDoc.doc);
                    System.out.println(document.get("title"));
                    System.out.println(document.get("titleKey"));
                    System.out.println(document.get("docKey"));
                    System.out.println(document.get("abstract"));
                    System.out.println("---------------------------------");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("=====================================");
        }
    }

}
