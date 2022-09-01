package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static gitlet.Utils.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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

    public static final File STAGE=join(GITLET_DIR,"SATGE");

    public static final File STAGING_DIR=join(GITLET_DIR,"staging");



     public void init(){
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
        writeObject(STAGE,new Stage());
        STAGING_DIR.mkdirs();

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

    /**
     * 1. Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
     * 2. If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there
     * (as can happen when a file is changed, added, and then changed back to it’s original version).
     * 3. The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     * @param filename
     */
    public void add(String filename){
        File file=join(CWD,filename);
        if(!file.exists()){
            System.out.println("File does exists.");
            System.exit(0);
        }

        Commit head = getHead();
        Stage stage = readStage();

        String headBlobId=head.getBlobs().getOrDefault(filename,"");
        String stageAddedId=stage.getAdded().getOrDefault(filename,"");

        Blob blob = new Blob(filename, CWD);
        String blobId = blob.getId();

        if(blobId.equals(headBlobId)){
            //no need to add to stage
            if(blobId.equals(stageAddedId)){
                //delete the file from staging
                join(STAGING_DIR,stageAddedId).delete();
                stage.getAdded().remove(stageAddedId);
                stage.getRemoved().remove(stageAddedId);
                writeStage(stage);
            }
        }else if(!blobId.equals(stageAddedId)){
            // update staging
            // del original, add the new version
            if(!stageAddedId.equals("")){
                join(STAGING_DIR,stageAddedId).delete();
            }
        }

        writeObject(join(STAGING_DIR, blobId), blob);
        // change stage added files
        stage.addFile(filename, blobId);
        writeStage(stage);
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     * @param filename
     */
    public void rm(String filename){
        File file=join(CWD,filename);

        Commit head=getHead();
        Stage stage = readStage();

        String headId = head.getBlobs().getOrDefault(filename, "");
        String stageId = stage.getAdded().getOrDefault(filename, "");

        if(headId.equals("")&&stageId.equals("")){
            System.out.println("No reason to remove the untracked file.");
            System.exit(0);
        }

        //Unstage the file if it is currently staged for addition.
        if(!stageId.equals("")){
            stage.getAdded().remove(stageId);
        }else{
            //stage it for removal.
            stage.getRemoved().add(stageId);
        }

        Blob blob=new Blob(filename,CWD);
        String blobId = blob.getId();
        // If the file is tracked in the current commit
        if(blob.exists()&&blobId.equals(headId)){
            //remove the file from the working directory.
            restrictedDelete(blobId);
        }
    }

    public void commit(String message){
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        Commit head = getHead();
        commitWith(message, List.of(head));
    }

    /**
     * Helper Functions
     */

    private void commitWith(String message, List<Commit> parents) {
        Stage stage = readStage();
        // If no files have been staged, abort.
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit commit = new Commit(message, parents, stage);
        clearStage(stage);
        writeCommitToFile(commit);

        String commitId = commit.getId();
        String branchName = getHeadBranchName();
        File branch = getBranchFile(branchName);
        writeContents(branch, commitId);
    }


    public void log() {
        StringBuffer sb = new StringBuffer();
        Commit commit = getHead();
        while (commit != null) {
            sb.append(commit.getCommitAsString());
            commit = getCommitFromId(commit.getFirstParentId());
        }

        System.out.print(sb);
    }

    public void global_log() {
        StringBuffer sb = new StringBuffer();
        List<String> filenames = plainFilenamesIn(COMMITS_DIR);
        for (String filename : filenames) {
            Commit commit = getCommitFromId(filename);
            sb.append(commit.getCommitAsString());
        }
        System.out.println(sb);
    }


    public void find(String message){
        StringBuffer sb = new StringBuffer();
        List<String> filenames = plainFilenamesIn(COMMITS_DIR);
        for (String filename : filenames) {
            Commit commit = getCommitFromId(filename);
            if(message.equals(commit.getMessage())){
                sb.append(commit.getId()).append("\n");
            }
        }
        if (sb.length() == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        System.out.println(sb);
    }


    /**
     * moving all staging dir's blob file to blobs dir.
     *
     * @param stage
     */
    private void clearStage(Stage stage) {
        File[] files = STAGING_DIR.listFiles();
        if (files == null) {
            return;
        }
        Path targetDir = BLOBS_DIR.toPath();
        for (File file : files) {
            Path source = file.toPath();
            try {
                Files.move(source, targetDir.resolve(source.getFileName()), REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeStage(new Stage());
    }


    private Commit getHead(){
        String branchName = getHeadBranchName();
        File branchFile = getBranchFile(branchName);
        Commit head = getCommitFromBranchFile(branchFile);

        if (head == null) {
            System.out.println("error! cannot find HEAD!");
            System.exit(0);
        }

        return head;
    }



    private String getHeadBranchName(){
        return readContentsAsString(HEAD);
    }

    private File getBranchFile(String branchName){
        return join(BRANCH_HEADS_DIR,branchName);
    }

    private Commit getCommitFromBranchFile(File branchFile){
        String id = readContentsAsString(branchFile);
        return getCommitFromId(id);
    }

    private Commit getCommitFromId(String CommitId){
        File file = join(COMMITS_DIR, CommitId);
        if("null".equals(CommitId)||!file.exists()){
            return null;
        }
        return readObject(file,Commit.class);
    }

    private Stage readStage(){
        return readObject(STAGE,Stage.class);
    }

    private void writeStage(Stage stage){
        writeObject(STAGE,stage);
    }


    private static void writeCommitToFile(Commit commit){
         File file=join(COMMITS_DIR,commit.getId());
         writeObject(file,commit);
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

    /**
     * check things
     */
    void checkIfInitDirectoryExists() {
        if (!GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    void checkCommandLength(int actual, int expected) {
        if (actual != expected) {
            messageIncorrectOperands();
        }
    }

    void checkEqual(String actual, String expected) {
        if (!actual.equals(expected)) {
            messageIncorrectOperands();
        }
    }

    void messageIncorrectOperands() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }
}
