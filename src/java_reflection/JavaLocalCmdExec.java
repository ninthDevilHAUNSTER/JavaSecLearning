package java_reflection;

import org.apache.commons.io.IOUtils;
import utill.HelpFunctions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class JavaLocalCmdExec {
    /**
     * CommandExecution的class_name
     */
    private static final String COMMAND_CLASS_NAME = "jni_sec.CommandExecution";

    /**
     * Arrays.toString(readFileReturnBytes(Paths.get("src", "jni_sec", "CommandExecution.class")))
     */
    private static final byte[] COMMAND_CLASS_BYTES = new byte[]{
            -54, -2, -70, -66, 0, 0, 0, 58, 0, 15, 10, 0, 2, 0, 3, 7, 0, 4, 12, 0, 5, 0, 6, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110,
            103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 7, 0, 8, 1, 0, 24, 106, 110,
            105, 95, 115, 101, 99, 47, 67, 111, 109, 109, 97, 110, 100, 69, 120, 101, 99, 117, 116, 105, 111, 110, 1, 0, 4, 67, 111,
            100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 4, 101, 120, 101, 99, 1, 0,
            38, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47,
            108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0,
            21, 67, 111, 109, 109, 97, 110, 100, 69, 120, 101, 99, 117, 116, 105, 111, 110, 46, 106, 97, 118, 97, 0, 33, 0, 7, 0,
            2, 0, 0, 0, 0, 0, 2, 0, 1, 0, 5, 0, 6, 0, 1, 0, 9, 0, 0, 0, 29, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0,
            1, 0, 10, 0, 0, 0, 6, 0, 1, 0, 0, 0, 3, 1, 9, 0, 11, 0, 12, 0, 0, 0, 1, 0, 13, 0, 0, 0, 2, 0, 14
    };

    /**
     * base64Encode(readFileReturnBytes(Paths.get("src", "jni_sec", "CommandExecution.class"))))
     */
    private static final String COMMAND_JNI_FILE_BYTES = "yv66vgAAADoADwoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWBwAIAQAYam5pX3NlYy9Db21tYW5kRXhlY3V0aW9uAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEABGV4ZWMBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwEAClNvdXJjZUZpbGUBABVDb21tYW5kRXhlY3V0aW9uLmphdmEAIQAHAAIAAAAAAAIAAQAFAAYAAQAJAAAAHQABAAEAAAAFKrcAAbEAAAABAAoAAAAGAAEAAAADAQkACwAMAAAAAQANAAAAAgAO";

    static File getTempJNILibFile() {
        File jniDir = new File(System.getProperty("java.io.tmpdir"), "jni-lib");
        if (!jniDir.exists()) {
            jniDir.mkdir();
        }
        return new File(jniDir, "cmd.dll");
    }

    /**
     * 写JNI链接库文件
     *
     * @param base64 JNI动态库Base64
     * @return 返回是否写入成功
     */
    static File writeJNILibFile(String base64) throws IOException {
        if (base64 != null) {
            File jniFile = getTempJNILibFile();

            if (!jniFile.exists()) {
                byte[] bytes = HelpFunctions.base64Decode(base64);

                if (bytes != null) {
                    FileOutputStream fos = new FileOutputStream(jniFile);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                }
            }
        }
        return null;
    }

    /**
     * 利用JNI文件，调用动态链接库来执行结果
     *
     * @throws Exception
     */
    public static void execCommandJNIBase64() {
        String cmd = "whoami";// 定于需要执行的cmd

        try {
            ClassLoader loader = new ClassLoader(JavaLocalCmdExec.class.getClassLoader()) {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    try {
                        return super.findClass(name);
                    } catch (ClassNotFoundException e) {
                        return defineClass(COMMAND_CLASS_NAME, COMMAND_CLASS_BYTES, 0, COMMAND_CLASS_BYTES.length);
                    }
                }
            };
            writeJNILibFile(COMMAND_JNI_FILE_BYTES);
            File jniFile = getTempJNILibFile();
            try {
                // load命令执行类
                Class commandClass = loader.loadClass("jni_sec.CommandExecution");
                Method loadLibrary0Method = ClassLoader.class.getDeclaredMethod("loadLibrary0", Class.class, File.class);
                loadLibrary0Method.setAccessible(true);
                loadLibrary0Method.invoke(loader, commandClass, jniFile);
                String content = (String) commandClass.getMethod("exec", String.class).invoke(null, cmd);
                System.out.println(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 利用JNI文件，调用动态链接库来执行结果
     *
     * @throws Exception
     */
    public static void execCommandJNIFile() {
        String cmd = "whoami";// 定于需要执行的cmd

        try {
            ClassLoader loader = new ClassLoader(JavaLocalCmdExec.class.getClassLoader()) {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    try {
                        return super.findClass(name);
                    } catch (ClassNotFoundException e) {
                        return defineClass(COMMAND_CLASS_NAME, COMMAND_CLASS_BYTES, 0, COMMAND_CLASS_BYTES.length);
                    }
                }
            };

            // 测试时候换成自己编译好的lib路径
            File libPath = new File("D:\\java_box\\java_sec_learning\\lib\\cmd.dll");

            // load命令执行类
            Class<?> commandClass = loader.loadClass("jni_sec.CommandExecution");

            // 可以用System.load也加载lib也可以用反射ClassLoader加载,如果loadLibrary0
            // 也被拦截了可以换java.lang.ClassLoader$NativeLibrary类的load方法。
//            System.load("/Users/yz/IdeaProjects/javaweb-sec/javaweb-sec-source/javase/src/main/java/com/anbai/sec/cmd/libcmd.jnilib/libcmd.jnilib");
            Method loadLibrary0Method = ClassLoader.class.getDeclaredMethod("loadLibrary0", Class.class, File.class);
            loadLibrary0Method.setAccessible(true);
            loadLibrary0Method.invoke(loader, commandClass, libPath);

            String content = (String) commandClass.getMethod("exec", String.class).invoke(null, cmd);
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Windows 的 ProcessImpl 类中 上没有 fork and exec 这个方法，比较僵硬，我得去ubuntu上运行。
     *
     * @throws Exception
     */
    public static void execCommandProcessImplForkAndExec() throws Exception {

    }


    /**
     * ProcessImpl是更为底层的实现，Runtime和ProcessBuilder执行命令实际上也是调用了ProcessImpl这个类
     * 对于ProcessImpl类我们不能直接调用，但是可以通过反射来间接调用ProcessImpl来达到执行命令的目的。
     *
     * @throws IOException 1
     */
    public static void execCommandProcessImpl() throws IOException {
        try {
//            static
//            String cmdarray[],
//                         java.util.Map<String,String> environment,
//                         String dir,
//                         ProcessBuilder.Redirect[] redirects,
//                         boolean redirectErrorStream
            String[] cmds = new String[]{"cmd", "/c", "dir"}; // 这里不能写一起
            Class<?> clazz = Class.forName("java.lang.ProcessImpl");
            // 按照start的样子构造方法
            Method method = clazz.getDeclaredMethod("start", String[].class, Map.class, String.class, ProcessBuilder.Redirect[].class, boolean.class);
            method.setAccessible(true);
            Process e = (Process) method.invoke(null, cmds, null, ".", null, true);
            System.out.println(IOUtils.toString(e.getInputStream(), "GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 利用 ProcessBuilder 执行命令
     *
     * @throws IOException
     */
    public static void execCommandProcessBuilder() throws IOException {
        System.out.println(IOUtils.toString(new ProcessBuilder("cmd /c dir").start().getInputStream(), "GBK"));
    }

    /**
     * 利用 Runtime 执行命令
     *
     * @throws IOException
     */
    public static void execCommandRuntime() throws IOException {
        System.out.println(IOUtils.toString(Runtime.getRuntime().exec("cmd /c dir").getInputStream(), "GBK"));
    }

    /**
     * 动态调用 Runtime 执行命令
     * <p>
     * method.invoke(方法实例对象, 方法参数值，多个参数值用","隔开);
     * <p>
     * method.invoke的第一个参数必须是类实例对象，如果调用的是static方法那么第一个参数值可以传null，因为在java中调用静态方法是不需要有类实例的，因为可以直接类名.方法名(参数)的方式调用。
     * <p>
     * method.invoke的第二个参数不是必须的，如果当前调用的方法没有参数，那么第二个参数可以不传，如果有参数那么就必须严格的依次传入对应的参数类型。
     */
    public static void execCommandWithoutRuntime() {
        try {
            // 获取Runtime类对象
            String cmd = "cmd /c dir";
            // 获取 Runtime类

            Class<?> evil_class = Class.forName(new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 82, 117, 110, 116, 105, 109, 101}));
            // 获取 .getRuntime方法
            Method getRuntime = evil_class.getMethod(new String(new byte[]{103, 101, 116, 82, 117, 110, 116, 105, 109, 101}));
            // 执行 .getRuntime方法 由于这是个静态方法，无输入参数，所以第一个参数为Null
            Object obj0 = getRuntime.invoke(null);
            // 获取 .exec 方法
            Method exec = evil_class.getMethod(new String(new byte[]{101, 120, 101, 99}), String.class);
            // 执行 Runtime().getRuntime().exec() 方法 该方法是动态方法，第一个为类名 Runtime()类，第二个参数为字符串
//            Object obj1 = exec.invoke(obj0, new Object[]{cmd}); 简写
            Object obj1 = exec.invoke(obj0, cmd);
            // 获取 .getInputStream 方法，
            Method getInputStream = obj1.getClass().getMethod(new String(new byte[]{103, 101, 116, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109}));

            System.out.println(Modifier.toString(getInputStream.getModifiers()));
            System.out.println(getInputStream.canAccess(obj1));
            getInputStream.setAccessible(true);
            Object obj2 = getInputStream.invoke(obj1);
            System.out.println(IOUtils.toString((InputStream) obj2, "GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        execCommandJNIBase64();
    }
}
