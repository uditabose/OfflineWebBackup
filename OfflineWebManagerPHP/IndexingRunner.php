<?php

require_once 'Configuration.php';
require_once 'RunStatus.php';

if ($argc < 2) {
    echo "Configuration file missing.\n";
    return;
}

$configFilePath = $argv[1];
echo "Creating a run configuration : $configFilePath";
$runConfiguration = Configuration::readConfiguration($configFilePath);

if ($runConfiguration) {
    $newRunStatus = new RunStatus($runConfiguration);
 
    $indexerClassName = ucfirst($runConfiguration->getIndexSource())
            . ucfirst($runConfiguration->getIndexType())
            . "Indexer";
    echo "Indexer : $indexerClassName\n";
    require_once $indexerClassName . ".php";
    
    $indexer = new $indexerClassName($runConfiguration);
    if ($newRunStatus->readRunstatus()) {
        echo "Resuming the indexer\n";
        $indexer->resumeIndexing();
    } else {
        echo "Starting the indexer\n";
        $indexer->startIndexing();
    }
    
}