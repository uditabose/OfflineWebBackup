<?php

/**
 * Description of Configuration
 *
 * @author papa2
 */
  class Configuration {
    private $indexSource;
    private $indexType;
    private $indexDir;
    private $contentFilePath;
    private $maxNoOfIndicesPerRun;
    private $loggerDir;
    private $metadataFilePath;

    function __construct($indexSource, $indexType, $contentFilePath, 
            $indexDir, $maxNoOfIndicesPerRun, $loggerDir, $metadataFilePath = "") {
        $this->indexSource = $indexSource;
        $this->indexType = $indexType;
        $this->indexDir = $indexDir;
        $this->contentFilePath = $contentFilePath;
        $this->maxNoOfIndicesPerRun = $maxNoOfIndicesPerRun;
        $this->loggerDir = $loggerDir;
        $this->metadataFilePath = $metadataFilePath;

    }
    
    public function getIndexSource() {
        return $this->indexSource;
    }

    public function getIndexType() {
        return $this->indexType;
    }

    public function getIndexDir() {
        return $this->indexDir;
    }

    public function getMaxNoOfIndicesPerRun() {
        return $this->maxNoOfIndicesPerRun;
    }

    public function getLoggerDir() {
        return $this->loggerDir;
    }
    
    function getContentFilePath() {
        return $this->contentFilePath;
    }
    
    function getMetadataFilePath() {
        return $this->metadataFilePath;
    }

    public function toString() {
        return "$this->indexSource-$this->indexType";
    }
    
    public static function readConfiguration($configFilePath) {
        if (is_file($configFilePath)) {
            $configArray = parse_ini_file($configFilePath);
            // print_r($configArray);
            if (array_key_exists("meta_path", $configArray)) {
                $configuration = new Configuration($configArray["source"], 
                    $configArray["type"], 
                    $configArray["index_content"], 
                    $configArray["index_dir"], 
                    $configArray["max_cnt"], 
                    $configArray["log_dir"],
                    $configArray["meta_path"]);
            } else {
                $configuration = new Configuration($configArray["source"], 
                    $configArray["type"], 
                    $configArray["index_content"], 
                    $configArray["index_dir"], 
                    $configArray["max_cnt"], 
                    $configArray["log_dir"]);
            }
            
            return $configuration;
        } else {
            return false;
        }
    }  
}
