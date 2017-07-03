
package offlineweb.api.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author papa
 */
@XmlRootElement
public class Content {
    private String content;
    private String contentId;

    public Content() {
    }

    public Content(String content, String contentId) {
        this.content = content;
        this.contentId = contentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
    
    

    @Override
    public String toString() {
        return "Content{" + "content=" + contentId + '}';
    }
}
