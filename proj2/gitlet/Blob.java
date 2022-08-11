package gitlet;

import java.io.File;
import java.io.Serializable;

/**
 *  represent a gitlet Blob object.
 *  will be called by "gitlet add"
 */
public class Blob implements Serializable {

    /** the global unique SHA-1 id of blob */
    private final String hashID;

    /** the name of the modified file */
    private final String name;

    /**  store the content which read from file as byte[] */
    private final byte[] content;

    /**  store the content which read from file as String */
    private final String contentAsString;

    /**
     * Constructor of Blob
     * @param name name of the modified file
     */
    public Blob(String name){
        File file=new File(name);
        this.name=name;
        this.content=Utils.readContents(file);
        this.contentAsString=Utils.readContentsAsString(file);
        hashID=Utils.sha1(name,contentAsString);
    }

    public String getHashID() {
        return hashID;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        return contentAsString;
    }
}
