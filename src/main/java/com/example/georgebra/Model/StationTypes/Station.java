package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.LineTypes.MetroLine;
import java.util.MissingFormatArgumentException;

public abstract class Station {
    //protected int stationNo; //3
    protected final String id; //NE3    Must have at least 2 letters, 1st denoting line, 2nd denoting station
    protected final String name; //Outram Park
    protected double x = 0;
    protected double y = 0;

    protected final String lineName; //North East MetroLine

    public Station(double x, double y, String id, String lineName, String name) throws MissingFormatArgumentException{
        if (name.isEmpty() || lineName.isEmpty()) {
            throw new MissingFormatArgumentException("Provide a MetroLine name.");
        }
        if (id.isEmpty() || !Character.isDigit(id.charAt(id.length() - 1)) || !Character.isLetter(id.charAt(0))) {
            throw new IllegalArgumentException("Invalid Station ID. (" + id + ")");
        }

        this.id = id;
        this.name = name.toLowerCase();
        this.lineName = lineName.toLowerCase(); //standard format
        //this.stationNo = stationNo;
        this.x = x;
        this.y = y;
    }

    //specialised for interchange: will have empty id
    public Station(double x, double y, String name) throws MissingFormatArgumentException {
        if (name.isEmpty()) throw new MissingFormatArgumentException("Provide a MetroLine name.");

        this.id = "";
        this.name = name.toLowerCase(); //Standard Format
        this.lineName = "";
        //this.stationNo = stationNo;
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    public String getStationLineColour() {
        if (this instanceof Interchange) {
            
        }
        return MetroLine.lineNameToColourMap.get(this.lineName);
    }
    public String getStationID() {
        if (this.id.isEmpty() && this instanceof Interchange) {
            throw new UnsupportedOperationException("Station is Interchange: use getAllLinesInfo");
        }
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getLineName() {
        if (this instanceof Interchange) return (String) ((Interchange)this).getAllLinesInfo().keySet().toArray()[0];
        return this.lineName;
    }

    public double getX() {
        return this.x;
    };
    public double getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return this.id + ": " + this.name + " " + this.x + " " + this.y;
    }
}