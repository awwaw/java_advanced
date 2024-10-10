package info.kgeorgiy.ja.podkorytov.walk;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;

public class JenkinsHash extends BaseHasher {
    public JenkinsHash(BufferedInputStream input, BufferedWriter output, String filePath) {
        super(input, output, filePath);
    }

    public void calculateFileHash() throws IOException, SecurityException {
        int hash = 0;
        byte[] buffer = new byte[4096];
        int wasRead = input.read(buffer);
        while (wasRead != - 1) {
            for (int i = 0; i < wasRead; i++) {
                hash += buffer[i] & 0xff;
                hash += hash << 10;
                hash ^= hash >>> 6;
            }
            wasRead = input.read(buffer);
        }

        hash += hash << 3;
        hash ^= hash >>> 11;
        hash += hash << 15;

        output.write(String.format("%08x", hash) + " " + filePath + "\n");
    }
}
