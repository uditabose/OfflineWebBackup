/*
 * We will decide later!
 */
package offlineweb.sys.aspecttester;

import java.util.Objects;
import offlineweb.sys.logmanager.annotations.Loggable;

/**
 *
 * @author uditabose
 */

@Loggable
public class LoggedClass {
    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        getPrivateString();
        this.id = id;
    }
    
    public void catchStuff() {
        try {
            throw new Exception("catchStuff");
        } catch (Exception ex) {
            // caught stuff
            //throw ex;
        }
    }
    
    private String getPrivateString() {
        return "a private string";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoggedClass other = (LoggedClass) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LoggedBean{" + "name=" + name + ", id=" + id + '}';
    }
    
    
}
