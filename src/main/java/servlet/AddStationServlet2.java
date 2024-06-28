package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import dao.BaseDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/servlet/save")
public class AddStationServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // 从请求中读取 JSON 数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String jsonInput = reader.readLine();

            try (Connection connection = BaseDao.getConnection()) {
                // 解析 JSON 数据并插入到数据库
                parseJsonAndInsert(jsonInput, connection);

                out.println("{\"message\":\"Station added successfully\"}");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private void parseJsonAndInsert(String jsonInput, Connection connection) throws SQLException {
        // 解析 JSON 数据
        JSONObject jsonObject = new JSONObject(jsonInput);

        // 从 JSON 中获取值
        String stationId = jsonObject.getString("station_id");
        String stationName = jsonObject.optString("station_name");
        int buildingYear = Integer.valueOf(jsonObject.optString("date").split("-")[0]);
        int buildingMonth = Integer.valueOf(jsonObject.optString("date").split("-")[1]);
        String management = jsonObject.optString("management");
        double eastLongitude = jsonObject.optDouble("east_longitude");
        double northenLatitude = jsonObject.optDouble("northen_latitude");
        String province = jsonObject.optString("province");
        String city = jsonObject.optString("city");
        String town = jsonObject.optString("town");
        String village = jsonObject.optString("village");
        String street = jsonObject.optString("street");
        String riverName = jsonObject.optString("river_name");
        String riverSystem = jsonObject.optString("river_system");
        String drainage = jsonObject.optString("drainage");
        String basicHydrology = jsonObject.optString("basic_hydrology");
        String precipitationEvaporation = jsonObject.optString("precipitation_evaporation");
        String rainCondition = jsonObject.optString("rain_condition");
        String waterQuality = jsonObject.optString("water_quality");

        // 将数据插入各个表
        insertBasicInformation(stationId, stationName, buildingYear, buildingMonth, management, connection);
        insertLocationInformation(stationId, eastLongitude, northenLatitude, province, city, town, village, street, connection);
        insertRiverInformation(stationId, riverName, riverSystem, drainage, connection);
        insertTypeInformation(stationId, basicHydrology, precipitationEvaporation, rainCondition, waterQuality, connection);
    }

    private void insertBasicInformation(String stationId, String stationName, int buildingYear, int buildingMonth,
                                        String management, Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO station_basic_information (station_id, station_name, building_year, building_month, management) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.setString(2, stationName);
            preparedStatement.setInt(3, buildingYear);
            preparedStatement.setInt(4, buildingMonth);
            preparedStatement.setString(5, management);

            preparedStatement.executeUpdate();
        }
    }

    private void insertLocationInformation(String stationId, double eastLongitude, double northenLatitude,
                                           String province, String city, String town, String village, String street, Connection connection)
            throws SQLException {
        String insertQuery = "INSERT INTO station_location_information (station_id, east_longitude, northen_latitude, province, " +
                "city, town, village, street) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.setDouble(2, eastLongitude);
            preparedStatement.setDouble(3, northenLatitude);
            preparedStatement.setString(4, province);
            preparedStatement.setString(5, city);
            preparedStatement.setString(6, town);
            preparedStatement.setString(7, village);
            preparedStatement.setString(8, street);

            preparedStatement.executeUpdate();
        }
    }

    private void insertRiverInformation(String stationId, String riverName, String riverSystem, String drainage, Connection connection)
            throws SQLException {
        String insertQuery = "INSERT INTO station_river_information (station_id, river_name, river_system, drainage) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.setString(2, riverName);
            preparedStatement.setString(3, riverSystem);
            preparedStatement.setString(4, drainage);

            preparedStatement.executeUpdate();
        }
    }

    private void insertTypeInformation(String stationId, String basicHydrology, String precipitationEvaporation,
                                       String rainCondition, String waterQuality, Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO station_type_information (station_id, basic_hydrology, precipitation_evaporation, " +
                "rain_condition, water_quality) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, stationId);
            preparedStatement.setString(2, basicHydrology);
            preparedStatement.setString(3, precipitationEvaporation);
            preparedStatement.setString(4, rainCondition);
            preparedStatement.setString(5, waterQuality);

            preparedStatement.executeUpdate();
        }
    }
}