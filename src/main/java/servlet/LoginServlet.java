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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/servlet/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 从请求中读取 JSON 数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String jsonInput = reader.readLine();

        JSONObject jsonObject = new JSONObject(jsonInput);
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");

        if (email == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            try (Connection connection = BaseDao.getConnection()) {
                // 查询用户信息
                String query = "SELECT * FROM user_information WHERE email = ? AND password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, password);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        // 登录成功
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("status", "success");
                        responseJson.put("message", "登陆成功");
                        responseJson.put("no", resultSet.getString("no"));
                        responseJson.put("name", resultSet.getString("name"));
                        responseJson.put("email", resultSet.getString("email"));
                        // 你可以在此处添加其他需要返回的用户信息
                        out.println(responseJson.toString());
                    } else {
                        // 登录失败
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("status", "failure");
                        responseJson.put("message", "邮箱或密码有误，请重试");
                        out.println(responseJson.toString());
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
