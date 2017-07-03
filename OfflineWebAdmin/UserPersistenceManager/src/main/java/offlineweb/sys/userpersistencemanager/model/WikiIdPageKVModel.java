/*
 *  We will decide later!
 */
package offlineweb.sys.userpersistencemanager.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author uditabose
 */
@Entity
@Table(name="WIKI_ID_PAGE_KV")
public class WikiIdPageKVModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "page_id")
    private Long pageId;
    
    @Column(name = "page_key")
    private String pageKey;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getPageKey() {
        return pageKey;
    }

    public void setPageKey(String pageKey) {
        this.pageKey = pageKey;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pageId != null ? pageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WikiIdPageKVModel)) {
            return false;
        }
        WikiIdPageKVModel other = (WikiIdPageKVModel) object;
        if ((this.pageId == null && other.pageId != null) 
                || (this.pageId != null && !this.pageId.equals(other.pageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WikiIdPageKVModel[ id=" + pageId + ", key=" + pageKey + "]";
    }
    
}
