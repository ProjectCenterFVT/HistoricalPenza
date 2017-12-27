package com.projectcenterfvt.historicalpenza;

/**
 * Created by Dmitry on 25.12.2017.
 */

public class Sight {
    public int id;
    public String title;
    public String description;
    public String img;
    public double x1;
    public double x2;
    public int flag;

    Sight(int id) {
        this.id = id;
    }

    Sight(int id, String title, String description, String img) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.img = img;
    }

    public void setCoordinates(String coordRaw) {
        this.x1 = Double.parseDouble(coordRaw.substring(0, coordRaw.indexOf(",")));
        this.x2 = Double.parseDouble(coordRaw.substring(coordRaw.indexOf(" ")+1, coordRaw.length()-1));
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
