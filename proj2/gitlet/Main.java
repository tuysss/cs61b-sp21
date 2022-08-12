package gitlet;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if(args.length==0){
            exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args,1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
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
