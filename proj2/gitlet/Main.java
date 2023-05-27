package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?

        validateNonEmptyArgs(args);

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.initialize();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.addFile(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.makeCommit(args[1]);
                break;
            case "checkout":
                validateNumArgs(args, 2, 3, 4);
                Repository.validateInitialization();
                validateCheckoutCommand(args);
                Repository.checkout(args);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.validateInitialization();
                Repository.printLog();
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.removeFile(args[1]);
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.validateInitialization();
                Repository.printGlobalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.findCommitMessage(args[1]);
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.createBranch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                Repository.validateInitialization();
                Repository.reset(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.validateInitialization();
                Repository.printStatus();
                break;
            case "merge":
                validateNumArgs(args, 2);
                Repository.mergeBranches(args[1]);
                break;
            case "add-remote":
                validateNumArgs(args, 3);
                Repository.addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                validateNumArgs(args, 2);
                Repository.removeRemote(args[1]);
                break;
            case "push":
                validateNumArgs(args, 3);
                Repository.pushToRemote(args[1], args[2]);
                break;
            case "fetch":
                validateNumArgs(args, 3);
                Repository.fetchFromRemote(args[1], args[2]);
                break;
            case "pull":
                validateNumArgs(args, 3);
                Repository.pullFromRemote(args[1], args[2]);
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
                break;
        }
    }

    private static void validateCheckoutCommand(String[] args){
        boolean noConnectionSign0 = args.length == 3 && !args[1].equals("--");
        boolean noConnectionSign1 = args.length == 4 && !args[2].equals("--");

        if(noConnectionSign0 || noConnectionSign1){
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void validateNumArgs(String[] args, int... nums) {
        boolean matched = false;

        for(int n: nums){
            if (args.length == n){
                matched = true;
                break;
            }
        }

        if (!matched) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void validateNonEmptyArgs(String[] args){
        if (args.length == 0){
            Utils.message("Please enter a command.");
            System.exit(0);
        }
    }
}
