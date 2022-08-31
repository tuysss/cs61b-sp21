package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * A Commit is a snapshot of the entire project at one point.
 */
public class Commit implements Serializable{
    private String message;
    private List<String> parents;
    private Date timestamp;
    /** The files this Commit tracks. filename-id.*/
    private Map<String,String> blobs;
    private String id;

    /**
     *  invoked by "init" command
     */
    public Commit(){
        this.message="init commit";
        this.parents=new ArrayList<>();
        this.timestamp=new Date(0);
        this.blobs=new HashMap<>();
        this.id=sha1(message,parents,timestamp,blobs);
    }

    /**
     *  Invoked by "commit" command.
     * @param message
     * @param parents
     * @param stage
     */
    public Commit(String message,List<Commit> parents,Stage stage){
        this.message=message;
        this.parents=new ArrayList<>();
        for (Commit parent : parents) {
            this.parents.add(parent.getId());
        }
        for (Map.Entry<String, String> entry : stage.getAdded().entrySet()) {
            String filename=entry.getKey();
            String blobId=entry.getValue();
            blobs.put(filename,blobId);
        }
        for (String filename : stage.getRemoved()) {
            blobs.remove(filename);
        }
    }

    public String getFirstParent(){
        if(parents.isEmpty()){
            return "null";
        }
        return parents.get(0);
    }

    private String getTimestampAsString(){
        DateFormat dateFormat=new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z",Locale.ENGLISH);
        return dateFormat.format(timestamp);
    }


    public String getMessage() {
        return message;
    }

    public List<String> getParents() {
        return parents;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getId() {
        return id;
    }

    public String getCommitAsString(){
        StringBuffer sb = new StringBuffer();
        sb.append("===\n");
        sb.append("commit " + this.id + "\n");
        if (parents.size() == 2) {
            sb.append("Merge: " + parents.get(0).substring(0, 7) + " " + parents.get(1).substring(0, 7) + "\n");
        }
        sb.append("Date: " + this.getTimestampAsString() + "\n");
        sb.append(this.message + "\n\n");
        return sb.toString();
    }
}
