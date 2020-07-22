package utill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import java.util.Base64;

public class HelpFunctions {
    public static byte[] readFileReturnBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] base64Decode(String str) {
        Base64.Decoder x = Base64.getDecoder();
        return x.decode(str);
    }

    public static String base64Encode(byte[] bytes) {
        byte[] res = Base64.getEncoder().encode(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte r : res) {
            sb.append((char) r);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(base64Decode(base64Encode(readFileReturnBytes(Paths.get("src", "jni_sec", "CommandExecution.class"))))));
        System.out.println(Arrays.toString(readFileReturnBytes(Paths.get("src", "jni_sec", "CommandExecution.class"))));
    }
}
