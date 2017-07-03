<?php

require_once 'Utility.php';
require_once 'Result.php';
require_once 'Doc.php';
require_once 'BaseController.php';

include('httpful.phar');

class SearchController extends BaseController {

    private static $logger = NULL;

    public function init() {
        if (self::$logger === NULL) {
            self::$logger = Utility::getLogger();
        }
    }

    public function indexAction() {
        self::$logger->info("SearchController : indexAction :: Starting the Search");

        $searchURL = Utility::getConfigProperty("searchURL");
        if ($searchURL === "") {
            self::$logger->info("Can not find the URL for searching index");
            $this->setDefaultViewValues();
            return;
        }
        
        $query = $_GET['q'];
        $pageNum = (array_key_exists("pn", $_GET)) ? intval($_GET["pn"]) : "1";
        $type = (array_key_exists("t", $_GET)) ? $_GET["t"] : "";
        $wide = (array_key_exists("w", $_GET)) ? $_GET["w"] : "";
        $source = "";

        self::$logger->info("SearchController : indexAction :: " 
                . sprintf($searchURL, urlencode($query), $pageNum, $source, $type, $wide));

        $this->view->title = $query;
        $this->view->searchType = ucfirst($type);
        $this->view->searchResult = 
            $this->searchAndResetResult(sprintf($searchURL, urlencode($query), 
                $pageNum, $source, $type, $wide));        
    }

    public function titleAction() {
        $this->setAjaxViewRenderProperties('application/json');

        $userQuery = trim($_GET['qt']);
        $searchURL = Utility::getConfigProperty("titleURL");
   
        try {
            $searchResult = $this->searchAndResetResult(sprintf($searchURL, urlencode($userQuery)));

            if (is_null($searchResult->getSerachResult()) || count($searchResult->getSerachResult()) === 0) {
                echo "No search result";
            } else {
                $searchArray = array();
                foreach ($searchResult->getSerachResult() as $result) {
                    $searchArray[] = array("value" => $result->getTitle());
                }
                echo html_entity_decode(json_encode($searchArray));
            }            
        } catch (Exception $exc) {
            self::$logger->info("SearchController : titleAction : " . $exc->getTraceAsString());
        }
    }

    public function wikiAction() {
        $docName = ucfirst(trim($_GET['t']));
        $docKey = trim($_GET['k']);
        self::$logger->info("SearchController : wikiAction :: " . $docName . "##" . $docKey);
        
        $wikiURL = Utility::getConfigProperty("wikiURL");
        $wikiURL = sprintf($wikiURL, $docKey, urlencode($docName));
        self::$logger->info("SearchController : wikiAction :: " . $wikiURL);
        
        $wikiContent = NULL;
        try {
            $response = \Httpful\Request::get($wikiURL)->send();
            if (!empty($response->body)) {
                $wikiContent = $response->body->content;
            }
        } catch (Exception $ex) {
            self::$logger->info("SearchController : wikiAction : " . $ex->getTraceAsString());
        }
        
        $wikiContent = (is_null($wikiContent)) ? "" : $wikiContent;

        $this->view->title = $docName;
        $this->view->wikiContent = $wikiContent;
    }

    public function gutenbergAction() {
        $docName = ucfirst(trim($_GET['t']));
        $docKey = trim($_GET['k']);
        self::$logger->info("SearchController : gutenbergAction : " . $docName . "##" . $docKey);
        
        $gutenURL = Utility::getConfigProperty("gutenURL");
        $gutenURL = sprintf($gutenURL, $docKey, urlencode($docName));
        self::$logger->info("SearchController : gutenbergAction : " . $gutenURL);
        $gutenContent = NULL;
        $epubURL = NULL;
        try {
            $response = \Httpful\Request::get($gutenURL)->send();
            if (!empty($response->body)) {
                $gutenContent = $response->body->content;
                $gutenId = $response->body->contentId;
                $epubURL = Utility::getConfigProperty("gutenEpubURL");
                
                $gutenIdArr = str_split($gutenId);
                for ($i = 0; $i < count($gutenIdArr) - 1; $i++) {
                    $epubURL .= $gutenIdArr[$i] . "/";
                }
                $epubURL .= "$gutenId/pg$gutenId-images.epub";
            }
        } catch (Exception $ex) {
            self::$logger->info("SearchController : gutenbergAction : " . $ex->getTraceAsString());
        }
        
        $gutenContent = (is_null($gutenContent)) ? "" : $gutenContent;
        $epubURL = (is_null($epubURL)) ? "" : $epubURL; 
        $this->view->epubURL = $epubURL;
        $this->view->title = $docName;
        $this->view->gutenContent = $gutenContent;
    }
    
