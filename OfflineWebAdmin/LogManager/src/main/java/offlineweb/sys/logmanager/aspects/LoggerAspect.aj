/*
 * We will decide later!
 */
package offlineweb.sys.logmanager.aspects;

import offlineweb.sys.logmanager.annotations.Loggable;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This aspect handles all logging. 
 * 
 * @author uditabose
 */
public aspect LoggerAspect {

    /**
     * Log Object Creation
     */
    static interface LogObject {
        Logger getLogger();
    }

    declare parents : (@Loggable *) implements LogObject;

    public Logger LogObject.getLogger() {
        return LogHolderAspect.aspectOf(this.getClass()).getLogger();
    }

    // loggable methods
    pointcut loggedMethod() : execution(@Loggable * *(..));

    // loggable method for exceoption handling
    pointcut loggedWithinMethod() : withincode(@Loggable * *(..));

    // loggable class
    pointcut loggedClass() : within(@Loggable *);

    // overridden object methods 
    pointcut objectMethods() : execution(public String *.toString()) ||
                               execution(public int *.hashCode())    ||
                               execution(public boolean *.equals());

    // all execution                          
    pointcut atExecution() : execution(* *(..));

    // at the start of the loggable methods
    before() : loggedMethod() && !objectMethods(){
        log(thisJoinPoint, "Begin");
    }

    // at the end of the loggable methods
    after() : loggedMethod() && !objectMethods(){
        log(thisJoinPoint,  "End");
    }

    // catch block of the loggable methods
    before (Throwable throwable): handler(Exception+) 
            && args(throwable) && loggedWithinMethod(){
        error(thisJoinPoint, throwable);
    }

    // at the start of the methods of loggable class
    before() : loggedClass() && atExecution() && !objectMethods(){
        log(thisJoinPoint, "Begin");
    }

    // at the end of the methods of loggable class
    after() : loggedClass() && atExecution() && !objectMethods(){
        log(thisJoinPoint,  "End");
    }

    // catch block of the methods of loggable class
    before (Throwable throwable): handler(Exception+) && args(throwable) && loggedClass(){
        error(thisJoinPoint, throwable);
    }

    //
    private void log(JoinPoint thisJoinPoint, String... prefix) {
        Logger logger = null;

        if (thisJoinPoint.getThis() instanceof LogObject) {
            logger = ((LogObject)thisJoinPoint.getThis()).getLogger();            
        } else {
            logger = LoggerFactory.getLogger(thisJoinPoint.getThis().getClass());
        } 
        
        if (prefix != null) {
            logger.info("{} # {}", prefix, unWrapJoinPoint(thisJoinPoint));
        } else {
            logger.info("{}", unWrapJoinPoint(thisJoinPoint));
        }
 
    }

    private void error(JoinPoint thisJoinPoint, Throwable throwable) {
        Logger logger = null;

        if (thisJoinPoint.getThis() instanceof LogObject) {
            logger = ((LogObject)thisJoinPoint.getThis()).getLogger();
        } else {
            logger = LoggerFactory.getLogger(thisJoinPoint.getThis().getClass()); 
        }

        logger.error("{} {}", unWrapJoinPoint(thisJoinPoint), throwable);
    }

    private StringBuilder unWrapJoinPoint(JoinPoint thisJoinPoint) {
        StringBuilder unWrappedJoinPoint = new StringBuilder();
        
        unWrappedJoinPoint.append(thisJoinPoint.getSourceLocation())
                          .append(":")
                          .append(thisJoinPoint.getSignature().getName())
                          .append("(");


        Object[] joinPointArgs = thisJoinPoint.getArgs();
        for (Object arg : joinPointArgs) {
            unWrappedJoinPoint.append(arg).append(":");
        }
        unWrappedJoinPoint.append(")");

        return unWrappedJoinPoint;
    }
}
