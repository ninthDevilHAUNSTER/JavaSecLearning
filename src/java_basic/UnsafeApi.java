package java_basic;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 是Java内部API，外部是禁止调用的，在编译Java类时如果检测到引用了Unsafe类也会有禁止使用的警告：Unsafe是内部专用 API, 可能会在未来发行版中删除。
 */
public class UnsafeApi {
    public static void useUnsafeApi() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        // 反射获取Unsafe的theUnsafe成员变量
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        // 反射设置theUnsafe访问权限
        theUnsafeField.setAccessible(true);
        // 反射获取theUnsafe成员变量值
        Unsafe unsafe = (Unsafe) theUnsafeField.get(null);

        Class<?> processClass = Class.forName("java.lang.ProcessImpl");
        Object processObject = unsafe.allocateInstance(processClass);


//        // 获取Unsafe无参构造方法
//        Constructor constructor = Unsafe.class.getDeclaredConstructor();
//        // 修改构造方法访问权限
//        constructor.setAccessible(true);
//        // 反射创建Unsafe类实例，等价于 Unsafe unsafe1 = new Unsafe();
//        Unsafe unsafe1 = (Unsafe) constructor.newInstance();

    }

    public static void main(String[] args) throws Exception {
        useUnsafeApi();

    }
}
