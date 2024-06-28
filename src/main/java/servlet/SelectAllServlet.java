package servlet;

import dao.BaseDao;
import pojo.all_information;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/servlet/select_all")
public class SelectAllServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            try (Connection connection = BaseDao.getConnection()) {
                // 使用 JOIN 操作查询所有站点的信息
                String query = "SELECT basic.*, location.*, river.*, type.* " +
                        "FROM station_basic_information AS basic " +
                        "LEFT JOIN station_location_information AS location ON basic.station_id = location.station_id " +
                        "LEFT JOIN station_river_information AS river ON basic.station_id = river.station_id " +
                        "LEFT JOIN station_type_information AS type ON basic.station_id = type.station_id";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    ResultSet resultSet = preparedStatement.executeQuery();

                    JSONArray stationsArray = new JSONArray();

                    while (resultSet.next()) {
                        JSONObject stationObject = new JSONObject();

                        // 假设列名和数据字段名对应，你需要根据实际情况修改
                        stationObject.put("station_id", resultSet.getString("station_id"));
                        stationObject.put("station_name", resultSet.getString("station_name"));
                        stationObject.put("date", Integer.toString(resultSet.getInt("building_year")) + "-" + Integer.toString(resultSet.getInt("building_month")));
                        stationObject.put("management", resultSet.getString("management"));
                        stationObject.put("address", (resultSet.getString("province") + resultSet.getString("city") + resultSet.getString("town") + resultSet.getString("village") + resultSet.getString("street")).replaceAll("null", ""));
                        stationObject.put("northen_latitude", resultSet.getDouble("northen_latitude"));
                        stationObject.put("east_longitude", resultSet.getDouble("east_longitude"));
                        stationObject.put("river_name", resultSet.getString("river_name"));
                        stationObject.put("basic_hydrology", resultSet.getString("basic_hydrology"));
                        stationObject.put("precipitation_evaporation", resultSet.getString("precipitation_evaporation"));
                        stationObject.put("rain_condition", resultSet.getString("rain_condition"));
                        stationObject.put("water_quality", resultSet.getString("water_quality"));
                        // ... 其他属性的获取

                        stationsArray.put(stationObject);
                    }

                    // 构造最终的 JSON 响应
                    out.println(stationsArray.toString());

                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
}

