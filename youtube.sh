#! / bin / bash           

while read line           
do            
    url="https://www.youtube.com/watch?v=$line \n"  
    echo  "$url \n" 
    cd  "/usr/local/great/workspace/OfflineWebData/youtubedump/video"
    youtube-dl --id $url     
done <"/usr/local/great/workspace/OfflineWebData/youtubedump/youtube-id"
