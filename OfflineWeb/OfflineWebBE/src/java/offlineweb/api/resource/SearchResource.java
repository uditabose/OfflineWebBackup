package offlineweb.api.resource;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import offlineweb.api.bean.Result;
import offlineweb.api.bean.Doc;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static offlineweb.api.util.SearchUtil.*;
import static offlineweb.api.util.SearchConstants.*;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;

/**
 * API for search
 *
 * @author papa2
 */
@Path("/search")
public class SearchResource {

    private static final Logger logger
            = LoggerFactory.getLogger(SearchResource.class);

    /**
     * searches for a search terms in title
     *
     * @param titlePart title to search
     * @return a list of titles containing words of the search term, at max 10
     * elements
     */
    @GET
    @Path("/title/{titlePart}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response searchTitle(@PathParam("titlePart") String titlePart) {

        logger.info("Searching for {}", titlePart);

        if (titlePart.length() < 3) {
            logger.info("Title is too short {}", titlePart);
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        Result titleResult = null;
        try {
            titleResult = getSearchResultAndUpdateCache(
                    titleCacheKey(titlePart, false),
                    singleTermQuery(titlePart, false),
                    null, 0, 10);

            if (titleResult == null) {
                logger.info("no result for {}", titlePart);
                return Response.noContent().status(Response.Status.OK).build();
            }
            
            titleResult.setSearchTerm(titlePart);

            return Response.ok(titleResult).build();

        } catch (ParseException ex) {
            logger.error("can't parse query {}", titlePart, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        } catch (IOException ex) {
            logger.error("Title search is failed : {}", titlePart, ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    /**
     * searches the terms in user query
     *
     * @param searchTerm user query
     * @param pageNum page number
     * @param searchSource source, can be wikipedia, gutenberg or youtube
     * @param contentType type, can be book, article or video
     * @param broadSearch is to include pages with title like "category:*" or
     * "template:*"
     * @return
     */
    @GET
    @Path("/fulltext/{searchTerm}/{pageNum}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response searchContent(@PathParam("searchTerm") String searchTerm,
            @PathParam("pageNum") int pageNum,
            @QueryParam("source") String searchSource,
            @QueryParam("type") String contentType,
            @QueryParam("broad") String broadSearch) {

        logger.info("content for {} {} {} {} {} {} {}", searchTerm,
                " from", searchSource, "type", contentType, "pages", pageNum);

        boolean isBroadSearch = (!isEmpty(broadSearch)
                && "yes|true".contains(broadSearch.toLowerCase()));

        try {
            Result result = getSearchResultAndUpdateCache(
                contentcacheKey(searchTerm, isBroadSearch, searchSource, contentType, pageNum),
                queryString(searchTerm, isBroadSearch),
                queryFilter(contentType, searchSource),
                ((pageNum - 1) * 10), 10);

            if (result == null) {
                logger.info("no result for {}", searchTerm);
                return Response.noContent().status(Response.Status.OK).build();
            }

            result.setCurrentPage(pageNum);
            result.setSearchTerm(searchTerm);

            return Response.ok(result).build();

        } catch (IOException ex) {
            logger.error("Content search is failed : {}", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        } catch (ParseException ex) {
            logger.error("Cannot parse query : {}", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity(ex).build();
        }
    }

    /**
     *
     * @param searchTerm
     * @return
     */
    @GET
    @Path("/more/{searchTerm}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response searchLikeThis(@PathParam("searchTerm") String searchTerm) {

        try {
            logger.info("Search more : {}", searchTerm);
            Result result = getLikeThisResult(searchTerm);
            return Response.ok(result).build();
        } catch (IOException ex) {
            logger.error("More like search is failed : {}", ex);
        }
        return Response.noContent().status(Response.Status.OK).build();
    }

    private Result getSearchResultAndUpdateCache(String cacheKey,
            final String queryString, final Filter queryFilter,
            final int offset, final int maxLimit)
            throws IOException, ParseException {

        logger.info("Requesting cache for {}", cacheKey);
        Result noResult = null;

        try {
            Result searchResult = getCache().get(cacheKey, new Callable<Result>() {

                @Override
                public Result call() throws Exception {
                    return getSearchResult(queryString, queryFilter, offset, maxLimit);
                }
            });

            return searchResult;
        } catch (ExecutionException e) {
            logger.error("Cache error", e);
        }

        return noResult;
    }

    private Result getSearchResult(String queryString,
            Filter queryFilter, int offset, int maxLimit) throws IOException, ParseException {

        logger.info("loading cache for {} {} {}", queryString, offset, maxLimit);

        // get the searcher instance
        IndexSearcher indexSearcher = getIndexSearcher();

        // parse query
        QueryParser queryParser = new QueryParser(TITLEKEY, new StandardAnalyzer());
        Query searchQuery = queryParser.parse(queryString);
        logger.info(searchQuery.toString(TITLEKEY));

        // group search
        GroupingSearch groupSearch = new GroupingSearch(TITLEKEY);
        groupSearch.setCachingInMB(10, true);

        // do search
        TopGroups searchResult = null;
        if (queryFilter != null) {
            searchResult = groupSearch.search(indexSearcher, queryFilter, searchQuery, offset, maxLimit);
        } else {
            searchResult = groupSearch.search(indexSearcher, searchQuery, offset, maxLimit);
        }

        Document document = null;
        Result result = new Result();
        if (searchResult == null) {
            return result;
        }

        // this might be wrong :: TODO : test
        result.setTotalResults(searchResult.totalHitCount);

        for (GroupDocs groupDoc : searchResult.groups) {
            if (groupDoc.scoreDocs.length > 0) {
                try {
                    ScoreDoc scoredDoc = groupDoc.scoreDocs[0];
                    document = indexSearcher.doc(scoredDoc.doc);
                    result.addDoc(buildDoc(document, false));
                } catch (Exception ex) {
                    logger.error("error returning document :", ex);
                }
            }
        }
        // ----- DEBUG
        logger.info(result.toString());
        return result;
    }

    private Result getLikeThisResult(String searchTerm) throws IOException {
        // get the searcher instance
        IndexSearcher indexSearcher = getIndexSearcher();
        MoreLikeThisQuery likeThisQuery = new MoreLikeThisQuery(formatQueryString(searchTerm),
                new String[]{CONTENT}, new StandardAnalyzer(), CONTENT);

        TopDocs moreDocs = indexSearcher.search(likeThisQuery, 5);

        if (moreDocs == null || moreDocs.totalHits == 0) {
            return null;
        }

        Result result = new Result();
        Document document = null;
        for (ScoreDoc scoreDoc : moreDocs.scoreDocs) {
            try {
                document = indexSearcher.doc(scoreDoc.doc);
                result.addDoc(buildDoc(document, true));

            } catch (Exception ex) {
                logger.error("error returning document :", ex);
            }
        }

        result.setTotalResults(0);
        result.setCurrentPage(0);
        result.setSearchTerm(searchTerm);
        return result;
    }

    private Doc buildDoc(Document document, boolean excludeAbstract) {
        Doc doc = new Doc();
        doc.setTitle(document.get(TITLE));
        doc.setType(document.get(TYPE));
        doc.setSource(document.get(SOURCE));
        doc.setDocId(document.get(DOCKEY));
        if (excludeAbstract) {
            doc.setDocAbstract("");
        } else {
            doc.setDocAbstract(document.get(ABSTRACT));
        }
        return doc;
    }

}
