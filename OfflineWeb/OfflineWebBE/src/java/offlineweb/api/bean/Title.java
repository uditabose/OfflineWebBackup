
package offlineweb.api.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author papa2
 */

@XmlRootElement
public class Title {
    private String title;

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Title{" + "title=" + title + '}';
    }
    
    
}
