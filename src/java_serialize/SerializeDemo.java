package java_serialize;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import sun.reflect.ReflectionFactory;
import utill.HelpFunctions;


public class SerializeDemo {
    private static final Path ser_path = Paths.get("D://tmp", "employee.ser");
    private static final Path ser_null_path = Paths.get("D://tmp", "employee_null.ser");
    private static final byte[] EMPLOYEE_SERIALIZED_BINARY = new byte[]{
            -84, -19, 0, 5, 115, 114, 0,//header
            23, //  "java_serialize.Employee".length()
            106, 97, 118, 97, 95, 115, 101, 114, 105, 97, 108, 105, 122, 101, 46, 69, 109, 112, 108, 111, 121, 101, // "java_serialize.Employee".getBytes()
            101, 67, -41, -100, 39, -114, -83, -54, -50, 2, 0, 3, 73, 0, 6, 110, 117,
            109, 98, 101, 114, 76, 0, 7, 97, 100, 100, 114, 101, 115, 115, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108,
            97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 0, 4, 110, 97, 109, 101, 113, 0, 126, 0, 1, 120,
            112, 0, 0, 0, 101, 116, 0, 25, 80, 104, 111, 107, 107, 97, 32, 75, 117, 97, 110, 44, 32, 65, 109, 98, 101,
            104, 116, 97, 32, 80, 101, 101, 114, 116, 0, 9, 82, 101, 121, 97, 110, 32, 65, 108, 105
    };

    private static final byte[] EMPLOYEE_SERIALIZED_NULL_BINARY = new byte[]{
            -84, -19, 0, 5, 115, 114, 0,
            23,
            106, 97, 118, 97, 95, 115, 101, 114, 105, 97, 108, 105, 122, 101, 46, 69, 109, 112, 108, 111, 121, 101,
            101, 67, -41, -100, 39, -114, -83, -54, -50, 2, 0, 3, 73, 0, 6, 110, 117,
            109, 98, 101, 114, 76, 0, 7, 97, 100, 100, 114, 101, 115, 115, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 0, 4, 110, 97, 109, 101, 113, 0, 126, 0, 1, 120, 112, 0, 0, 0, 0, 112, 112};


    /**
     * 核心逻辑
     * 使用ObjectOutputStream类的writeObject方法序列化DeserializationTest类，
     * 使用ObjectInputStream类的readObject方法反序列化DeserializationTest类。
     */
    public static void ObjectSerializeAndDeserializeWithStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // 创建DeserializationTest类，并类设置属性值
            Employee t = new Employee();
            t.setName("yz");
            t.setAddress("admin@javaweb.org");
            t.setNumber(1231231);


            // 创建Java对象序列化输出流对象
            ObjectOutputStream out = new ObjectOutputStream(baos);

            // 序列化DeserializationTest类
            out.writeObject(t);
            out.flush();
            out.close();

            // 打印DeserializationTest类序列化以后的字节数组，我们可以将其存储到文件中或者通过Socket发送到远程服务地址
            System.out.println("DeserializationTest类序列化后的字节数组:" + Arrays.toString(baos.toByteArray()));

            // 利用DeserializationTest类生成的二进制数组创建二进制输入流对象用于反序列化操作
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            // 通过反序列化输入流(bais),创建Java对象输入流(ObjectInputStream)对象
            ObjectInputStream in = new ObjectInputStream(bais);

            // 反序列化输入流数据为DeserializationTest对象
            Employee test = (Employee) in.readObject();

            // 关闭ObjectInputStream输入流
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 使用反序列化方式在不调用类构造方法的情况下创建类实例
     * <p>
     * JAVA 创建一个类有四种方法
     * 1.使用new关键字 2.使用clone方法 3.反射机制 4.反序列化
     * 其中1,3都会明确的显式的调用构造函数
     * 2是在内存上对已有对象的影印 所以不会调用构造函数
     * 4是从文件中还原类的对象 也不会调用构造函数
     */
    public static void deserializeWithOutCallConstructor() {

        byte[] head = {-84, -19, 0, 5, 115, 114, 0};
        // 这个ass咋写还是很有讲究的，否则会报错。目前还没弄明白是咋写的
        byte[] ass = {0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 120, 112};
        try {
            String name = Employee.class.getName();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(head);
            baos.write(name.length());
            baos.write(name.getBytes());
            baos.write(ass);
            baos.flush();
            baos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            Employee o = (Employee) ois.readObject();
            ois.close();
            System.out.println("创建对象: " + o);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 正常的serialize
     */
    public static void serialize() {
        Employee e = Employee.getEmployee();
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("/tmp/employee_null.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(e);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in /tmp/employee.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * 可以看出，反序列化不触发构造函数的特征，毕竟它是从文件中还原的。
     */
    public static void deserializeFromBytes() {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(EMPLOYEE_SERIALIZED_BINARY);
            ObjectInputStream in = new ObjectInputStream(bais);
            Object o = in.readObject();
            bais.close();
            in.close();
            System.out.println(o.toString());
        } catch (IOException | ClassNotFoundException d) {
            d.printStackTrace();
        }
    }

    /**
     * 可以看出，反序列化不触发构造函数的特征，毕竟它是从文件中还原的。
     */
    public static void deserializeFromSerFile() {
        try {
            FileInputStream fileIn = new FileInputStream(ser_path.toString());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Object o = in.readObject();
            fileIn.close();
            in.close();
            System.out.println(o.toString());
        } catch (IOException | ClassNotFoundException d) {
            d.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        serialize();
//        System.out.println(Arrays.toString(HelpFunctions.readFileReturnBytes(ser_null_path)));
//        deserializeWithOutCallConstructor();
        ObjectSerializeAndDeserializeWithStream();
    }
}