package gitlet;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    public static void main(String[] args) {
        if(args.length==0){
            exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args,1);
                Repository.init();
            case "add":
                Repository.checkWorkingDir();
                validateNumArgs(args,2);
                String fileName=args[1];
                new Repository().add(fileName);
                break;
            case "commit":
                Repository.checkWorkingDir();
                validateNumArgs(args,2);
                String msg=args[1];
                new Repository().commit(msg);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    private static void validateNumArgs(String[] args,int n){
        if(args.length!=n){
            exit("Incorrect operands.");
        }
    }
}
