package gitlet;


import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 */
public class Commit implements Serializable{

    /** The message of this Commit. */
    private String message;

    /** the global unique id calculated by SHA-1 algorithm */
    private String hashID;

    /** time that commit being created */
    private Date timestamp;

    /** the list of parent commits uniquely identified by its hashID ,
     * will be called by "gitlet reset" command */
    private List<String> parents;

    /** the branch(head of commit list) of the commit,
     * identified by name     */
    private String branch;

    /** blobs that tracked by this commit
     */
    private Map<String, Blob> blobs;

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
        timestamp=new Date(0);
        hashID=generateHashId();
        branch="master";
        parents =new LinkedList<>();
        blobs=new TreeMap<>();
        file=getObjectFile(this.hashID);
    }

    /**
     * Commit constructor called by "commit" command.
     * @param message
     * @param parentsId
     * @param branch
     * @param blobs
     */
    public Commit(String message,List<String> parentsId,String branch,TreeMap<String,Blob> blobs){
        this.message=message;
        this.parents=parentsId;
        this.branch=branch;
        this.blobs=blobs;
        this.timestamp=new Date();
        generateHashId();
        file=getObjectFile(this.hashID);
    }

    /**
     *  persistence the commit into .gitlet/objects folder
     */
    public  void save(){
        writeObject(file,this);
    }

    /**
     * using sha-1 algorithm, generate uid of commit,
     * with its message+timestamp+BlobToString+parentToString
     * @return hashId of this commit object
     */
    private String generateHashId(){
        String BlobToString="";
        String parentToString= Arrays.toString(new List[]{parents});
        if(blobs!=null){
            BlobToString=blobs.toString();
        }
        String contentOfHash=message+timestamp+BlobToString+parentToString;
        return sha1(contentOfHash);
    }

    public String getMessage() {
        return message;
    }

    public String getHashID() {
        return hashID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<String> getParents() {
        return parents;
    }

    public String getBranch() {
        return branch;
    }

    public Map<String, Blob> getBlobs() {
        return blobs;
    }

    public File getFile() {
        return file;
    }
}
