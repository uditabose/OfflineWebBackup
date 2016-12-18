<?php

require_once 'Configuration.php';
require_once 'RunStatus.php';
require_once 'BaseIndexer.php';
require_once 'Constants.php';
require_once 'Zend/Loader/Autoloader.php';
require_once 'Zend/Search/Lucene.php';

/**
 * Description of WikipediaAbstractIndexer
 *
 * @author papa2
 */
class WikipediaAbstractIndexer extends BaseIndexer {
    
    public function resumeIndexing() {
        $wikiAbstractIndex = 
                Zend_Search_Lucene::open($this->runConfiguration->getIndexDir()); 
        $this->processIndexing($wikiAbstractIndex);
    }

    public function startIndexing() {
        $this->runStatus->setLastIndexCount(0);
        $wikiAbstractIndex = 
                Zend_Search_Lucene::create($this->runConfiguration->getIndexDir()); 
        $this->processIndexing($wikiAbstractIndex);
    }
    
    protected function processIndexing($wikiAbstractIndex) {
        if(!$this->validateContentFile()) {
            return;
        }

        $abstractCount = 0;
        $wikiAbstractReader = new XMLReader();
        $wikiAbstractReader->open($this->runConfiguration->getContentFilePath());
        $abstractDoc = NULL;
        
        while($wikiAbstractReader->read()) {
            if ($wikiAbstractReader->nodeType == XMLReader::ELEMENT ) {

                if (!$this->checkForAvailableMemory()) {
                    $this->pauseIndexing($wikiAbstractIndex, $abstractCount);
                    echo "Indexing is paused as the process memory is close to the max memory allocated\n";
                    return;
                }

                $tagName = $wikiAbstractReader->localName;

                if ($tagName === 'doc') {
                    $abstractCount += 1;
                }

                if ($abstractCount < $this->runStatus->getLastIndexCount()) {
                    echo "Continuing without parsing";
                    continue;
                }

                if ($tagName === "title" 
                        || $tagName === "abstract"
                        || $tagName === "url") {
                    $wikiAbstractReader->read();
                    $tagValue = $wikiAbstractReader->value;
                    if ($tagName === "title") {
                        $abstractDoc = new Zend_Search_Lucene_Document();
                        $abstractDoc->addField(Zend_Search_Lucene_Field::Text($tagName, 
                                utf8_encode(str_replace("Wikipedia:", "", $tagValue))));
                    } elseif ($tagName === "abstract") {
                        $abstractDoc->addField(Zend_Search_Lucene_Field::Text($tagName, 
                                utf8_encode($tagValue)));
                        $wikiAbstractIndex->addDocument($abstractDoc);
                    } elseif ($tagName === "url") {
                        $abstractDoc->addField(Zend_Search_Lucene_Field::Text($tagName, 
                                utf8_encode($tagValue)));
                        $abstractDoc->addField(Zend_Search_Lucene_Field::Text("source", 
                                utf8_encode("wikipedia")));
                        $wikiAbstractIndex->addDocument($abstractDoc);
                    }
                }
                
                
            }
            
            if ($abstractCount % $this->runConfiguration->getMaxNoOfIndicesPerRun() == 0) {
                $this->runStatus->updateAndWriteStatus(Constants::$STATUS_UPDATE, $abstractCount);
            } 
        }

        $this->finalizeIndexing($wikiTitleIndex, $abstractCount);
    }


}
