package offlineweb.api.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import offlineweb.api.bean.Result;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static offlineweb.api.util.SearchConstants.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.NIOFSDirectory;

/**
 *
 * @author papa
 */
public class SearchUtil {

    private static final Logger logger
            = LoggerFactory.getLogger(SearchUtil.class);

    private static final String INDEX_DIR_KEY = "api.index.dir";
    private static final List<String> INDEX_PATH_LIST = new ArrayList<>();
    private static IndexSearcher INDEX_SEARCHER = null;
    private static final Cache<String, Result> INDEX_CACHE;
    private static int CACHE_HIT = 0;

    static {
        INDEX_CACHE = CacheBuilder
                .newBuilder()
                .maximumSize(1000)
                .recordStats()
                .build();
        
    }

    private SearchUtil() {
        // prevent instantiation
    }

    public static void warmUp() {
        try {
            getIndexSearcher();
        } catch (Exception ex) {
            // there is nothing to do
        }
    }
    
    public static IndexSearcher getIndexSearcher() throws IOException {

        if (INDEX_SEARCHER == null) {
            synchronized(INDEX_PATH_LIST) {
                INDEX_PATH_LIST.clear();
                INDEX_PATH_LIST.addAll(CommonUtil.configValues(INDEX_DIR_KEY));

                IndexReader[] subReaders = new IndexReader[INDEX_PATH_LIST.size()];
                
                int idxCounter = 0;
                for (String idxDirPath : INDEX_PATH_LIST) {
                    subReaders[idxCounter++] = 
                            DirectoryReader.open(
                                    NIOFSDirectory.open(new File(idxDirPath))
                            );
                }
                
                MultiReader indicesReader = new MultiReader(subReaders);
                INDEX_SEARCHER = new IndexSearcher(indicesReader);
            }
        }
        return INDEX_SEARCHER;
    }
    
    public static Cache<String, Result> getCache() {
        synchronized(INDEX_CACHE) {
            CACHE_HIT++;
            
            if (CACHE_HIT % 50 == 0) { // increase the number after debug
                CacheStats indexCacheStats = INDEX_CACHE.stats();
                logger.info("Cache hitCount {}", indexCacheStats.hitCount());
                logger.info("Cache hitRate {}", indexCacheStats.hitRate());
                logger.info("Cache loadCount {}", indexCacheStats.loadCount());
                logger.info("Cache missCount {}", indexCacheStats.missCount());
                logger.info("Cache missRate {}", indexCacheStats.missRate());
                logger.info("Cache evictionCount {}", indexCacheStats.evictionCount());
                logger.info("Cache requestCount {}", indexCacheStats.requestCount());
            }
        }
        return INDEX_CACHE;
    }
    
    public static boolean isEmpty(String testString) {
        return (testString == null || testString.isEmpty());
    }

    public static String decodeString(String aString) {
        try {
            aString = URLDecoder.decode(aString, "UTF-8");
        } catch (Exception e) {
            // do nothing
        }

        return aString;
    }

    public static String formatQueryString(String queryString) {
        return decodeString(queryString).replaceAll("\\+", BLANK);
    }
    
    public static String singleTermQuery(String userQuery, boolean isBroadSearch) {

        String formattedQuery = formatQueryString(userQuery);

        StringBuilder queryBuilder = new StringBuilder(SINGLETERM_QUERY_TEMPLATE);
        if (!isBroadSearch) {
            queryBuilder.append(BLANK)
                    .append(EXCLUDE_QUERY_TEMPLATE);
        }
        
        String queryString = queryBuilder.toString();
        queryString = queryString.replaceAll(TITLEKEY_TEMPLATE, 
                formattedQuery.replaceAll(BLANK, "").toLowerCase());
        
        queryString = queryString.replaceAll(TITLE_TEMPLATE, formattedQuery);
        queryString = queryString.replaceAll(CONTENT_TEMPLATE, formattedQuery);
        return queryString;
    }
    
