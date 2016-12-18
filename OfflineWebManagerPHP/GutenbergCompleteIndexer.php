<?php

require_once 'Configuration.php';
require_once 'RunStatus.php';
require_once 'BaseIndexer.php';
require_once 'Constants.php';
require_once 'Zend/Loader/Autoloader.php';
require_once 'Zend/Search/Lucene.php';

/**
 * Description of GutenbergCompleteIndexer
 *
 * @author papa2
 */

class GutenbergCompleteIndexer extends BaseIndexer {

	public  function startIndexing() {
		$this->runStatus->setLastIndexCount(0);
		$gutenCompleteIndex = 
                Zend_Search_Lucene::open($this->runConfiguration->getIndexDir()); 
        $this->processIndexing($gutenCompleteIndex);

	}
    public  function resumeIndexing() {
        $gutenCompleteIndex = 
                Zend_Search_Lucene::open($this->runConfiguration->getIndexDir()); 
        $this->processIndexing($gutenCompleteIndex);
    } 

    protected function processIndexing($gutenCompleteIndex) {
    	 if (!is_file($this->runConfiguration->getMetadataFilePath())) {
    	 	echo "Please specify a directory listing\n";
         	return;
    	 }
    	 $dirsList = file($this->runConfiguration->getMetadataFilePath());
    	 $gutenXmlReader = NULL;
    	 $gutenDocCount = 0;

    	 foreach ($dirsList as $dirName) {
    	 	$dirName = trim($dirName);
    	 	$gutenContentFilePath = $this->runConfiguration->getContentFilePath() . "/"
    	 			. $dirName . "/pg$dirName.rdf";
    	 	if (!is_file($gutenContentFilePath)) {
    	 		echo "No file found $gutenContentFilePath \n";
    	 		continue;
    	 	} 

    	 	$gutenDocCount += 1;

    	 	try {
    	 		$gutenXmlReader = new XMLReader();
    	 		$gutenXmlReader->open($gutenContentFilePath);
    	 		$gutenIndexDoc = new Zend_Search_Lucene_Document();

    	 		while ($gutenXmlReader->read()) {
    	 			if ($gutenXmlReader->nodeType != XMLReader::ELEMENT ) {
    	 				continue;
    	 			}

    	 			if (!$this->checkForAvailableMemory()) {
	                    $this->pauseIndexing($gutenCompleteIndex, $dirName);
	                    echo "Indexing is paused as the process memory is close to the max memory allocated\n";
	                    return;
	                }

	                //$gutenDocCount += 1;
	                $tagName = $gutenXmlReader->localName;

	                if ($tagName === "title") {
	                	$gutenXmlReader->read();
	                	$gutenDocName = $gutenXmlReader->value;
	                	/*$gutenIndexDoc->addField(Zend_Search_Lucene_Field::Text($tagName, 
                                utf8_encode($gutenDocName)));*/
	                	echo "$dirName - $gutenDocName\n";
	                } else if ($tagName === "creator" || $tagName === "trl") {
	                	while($gutenXmlReader->read()) {
	                		$subTagName = $gutenXmlReader->localName;
	                		if ($subTagName === "name") {
	                			$gutenXmlReader->read();
	                			$gutenDocAuthor = $gutenXmlReader->value;
	                			/*$gutenIndexDoc->addField(Zend_Search_Lucene_Field::Text($tagName, 
                                		utf8_encode($gutenDocAuthor)));*/
	                			//echo "$tagName - $gutenDocAuthor \n";
	                			break;
	                		}
	                	}
	                }
    	 		}
    	 		//$gutenIndexDoc->addField(Zend_Search_Lucene_Field::Text("source", "gutenberg"));
    	 		//$gutenCompleteIndex->addDocument($gutenIndexDoc);

    	 		if ($gutenDocCount % $this->runConfiguration->getMaxNoOfIndicesPerRun() == 0) {
	                $this->runStatus->updateAndWriteStatus(Constants::$STATUS_UPDATE, $gutenDocCount);
	            } 

    	 	} catch (Exception $exc) {
    	 		echo $exc->getTraceAsString();
    	 	} finally {
    	 		if ($gutenXmlReader != NULL) {
    	 			$gutenXmlReader->close();
    	 		}
    	 	}

    	 	echo "---------------------------- \n";
    	 }

    	 $this->finalizeIndexing($gutenIndexDoc, $gutenDocCount);
    }
}