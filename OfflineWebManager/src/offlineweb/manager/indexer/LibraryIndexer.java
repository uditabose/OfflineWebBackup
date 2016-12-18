
package offlineweb.manager.indexer;


import offlineweb.manager.indexer.wikipedia.WikipediaXMLAbstarctIndexer;
import offlineweb.manager.indexer.wikipedia.WikipediaTitleIndexer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author papa2
 */
public class LibraryIndexer {
    
    private static final Logger logger = LoggerFactory.getLogger(LibraryIndexer.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // 3 input parameters are required
        Options options = new Options();
        OptionGroup typeGroup = new OptionGroup();
        typeGroup.addOption(new Option("t", false, "To index title"));
        typeGroup.addOption(new Option("a", false, "To index abstract"));
        typeGroup.addOption(new Option("n", false, "To index content"));
        
        options.addOptionGroup(typeGroup);
        options.addOption("c", true, "Which document type, wiki, you, gut");
        options.addOption("d", true, "The directory to create index");
        options.addOption("l", true, "Status log file");
        options.addOption("f", true, "Indexable file");
        
        CommandLineParser commandLineParser = new GnuParser();
        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            String contentType = commandLine.getOptionValue("c");
            String indexDirPath = commandLine.getOptionValue("d");
            String statusLogFilePath = commandLine.getOptionValue("l");
            logger.info(contentType);
            if (commandLine.hasOption("t") && "wiki".equals(contentType)) {
                List<String> indexedFileList = new ArrayList<>();
                indexedFileList.add(commandLine.getOptionValue("f"));
                BaseIndexer wikiIndexer = new WikipediaTitleIndexer(indexDirPath, 
                        statusLogFilePath, indexedFileList);
                wikiIndexer.startIndexing();
            } else if (commandLine.hasOption("a") && "wiki".equals(contentType)) {
                List<String> indexedFileList = new ArrayList<>();
                indexedFileList.add(commandLine.getOptionValue("f"));
                BaseIndexer wikiIndexer = new WikipediaXMLAbstarctIndexer(indexDirPath, 
                        statusLogFilePath, indexedFileList);
                wikiIndexer.startIndexing();
            }
        } catch (ParseException ex) {
            logger.error("Can't parse", ex);
        }
      
    }

}
