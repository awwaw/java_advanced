package info.kgeorgiy.ja.podkorytov.walk;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAHasher extends BaseHasher {

    private MessageDigest md;

    public SHAHasher(BufferedInputStream input, BufferedWriter output, String filePath) throws NoSuchAlgorithmException {
        super(input, output, filePath);
        md = MessageDigest.getInstance("SHA-1");
    }

    public void calculateFileHash() throws IOException, SecurityException {
        byte[] buffer = new byte[4096];
        int wasRead = input.read(buffer);
        while (wasRead != -1) {
            md.update(buffer, 0, wasRead);
            wasRead = input.read(buffer);
        }
        byte[] result = md.digest();
        output.write(String.format("%040x", new BigInteger(1, result)) + " " + filePath + "\n");
    }
}
