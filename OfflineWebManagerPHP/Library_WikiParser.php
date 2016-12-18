<?php 


class Library_WikiParser {

	public function __construct() {

	}

	public function parse($fileName) {

		if (!is_file($fileName)) {
			echo "$fileName is not a valid file!\n";
			return;
		}

		$partType = ""; 
		$partContent = "";
		$renderContent = "";
		$contentFile = fopen($fileName, "r");

		while (!feof($contentFile)) {
			$line = trim(fgets($contentFile)); // trimming to get rid of trailing whitespaces
			if ($line === "" || strpos($line, "<!--") === 0) {
				// ignore the blank lines and comments
				continue;
			}
			if ($partType !== "") { // an old section
				$partContent .= "\n" . $line;
				$lineEnd = $line[strlen($line) - 2]. $line[strlen($line) - 1];

				if ($partType === "Infobox" && $lineEnd === "}}") { // end of infobox
					$renderContent .= $this->renderPart($partContent, $partType);
					$partType = "";

				} else if ($partType === "Indent") { // end of indentation
					if ($line[0] === "#" || $line[0] === "*" 
							|| $line[0] === ";" || $line[0] === ":") {
						$renderContent .= $this->renderPart($partContent, $partType);
						$partContent = $line;
					} else {
						$renderContent .= "</ul>";
						$renderContent .= $this->renderPart($line, "Text");
						$partType = "";
					}

				} else if ($partType === "Table" && $lineEnd === "|}") { // list and indentation
					$renderContent .= $this->renderPart($partContent, $partType);
					$partType = "";

				} 
			} else { // a new section
				$partContent = ""; // reset the content

				if (strpos($line, "{{Redirect") === 0) { // type redirect
					$renderContent .= $this->renderPart($line, "Redirect");

				} else if (strpos($line, "{{Infobox") === 0) { // type infobox
					$partType = "Infobox";
					$partContent = $line;
				} else if (strpos($line, "==") === 0 ) { // section headers
					$renderContent .= $this->renderPart($line, "Level");
				} else if (strpos($line, "----") === 0) { // horizontal line
					$renderContent .= $this->renderPart($line, "Line");
				} else if (strpos($line, "#") === 0 ||
						   strpos($line, "*") === 0 ||
						   strpos($line, ":") === 0 ||
						   strpos($line, ";") === 0 ) { // list and indentation

					$partType = "Indent";
					$renderContent .= "<ul>";
					$partContent = $line;

				} else if (strpos($line, "{|") === 0) { // table
					$partType = "Table";
					$partContent = $line;

				} else  { // text					
					$renderContent .= $this->renderPart($line, "Text");
				}
			}
		}

		try {
			fclose($contentFile);
		} catch (Exception $ex) {
			// do nothing
		}
		
		return $renderContent;
	}


	private function renderPart($partContent, $partType) {
		$renderMethod = "render$partType";
		return $this->$renderMethod($partContent);
	}

	private function renderRedirect($partContent)
	{
		$htmlContent = "<div class=\"redirect\">Redirects from ";
		$contentParts = explode("|", $partContent);
		$partLength = count($contentParts);

		for ($i=1; $i < $partLength; $i++) { 
			$htmlContent .= $this->formatText($contentParts[$i]);
			if ($i !== $partLength - 1) {
				$htmlContent .= ", ";
			}
		}

		$htmlContent .= "</div>" . "<br/>";

		return $htmlContent;	
	}

	private function renderInfobox($partContent)
	{
		return $partContent . "<br/>";
	}


	private function renderLevel($partContent)
	{
		$htmlContent = "";
		if (strpos($partContent, "======") === 0) {
			$htmlContent = str_replace("======", "", $partContent);
			$htmlContent = "<h6>" . $htmlContent . "</h6>";
		} else if (strpos($partContent, "=====") === 0) {
			$htmlContent = str_replace("=====", "", $partContent);
			$htmlContent = "<h5>" . $htmlContent . "</h5>";
		} else if (strpos($partContent, "====") === 0) {
			$htmlContent = str_replace("====", "", $partContent);
			$htmlContent = "<h4>" . $htmlContent . "</h4>";
		} else if (strpos($partContent, "===") === 0) {
			$htmlContent = str_replace("===", "", $partContent);
			$htmlContent = "<h3>" . $htmlContent . "</h3>";
		} else if (strpos($partContent, "==") === 0) {
			$htmlContent = str_replace("==", "", $partContent);
			$htmlContent = "<h2>" . $htmlContent . "</h2>";
		}
		return $htmlContent . "<br>"; 
	}

