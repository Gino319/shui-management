package pojo;

import lombok.Data;

@Data
public class rece_information
{
    private String station_id;
    private String station_name;
    private String management;
    private Double east_longitude;
    private Double northen_latitude;
    private String province;
    private String city;
    private String town;
    private String village;
    private String street;
    private String river_name;
    private String basic_hydrology;
    private String precipitation_evaporation;
    private String rain_condition;
    private String water_quality;
    private String data;
}
