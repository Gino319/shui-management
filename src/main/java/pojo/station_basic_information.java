package pojo;

import lombok.Data;

import java.util.Date;

@Data
public class station_basic_information
{
    private String station_id;
    private String station_name;
    private Integer building_year;
    private Integer building_month;
    private String management;

}
