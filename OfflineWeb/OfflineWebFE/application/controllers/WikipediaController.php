<?php

require_once 'BaseController.php';
require_once 'Utility.php';
include('httpful.phar');
/**
 * Description of WikipediaController
 *
 * @author papa
 */

class WikipediaController extends BaseController {
    private static $logger = NULL;
    
    public function init() {
        if (self::$logger === NULL) {
            self::$logger = Utility::getLogger();
        }
    }
   
    public function fetchAction() {
        self::$logger->info("WikipediaController : fetchAction :: Starting the fetch");
        $wikiPageTitle = $_GET['t'];
        $wikiPageTitle = str_replace("_", " ", $wikiPageTitle);
        
        $wikiURL = Utility::getConfigProperty("wikiURL");
        $wikiURL = sprintf($wikiURL, "", urlencode($wikiPageTitle));
        self::$logger->info("WikipediaController : fetchAction :: " . $wikiURL);
        
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
        $this->view->title = $wikiPageTitle;
        $this->view->wikiContent = $wikiContent;
    }
}
