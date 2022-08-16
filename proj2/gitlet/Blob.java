package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/**
 *  represent a gitlet Blob object.
 *  will be called by "gitlet add"
 */
public class Blob implements Serializable {

    /** the global unique SHA-1 id of blob */
    private final String hashID;

    /**
     * The source file from constructor.
     * source points to file in CWD.
     */
    private final File source;

    /**  store the content which read from file as byte[] */
    private final byte[] content;

    /**
     * The file of this instance with the path generated from SHA1 id.
     * File points to file in .gitlet/objects
     */
    private final File file;

    /**
     * Constructor of Blob.
     * @param sourceFile
     */
    public Blob(File sourceFile){
        this.source=sourceFile;
        this.content=readContents(source);
        String filePath=sourceFile.getPath();
        this.hashID=sha1(filePath,content);
        //尚未持久化，只是指向file对象
        this.file=getObjectFromFile(hashID);
    }

    /**
     * Save blob object into ./gitlet/objects
     */
    public void save(){
        saveObjectToFile(file,this);
    }

    /**
     * Get a blob obj from file with uid.
     * @param hashID
     * @return
     */
    public Blob fromFile(String hashID){
        return readObject(getObjectFromFile(hashID),Blob.class);
    }

    public String getHashID() {
        return hashID;
    }

    public File getSource() {
        return source;
    }

    public byte[] getContent() {
        return content;
    }

    public File getFile() {
        return file;
    }
}
