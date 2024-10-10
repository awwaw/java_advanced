package info.kgeorgiy.ja.podkorytov.walk;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class BaseHasher {

    protected BufferedInputStream input;
    protected BufferedWriter output;

    protected final String filePath;

    public BaseHasher(BufferedInputStream input, BufferedWriter output, String filePath) {
        this.input = input;
        this.output = output;
        this.filePath = filePath;
    }

    abstract void calculateFileHash() throws IOException,
            SecurityException,
            NoSuchAlgorithmException;
}
