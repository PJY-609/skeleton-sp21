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
                Repository.addFile(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                validateNumArgs(args, 2);
                Repository.makeCommit(args[1]);
                break;
            case "checkout":
                validateNumArgs(args, 2, 3, 4);
                validateCheckoutCommand(args);
                Repository.checkout(args);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.printLog();
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
                break;
        }
    }

    private static void validateCheckoutCommand(String[] args){
        boolean noConnectionSign0 = args.length == 2 && !args[0].equals("--");
        boolean noConnectionSign1 = args.length == 3 && !args[1].equals("--");

        if(noConnectionSign0 || noConnectionSign1){
            Utils.message("No command with that name exists.");
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
