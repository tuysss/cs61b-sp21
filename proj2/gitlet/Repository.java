package gitlet;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author tuysss
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The objects directory.     */
    public static final File OBJECTS_DIR=join(GITLET_DIR,"objects");

    /** The commits directory. */
    public static final File COMMITS_DIR=join(OBJECTS_DIR,"commits");

    /** The blobs directory. */
    public static final File BLOBS_DIR=join(OBJECTS_DIR,"blobs");

    /** The refs directory.     */
    public static final File REFS_DIR=join(GITLET_DIR,"refs");

    /** The refs/heads directory,defined by init-commit-id     */
    public static final File BRANCH_HEADS_DIR=join(REFS_DIR,"heads");

    /** pointer to latest version     */
    public static final File HEAD=join(GITLET_DIR,"HEAD");


    /**
     * Initialize a repo at the current directory.
     *
     * .gitlet/          -- top level folder for all persistent data in your lab12 folder
     *    - objects/     -- folder containing all persistent data for blobs and commits
     *          - blobs/
     *          - commits/
     *    - refs/        -- stores pointers into commit objects in that data
     *          - heads/ -- pointers to the heads of branch
     *    - HEAD         -- the latest version.
     *    - index        -- stores stagingArea
     */
     public static void init(){
        if(GITLET_DIR.exists()){
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        REFS_DIR.mkdirs();
        BRANCH_HEADS_DIR.mkdirs();

        // initial commit
        Commit initCommit=new Commit();
        writeCommitToFile(initCommit);
        String id=initCommit.getId();

        // create default branch: master
        String branchName="master";
        File master=join(BRANCH_HEADS_DIR,branchName);
        writeContents(master,id);

        //create HEAD    ???
         writeContents(HEAD,branchName);
    }










    private static void writeCommitToFile(Commit commit){
         File file=join(COMMITS_DIR,commit.getId());
         writeObject(file,commit);
    }

    /**
     * Get a file instance from CWD by file name.
     * @param fileName
     * @return File instance
     */
    private File getFileFromCWD(String fileName){
        return Paths.get(fileName).isAbsolute()?
                new File(fileName):
                join(CWD,fileName);
    }

    /**
     *  Pre check for git preforms.
     */
    public static void checkWorkingDir(){
        if(!GITLET_DIR.exists()||!GITLET_DIR.isDirectory()){
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
