
package offlineweb.manager.realscratch;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author papa2
 */
public class UnavailableStart {
    public static void main(String[] args) {
        String contentDirpath = "/media/papa2/great/workspace/OfflineWebData/wikidump/contentid";
        final String pagesDirPath = "/media/papa2/great/workspace/DataPort/OfflineWebData/wikidump/pages";
        
        final StringBuilder unavailableDirs = new StringBuilder();
        
        File contentDir = new File(contentDirpath);
        File[] childContentDirs = contentDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File childDir) {
                if (!childDir.getName().contains("nametoid-")) {
                    return false;
                }
                String[] keys = childDir.getName().split("nametoid-");
                if (keys.length < 2) {
                    return false;
                }
                String dirKey = keys[1];
                File pageDir = new File(pagesDirPath + File.separator + dirKey);
                
                if (!(pageDir.exists() && pageDir.isDirectory())) {
                    unavailableDirs.append(dirKey)
                            .append(";");
                }
                return !(pageDir.exists() && pageDir.isDirectory());
            }
        });
        
        
        System.out.println(unavailableDirs);
    }
}