	private function renderTable($partContent)
	{
		$contentArray = explode("\n", $partContent);
		$htmlContent = "<table ";
		if (strlen($contentArray[0]) > 2) { //   after the "{|"
			$htmlContent .= substr($contentArray[0], 2);
		}
		$htmlContent .= ">\n";

		$currentIdx = 1;
		$rowCont = FALSE;
		while ($currentIdx < count($contentArray)) {
			$tempLine = trim($contentArray[$currentIdx]);
			$currentIdx++;

			if (stripos($tempLine, "|+") === 0) { // table caption
				$htmlContent .= "<caption>";
				$htmlContent .= $this->formatText($this->renderSingleCite(str_replace("|+", "", $tempLine)));
				$htmlContent .= "</caption>";
				
			} else if (stripos($tempLine, "|-") === 0) { // table row start
				if ($rowCont === TRUE) {
					$htmlContent .= "</tr>";
				} else {
					$rowCont = TRUE;
				}
				$htmlContent .= "<tr>";
				
			} else if (stripos($tempLine, "|") === 0 || stripos($tempLine, "!") === 0) { // table body cell
				$cellType = "";
				if ($tempLine[0] === "|") {
					$cellType = "td";
				} else {
					$cellType = "th";
				}

				$tempLine = substr($tempLine, 1); // removing first character

				if (stripos($tempLine, "||") === FALSE && stripos($tempLine, "!!") === FALSE) { // single cell

					$cellStyle = "";
					$cellContent = "";

					$divnPos = stripos($tempLine, "|");
					echo "$tempLine :::: $divnPos\n+++++++++++++++++++++++++++++++\n";
					if ($divnPos === FALSE || $tempLine[$divnPos + 1] === "|" || $divnPos + 1 === strlen($tempLine)) {
						$cellStyle = "";
						$cellContent = $tempLine;
					} else {
						$cellStyle = substr($tempLine, 0, $divnPos);
						$cellContent = str_replace("!", "", str_replace("|", "", substr($tempLine, $divnPos + 1)));
					}

					$cellContent = str_replace("!", "", $cellContent);
					$cellContent = str_replace("|", "", $cellContent);

					$htmlContent .= "<$cellType $cellStyle >";
					$htmlContent .= $this->formatText($cellContent);
					$htmlContent .= "</$cellType>";

				} else {
					$searchStart = 1;
					$contFlag = TRUE;
					do {
						$nextCellPos = stripos($tempLine, "||", $searchStart);
						$nextHeadPos = stripos($tempLine, "!!", $searchStart);
						$nextStartPos = -1;

						if ($nextCellPos === FALSE && $nextHeadPos === FALSE) { // no more
							$contFlag = FALSE;
							$nextStartPos = strlen($tempLine);
						} else if ($nextHeadPos === FALSE && $nextCellPos !== FALSE) {
							$nextStartPos = $nextCellPos;
						} else if ($nextHeadPos !== FALSE && $nextCellPos === FALSE) {
							$nextStartPos = $nextHeadPos;
						} else {
							if ($nextHeadPos < $nextCellPos) {
								$nextStartPos = $nextHeadPos;
							} else {
								$nextStartPos = $nextCellPos;
							}
						}
						echo "$tempLine \n$$$$$$$$$$$$$$$$$$$$$$$$$\n";
						echo "$searchStart ::  $nextStartPos \n============================\n";
						$thePiece =  substr($tempLine, $searchStart, ($nextStartPos - $searchStart));
						echo "$thePiece \n@@@@@@@@@@@@@@@@@@@@@@@@@\n";
						
						if ($searchStart !== 1) {
							if ($tempLine[$searchStart - 1] === "|") {
								$cellType = "td";	
							} else {
								$cellType = "th";
							}
						}
						
						$cellStyle = "";
						$cellContent = "";
						$divnPos = stripos($thePiece, "|");
						if ($divnPos === FALSE || $thePiece[$divnPos + 1] === "|" || $divnPos + 1 === strlen($thePiece)) {
							$cellStyle = "";
							$cellContent = $thePiece;
						} else {
							$cellStyle = str_replace("!", "", str_replace("|", "", substr($thePiece, 0, $divnPos)));
							$cellContent = substr($thePiece, $divnPos + 1);
						}

						$cellContent = str_replace("!", "", $cellContent);
						$cellContent = str_replace("|", "", $cellContent);

						echo "$cellStyle :##: $cellContent \n------------\n";

						$htmlContent .= "<$cellType $cellStyle >";
						$htmlContent .= $this->formatText($cellContent);
						$htmlContent .= "</$cellType>";

						$searchStart = $nextStartPos + 	2;


					} while ($contFlag !== FALSE);
				}
			} 
		}

		$htmlContent .= "</table>\n";
		return $htmlContent . "<br>";
	}

