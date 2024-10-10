package info.kgeorgiy.ja.podkorytov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class RecursiveWalk {
    private static final String USAGE = "Correct usage: walk <input file> <output file> (optional)<mode>";

    public static void printError(final String error) {
        System.out.println(error);
        System.out.println(USAGE);
    }

    public static void main(String[] argv) {
        if (argv != null && argv.length == 3) {
            advancedWalk(argv);
            return;
        }
        if (argv == null || argv.length != 2) {
            printError("Invalid amount of arguments: see usage for more details");
            return;
        }
        if (argv[0] == null || argv[1] == null) {
            printError("You must enter both input and output filenames, see usage for more details");
            return;
        }
        walk(argv[0], argv[1], Integer.MAX_VALUE, true);
    }

    public static void advancedWalk(String[] args) {
        if (args[0] == null || args[1] == null || args[2] == null) {
            printError("You must enter both input and output filenames, see usage for more details");
            return;
        }
        if (!args[2].equals("jenkins") && !args[2].equals("sha-1")) {
            printError("Hash method must be either 'jenkins' or 'sha-1'");
            return;
        }
        boolean jenkins = args[2].equals("jenkins");
        walk(args[0], args[1], Integer.MAX_VALUE, jenkins);
    }

    public static void walk(final String in, final String out, int depth, boolean jenkins) {
        try {
            List<String> lines = Files.readAllLines(Path.of(in), StandardCharsets.UTF_8);
            Path output = Path.of(out);
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            MyFileVisitor visitor = new MyFileVisitor(output, jenkins);
            for (String filename : lines) {
                try {
                    Path path = Path.of(filename);
                    Files.walkFileTree(path, Collections.emptySet(), depth, visitor);
                } catch (InvalidPathException e) {
                    visitor.writeErrorHash(filename);
                    printError(e.getMessage());
                } catch (IOException | SecurityException e) {
                    printError(e.getMessage());
                }
            }
            visitor.close();
        } catch (InvalidPathException exception) {
            printError("Couldn't find input file: " + exception.getMessage());
        } catch (IOException | SecurityException exception) {
            printError("Can't open input file " + exception.getMessage());
        }
    }
}
