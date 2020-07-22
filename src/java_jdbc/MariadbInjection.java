package java_jdbc;

import org.mariadb.jdbc.Driver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

public class MariadbInjection {
    // 数据库驱动类名
    public static final String CLASS_NAME = "org.mariadb.jdbc.Driver";

    // 数据库链接字符串
    public static final String URL = "jdbc:mariadb://localhost:3306";

    // 数据库用户名
    public static final String USERNAME = "root";

    // 数据库密码
    public static final String PASSWORD = "password";

    Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(CLASS_NAME);// 注册JDBC驱动类
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public void execSql(String user) throws SQLException {

        try (Connection connection = getConnection()) {
            // 建立数据库连接

            // 定义最终执行的SQL语句，这里会将用户从请求中传入的host字符串拼接到最终的SQL
            // 语句当中，从而导致了SQL注入漏洞。
//            String sql = "select host,user from mysql.user where user = ? ";
            String sql = "select host,user from mysql.user where user = '" + user + "'";
            System.out.println("SQL:" + sql);

            // 创建预编译对象
            PreparedStatement pstt = connection.prepareStatement(sql);
//            pstt.setObject(1, user);

            // 执行SQL语句并获取返回结果对象
            ResultSet rs = pstt.executeQuery();


            // 输出SQL语句执行结果
            while (rs.next()) {
                // 获取SQL语句中查询的字段值
                System.out.println("<td>" + rs.getObject("host") + "</td>");
                System.out.println("<td>" + rs.getObject("user") + "</td>");
            }
            // 关闭查询结果
            rs.close();

            // 关闭预编译对象
            pstt.close();
        } catch (Exception e) {
            // 输出异常信息到浏览器
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.out.println(sw);
        }
        // 关闭数据库连接

    }

}
