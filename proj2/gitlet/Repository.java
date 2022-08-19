package gitlet;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * The current branch name.
     */
    private final Lazy<String> currentBranch=lazy(()->{
       String HEADFileContent=readContentsAsString(HEAD);
       return HEADFileContent.replace(HEADFileContent,"");
    });

    /**
     * The commit that HEAD points to. The current commit.
     */
    private final Lazy<Commit> HEADCommit = lazy(() -> getBranchHeadCommit(currentBranch.get()));

    /**
     * The staging area instance. Initialized in the constructor.
     */
    private final Lazy<StagingArea> stagingArea = lazy(() -> {
        StagingArea s = INDEX.exists()
                ? StagingArea.fromFile()
                : new StagingArea();
        s.setTracked(HEADCommit.get().getTracked());
        return s;
    });

    /**
     * Get commit of head of branch with branch name. Helper of #HEADCommit
     * @param branchName
     * @return Commit obj
     */
    private static Commit getBranchHeadCommit(String branchName){
        File branchHeadFile=getBranchHeadFile(branchName);
        return getBranchHeadCommit(branchHeadFile);
    }

    /**
     * Helper of .getBranchHeadCommit(String branchName)
     * @param branchHeadFile
     * @return
     */
    private static Commit getBranchHeadCommit(File branchHeadFile){
        String headCommitId=readContentsAsString(branchHeadFile);
        return Commit.getCommitFromFile(headCommitId);
    }

    /**
     * Helper of .getBranchHeadCommit(String branchName)
     * @param branchName
     * @return
     */
    private static File getBranchHeadFile(String branchName){
        return join(BRANCH_HEADS_DIR,branchName);
    }


    /**
     * Initialize a repo at the current directory.
     *
     * .gitlet/          -- top level folder for all persistent data in your lab12 folder
     *    - objects/     -- folder containing all persistent data for blobs and commits
     *    - refs/        -- stores pointers into commit objects in that data
     *          - heads/ -- pointers to the heads of branch
     *    - HEAD         -- the branch you currently have checked out (prefix+branch name)
     *    - index        -- stores stagingArea
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
        setBranchHeadCommit(DEFAULT_BRANCH_NAME,initCommit.getHashId());
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

    /**
     * Perform "add" command.
     * @param fileName
     */
    public void add(String fileName){
        File file=getFileFromCWD(fileName);
        if(!file.exists()){
            exit("File does not exist.");
        }
        if(stagingArea.get().add(file)){
            stagingArea.get().save();
        }
    }

    /**
     * Perform "commit" command.
     * @param msg
     */
    public void commit(String msg){
       commit(msg,null);
    }

    /**
     * Perform a commit with message and two parents.
     * @param msg          Commit msg
     * @param secondParent second parent Commit SHA1 id.
     */
    private void commit(String msg,String secondParent){
        if(stagingArea.get().isClean()){
            exit("No changing added to the commit.");
        }
        Map<String,String> newTrackedFileMap=stagingArea.get().getTracked();
        stagingArea.get().save();
        List<String> parents=new ArrayList<>();
        parents.add(HEADCommit.get().getHashId());
        if(secondParent!=null){
            parents.add(secondParent);
        }
        Commit newCommit=new Commit(msg,parents,newTrackedFileMap);
        newCommit.save();
        setBranchHeadCommit(currentBranch.get(),newCommit.getHashId());
    }

    /**
     * Perform "rm" command.
     * @param fileName
     */
    public void remove(String fileName){
        File file = getFileFromCWD(fileName);
        if(stagingArea.get().remove(file)){
            stagingArea.get().save();
        }else{
            exit("No reason to remove the file.");
        }
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
            exit("Not in an initialized Gitlet directory.");
        }
    }

}
