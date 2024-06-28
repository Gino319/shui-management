package servlet;

import dao.BaseDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/servlet/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 邮箱正则表达式模式
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    // 验证邮箱格式的方法
    private boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 从请求中读取 JSON 数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String jsonInput = reader.readLine();

        JSONObject jsonObject = new JSONObject(jsonInput);
        String email = jsonObject.getString("email");
        String name = jsonObject.getString("name");
        String password = jsonObject.getString("password");

        if (email == null || name == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 验证邮箱格式
        if (!isValidEmail(email)) {
            try (PrintWriter out = response.getWriter()) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("status", "failure");
                responseJson.put("message", "错误的邮箱格式");
                out.println(responseJson.toString());
            }
            return;
        }

        // 验证密码是否为空
        if (password.trim().isEmpty()) {
            try (PrintWriter out = response.getWriter()) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("status", "failure");
                responseJson.put("message", "密码不能为空");
                out.println(responseJson.toString());
            }
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            try (Connection connection = BaseDao.getConnection()) {
                // 检查邮箱是否已注册
                String checkQuery = "SELECT * FROM user_information WHERE email = ?";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, email);
                    ResultSet checkResultSet = checkStatement.executeQuery();

                    if (checkResultSet.next()) {
                        // 邮箱已注册，返回相关信息
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("status", "failure");
                        responseJson.put("message", "邮箱已注册");
                        responseJson.put("no", checkResultSet.getString("no"));
                        responseJson.put("name", checkResultSet.getString("name"));
                        responseJson.put("email", checkResultSet.getString("email"));
                        out.println(responseJson.toString());
                    } else {
                        // 邮箱未注册，插入新用户信息
                        String insertQuery = "INSERT INTO user_information (email, name, password) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                            insertStatement.setString(1, email);
                            insertStatement.setString(2, name);
                            insertStatement.setString(3, password);
                            int rowsAffected = insertStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                // 注册成功
                                JSONObject responseJson = new JSONObject();
                                responseJson.put("status", "success");
                                responseJson.put("message", "注册成功");
                                out.println(responseJson.toString());
                            } else {
                                // 注册失败
                                JSONObject responseJson = new JSONObject();
                                responseJson.put("status", "failure");
                                responseJson.put("message", "注册失败，请重试");
                                out.println(responseJson.toString());
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", e.getMessage());
                out.println(errorJson.toString());
            }
        }
    }
}
