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

    /**
     * hex转byte数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }

    /**
     * byte数组转hex
     *
     * @param bytes byte[]
     * @return
     */
    public static String byteToHexString(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (byte aByte : bytes) {
            strHex = Integer.toHexString(aByte & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(base64Decode(base64Encode(readFileReturnBytes(Paths.get("src", "jni_sec", "CommandExecution.class"))))));
        System.out.println(Arrays.toString(readFileReturnBytes(Paths.get("src", "jni_sec", "CommandExecution.class"))));
    }
}
