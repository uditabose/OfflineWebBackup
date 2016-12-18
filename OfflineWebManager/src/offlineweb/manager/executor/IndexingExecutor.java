
package offlineweb.manager.executor;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import offlineweb.manager.indexer.BaseIndexer;
import offlineweb.manager.util.LoggerUtil;

/**
 * Executes the indexers in a sequence
 * @author papa2
 */
public class IndexingExecutor {
    
    private static final String TASK_TYPE_KEY = "index.task.type.%d";
    private static final String TASK_INPUT_KEY = "index.task.in.%d";
    private static final String TASK_OUTPUT_KEY = "index.task.out.%d";
    private static final String TASK_LOG_KEY = "index.task.log.%d";

    private static LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(IndexingExecutor.class, null);
    
    public static void main(String[] args) {
        /*if (args == null || args.length == 0) {
            logger.log("Minimum one agrgument, a property file path is required");
            return;
        }*/
        
        String executorConfigPath = "indexer-config.properties";
        InputStream indexConfigStream = IndexingExecutor.class
                .getClassLoader().getResourceAsStream(executorConfigPath);
        
        Properties executorConfig = new Properties();
        try {
            executorConfig.load(indexConfigStream);
        } catch (IOException ex) {
            logger.log("can not load the executor configs", 
                    executorConfigPath, ex.getMessage());
            return;
        }
 
        executeTasks(executorConfig);
        
    }

    private static void executeTasks(Properties executorConfig) {
        int taskIdx = 1;
        while(executorConfig.containsKey(String.format(TASK_TYPE_KEY, taskIdx))) {
            String taskType = 
               executorConfig.getProperty(String.format(TASK_TYPE_KEY, taskIdx));
            String taskInput = 
               executorConfig.getProperty(String.format(TASK_INPUT_KEY, taskIdx));
            String taskOutput = 
               executorConfig.getProperty(String.format(TASK_OUTPUT_KEY, taskIdx));
            String taskLog = 
               executorConfig.getProperty(String.format(TASK_LOG_KEY, taskIdx));
            
            try {
                List<String> infileList = Arrays.asList(taskInput.split(";"));
                Class executorClass = Class.forName(taskType);
                BaseIndexer indexer = (BaseIndexer) executorClass.newInstance();
                
                indexer.setIndexDirPath(taskOutput);
                indexer.setIndexedFileList(infileList);
                indexer.initiateLogger(taskLog);
                
                indexer.startIndexing();

                Thread.sleep(1000); // wait 5 minutes before starting the next index
                
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                logger.log("can not load the executor class", taskType, ex.getMessage());
            } catch (InterruptedException ex) {
                logger.log("thread interrupted", taskType, ex.getMessage());
            }

            taskIdx++;
        }
    }
}
