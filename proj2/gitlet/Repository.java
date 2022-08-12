package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author tuysss
 */
public class Repository {
    /** Default branch name.     */
    private static final String DEFAULT_BRANCH_NAME = "master";

    /** HEAD ref prefix.     */
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs/heads/";

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The objects directory.     */
    public static final File OBJECTS_DIR=join(GITLET_DIR,"objects");

    /** The refs directory.     */
    public static final File REFS_DIR=join(GITLET_DIR,"refs");

    /** The refs/heads directory.     */
    public static final File BRANCH_HEADS_DIR=join(REFS_DIR,"heads");

    /** The HEAD file, store the uid of the current branch head.     */
    public static final File HEAD=join(GITLET_DIR,"HEAD");

    /** The index file, store the mapping: commit-blobs.     */
    public static final File INDEX=join(GITLET_DIR,"index");


    /**
     * Initialize a repo at the current directory.
     *
     * .gitlet/          -- top level folder for all persistent data in your lab12 folder
     *    - objects/     -- folder containing all persistent data for blobs and commits
     *    - refs/        -- stores pointers into commit objects in that data
     *          - heads/ -- pointers to the heads of lists of commits
     *    - HEAD         -- points to the branch you currently have checked out
     *    - index        -- stores staging area
     */
     public static void init(){
        if(GITLET_DIR.exists()){
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        REFS_DIR.mkdirs();
        BRANCH_HEADS_DIR.mkdirs();
        setCurrentBranch(DEFAULT_BRANCH_NAME);
        createInitialCommit();
    }

    /**
     *  write branch( fixed prefix + customised name) into HEAD file.
     * @param branchName customised name from keyboard
     */
    private static void setCurrentBranch(String branchName){
         writeContents(HEAD,HEAD_BRANCH_REF_PREFIX+branchName);
    }

    /**
     * Helper class of #init()
     * the global shared same commit for repo init.
     */
    private static void createInitialCommit(){
        Commit initCommit=new Commit();
        initCommit.save();
        setBranchHeadCommit(DEFAULT_BRANCH_NAME,initCommit.getHashID());
    }

    /**
     * Helper of #createInitialCommit
     * @param branchHeadFile
     * @param commitId
     */
    private static void setBranchHeadCommit(File branchHeadFile,String commitId){
        writeContents(branchHeadFile,commitId);
    }

    /**
     * Helper of #createInitialCommit
     * @param branchName
     * @param commitId
     */
    private static void setBranchHeadCommit(String branchName,String commitId){
        File branchHeadFile =getBranchFile(branchName);
        setBranchHeadCommit(branchHeadFile,commitId);
    }

    /**
     * branch file is named by its name.
     * @param branchName
     * @return
     */
    private static File getBranchFile(String branchName){
        return join(BRANCH_HEADS_DIR,branchName);
    }

}
