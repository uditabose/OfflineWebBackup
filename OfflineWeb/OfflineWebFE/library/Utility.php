<?php

//require_once 'WikiParser.php';

class Utility {

    private static $appLogger = NULL;

    public static function getSession() {
        return new Zend_Session_Namespace("searchSession");
    }

    public static function getLogger() {
        if (self::$appLogger == NULL) {
            $logPath = self::getConfigProperty('log');
            if ($logPath === ""){    
                $logPath = '/tmp/fe.log';
            } 
            self::$appLogger = 
                    new Zend_Log(new Zend_Log_Writer_Stream($logPath));
        }
        return self::$appLogger;
    }
    
    public static function getConfigProperty($configProp) {
        $appPath = realpath(dirname(__FILE__) . '/../application');
        if (is_file(APPLICATION_PATH . '/configs/library.ini')) {
            
            $libraryConfig = parse_ini_file(APPLICATION_PATH . '/configs/library.ini');
            if (array_key_exists("offline.fe.$configProp", $libraryConfig)){
                return $libraryConfig["offline.fe.$configProp"];
            }
        } else {
            echo 'APPLICATION_PATH not defined\n';
        }

        return "";
    }
    
    
    public static function getImageAsBase64String($imageFilePath, $defaultImagePath=NULL) {

        if ($imageFilePath === NULL || $imageFilePath === "") {
            return NULL;
        } elseif (!is_file($imageFilePath)) {
            if ($defaultImagePath === NULL || !is_file($defaultImagePath)) {
                return NULL;
            } else {
                $imageFilePath = $defaultImagePath;
            }
        } 

        $fileType = pathinfo($imageFilePath, PATHINFO_EXTENSION);
        $imageSrc =  
            "data:image/" . $fileType . ";base64," 
                . base64_encode(file_get_contents($imageFilePath)); 
        
        return $imageSrc;

    }
    
     public static function getFileId($idFilePath, $fileName) {
        self::getLogger();

        if (!is_file($idFilePath)) {
            self::$appLogger->info("getFileId :: InValid : " . $idFilePath);
            return NULL;
        }
        self::$appLogger->info("getFileId :: Valid : " . $idFilePath);
        
        $idFile = fopen($idFilePath, "r");
        $fileId = NULL;
        while (!feof($idFile)) {
            $nameToId = trim(fgets($idFile));
            if (strpos($nameToId, "$fileName=") === 0) {
                $fileId = end(explode("=", $nameToId));
                break;
            }
        }
        fclose($idFile);
        
        return $fileId;
    }

    public static function getWikiPage($wikiTitle) {
        $wikiPageKey = strtoupper(mb_substr($wikiTitle, 0, 1, 'utf-8'));
        $wikiPath = self::getConfigProperty('wikiMap');

        if ($wikiPath !== "") {
            $wikiPath = sprintf($wikiPath, $wikiPageKey);
        }
        
        $wikiId = Utility::getFileId($wikiPath, $wikiTitle);
        if ($wikiId === NULL) {
            self::$appLogger->info("Utility : getWikiPageContent :: ID for wiki page not found :: " . $wikiPath);
            return "";
        } else {
            return self::getWikiPageContent($wikiTitle, $wikiId);
        }
    }
    
    public static function getWikiPageContent($wikiPageName, $wikiPageId) {
        self::getLogger();
        $wikiContent = "";
        self::$appLogger->info("Utility : getWikiPageContent :: " . $wikiPageName);
        try {
            if ($wikiPageId === "") {
                self::$appLogger->info("Utility : getWikiPageContent :: invalid page id");
                return;
            }
            
            $wikiPageKey = strtoupper(mb_substr($wikiPageName, 0, 1, 'utf-8'));

            $wikiContentBasePath = self::getConfigProperty('wikiPage');
            if ($wikiContentBasePath === "") {
                self::$appLogger->info("Utility : getWikiPageContent :: Can not read config file");
                return;
            }

            $subPath = "/" .$wikiPageKey . "/";
            $wikiIdArr = str_split($wikiPageId);
            for ($i = 0; $i < count($wikiIdArr) - 1; $i++) {
                $subPath .= $wikiIdArr[$i] . "/";
            }

            $wikiContentPath = sprintf($wikiContentBasePath, $subPath, $wikiPageId . "-h");

            self::$appLogger->info("Utility : getWikiPageContent :: Searching :: " . $wikiContentPath);

            if (is_file($wikiContentPath)) {
                $imgBaseURL = self::getConfigProperty("wikiImageURL");
                $wikiContent = file_get_contents($wikiContentPath);
                $wikiContent = str_replace("{{image_url}}", $imgBaseURL, $wikiContent);
            } 
        } catch (Exception $ex) {
            self::$appLogger->info("Utility : getWikiPageContent : " . $ex->getTraceAsString());
        }
        
        return $wikiContent;
    }
}
