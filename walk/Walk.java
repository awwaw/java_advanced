package info.kgeorgiy.ja.podkorytov.walk;

import static info.kgeorgiy.ja.podkorytov.walk.RecursiveWalk.printError;

public class Walk {
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            printError("Invalid amount of arguments: see usage for more details");
            return;
        }
        if (args[0] == null || args[1] == null) {
            printError("You must enter both input and output filenames, see usage for more details");
            return;
        }
        RecursiveWalk.walk(args[0], args[1], 0, true);
    }
}
