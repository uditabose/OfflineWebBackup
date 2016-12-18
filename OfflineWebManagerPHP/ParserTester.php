<?php

require_once "Library_WikiParser.php";

$parser = new Library_WikiParser();
$outFile = fopen("out.html", "w") ;
fwrite($outFile, $parser->parse("/usr/local/great/workspace/WikiParser/645042"));
echo "\n===================================================\n";