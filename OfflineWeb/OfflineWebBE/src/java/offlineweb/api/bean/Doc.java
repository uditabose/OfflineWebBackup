
package offlineweb.api.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author papa2
 */

@XmlRootElement
public class Doc {

    private String title;
    private String docId;
    private String docAbstract;
    private String type;
    private String source;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocAbstract() {
        return docAbstract;
    }

    public void setDocAbstract(String docAbstract) {
        this.docAbstract = docAbstract;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Doc{" + "title=" + title + ", docId=" + docId + ", source=" + source + '}';
    }

}
