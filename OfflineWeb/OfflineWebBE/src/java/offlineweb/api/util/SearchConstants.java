package offlineweb.api.util;

/**
 *
 * @author papa
 */
public interface SearchConstants {
     String TITLE = "title";
     String TITLEKEY = "titleKey";
     String DOCKEY = "docKey";
     String CONTENT = "content";
     String TYPE = "type";
     String SOURCE = "source";
     String ABSTRACT = "abstract";
     String BLANK = " ";
     String PLUS = "+";
     String UNDERSCORE = "_";
     String TITLEKEY_TEMPLATE = "#titleKey#";
     String TITLE_TEMPLATE = "#title#";
     String CONTENT_TEMPLATE = "#content#";

     String  SINGLETERM_QUERY_TEMPLATE = 
            "#titleKey# title:\"#title#\" content:\"#content#\"";
    
     String MULTITERM_QUERY_TEMPLATE = 
            "#titleKey# title:\"#title#\" content:\"#content#\""
            + BLANK + "title:(#title#) content:(#content#)";
    
     String EXCLUDE_QUERY_TEMPLATE = 
            "-title:Category* -title:Template*";
    
     String SOURCE_QUERY_TEMPLATE = "source:";
     String CONTENT_QUERY_TEMPLATE = "type:";
}
