package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 */
public class Commit implements Serializable{

    /** The message of this Commit. */
    private String message;

    /** the global unique id calculated by SHA-1 algorithm */
    private String hashID;

    /** format: 00:00:00 UTC, Thursday, 1 January 1970 */
    private String timestamp;

    /** the list of parent commits uniquely identified by its hashID ,
     * will be called by "gitlet reset" command */
    private String[] parents;

    /** the branch(head of commit list) of the commit,
     * identified by its hashID     */
    private String branch;

    /** blobs that tracked by this commit
     */
    private TreeMap<String, Blob> blobs;

    /**
     * File to persistent this commit into.
     */
    private  final File file;


    /**
     * Commit constructer called by "init" command.
     * All repositories will automatically share this commit (have the same UID)
     * and all commits in all repositories will trace back to it.
     */
    public Commit(String message){
        this.message=message;
        timestamp="00:00:00 UTC, Thursday, 1 January 1970";
        hashID=generateHashId();
        branch=hashID;
        parents =null;
        blobs=null;
        file=getObjectFile(this.hashID);
    }

    /**
     * Commit constructor called by "commit" command.
     * @param message
     * @param parentsId
     * @param branch
     * @param blobs
     */
    public Commit(String message,String[] parentsId,String branch,TreeMap<String,Blob> blobs){
        this.message=message;
        this.parents=parentsId;
        this.branch=branch;
        this.blobs=blobs;
        ZonedDateTime now=ZonedDateTime.now();
        this.timestamp=now.format(DateTimeFormatter.ofPattern(
                "EEE MMM d HH:mm:ss yyyy xxxx",Locale.ENGLISH));
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
        String parentToString= Arrays.toString(parents);
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

    public String getTimestamp() {
        return timestamp;
    }

    public String[] getParents() {
        return parents;
    }

    public String getBranch() {
        return branch;
    }

    public TreeMap<String, Blob> getBlobs() {
        return blobs;
    }

    public File getFile() {
        return file;
    }
}
