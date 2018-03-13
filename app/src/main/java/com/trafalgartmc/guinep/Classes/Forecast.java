package com.trafalgartmc.guinep.Classes;

/**
 * Created by vmbssy116 on 5/3/2017.
 */

public class Forecast {
    private String temp;
    private String temp_max;
    private String temp_min;
    private String icon;
    private String label;
    private String description;
    private String country;
    private String city;
    private long date;

    public Forecast(String temp, String temp_min, String temp_max, String icon, String label, String description, long date, String country, String city) {
        this.temp = temp;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
        this.icon = icon;
        this.label = label;
        this.description = description;
        this.date = date;
        this.country = country;
        this.city = city;
    }

    public String getTemp() {
        return temp;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {return label;}

    public String getCountry() {return country;}

    public String getCity() {return city;}

    public String getDescription() {
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    public long getDate() {
        return date;
    }
}
