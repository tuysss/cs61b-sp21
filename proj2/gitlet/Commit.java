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

    /** the list of parent commits uniquely identified by its hashID ,
     * will be called by "gitlet reset" command */
    private final List<String> parents;

    /** the branch(head of commit list) of the commit,
     * identified by name
    private final String branch;
     放到外部，repo中记录 */

    /** blobs that tracked by this commit
     */
    private final Map<String, Blob> blobs;

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
        blobs=new TreeMap<>();
        hashId =generateHashId();
        file= getObjectFromFile(this.hashId);
    }

    /**
     * Commit constructor called by "commit" command.
     * @param message
     * @param parentsId
     * @param blobs
     */
    public Commit(String message,List<String> parentsId,Map<String,Blob> blobs){
        this.message=message;
        this.parents=parentsId;
        this.blobs=blobs;
        this.date =new Date();
        this.hashId = generateHashId();
        file= getObjectFromFile(this.hashId);
    }

    /**
     *  persistence the commit into .gitlet/objects folder
     */
    public void save(){
        writeObject(file,this);
    }

    public Commit getCommitFromFile(String id){
        return readObject(getObjectFromFile(id),Commit.class);
    }

    /**
     * using sha-1 algorithm, generate uid of commit,
     * with its message+timestamp+BlobToString+parentToString
     * @return hashId of this commit object
     */
    private String generateHashId(){
        return sha1(getTimestamp(),message,parents.toString(),blobs.toString());
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


    public Map<String, Blob> getBlobs() {
        return blobs;
    }

    public File getFile() {
        return file;
    }
}