	private function renderIndent($partContent)
	{
		// Ignoring the numbered list for now, only bulleted list of items
		$firstWhiteSpace = strpos($partContent, " ");

		if ($firstWhiteSpace === FALSE) {
			return $this->formatText($partContent);
		} else {
			return "<li>" . $this->formatText(substr($partContent, $firstWhiteSpace)) . "</li>";
		}
	}

	private function renderLine($partContent)
	{
		return "<hr/>";
	}

	private function renderText($partContent)
	{
		return $this->formatText($partContent) . "<br>";
	}

	private function renderSingleCite($partContent)
	{
		echo "$partContent \n=======================================\n";
		$partContent = str_replace("{{", "", $partContent);
		$partContent = str_replace("}}", "", $partContent);
		$citeArray = explode("|", $partContent);

		$htmlContent = "<table class=\"citation\">\n";
		$htmlContent .= "<thead><th colspan='2'>";
		$htmlContent .= ucfirst(trim(str_replace("cite", "", $citeArray[0])));
		$htmlContent .= "</th></thead>\n";
		$htmlContent .= "<tbody>\n";

		for ($i = 1; $i < count($citeArray); $i++) {
			$partSplitted = explode("=", $citeArray[$i]);
			$htmlContent .= "<tr><th>" . ucfirst(trim($partSplitted[0])) . "</th>"
							. "<td>" . ucfirst(trim($partSplitted[1])) . "</td></tr>";
		}
		$htmlContent .= "<tbody>\n</table>";
		return $htmlContent;
	}

	private function renderMultiCite($partContent)
	{
		return $this->renderSingleCite($partContent);
	}

	private function formatText($partContent) {
		$partContent = $this->formatLinks($partContent);
		$partContent = $this->formatFont($partContent);
		$partContent = $this->formatRef($partContent);
		return $partContent;
	}

	private function formatLinks($partContent) {
		$linkStart = strpos($partContent, "[[");
		if ($linkStart === FALSE) {
			return $partContent;
		}

        $htmlContent = "";
        $pieces = explode("]]", $partContent);
        foreach($pieces as $part) {
            $twoPart = explode("[[", $part);
            $htmlContent .= $twoPart[0];
            $linkParts = explode("|", "$twoPart[1]");

            if (stripos("$twoPart[1]", "File:") === 0) { // process images
            	$htmlContent .= $this->formatImage($linkParts);
            } else {
            	$htmlContent .= $this->formatSingleLink($linkParts);
            }
        }
        return $htmlContent;
	}

	private function formatFont($partContent) {
		if (strpos($partContent, "''") === FALSE) {
			return $partContent;
		}

		$parts = explode("'''''", $partContent);
		$htmlContent = $parts[0];
		for ($i=1; $i < count($parts); $i += 2) { 
			$htmlContent .= "<i><b>" . $parts[$i] ."</b></i>";
			if (count($parts) - 1 >= $i + 1) {
				$htmlContent .= $parts[$i + 1];
			}
		}

		$parts = explode("'''", $htmlContent);
		$htmlContent = $parts[0];
		for ($i=1; $i < count($parts); $i += 2) { 
			$htmlContent .= "<i>" . $parts[$i] ."</i>";
			if (count($parts) - 1 >= $i + 1) {
				$htmlContent .= $parts[$i + 1];
			}
		}

		$parts = explode("''", $htmlContent);
		$htmlContent = $parts[0];
		for ($i=1; $i < count($parts); $i += 2) { 
			$htmlContent .= "<b>" . $parts[$i] ."</b>";
			if (count($parts) - 1 >= $i + 1) {
				$htmlContent .= $parts[$i + 1];
			}
		}

		return $htmlContent;
	}

