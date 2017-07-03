/*
 *  We will decide later!
 */
package offlineweb.sys.logmanager.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface, class and methods can be declared as @Loggable.
 * 
 * For @Loggable classes only the public methods except for toString()
 * hashC
 * 
 * @author uditabose
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {
    
}
