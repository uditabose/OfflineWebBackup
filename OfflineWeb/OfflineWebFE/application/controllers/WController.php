<?php

require_once 'Utility.php';
require_once 'BaseController.php';
include('httpful.phar');
/**
 * Description of WController
 *
 * @author papa
 */


class WController extends BaseController  {
    
    private static $logger = NULL;
    
    public function init() {
        if (self::$logger === NULL) {
            self::$logger = Utility::getLogger();
        }
    }
    
    public function indexAction() {
        // just redirect to the page saying 
        // title=New_York_City&redirect=no
    }
    public function indexPhpAction() {
        // just redirect to the page saying 
        // title=New_York_City&redirect=no
        $title = $_GET["title"];
        $title = str_replace("_", " ", $title);
        self::$logger->info("WController : indexPhpAction :: Trying to find a page $title");
        
        $wikiURL = Utility::getConfigProperty("wikiURL");
        $wikiURL = sprintf($wikiURL, "", urlencode($title));
        self::$logger->info("WController : indexPhpAction :: " . $wikiURL);
        
        $wikiContent = NULL;
        try {
            $response = \Httpful\Request::get($wikiURL)->send();
            if (!empty($response->body)) {
                $wikiContent = $response->body->content;
            }
        } catch (Exception $ex) {
            self::$logger->info("WikipediaController : fetchAction : " . $ex->getTraceAsString());
        }
        
        $wikiContent = (is_null($wikiContent)) ? "" : $wikiContent; 
        $this->view->wikiContent = $wikiContent;
        $this->view->title = $title;
    }
}
