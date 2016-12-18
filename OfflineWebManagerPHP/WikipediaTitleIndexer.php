<?php

require_once 'Configuration.php';
require_once 'RunStatus.php';
require_once 'BaseIndexer.php';
require_once 'Constants.php';
require_once 'Zend/Loader/Autoloader.php';
require_once 'Zend/Search/Lucene.php';


class WikipediaTitleIndexer extends BaseIndexer {
    
    public function resumeIndexing() {        
        $wikiTitleIndex = 
                Zend_Search_Lucene::open($this->runConfiguration->getIndexDir()); 
        $this->processIndexing($wikiTitleIndex);
    }

    public function startIndexing() {
        $this->runStatus->setLastIndexCount(0);
        $wikiTitleIndex = 
                Zend_Search_Lucene::create($this->runConfiguration->getIndexDir()); 
        $this->processIndexing($wikiTitleIndex);
    }
    
    protected function processIndexing($wikiTitleIndex) {
        if(!$this->validateContentFile()) {
            return;
        }

        $titleFile = new SplFileObject($this->runConfiguration->getContentFilePath(), 'r');
        $titleFile->seek($this->runStatus->getLastIndexCount());
        
        $titleCount = 0;
        $maxMemory = 0.9 * 2048 * 1024 * 1024;
        while (!$titleFile->eof()) {
            $currentMemory = memory_get_peak_usage(true);
            if ($currentMemory > $maxMemory) {
                $this->pauseIndexing($wikiTitleIndex, $titleCount);
                echo "Indexing is paused as the process memory is close to the max memory allocated";
                return;
            }
            $titleCount += 1;
            $this->addTitleToIndex($wikiTitleIndex, $titleFile->current());
            if ($titleCount % $this->runConfiguration->getMaxNoOfIndicesPerRun() == 0) {
                $this->runStatus->updateAndWriteStatus(Constants::$STATUS_UPDATE, $titleCount);
            } 

            $titleFile->next();
        }
        
        $this->finalizeIndexing($wikiTitleIndex, $titleCount);
    }
    
    
    private function addTitleToIndex($wikiTitleIndex, $title) {
        $title = str_replace("_", " ", trim(strtolower($title)));
        $titleIndexDoc = new Zend_Search_Lucene_Document(); 
        $titleIndexDoc->addField(Zend_Search_Lucene_Field::Text("title",
                utf8_encode($title)));
        $wikiTitleIndex->addDocument($titleIndexDoc);
    }
    
}
 


