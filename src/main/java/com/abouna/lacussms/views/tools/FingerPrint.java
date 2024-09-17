package com.abouna.lacussms.views.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FingerPrint {
    private FingerPrint(){}

    public static boolean checkFingerprint() {

        return true;
    }

    public static String getEncodedMacAddress() {
        return Utils.hacher("SHA-256", Objects.requireNonNull(Utils.getMacAddress()));
    }

    public static void writeByte(String message) {
        byte[] allBytes = message.getBytes(StandardCharsets.UTF_8);
        try {
            //Files.write(Paths.get("C:\\Users\\eabou\\.lacuss\\fingerprint"), allBytes);
            FileOutputStream fos = new FileOutputStream("C:\\Users\\eabou\\.lacuss\\fingerprint");
            fos.write(allBytes, 0, allBytes.length);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBinary(String message) throws IOException {
        File outFile = new File ("C:\\Users\\eabou\\.lacuss\\output.bin");
        FileOutputStream outStream = new FileOutputStream (outFile);
        DataOutputStream output = new DataOutputStream(outStream);

        try
        {
            output.writeUTF (message);
        }
        catch (Exception e)
        {
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            outStream.close();
        }
    }

    public static String readBinaryFile(String path) throws IOException {
        File inFile = new File (path);
        FileInputStream inStream = new FileInputStream (inFile);
        DataInputStream input = new DataInputStream (inStream);
        String text = null;

        try
        {
            while (true)
            {
                text = input.readUTF();

            }
        }
        catch (EOFException e)
        {
            return text;
        }
        catch (Exception e)
        {
            System.out.println (e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
        finally
        {
            inStream.close();
        }
        return null;
    }
}