    public function youtubeAction() {
        self::$logger->info("SearchController : youtubeAction");
        $docName = ucfirst(trim($_GET['t']));
        $docKey = trim($_GET['k']);
        self::$logger->info("SearchController : youtubeAction : $docName ## $docKey");
       
        $youtubeURL = Utility::getConfigProperty("youtubeURL");
        $youtubeURL = sprintf($youtubeURL, $docKey, urlencode($docName));
        self::$logger->info("SearchController : youtubeAction : $youtubeURL");
        $youtubeContent = NULL;
        try {
            $response = \Httpful\Request::get($youtubeURL)->send();
            if (!empty($response->body)) {
                $youtubeContent = $response->body->content;
            }
        } catch (Exception $ex) {
            self::$logger->info("SearchController : youtubeAction : " . $ex->getTraceAsString());
        }
 
        $youtubeContent = (is_null($youtubeContent)) ? "" : $youtubeContent;
        $this->view->youtubeContent = $youtubeContent;
        $this->view->title = $docName;
    }

    public function nodocAction() {
        // an action for wrongly indexed document 
    }
    
    public function moreAction() {
        self::$logger->info("SearchController : moreAction");
        $bootstrap = $this->getInvokeArg('bootstrap');
        $userAgent = $bootstrap->getResource('useragent');
        /** call to initialize browser type */
        $userAgent->getDevice();
        $this->setAjaxViewRenderProperties('text/html');
        
        $htmlText =  "";
        $userQuery = trim($_GET['t']);
        if (is_null($userQuery) || empty($userQuery) || $userQuery === "undefined") {
            self::$logger->info("SearchController : moreAction : invalid query :: $userQuery");
            return $htmlText ;
        }

        $moreURL = Utility::getConfigProperty("moreURL");
        if ($moreURL === "") {
            self::$logger->info("SearchController : moreAction : Can not find the URL for searching index :: $userQuery");
            return $htmlText ;
        }

        $searchResult = $this->searchAndResetResult(sprintf($moreURL, urlencode($userQuery)));
        $resultArray = $searchResult->getSerachResult();
        if (is_null($resultArray) || count($resultArray) === 0) {
            echo $htmlText;
            return;
        }
            
        /* if desktop do nothing, so use standard layout file names */
        if ($userAgent->getBrowserType() === 'desktop') {
            foreach ($resultArray as $result) {
                $link = "/search/" . $result->getSource() 
                        . "?t=" . urlencode($result->getTitle()) 
                        . "&k=" . urlencode($result->getDocId());
                $htmlText .= "<a class='list-group-item navbar-link' href='$link'>".$result->getTitle()."</a>";
            }

            //$htmlText .=  "</ul>";                
        } else {
            $htmlText =  "<table class='table'>";
            $rowCount = 0;
            foreach ($resultArray as $result) {
                $link = "/search/" . $result->getSource() 
                        . "?t=" . urlencode($result->getTitle()) 
                        . "&k=" . urlencode($result->getDocId());
                
                if ($rowCount % 2 === 0) {
                    $htmlText .= "<tr>";
                }
                
                $htmlText .= "<td><a href='$link' class='navbar-link'>".$result->getTitle()."</a></td>";
                
                if ($rowCount % 2 === 1) {
                    $htmlText .= "</tr>";
                }
                $rowCount += 1;
            }
            $htmlText .=  "</table>"; 
        }
        echo $htmlText ;
    }
    
    private function searchAndResetResult($searchURL) {
        self::$logger->info("SearchController : searchAndResetResult : $searchURL");
        
        $response = NULL;
        try {
            $response = \Httpful\Request::get($searchURL)->send(); 
        } catch (Exception $ex) {
            self::$logger->info("SearchController : searchForQuery : " . $ex->getTraceAsString());
        }
        
        $searchResult = new Result();
        if (is_null($response) || empty($response->body) || !isset($response->body)) {
            $this->setDefaultViewValues();
        } else {  
            $renponseBody = $response->body;
            $searchResult->setCurrentPage($renponseBody->currentPage);
            $searchResult->setSearchTerm($renponseBody->searchTerm);
            $searchResult->setTotalResults($renponseBody->totalResults);

            $docs = isset($renponseBody->searchResult) ? $renponseBody->searchResult : NULL;

            if (is_null($docs) || !is_array($docs) || count($docs) === 0) {
                self::$logger->info("SearchController : searchAndResetResult : NO RESULT : $searchURL");
            } else {
                foreach ($docs as $docElem) {
                    $aDoc = new Doc($docElem->title, $docElem->type, 
                            $docElem->source, $docElem->docId, $docElem->docAbstract);
                    $searchResult->addDoc($aDoc);
                }
            }
        }
        return $searchResult;
    }

    private function setDefaultViewValues($searchTerm = "") {
        $searchResult = new Result(1, 0, $searchTerm, NULL);
        $this->view->searchResult = $searchResult;
    }
    
    private function setAjaxViewRenderProperties($contentType) {
        $this->getHelper('Layout')->disableLayout();
        $this->getHelper('ViewRenderer')->setNoRender();
        $this->getResponse()->setHeader('Content-Type', $contentType);
    }
   
}
