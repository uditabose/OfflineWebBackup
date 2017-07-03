<?php

/**
 * Description of Result
 *
 * @author papa2
 */
require_once 'Doc.php';

class Result {
    private $currentPage;
    private $totalResults;
    private $searchTerm;
    private $serachResult;
    
    function __construct($currentPage=1, $totalResults=0, $searchTerm="", $serachResult=NULL) {
        $this->currentPage = $currentPage;
        $this->totalResults = $totalResults;
        $this->searchTerm = $searchTerm;
        $this->serachResult = $serachResult;
    }
    
    function getCurrentPage() {
        return $this->currentPage;
    }

    function getTotalResults() {
        return $this->totalResults;
    }

    function getSearchTerm() {
        return $this->searchTerm;
    }

    function getSerachResult() {
        return $this->serachResult;
    }

    function setCurrentPage($currentPage) {
        $this->currentPage = $currentPage;
    }

    function setTotalResults($totalResults) {
        $this->totalResults = $totalResults;
    }

    function setSearchTerm($searchTerm) {
        $this->searchTerm = $searchTerm;
    }

    function setSerachResult($serachResult) {
        $this->serachResult = $serachResult;
    }
    
    function addDoc(Doc $aDoc) {
        if ($this->searchTerm === NULL) {
            $this->searchTerm = array();
        }
        
        $this->serachResult[] = $aDoc;
    }
}
