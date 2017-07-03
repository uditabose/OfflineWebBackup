<?php

/**
 * Description of Doc
 *
 * @author papa2
 */

require_once 'Utility.php';

class Doc {
    
    private static $logger = NULL;
    
    private $title;
    private $docId;
    private $docAbstarct;
    private $type;
    private $source;
    
    function __construct($title="", $type="", $source="", $docId="", $docAbstarct="") {
        self::$logger = Utility::getLogger();
        $this->title = $title;
        $this->type = $type;
        $this->source = $source;
        $this->docAbstarct = $docAbstarct;
        $this->docId = $docId;
    }
    
    function getTitle() {
        return $this->title;
    }

    function getType() {
        return $this->type;
    }

    function getSource() {
        return $this->source;
    }
    
    function getDocId() {
        return $this->docId;
    }

    function getDocAbstarct() {
        if ($this->docAbstarct === NULL || $this->docAbstarct === "") {
            return "";
        } else {
            return $this->docAbstarct . "...";
        }
    }

    function setTitle($title) {
        $this->title = $title;
    }

    function setType($type) {
        $this->type = $type;
    }

    function setSource($source) {
        $this->source = $source;
    }
    
    function setDocId($docId) {
        $this->docId = $docId;
    }

    function setDocAbstarct($docAbstarct) {
        $this->docAbstarct = $docAbstarct;
    }

       
    function hasThumbnail() {
        if ($this->source === "youtube") {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    function getThumbnailURL() {
        $thumbURL = Utility::getConfigProperty('youtubeImageURL') . "/" . $this->docId . ".jpg";
        self::$logger->info("getThumbnailURL : " . $thumbURL);
        return $thumbURL;
    }
}