/*
 *  We will decide later!
 */
package offlineweb.sys.systempersistencemanager.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author uditabose
 */
@Entity
@Table(name="WIKI_PAGE")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WikiPageModel implements Serializable {
    
    public enum PageStatus {
        New,
        Current,
        Failed,
        UpdatePending,
        UpdateInProgress,
        FormatPending,
        FormatInProgress,
        IndexPending,
        IndexInProgress 
    }

    private static final long serialVersionUID = 1L;
    @Id
    private Long pageId;
    
    @Column(name = "title_key")
    private String pageKey;
    
    @Column(name = "title")
    private String pageTitle;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PageStatus pageStatus;
    
    @Column(name = "update_time")
    private Long updateTime;

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

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public PageStatus getPageStatus() {
        return pageStatus;
    }

    public void setPageStatus(PageStatus pageStatus) {
        this.pageStatus = pageStatus;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pageId != null ? pageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WikiPageModel)) {
            return false;
        }
        WikiPageModel other = (WikiPageModel) object;
        if ((this.pageId == null && other.pageId != null) 
                || (this.pageId != null && !this.pageId.equals(other.pageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder(this.getClass().getSimpleName());
        toStringBuilder.append("[ ")
                .append(" pageId=").append(pageId)
                .append(" pageTitle=").append(pageTitle)
                .append(" ]");
      
        return toStringBuilder.toString();
    }
    
}
