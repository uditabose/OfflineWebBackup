
package offlineweb.manager.searcher;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author papa2
 */
public class LibrarySearcher {
    
    private static final String DEFAULT_INDEX_DIR = 
            "/media/papa2/great/workspace/wikidx/wiki_abstract_indexer";
    
    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String indexDirPath = DEFAULT_INDEX_DIR;
        if (args != null && args.length > 0) {
            indexDirPath = args[0];
        }
        IndexSearcher wikiSearcher = 
                new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexDirPath))));
        PhraseQuery titleQuery = new PhraseQuery();
        titleQuery.add(new Term("title", "food"));
        PhraseQuery abstractQuery = new PhraseQuery();
        abstractQuery.add(new Term("abstract", "food"));
        
        BooleanQuery combinedQuery = new BooleanQuery();
        combinedQuery.add(new BooleanClause(titleQuery, BooleanClause.Occur.SHOULD));
        combinedQuery.add(new BooleanClause(abstractQuery, BooleanClause.Occur.SHOULD));
        
        TopDocs topDocs = wikiSearcher.search(combinedQuery, 100);

        for (ScoreDoc hitDoc : topDocs.scoreDocs) {
            /*
            System.out.println("Score : " + hitDoc.getScore());
            System.out.println("Title : " + hitDoc.getDocument().get("title"));
            System.out.println("URL : " + hitDoc.getDocument().get("url"));
            System.out.println("Source : " + hitDoc.getDocument().get("source"));
            System.out.println("Abstract : " + hitDoc.getDocument().get("abstract"));
            System.out.println("------------------------------");
            */
        }
       
    }

}
