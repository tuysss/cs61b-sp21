package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.MyUtils.saveObjectToFile;
import static gitlet.Utils.*;

/**
 *  The stagingArea presentation.
 */
public class StagingArea implements Serializable {
    /**
     *  The added files Mapping:filepath-sha1ID.
     */
    private final Map<String,String> added=new HashMap<>();

    /**
     *  The removed files set with file path as key.
     */
    private final Set<String> removed=new HashSet<>();

    /**
     * The tracked blobs Mapping of filepath-sha1ID.
     * Added and removed both are included.
     */
    private transient Map<String,String> tracked=new HashMap<>();

    /**
     *  Read obj from file from the fixed file:Index
     * @return StagingArea obj.
     */
    public static StagingArea fromFile(){
        return readObject(Repository.INDEX,StagingArea.class);
    }

    public void save(){
        saveObjectToFile(Repository.INDEX,this);
    }


    /**
     * Add file to StagingArea.
     * 1.Staging an already-staged file overwrites the previous entry
     *
     * If blob exists(i.e. file isn't changed), return false, no need to save.
     * @param file File instance
     * @return true if the stagingArea is changed.
     */
    public boolean add(File file){
        String filePath=file.getPath();

        Blob blob=new Blob(file);
        String blobId=blob.getHashID();

        String trackedBlobId = tracked.get(filePath);
        if (trackedBlobId != null) {
            if (trackedBlobId.equals(blobId)) {
                if (added.remove(filePath) != null) {
                    return true;
                }
                return removed.remove(filePath);
            }
        }

        //Returns: the previous value associated with key, or null if there was no mapping for key.
        String prevBlobId = added.put(filePath, blobId);
        if (prevBlobId != null && prevBlobId.equals(blobId)) {
            return false;
        }

        if (!blob.getFile().exists()) {
            blob.save();
        }
        return true;
    }


    /**
     * Remove file if it exists in stage area(track files for addition for not tracked by parent(s))
     * If in added, remove it from stagingArea.
     * If in current commit, remove and delete the real file from CWD.
     *
     * @param file File instance
     * @return true if remove success.
     *         false if fail (file not in neither stage-added nor commitTracked)
     */
    public boolean remove(File file){
        String filePath=file.getPath();

        if(added.containsKey(filePath)){
            added.remove(filePath);
            return true;
        }

        if(tracked.containsKey(filePath)){
            if(file.exists()){
                file.delete();
            }
            removed.add(filePath);
            return true;
        }
        return false;
    }


    /**
     * Perform a commit.
     * @return Map with file path as key and SHA1 id as value.
     */
    public Map<String,String> commit(){
        tracked.putAll(added);
        for(String filePath: removed){
            tracked.remove(filePath);
        }
        return tracked;
    }

    /**
     *  Clear the stagingArea.
     */
    private void clear(){
        added.clear();
        removed.clear();
    }

    public boolean isClean(){
        return added.isEmpty() && removed.isEmpty();
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public Map<String, String> getTracked() {
        return tracked;
    }

    public void setTracked(Map<String, String> tracked) {
        this.tracked = tracked;
    }
}
