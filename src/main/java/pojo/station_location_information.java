package pojo;

import lombok.Data;

@Data
public class station_location_information
{
    private String station_id;
    private Double east_longitude;
    private Double northen_latitude;
    private String province;
    private String city;
    private String town;
    private String village;
    private String street;
}
