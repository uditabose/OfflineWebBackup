<?php

/**
 * Description of RunStatus
 *
 * @author papa2
 */
require_once 'Configuration.php';

class RunStatus {
    private $runConfiguration; 
    private $lastIndexCount;
    private $lastStatus;
    private $lastIndexUpdateTime;
    
    public function __construct($runConfiguration) {
        $this->runConfiguration = $runConfiguration;
    }
    
    public function getRunConfiguration() {
        return $this->runConfiguration;
    }

    public function getLastIndexCount() {
        return $this->lastIndexCount;
    }

    public function getLastIndexUpdateTime() {
        return $this->lastIndexUpdateTime;
    }

    public function setRunConfiguration($runConfiguration) {
        $this->runConfiguration = $runConfiguration;
    }

    public function setLastIndexCount($lastIndexCount) {
        $this->lastIndexCount = $lastIndexCount;
    }

    public function setLastIndexUpdateTime($lastIndexUpdateTime) {
        $this->lastIndexUpdateTime = $lastIndexUpdateTime;
    }

    public function getLastStatus() {
        return $this->lastStatus;
    }

    public function setLastStatus($lastStatus) {
        $this->lastStatus = $lastStatus;
    }
 
    public function writeRunStatus() {
        $statusLogFilePath = $this->runConfiguration->getLoggerDir() 
                . "/" .  $this->runConfiguration->getIndexSource()
                . "-" . $this->runConfiguration->getIndexType()
                . ".log";
        if (file_exists($statusLogFilePath)) {
            rename($statusLogFilePath, $statusLogFilePath . time());
        }
        $this->lastIndexUpdateTime = time();
        try {
            $statusLogFile = fopen($statusLogFilePath, 'w+');
            fwrite($statusLogFile, "status=$this->lastStatus\n");
            fwrite($statusLogFile, "time=$this->lastIndexUpdateTime\n");
            fwrite($statusLogFile, "lastCount=$this->lastIndexCount\n");
            fclose($statusLogFile);
        } catch (Exception $exc) {
            echo $exc->getTraceAsString();
        }   
    }
    
    public function readRunstatus() {
        $statusLogFilePath = $this->runConfiguration->getLoggerDir() 
                . "/" .  $this->runConfiguration->getIndexSource()
                . "-" . $this->runConfiguration->getIndexType()
                . ".log";
        if (!file_exists($statusLogFilePath)) {
            echo "Latest run status is not available";
            return;
        }
        try {
            $statusArray = parse_ini_file($statusLogFilePath);
            $this->lastIndexCount = $statusArray["lastCount"];
            return TRUE;
        } catch (Exception $exc) {
            echo $exc->getTraceAsString();
        } 
        
        return FALSE;
        
    }
    
    public function updateAndWriteStatus($runStatus, $lastIndex) {
        $this->setLastIndexCount($lastIndex);
        $this->setLastStatus($runStatus);
        $this->writeRunStatus();
    }
    
    public function toString() {
        return $this->runConfiguration->toString()
                . " indexed on $this->lastIndexUpdateTime\n"
                . " ended with status $this->lastStatus"
                . " at $this->lastIndexCount";
        
                    
    }
}
