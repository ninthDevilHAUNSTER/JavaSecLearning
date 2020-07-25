package java_serialize;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 请注意，一个类的对象要想序列化成功，必须满足两个条件：
 * 该类必须实现 java.io.Serializable 接口。
 * 该类的所有属性必须是可序列化的。如果有一个属性不是可序列化的，则该属性必须注明是短暂的。
 * 如果你想知道一个 Java 标准类是否是可序列化的，请查看该类的文档。检验一个类的实例是否能序列化十分简单， 只需要查看该类有没有实现 java.io.Serializable接口。
 * <p>
 * java.io.Serializable 是一个空的接口，仅仅是为了标识该类是可以序列化的
 * <p>
 * 在Externalizable中，用户必须覆盖序列化与反序列化方法，那么在Serializable中，可否覆盖呢？
 * 也是可以的，但我理解为一种隐藏的覆盖方法。
 * <p>
 * 在序列化与反序列化的过程中，往往会用到 ObjectInputStream 和 ObjectOutputStream 中的 readObj与writeObj方法。
 * 这两个Stream均继承自java.io.ObjectStreamClass，该方法继承了 Serializable 的接口
 * 在 java.io.ObjectStreamClass#ObjectStreamClass(java.lang.Class):532/535行中有着这两行代码
 * <p>
 * writeObjectMethod = getPrivateMethod(cl, "writeObject",
 * new Class[] { ObjectOutputStream.class },
 * Void.TYPE);
 * 虽然我涉世未深，但是也能猜得出，这是在动态获取writeObject这两个方法，并且是Private的
 * <p>
 * 继续跟入，可以发现，总共需要扩展这五个方法
 *
 *
 * private void writeObject 自定义序列化。
 * private void readObject  自定义反序列化。
 * private void readObjectNoData 空数据的反序列化
 * ~static|~abstract Object writeReplace 写入时替换对象
 * ~static|~abstract Object readResolve  读出时替换对象
 *
 */

class Employee implements java.io.Serializable {
    public String name;
    public String address;
    public transient int SSN; // 暂时的
    public int number;

    Employee() {
        System.out.println("init Employee Class");
    }


    public static @NotNull Employee getEmployee() {
        return new Employee();
    }

    public void mailCheck() {
        System.out.println("Mailing a check to " + name
                + " " + address);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", SSN=" + SSN +
                ", number=" + number +
                '}';
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSSN(int SSN) {
        this.SSN = SSN;
    }

    /*
     * 根据之前的分析，不难发现，下面的函数是会被调用的
     */

    /**
     * 自定义反序列化类对象
     *
     * @param ois 反序列化输入流对象
     * @throws IOException            IO异常
     * @throws ClassNotFoundException 类未找到异常
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        System.out.println("readObject...");
        // 调用ObjectInputStream默认反序列化方法
        ois.defaultReadObject();
        // 省去调用自定义反序列化逻辑...
    }

    /**
     * 自定义序列化类对象
     *
     * @param oos 序列化输出流对象
     * @throws IOException IO异常
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();


        System.out.println("writeObject...");
        // 省去调用自定义序列化逻辑...
    }

    private void readObjectNoData() {
        System.out.println("readObjectNoData...");
    }

    /**
     * 写入时替换对象
     *
     * @return 替换后的对象
     */
    protected Object writeReplace() {
        System.out.println("writeReplace....");
        return this;
    }

    protected Object readResolve() {
        System.out.println("readResolve....");
        return this;
    }


}
