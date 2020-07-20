package java_basic;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JavaLocalCmdExec {

    private static final byte[] COMMAND_CLASS_BYTES = new byte[]{
            -54, -2, -70, -66, 0, 0, 0, 49, 0, 15, 10, 0, 3, 0, 12, 7, 0, 13, 7, 0, 14, 1,
            0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100,
            101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108,
            101, 1, 0, 4, 101, 120, 101, 99, 1, 0, 38, 40, 76, 106, 97, 118, 97, 47, 108, 97,
            110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 108,
            97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114,
            99, 101, 70, 105, 108, 101, 1, 0, 21, 67, 111, 109, 109, 97, 110, 100, 69, 120,
            101, 99, 117, 116, 105, 111, 110, 46, 106, 97, 118, 97, 12, 0, 4, 0, 5, 1, 0, 34,
            99, 111, 109, 47, 97, 110, 98, 97, 105, 47, 115, 101, 99, 47, 99, 109, 100, 47, 67,
            111, 109, 109, 97, 110, 100, 69, 120, 101, 99, 117, 116, 105, 111, 110, 1, 0, 16,
            106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 0, 33, 0,
            2, 0, 3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 4, 0, 5, 0, 1, 0, 6, 0, 0, 0, 29, 0, 1, 0, 1,
            0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 1, 0, 7, 0, 0, 0, 6, 0, 1, 0, 0, 0, 7, 1,
            9, 0, 8, 0, 9, 0, 0, 0, 1, 0, 10, 0, 0, 0, 2, 0, 11
    };

    /**
     * 利用JNI文件，调用动态链接库来执行结果。目前还没掌握这种操作
     * @throws Exception
     */
    public static void execCommandJNI() throws Exception {
        Path show_class_path = Paths.get("rubbish", "show.class");
        Files.write(show_class_path, COMMAND_CLASS_BYTES);
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
//            getInputStream.setAccessible(true);
            Object obj2 = getInputStream.invoke(obj1);
            System.out.println(IOUtils.toString((InputStream) obj2, "GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        execCommandJNI();
    }
}
