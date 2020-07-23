package java_serialize;

import java.io.*;
import java.util.Arrays;

/**
 * java.io.Externalizable接口定义了
 * writeExternal和readExternal方法需要序列化和反序列化的类实现
 * 其余的和java.io.Serializable并无差别。
 */
public class Student implements java.io.Externalizable {

    private String username;

    private String email;

    public Student(String _username, String _email) {
        setEmail(_email);
        setUsername(_username);
    }

    public Student() {
        setUsername(null);
        setEmail(null);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(username);
        out.writeObject(email);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.username = (String) in.readObject();
        this.email = (String) in.readObject();
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Student{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
