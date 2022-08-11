package gitlet;

// TODO: any imports you need here

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;

import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 */
public class Commit {

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
    }

    /**
     *
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
    }
    private String generateHashId(){
        String BlobToString="";
        String parentToString= Arrays.toString(parents);
        if(blobs!=null){
            BlobToString=blobs.toString();
        }
        String contentOfHash=message+timestamp+BlobToString+parentToString;
        return sha1(contentOfHash);
    }




}
