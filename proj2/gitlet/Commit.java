package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 */
public class Commit implements Serializable{

    /** The message of this Commit. */
    private final String message;

    /** the global unique id calculated by SHA-1 algorithm */
    private final String hashId;

    /** time that commit being created */
    private final Date date;

    /**
     *  last commits of this with SHA1 id.
     *  By default, one parent.
     *  If there is a merge, then two.
     */
    private final List<String> parents;

    /** blobs that tracked by this commit,
     * map of file path as key and SHA1 id as value
     */
    private final Map<String, String> tracked;

    /**
     * File to persistent this commit into.
     */
    private final File file;

    /**
     * Commit constructer called by "init" command.
     * All repositories will automatically share this commit (have the same UID)
     * and all commits in all repositories will trace back to it.
     */
    public Commit(){
        this.message="initial commit";
        date =new Date(0);
        parents =new ArrayList<>();
        tracked =new TreeMap<>();
        hashId =generateHashId();
        file= getObjectFromFile(this.hashId);
    }

    /**
     * Commit constructor called by "commit" command.
     * @param message
     * @param parents
     * @param tracked
     */
    public Commit(String message,List<String> parents,Map<String,String> tracked){
        this.message=message;
        this.parents=parents;
        this.tracked =tracked;
        this.date =new Date();
        this.hashId = generateHashId();
        file= getObjectFromFile(this.hashId);
    }

    public String getLog(){
        StringBuilder logBuilder=new StringBuilder();
        logBuilder.append("===").append("\n");
        logBuilder.append("commit").append(" ").append(hashId).append("\n");
        if(parents.size()>1){
            logBuilder.append("Merge:");
            for(String parent: parents){
                logBuilder.append(" ").append(parent,0,7);
            }
        }
        logBuilder.append("Date:").append(" ").append(getTimestamp()).append("\n");
        logBuilder.append(message).append("\n");
        return logBuilder.toString();

    }

    /**
     *  persistence the commit into .gitlet/objects folder
     */
    public void save(){
        saveObjectToFile(file,this);
    }

    public static Commit getCommitFromFile(String id){
        return readObject(getObjectFromFile(id),Commit.class);
    }

    /**
     * using sha-1 algorithm, generate uid of commit,
     * with its message+timestamp+BlobToString+parentToString
     * @return hashId of this commit object
     */
    private String generateHashId(){
        return sha1(getTimestamp(),message,parents.toString(), tracked.toString());
    }

    public String getMessage() {
        return message;
    }

    public String getHashId() {
        return hashId;
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    public List<String> getParents() {
        return parents;
    }

    public Map<String, String> getTracked() {
        return tracked;
    }

    public File getFile() {
        return file;
    }
}
