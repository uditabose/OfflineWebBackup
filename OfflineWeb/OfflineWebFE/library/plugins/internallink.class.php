<?php
/* 
 * @package     PHP5 Wiki Parser
 * @author      Dan Goldsmith
 * @copyright   Dan Goldsmith 2012
 * @link        http://d2g.org.uk/
 * @version     {SUBVERSION_BUILD_NUMBER}
 * 
 * @licence     MPL 2.0
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
require_once(dirname(__FILE__) . '/../interface/startOfLine.interface.php');
require_once "Utility.php";

class internallink implements startOfLine
{
    const regular_expression = '/(\[\[(([^\]]*?)\:)?([^\]]*?)([\|]([^\]]*?))?\]\]([a-z]+)?)/i';
    private static $logger = NULL;
    
    private $currentLine = NULL;
    public function __construct()
    {
        if (self::$logger === NULL) {
            self::$logger = Utility::getLogger();
        }
    }
    
    public function startOfLine($line) 
    {
        // self::$logger->info("internallink : startOfLine : $line");
        $this->currentLine = $line;
        //So although were passed a line of text we might not actually need to do anything with it.
        return preg_replace_callback(internallink::regular_expression,array($this,'replace_callback'),$line);
    }

    private function getImageTag($imageFileName) {
        self::$logger->info("internallink : getImageData : $imageFileName");
        $nameHash = md5($imageFileName);

        $imagePath = "/usr/local/great/workspace/wikidump/images/wikipedia/en/"
                        . $nameHash[0] . "/" . $nameHash[0] . $nameHash[1] . "/" . $imageFileName;
        self::$logger->info("internallink : getImageData : $imagePath");

        $imageTextparts = explode("|", $this->currentLine);
        $imageText = "";

        if (0 === strpos($imageTextparts[count($imageTextparts) - 2], 'alt=')) {
            $imageText = $imageTextparts[count($imageTextparts) - 2];
            
        } 
        if ($imageTextparts[count($imageTextparts) - 1] !== "") {
            $imageText = $imageText . $imageTextparts[count($imageTextparts) - 1];
        }

        $imageText = str_replace("[[", "", $imageText);
        $imageText = str_replace("]]", "", $imageText);
        $imageText = str_replace("alt=", "", $imageText);

        $imageSrc = "/images/noPhoto.jpg";
        if (is_file($imagePath)) {
            self::$logger->info("internallink : getImageData : file path exists");
            $nameParts = explode(".", $imageFileName);
            $fileType = $nameParts[count($nameParts) - 1];
            $imageSrc =  
                "data:image/" . $fileType . ";base64," . base64_encode(file_get_contents($imagePath)); 
        }

        self::$logger->info("internallink : getImageData : $imageText");

        $imageTag = "<div class=\"highlight col-md-4\">" .
                        "<pre><img height='200' width='200' class=\"img-thumbnail\" src=\"$imageSrc\" /></pre>" .
                        "<pre>$imageText</pre>" .
                    "</div><br/>";


        return $imageTag;
    }
    
    private function replace_callback($matches)
    {
        //Url is in index 4
        $url        = $matches[4];
        $title      = "";
        $namespace  = "";
        
        if(array_key_exists(6, $matches) && $matches[6] !== "")
        {
            $title = $matches[6];
        }
        else
        {
            $title = $url;
            if(array_key_exists(7, $matches))
            {
                $title .= $matches[7];
            }
        }
        
        $title = preg_replace('/\(.*?\)/','',$title);
        $title = preg_replace('/^.*?\:/','',$title);
        
        if(array_key_exists(3, $matches))
        {
            $namespace = $matches[3];
        }
        
        //TODO: Image Namspace Support
        $config = WikiParser::getConfigINI();
        
        if(strtoupper($namespace) === "FILE")
        {
            return $this->getImageTag($matches[4]);
            // "<img src=\"" . $this->getImageData($matches[4]) . "\" alt=\"" . $matches[5] . "\"/>";
        }
        else
        {
            $default_format = '?><a href="http://localhost/wiki/index.php?<?php if($namespace != ""){?>namespace=<?php echo $namespace;?>&<?php }?>document_id=<?php echo $url;?>" target="_blank"><?php echo $title;?></a>';

            if(array_key_exists('INTERNAL_LINKS', $config) && array_key_exists('FORMATTED_URL', $config['INTERNAL_LINKS']))
            {
                $default_format = '?>' . $config['INTERNAL_LINKS']['FORMATTED_URL'];
            }
            
            ob_start();
            eval($default_format);
            $link_html = ob_get_contents();
            ob_end_clean();
            return $link_html;
        }
    }
    
}

?>