	private function formatRef($partContent) {
		$refEndPos = stripos($partContent, "/>");
		$htmlContent = $partContent;

		while ($refEndPos !== FALSE) {
			if ($htmlContent[$refEndPos - 2].$htmlContent[$refEndPos - 1] !== "br") {
				$beforePart = substr($htmlContent, 0, $refEndPos);
				$afterPart = substr($htmlContent, $refEndPos + 2);
				$htmlContent = $beforePart . "></ref>" . $afterPart;
			} 
			
			$refEndPos  = stripos($htmlContent, "/>", $refEndPos + 2);
		}

		$htmlContent = str_replace("ref>", "sup>", $htmlContent);
		$htmlContent = str_replace("<ref", "<sup ", $htmlContent);

		return $htmlContent;
	}

	private function getImageSrc($imageFileName) {
		$imageFileName = str_replace("File:", "", $imageFileName);
	    $nameHash = md5($imageFileName);
	    $imagePath = "/usr/local/great/workspace/wikidump/images/wikipedia/en/"
	                    . $nameHash[0] . "/" . $nameHash[0] . $nameHash[1] . "/" . $imageFileName;

	    $imageSrc = "/images/noPhoto.jpg";
	    if (is_file($imagePath)) {
	        $nameParts = explode(".", $imageFileName);
	        $fileType = $nameParts[count($nameParts) - 1];
	        $imageSrc =  
	            "data:image/" . $fileType . ";base64," . base64_encode(file_get_contents($imagePath)); 
	    }
	    return $imageSrc;
	}

	private function formatImage($linkParts) {
		$htmlContent = "";
		
		$imgStart = stripos($linkParts[0], "[[File:");
		$htmlContent .= substr($linkParts[0], 0, $imgStart); //  before the image starts


		$imgPart = substr($linkParts[0], $imgStart);
		//echo "$imgPart\n";
		$imgParts = explode("[[", $imgPart);
		$imageSubParts = explode("|", $imgParts[0]);
		$imageSrc = $this->getImageSrc($imageSubParts[0]);

		$htmlContent .= "<div class=\"highlight col-md-4\">";
	    $htmlContent .= 	"<pre><img height='200' width='200' class=\"img-thumbnail\" src=\"$imageSrc\">&nbsp;</img>";
	    $htmlContent .=     "<pre>";
	    $htmlContent .=     $imageSubParts[count($imageSubParts) - 1];
	    if (count($imgParts) > 1) {
	    	$htmlContent .= $this->formatSingleLink(explode("|", $imgParts[1]));
	    }

	    /*if (count($linkParts) > 1) {
	    	for ($i=1; $i < count($linkParts); $i++) { 
	    		$imgParts = explode("[[", $linkParts[$i]);
	    		$htmlContent .=     $imgParts[0];
	    		if (count($imgParts) > 1) {
			    	$htmlContent .= $this->formatSingleLink(explode("|", $imgParts[1])); 	
			    }
	    	}
	    }*/
	    $htmlContent .=     "</pre>" ;
	    $htmlContent .= "</div><br/>";

		return $htmlContent;
	}

	private function formatSingleLink($linkParts) {
		if ($linkParts[0] === "") {
			return "";
		}
		$htmlContent = "<a href=\"/search/wikipedia/t=" . $linkParts[0] . "\">";
		if (count($linkParts) > 1) {
			$htmlContent .= $this->formatFont($linkParts[1]); 
		} else {
			$htmlContent .= $this->formatFont("'", "", $linkParts[0]); 
		}
		$htmlContent .= "</a>";
		return $htmlContent;
	}
}


