package gitlet;

import java.util.Arrays;
import java.util.Comparator;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    public static void main(String[] args) {
        if(args.length==0){
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":{
                validateNumArgs(args,1);
                Repository.init();
                break;
            }
            case "add": {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().add(fileName);
                break;
            }
            case "commit":{
                Repository.checkWorkingDir();
                validateNumArgs(args,2);
                String message=args[1];
                if (message.length() == 0) {
                    exit("Please enter a commit message.");
                }
                new Repository().commit(message);
                break;
            }
            case "rm":{
                Repository.checkWorkingDir();
                validateNumArgs(args,2);
                String fileName=args[1];
                new Repository().remove(fileName);
                break;
            }
            case "log":{
                Repository.checkWorkingDir();
                validateNumArgs(args,1);
                new Repository().log();
                break;
            }
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    private static void validateNumArgs(String[] args,int n){
        if(args.length!=n){
            message("Incorrect operands.");
            System.exit(0);
        }
    }
}
