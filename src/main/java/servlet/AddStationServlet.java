package servlet;

import dao.BaseDao;
import pojo.station_basic_information;
import pojo.station_location_information;
import pojo.station_river_information;
import pojo.station_type_information;

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

@WebServlet("/servlet/1")
public class AddStationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // 读取前端传来的 JSON 数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String jsonInput = reader.readLine();

            try (Connection connection = BaseDao.getConnection();) {
                // 解析 JSON 数据并插入数据库
                String station_id = parseJsonAndInsert(jsonInput, connection);

                // 返回插入的站点数据
                out.println("{\"id\":" + station_id + ", \"message\":\"Station added successfully\"}");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private String parseJsonAndInsert(String jsonInput, Connection connection) throws SQLException {
        JSONObject jsonObject = new JSONObject(jsonInput);
        station_basic_information sb = new station_basic_information();
        station_location_information sl = new station_location_information();
        station_river_information sr = new station_river_information();
        station_type_information st = new station_type_information();

        sb.setStation_id(jsonObject.getString("station_id"));
        sb.setStation_name(jsonObject.getString("station_name"));
        sb.setManagement(jsonObject.getString("management"));
        sb.setBuilding_year(Integer.valueOf(jsonObject.getString("date").split("-")[0]));
        sb.setBuilding_month(Integer.valueOf(jsonObject.getString("date").split("-")[1]));

        sl.setStation_id(jsonObject.getString("station_id"));
        sl.setEast_longitude(jsonObject.getDouble("east_longitude"));
        sl.setNorthen_latitude(jsonObject.getDouble("northen_latitude"));
        sl.setProvince(jsonObject.getString("province"));
        sl.setCity(jsonObject.getString("city"));
        sl.setTown(jsonObject.getString("town"));
        sl.setVillage(jsonObject.getString("village"));
        sl.setStreet(jsonObject.getString("street"));

        sr.setStation_id(jsonObject.getString("station_id"));
        sr.setRiver_name(jsonObject.getString("river_name"));
        sr.setRiver_system(jsonObject.getString("river_system"));
        sr.setDrainage(jsonObject.getString("drainage"));

        st.setStation_id(jsonObject.getString("station_id"));
        st.setBasic_hydrology(jsonObject.getString("basic_hydrology"));
        st.setPrecipitation_evaporation(jsonObject.getString("precipitation_evaporation"));
        st.setRain_condition(jsonObject.getString("rain_condition"));
        st.setWater_quality(jsonObject.getString("water_quality"));

        String sb_sql = "insert into station_basic_information (station_id, station_name, building_year, building_month, management) values(?, ?, ?, ?, ?)";
        String sl_sql = "insert into station_basic_information (station_id, east_longitude, northen_latitude, province, city" +
                "town, village, street) values(?, ?, ?, ?, ?, ?, ?, ?)";
        String sr_sql = "insert into station_basic_information (station_id, river_name, river_system, drainage) values(?, ?, ?, ?)";
        String st_sql = "insert into station_basic_information (station_id, basic_hydrology, precipitation_evaporation, rain_condition, water_quality) values(?, ?, ?, ?, ?)";

        // 将数据插入数据库
        try (PreparedStatement preparedStatement = connection.prepareStatement(sb_sql)) {
            preparedStatement.setString(1, sb.getStation_id());
            preparedStatement.setString(2, sb.getStation_name());
            preparedStatement.setInt(3, sb.getBuilding_year());
            preparedStatement.setInt(4, sb.getBuilding_month());
            preparedStatement.setString(5, sb.getManagement());
            preparedStatement.executeUpdate();
        }
        catch (Exception e) {System.out.println("111");}
        try (PreparedStatement preparedStatement = connection.prepareStatement(sl_sql)) {
            preparedStatement.setString(1, sl.getStation_id());
            preparedStatement.setDouble(2, sl.getEast_longitude());
            preparedStatement.setDouble(3, sl.getNorthen_latitude());
            preparedStatement.setString(4, sl.getProvince());
            preparedStatement.setString(5, sl.getCity());
            preparedStatement.setString(6, sl.getTown());
            preparedStatement.setString(7, sl.getVillage());
            preparedStatement.setString(8, sl.getStreet());
            preparedStatement.executeUpdate();
        }  catch (Exception e) {System.out.println("222");}
        try (PreparedStatement preparedStatement = connection.prepareStatement(sr_sql)) {
            preparedStatement.setString(1, sr.getStation_id());
            preparedStatement.setString(2, sr.getRiver_name());
            preparedStatement.setString(3, sr.getRiver_system());
            preparedStatement.setString(4, sr.getDrainage());
            preparedStatement.executeUpdate();
        }catch (Exception e) {System.out.println("333");}
        try (PreparedStatement preparedStatement = connection.prepareStatement(st_sql)) {
            preparedStatement.setString(1, st.getStation_id());
            preparedStatement.setString(2, st.getBasic_hydrology());
            preparedStatement.setString(3, st.getPrecipitation_evaporation());
            preparedStatement.setString(4, st.getRain_condition());
            preparedStatement.setString(5, st.getWater_quality());
            preparedStatement.executeUpdate();
        }catch (Exception e) {System.out.println("444");}

        // 创建并返回新插入的站点数据对象
        return sb.getStation_id();
    }
}
