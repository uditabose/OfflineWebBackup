package offlineweb.api.util;

import offlineweb.api.bean.Content;

/**
 *
 * @author papa
 */
public interface ConfigConstants {
    
    String NO_ID = "no_id";
    String BLANK = "";
    Content NO_CONTENT = new Content();
    String MAPPER_FILE = "nametoid";
    String IMAGE_URL_PLACEHOLDER = "\\{\\{image_url\\}\\}";
    
    interface API {
        String INDEX_DIR_KEY = "api.index.dir";
    }
    
    interface Repo {
        String WIKI_MAPPER_BASE = "repo.wiki.mapper.basedir";
        String WIKI_CONTENT_BASE = "repo.wiki.text.basedir";
        
        String GUTEN_MAPPER_BASE = "repo.guten.mapper.basedir";
        String GUTEN_HTML_BASE = "repo.guten.html.basedir";
        String GUTEN_TEXT_BASE = "repo.guten.text.basedir";
        
        String YOUTUBE_MAPPER_BASE = "repo.youtube.mapper.basedir";
    }
    
    interface Media {
        String WIKI_IMAGE = "media.wiki.image.url";
  
        String GUTEN_IMAGE = "media.guten.image.url";
        String GUTEN_EPUB = "media.guten.epub.url";
        
        String YOUTUBE_THUMB = "media.youtube.image.url";
        String YOUTUBE_VIDEO = "media.youtube.video.url";
    }
    
}
