/*
 *  We will decide later!
 */
package offlineweb.sys.aspecttester;

/**
 *
 * @author uditabose
 */
public class LogMain {
    
    public static void main(String[] args) throws Throwable {
        System.out.println("\n******* LOGGING CLASS *******\n");
        LoggedClass loggedClass = new LoggedClass();
        loggedClass.setId(1000);
        loggedClass.setName("HaHaHa");
        loggedClass.getId();
        loggedClass.getName();
        loggedClass.catchStuff();
        System.out.println(loggedClass.toString());
        System.out.println("");
        System.out.println("\n******* LOGGING METHOD *******\n");
        LoggedMethod loggedMethod = new LoggedMethod();
        loggedMethod.getId();
        loggedMethod.getName();
        loggedMethod.catchStuff();
        System.out.println(loggedMethod.toString());
        
    }
    
}
