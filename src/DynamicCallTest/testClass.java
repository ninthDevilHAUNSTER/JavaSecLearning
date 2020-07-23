package DynamicCallTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class testClass {
    public static void main(String[] args) throws Exception {
        Method[] ms = Class.forName("DynamicCallTest.FinalEmployee").getDeclaredMethods();
        for (Method method
                : ms
        ) {
            System.out.println(method.getName() + "\t" + method.isAccessible());
            FinalEmployee fe = new FinalEmployee();
            System.out.println(method.getName() + "\t" + method.invoke(fe));
        }
    }
}
