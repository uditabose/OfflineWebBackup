/*
 *  We will decide later!
 */
package offlineweb.sys.systempersistencemanager.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author uditabose
 */
@Entity
@Table(name="WIKI_MEDIA")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WikiMediaModel implements Serializable {
    
    public enum MediaStatus {
        New,
        Current,
        Failed
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="page_id")
    private WikiPageModel page;
    
    @Column(name = "url")
    private String mediaURL;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MediaStatus mediaStatus;
    
    @Column(name = "update_time")
    private Long updateTime; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WikiPageModel getPage() {
        return page;
    }

    public void setPage(WikiPageModel page) {
        this.page = page;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public MediaStatus getMediaStatus() {
        return mediaStatus;
    }

    public void setMediaStatus(MediaStatus mediaStatus) {
        this.mediaStatus = mediaStatus;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WikiMediaModel)) {
            return false;
        }
        WikiMediaModel other = (WikiMediaModel) object;
        if ((this.id == null && other.id != null) 
                || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WikiMediaModel[ id=" + id + " ]";
    }
    
}
