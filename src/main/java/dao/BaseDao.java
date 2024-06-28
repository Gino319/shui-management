package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao
{
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        // 通过类加载器读取对应的资源
        Properties properties = new Properties();
        // 通过类加载器读取对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    //获取数据库的链接
    public static Connection getConnection() {
        // 此句是用于提升作用域，用于 return 数据
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    // 编写查询公共类
    public static ResultSet quiry(Connection connection, String sql, Object[] params, ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException, SQLException {
        // 预编译SQL语句，后边查询时不需要传参数
        preparedStatement = connection.prepareStatement(sql);
        // 使用循环填充参数
        for (int i = 0; i < params.length; i++) {
            // setObject,占位符从1开始，但是数组下标从0开始
            preparedStatement.setObject(i + 1, params[i]);
        }
        // 进行参数化查询，这里不需要传参了
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    // 编写增删改公共方法，相比于查询不需要返回数据返回 int 类型表示查询结果即可
    public static int excute(Connection connection, String sql, Object[] params, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        // 使用循环填充参数
        for (int i = 0; i < params.length; i++) {
            // setObject,占位符从1开始，但是数组下标从0开始
            preparedStatement.setObject(i + 1, params[i]);
        }
        // 进行更新操作，这里不需要传参了
        int updataRows = preparedStatement.executeUpdate();
        return updataRows;
    }


    // 释放资源
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet) {
        // 标识符
        boolean flag = true;

        if (resultSet != null) {
            try {
                resultSet.close();
                // GC回收
                resultSet = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        if (connection != null) {
            try {
                connection.close();
                // GC回收
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                // GC回收
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }


        // 如果都释放成功返回 true，否则返回 false
        return flag;
    }
}
