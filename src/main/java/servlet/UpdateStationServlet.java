package servlet;

import dao.BaseDao;
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

import org.json.JSONObject;

@WebServlet("/servlet/update")
public class UpdateStationServlet extends HttpServlet {
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
                // 解析 JSON 数据并更新到数据库
                updateStation(jsonInput, connection);

                out.println("{\"message\":\"Station updated successfully\"}");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private void updateStation(String jsonInput, Connection connection) throws SQLException {
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

        // 更新各个表中的数据
        updateBasicInformation(stationId, stationName, buildingYear, buildingMonth, management, connection);
        updateLocationInformation(stationId, eastLongitude, northenLatitude, province, city, town, village, street, connection);
        updateRiverInformation(stationId, riverName, riverSystem, drainage, connection);
        updateTypeInformation(stationId, basicHydrology, precipitationEvaporation, rainCondition, waterQuality, connection);
    }

    private void updateBasicInformation(String stationId, String stationName, int buildingYear, int buildingMonth,
                                        String management, Connection connection) throws SQLException {
        String updateQuery = "UPDATE station_basic_information SET station_name = ?, building_year = ?, building_month = ?, " +
                "management = ? WHERE station_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, stationName);
            preparedStatement.setInt(2, buildingYear);
            preparedStatement.setInt(3, buildingMonth);
            preparedStatement.setString(4, management);
            preparedStatement.setString(5, stationId);

            preparedStatement.executeUpdate();
        }
    }

    private void updateLocationInformation(String stationId, double eastLongitude, double northenLatitude,
                                           String province, String city, String town, String village, String street, Connection connection)
            throws SQLException {
        String updateQuery = "UPDATE station_location_information SET east_longitude = ?, northen_latitude = ?, province = ?, " +
                "city = ?, town = ?, village = ?, street = ? WHERE station_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, eastLongitude);
            preparedStatement.setDouble(2, northenLatitude);
            preparedStatement.setString(3, province);
            preparedStatement.setString(4, city);
            preparedStatement.setString(5, town);
            preparedStatement.setString(6, village);
            preparedStatement.setString(7, street);
            preparedStatement.setString(8, stationId);

            preparedStatement.executeUpdate();
        }
    }

    private void updateRiverInformation(String stationId, String riverName, String riverSystem, String drainage, Connection connection)
            throws SQLException {
        String updateQuery = "UPDATE station_river_information SET river_name = ?, river_system = ?, drainage = ? WHERE station_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, riverName);
            preparedStatement.setString(2, riverSystem);
            preparedStatement.setString(3, drainage);
            preparedStatement.setString(4, stationId);

            preparedStatement.executeUpdate();
        }
    }

    private void updateTypeInformation(String stationId, String basicHydrology, String precipitationEvaporation,
                                       String rainCondition, String waterQuality, Connection connection) throws SQLException {
        String updateQuery = "UPDATE station_type_information SET basic_hydrology = ?, precipitation_evaporation = ?, " +
                "rain_condition = ?, water_quality = ? WHERE station_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, basicHydrology);
            preparedStatement.setString(2, precipitationEvaporation);
            preparedStatement.setString(3, rainCondition);
            preparedStatement.setString(4, waterQuality);
            preparedStatement.setString(5, stationId);

            preparedStatement.executeUpdate();
        }
    }
}