    public static String queryString(String userQuery, boolean isBroadSearch) {
        String formattedQuery = formatQueryString(userQuery);

        StringBuilder queryBuilder = new StringBuilder();
        
        if (userQuery.contains(BLANK)) {
            queryBuilder.append(MULTITERM_QUERY_TEMPLATE);
        } else {
            queryBuilder.append(SINGLETERM_QUERY_TEMPLATE);
        }
        
        if (!isBroadSearch) {
            queryBuilder.append(BLANK)
                    .append(EXCLUDE_QUERY_TEMPLATE);
        }
        
        String queryString = queryBuilder.toString();
        queryString = queryString.replaceAll(TITLEKEY_TEMPLATE, 
                formattedQuery.replaceAll(BLANK, "").toLowerCase());
        
        queryString = queryString.replaceAll(TITLE_TEMPLATE, formattedQuery);
        queryString = queryString.replaceAll(CONTENT_TEMPLATE, formattedQuery);

        return queryString;
    }
    
    public static String queryString(String userQuery, boolean isBroadSearch, 
            String sourceType, String contentType) {
        
        if (isEmpty(contentType) && isEmpty(sourceType)) {
            return queryString(userQuery, isBroadSearch);
        }
        
        StringBuilder queryBuilder = 
                new StringBuilder(queryString(userQuery, isBroadSearch));
        
        if (!isEmpty(contentType)) {
            queryBuilder.append(BLANK)
                    .append(CONTENT_QUERY_TEMPLATE)
                    .append(contentType.toLowerCase());
        }
        
        if (!isEmpty(sourceType)) {
            queryBuilder.append(BLANK)
                    .append(SOURCE_QUERY_TEMPLATE)
                    .append(sourceType.toLowerCase());
        }
        return queryBuilder.toString();
    }
    
    public static Filter queryFilter(String contentType, String contentSource) {
        Filter theFilter = null;
        
        Query typeQuery = null;
        Query sourceQuery = null;
        
        if (!isEmpty(contentType) && !"all".equalsIgnoreCase(contentType)) {
            typeQuery = new TermQuery(new Term(TYPE, contentType));
        }
        
        if (!isEmpty(contentSource)) {
            sourceQuery = new TermQuery(new Term(SOURCE, contentSource));
        }
        
        Query finalQuery = null;
        
        if (typeQuery != null && sourceQuery != null) {
            finalQuery = new BooleanQuery();
            ((BooleanQuery)finalQuery).add(typeQuery, BooleanClause.Occur.MUST);
            ((BooleanQuery)finalQuery).add(sourceQuery, BooleanClause.Occur.MUST);
        } else if (typeQuery != null && sourceQuery == null) {
            finalQuery = typeQuery;
        } else if (typeQuery == null && sourceQuery != null) {
            finalQuery = sourceQuery;
        }
        
        if (finalQuery != null) {
            theFilter = new QueryWrapperFilter(finalQuery);
        }
        
        return theFilter;
    }
    
    public static String titleCacheKey(String searchTerm, 
            boolean isBroadSearch) {
        
        String formattedQuery = formatQueryString(searchTerm);

        StringBuilder titleCacheKey = new StringBuilder(TITLE);
        titleCacheKey.append(UNDERSCORE)
                .append(formattedQuery.replaceAll(BLANK, UNDERSCORE).toLowerCase())
                .append(UNDERSCORE).append(isBroadSearch);
        
        logger.info("titleCacheKey {}", titleCacheKey);
        return titleCacheKey.toString();
    }
    
    public static String contentcacheKey(String searchTerm, 
            boolean isBroadSearch, String source, String type, int pageNum) {
        String formattedQuery = formatQueryString(searchTerm);
        
        StringBuilder contentCacheKey = new StringBuilder(CONTENT);
        contentCacheKey.append(UNDERSCORE)
                .append(formattedQuery.replaceAll(BLANK, UNDERSCORE).toLowerCase())
                .append(UNDERSCORE).append(pageNum)
                .append(UNDERSCORE).append(isBroadSearch)
                .append(UNDERSCORE).append(isEmpty(source) ? "n" : source)
                .append(UNDERSCORE).append(isEmpty(type) ? "n" : type);
        logger.info("contentCacheKey {}", contentCacheKey);
        return contentCacheKey.toString();
    }
}
