package info.kgeorgiy.ja.podkorytov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MyFileVisitor implements FileVisitor<Path> {
    private BufferedWriter writer;

    private final boolean jenkins;

    public MyFileVisitor(Path output, boolean jenkins) throws IOException {
        this.writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
        this.jenkins = jenkins;
    }

    public void close() throws IOException {
        writer.close();
    }

    public void writeErrorHash(String filename) throws IOException {
        writer.write(String.format(jenkins ? "%08x" : "%040x", 0) + " " + filename + "\n");
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Objects.requireNonNull(dir);
        return FileVisitResult.CONTINUE;
    }

    private BaseHasher getHasher(BufferedInputStream input, String filename) throws NoSuchAlgorithmException {
        if (jenkins) {
            return new JenkinsHash(input, writer, filename);
        }
        return new SHAHasher(input, writer, filename);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException,
            SecurityException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(attrs);
        try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(file))) {
            getHasher(input, file.toString()).calculateFileHash();
        } catch (IOException | InvalidPathException e) {
            writeErrorHash(file.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exc != null) {
            writeErrorHash(file.toString());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Objects.requireNonNull(dir);
        if (exc != null) {
            throw exc;
        }
        return FileVisitResult.CONTINUE;
    }
}
