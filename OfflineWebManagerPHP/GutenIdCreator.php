<?php 
/*require_once 'Configuration.php';
require_once 'RunStatus.php';
require_once 'BaseIndexer.php';
require_once 'Constants.php';
require_once 'Zend/Loader/Autoloader.php';
require_once 'Zend/Search/Lucene.php';
*/
/**
 * Description of GutenbergCompleteIndexer
 *
 * @author papa2
 */

function createGutenId() {
	if (!is_file("/usr/local/great/workspace/gutendump/listed")) {
    	 	echo "Please specify a directory listing\n";
         	return;
    	 }
    	 $dirsList = file("/usr/local/great/workspace/gutendump/listed");
    	 $gutenXmlReader = NULL;
    	 $gutenDocCount = 0;

    	 $nameToIdFile = fopen("/usr/local/great/workspace/gutendump/nametoid", "w");

    	 foreach ($dirsList as $dirName) {
    	 	$dirName = trim($dirName);
    	 	$gutenContentFilePath = 
    	 		"/usr/local/great/workspace/gutendump/cache/epub/$dirName/pg$dirName.rdf";
    	 	if (!is_file($gutenContentFilePath)) {
    	 		echo "No file found $gutenContentFilePath \n";
    	 		continue;
    	 	} 

    	 	$gutenDocCount += 1;

    	 	try {
    	 		$gutenXmlReader = new XMLReader();
    	 		$gutenXmlReader->open($gutenContentFilePath);

    	 		while ($gutenXmlReader->read()) {
    	 			if ($gutenXmlReader->nodeType != XMLReader::ELEMENT ) {
    	 				continue;
    	 			}

	                $tagName = $gutenXmlReader->localName;

	                if ($tagName === "title") {
	                	$gutenXmlReader->read();
	                	$gutenDocName = $gutenXmlReader->value;
	                	fwrite($nameToIdFile, "$gutenDocName=$dirName\n");
	                	echo "$dirName - $gutenDocName\n";
	                } 
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

    	 fclose($nameToIdFile);
}

createGutenId();

?>