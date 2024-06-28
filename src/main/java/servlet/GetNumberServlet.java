package servlet;

import dao.BaseDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/servlet/getnumber")
public class GetNumberServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            try (Connection connection = BaseDao.getConnection()) {
                // 查询各个条件下的记录数
                int countBasicHydrology = getCount(connection, "basic_hydrology");
                int countPrecipitationEvaporation = getCount(connection, "precipitation_evaporation");
                int countRainCondition = getCount(connection, "rain_condition");
                int countWaterQuality = getCount(connection, "water_quality");

                // 构造 JSON 响应
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("bh", countBasicHydrology);
                jsonResponse.put("pe", countPrecipitationEvaporation);
                jsonResponse.put("rc", countRainCondition);
                jsonResponse.put("wq", countWaterQuality);

                out.println(jsonResponse.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private int getCount(Connection connection, String columnName) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM station_type_information WHERE " + columnName + " IS NOT NULL";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count");
            } else {
                return 0;
            }
        }
    }

}
