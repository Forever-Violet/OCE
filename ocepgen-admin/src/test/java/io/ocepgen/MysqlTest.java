package io.ocepgen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MysqlTest {

    public static void main(String[] args) {
        try {
            // 加载MySQL JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立数据库连接
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://120.46.58.64:3306/ocepgen",
                    "root",
                    "ufo7338199");

            System.out.println("Connected to the database");

            // 创建一个Statement对象
            Statement statement = connection.createStatement();

            // 执行一个简单的SQL查询
            ResultSet resultSet = statement.executeQuery("SELECT VERSION()");
            resultSet.next();
            String version = resultSet.getString(1);
            System.out.println("MySQL version: " + version);

            // 关闭资源
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Connection closed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
