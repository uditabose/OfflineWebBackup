<?php

/**
 * Description of BaseIndexer
 *
 * @author papa2
 */

require_once 'Configuration.php';
require_once 'RunStatus.php';
require_once 'Constants.php';

abstract class BaseIndexer {
    
    protected $runConfiguration;
    protected $runStatus;
    
    function __construct($runConfiguration) {
        $this->runConfiguration = $runConfiguration;
        $this->runStatus = new RunStatus($this->runConfiguration);
    }
    
    protected function validateContentFile() {
        if(!is_file($this->runConfiguration->getContentFilePath())) {
            echo "The content source is not valid!";
            return FALSE;
        }
        return TRUE;
    }
    
    protected function pauseIndexing($wikiTitleIndex, $titleCount) {
        try {
            $wikiTitleIndex->close();
            $this->runStatus->updateAndWriteStatus(Constants::$STATUS_PAUSE, $titleCount);
        } catch (Exception $exc) {
            $this->runStatus->updateAndWriteStatus(Constants::$STATUS_FAILURE, $titleCount);
            echo $exc->getTraceAsString();
        }
    }
    
    protected function finalizeIndexing($wikiTitleIndex, $titleCount) {
        try {
            if (!$this->checkForAvailableMemory()) {
                $this->pauseIndexing($wikiTitleIndex, $titleCount);
                echo "Indexing is paused as the process memory is close to the max memory allocated\n";
                return;
            }
            $wikiTitleIndex->optimize();
            $wikiTitleIndex->close();
            $this->runStatus->updateAndWriteStatus(Constants::$STATUS_SUCCESS, $titleCount);
        } catch (Exception $exc) {
            $this->runStatus->updateAndWriteStatus(Constants::$STATUS_FAILURE, $titleCount);
            echo $exc->getTraceAsString();
        }
    }

    protected function checkForAvailableMemory() {
        $currentMemory = memory_get_peak_usage(true);
        if ($currentMemory >= Constants::$MAX_MEMORY) {
            return FALSE;
        }

        return TRUE;
    }

    public abstract function startIndexing();
    public abstract function resumeIndexing(); 
    protected abstract function processIndexing($wikiTitleIndex);
}
