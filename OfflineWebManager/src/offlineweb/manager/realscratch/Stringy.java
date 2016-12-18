
package offlineweb.manager.realscratch;

/**
 *
 * @author papa
 */
public class Stringy {
    public static void main(String[] args) {
        String theString = "ohh la la {{image_url}} la la la";
        String replaced = theString.replaceAll("\\{\\{image_url\\}\\}", "ma ma ma");
        System.out.println(replaced);
        
        String child = "some.txt.pdf";
        String fExt = child;
        fExt = fExt.substring(fExt.lastIndexOf(".") + 1);
        
        System.out.println(fExt);
    }
}
