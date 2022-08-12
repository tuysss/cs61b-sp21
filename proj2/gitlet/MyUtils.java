package gitlet;
import java.io.File;

import static gitlet.Utils.*;

/** Assorted utilities.
 *  @author tuysss
 */
public class MyUtils {
    public static void exit(String msg){
        message(msg);
        System.exit(0);
    }

    /**
     *  Get the file of the object.
     * @param id the hashid/uid of commit or blob
     * @return the specific file that stores the object
     */
    public static File getObjectFile(String id){
        String dirName=getObjectDirName(id);
        String fileName=getObjectFileName(id);
        return join(Repository.GITLET_DIR,dirName,fileName);
    }

    /**
     *  Helper class of #getObjectFile
     */
    private static String getObjectDirName(String id){
        return id.substring(0,2);
    }

    /**
     *  Helper class of #getObjectFile
     */
    private static String getObjectFileName(String id){
        return id.substring(2);
    }


}
