package utill;

import java.io.IOException;

/**
 * Creator: yz
 * Date: 2019/12/18
 */
public class CMD {
    public static Process exec(String cmd) throws IOException {
        return Runtime.getRuntime().exec(cmd);
    }

}