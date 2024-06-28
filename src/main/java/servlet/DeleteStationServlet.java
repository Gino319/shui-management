package servlet;

import dao.BaseDao;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/servlet/delete")
public class DeleteStationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // 从请求中读取 JSON 数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String jsonInput = reader.readLine();

            // 解析 JSON 数据
            JSONObject jsonObject = new JSONObject(jsonInput);
            String stationId = jsonObject.getString("station_id");

            try (Connection connection = BaseDao.getConnection()) {
                // 删除数据库中对应的记录
                deleteStation(stationId, connection);

                out.println("{\"message\":\"Station deleted successfully\"}");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private void deleteStation(String stationId, Connection connection) throws SQLException {
        // 执行删除操作
        String sb_sql = "DELETE FROM station_basic_information WHERE station_id = ?";
        String sl_sql = "DELETE FROM station_location_information WHERE station_id = ?";
        String sr_sql = "DELETE FROM station_river_information WHERE station_id = ?";
        String st_sql = "DELETE FROM station_type_information WHERE station_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sl_sql)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.executeUpdate();
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(sr_sql)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.executeUpdate();
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(st_sql)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.executeUpdate();
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(sb_sql)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.executeUpdate();
        }
    }
}
