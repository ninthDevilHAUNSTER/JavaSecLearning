package java_basic;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


public class JavaFileSystem {
    /**
     * java.io.FileInputStream.readBytes(FileInputStream.java:219)
     * java.io.FileInputStream.read(FileInputStream.java:233)
     * java_basic.JavaFileSystem.main(FileInputStreamDemo.java:27)
     *
     * @throws IOException
     */
    public static void ReadFileDemo() throws IOException {
        File file = new File("README.md");
        FileInputStream fis = new FileInputStream(file);
        int a = 0;
        byte[] buffer = new byte[4096];

        // 不得不承认 JAVA的原生读取方法实在是复杂
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 写到内存中

        while ((a = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, a);
        }
        System.out.println(baos.toString());
    }

    /**
     * FileSystem 写文件
     * @throws IOException
     */

    public static void WriteFileDemo() throws IOException {
        // 定义写入文件路径
        File file = new File(Paths.get(".", "A.txt").toString());

        // 定义待写入文件内容
        String content = "Hello World.";

        // 创建FileOutputStream对象
        FileOutputStream fos = new FileOutputStream(file);

        // 写入内容二进制到文件
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
    }

    /**
     * FileSystem 读写文件
     * @throws IOException
     */

    public static void RandomAccessFileDemo() throws IOException {
        File file = new File(Paths.get(".", "A.txt").toString());
        // 创建RandomAccessFile对象,rw表示以读写模式打开文件，一共有:r(只读)、rw(读写)、
        // rws(读写内容同步)、rwd(读写内容或元数据同步)四种模式。
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        int _a = 0;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((_a = raf.read(buffer)) != -1) {
            baos.write(buffer, 0, _a);
        }
        System.out.println(baos.toString());


        raf.write("\nShaobao niubi ".getBytes());
        raf.close();

    }

    /**
     * Nio 读文件
     */
    public static void NioReadFileDemo() {
        Path file_path = Paths.get(".", "A.txt");
        try {
            byte[] bytes = Files.readAllBytes(file_path); // 返回bytes
            System.out.println(new String(bytes));

            List<String> strings = Files.readAllLines(file_path); // 返回String List
            System.out.println(Arrays.toString(strings.toArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Nio 写文件
     */

    public static void NioWriteFileDemo() {
        Path file_path = Paths.get(".", "A.txt");
        Path file_path2 = Paths.get(".", "B.txt");

        try {
            Files.write(file_path, "\nshaobaoniubi!".getBytes()); // 写bytes
            Files.writeString(file_path, "\nshaobaoniubi!!", StandardOpenOption.APPEND); // 写String(追加模式）
//            StandardOpenOption 中还是有很多方法的。
            Files.copy(file_path, file_path2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * JAVA 遍历文件
     */
    public static void DirWalk() {
        String dirStr = Paths.get(".", "src", "JavaFileSystem.java").toString();
        if (dirStr != null) {
            File dir = new File(dirStr);
            if (dir.isDirectory()) {
                File[] dirs = dir.listFiles();
                assert dirs != null;
                for (File file : dirs) {
                    System.out.println(file.getName());
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        //
//        ReadFileDemo();
//        WriteFileDemo();
//        RandomAccessFileDemo();
//        NioReadFileDemo();
//        NioWriteFileDemo();
        DirWalk();
    }
}
