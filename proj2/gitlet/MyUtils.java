package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.function.Supplier;

import static gitlet.Utils.*;

/** Assorted utilities.
 *  @author tuysss
 */
public class MyUtils {

    /**
     * Get a lazy initialized value.
     *
     * @param delegate Function to get the value
     * @param <T>      Type of the value
     * @return Lazy instance
     */
    public static <T> Lazy<T> lazy(Supplier<T> delegate) {
        return new Lazy<>(delegate);
    }

    public static void exit(String msg){
        message(msg);
        System.exit(0);
    }

    /**
     * Create a directory from the File object.
     *
     * @param dir Directory File instance
     */
    public static void mkdir(File dir) {
        if (!dir.mkdir()) {
            throw new IllegalArgumentException(String.format("mkdir: %s: Failed to create.", dir.getPath()));
        }
    }

    public static File getCommitFile(String id){
        return getObjectFile(id,Commit.class);
    }

    public static File getBlobFile(String id){
        return getObjectFile(id,Blob.class);
    }

    /**
     *  Get the file of the object.
     * @param id the hashid/uid of commit or blob
     * @return the specific file that stores the object
     */
    private static File getObjectFile(String id,Class expectedClass){
        String dirName=getObjectDirName(id);
        String fileName=getObjectFileName(id);
        if((Commit.class).equals(expectedClass)){
            return join(Repository.COMMITS_DIR,dirName,fileName);
        }else if(Blob.class.equals(expectedClass)){
            return join(Repository.BLOBS_DIR,dirName,fileName);
        }else{
            throw new IllegalArgumentException("Object Type is neither commit nor blob.");
        }
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

    /**
     * Save the serializable object to the file path.
     * @param file
     * @param obj
     */
    public static void saveObjectToFile(File file, Serializable obj){
        File dir=file.getParentFile();
        if(!dir.exists()){
            mkdir(dir);
        }
        writeObject(file,obj);
    }
}
