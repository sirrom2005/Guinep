package com.trafalgartmc.guinep.Classes;

import java.io.Serializable;

/**
 * Created by Rohan MOrris on 5/4/2017.
 */

public class DataObject implements Serializable {
    protected static final long serialVersionUID = 1129260861L;
    private int id;
    private String title, subTitle, image, body, phone;

    public DataObject(int id, String title, String subTitle, String body, String image, String phone) {
        this.id         = id;
        this.title      = title;
        this.subTitle   = subTitle;
        this.image      = image;
        this.body       = body;
        this.phone      = phone;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getImage() {
        return image;
    }

    public String getBody() {
        return body;
    }

    public String getPhone() {
        return phone;
    }
}
