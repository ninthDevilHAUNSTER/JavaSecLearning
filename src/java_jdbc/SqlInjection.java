package java_jdbc;

import java.sql.SQLException;

public class SqlInjection {
    public static void main(String[] args) throws SQLException {
        MariadbInjection sql = new MariadbInjection();
//        sql.execSql("root");
        sql.execSql(" ");
    }

}
