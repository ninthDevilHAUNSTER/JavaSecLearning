package utill;

import java.io.IOException;

//D:\java_box\java_sec_learning\src (master -> origin)
//λ D:\WINDOWS.X64_180000_db_home\jdk\bin\javah.exe -classpath D:\java_box\java_sec_learning\src\ -jni utill.Evil

public class Evil {
    static {
        System.loadLibrary("evi1");
    }

    public native String exec(String cmd);

    public static void main(String[] args) {

    }
}
