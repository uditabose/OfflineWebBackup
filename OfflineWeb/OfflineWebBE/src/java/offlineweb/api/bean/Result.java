
package offlineweb.api.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author papa2
 */

@XmlRootElement
public class Result {
    private List<Doc> searchResult;
    private int currentPage;
    private long totalResults = -1;
    private String searchTerm;

    public List<Doc> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(List<Doc> searchResult) {
        this.searchResult = searchResult;
    }

    /**
     *
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     *
     * @return
     */
    public long getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     */
    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    /**
     *
     * @return
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     *
     * @param searchTerm
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    /**
     *
     * @param doc
     */
    public void addDoc(Doc doc) {
        if (searchResult == null) {
            searchResult = new ArrayList<>();
        }
        searchResult.add(doc);
    }

    @Override
    public String toString() {
        return "Result{" + "currentPage=" + currentPage + ", totalResults=" + totalResults + ", searchTerm=" + searchTerm + '}';
    }
    
    
}
