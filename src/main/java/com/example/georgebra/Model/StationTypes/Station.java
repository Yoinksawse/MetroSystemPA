package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.LineTypes.MetroLine;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.MissingFormatArgumentException;

public abstract class Station { //implements Comparable<Station>{
    //protected int stationNo; //3
    protected final String id; //NE3    Must have at least 2 letters, 1st denoting line, 2nd denoting station
    protected final String name; //Outram Park
    protected double x = 0;
    protected double y = 0;

    protected final String lineName; //North East MetroLine

    protected boolean selected;
    protected boolean dragging;
    public final DropShadow highlightShadow =
            new DropShadow(20, Color.rgb(255, 255, 150, 0.8));

    public Station(double x, double y, String id, String lineName, String name) throws MissingFormatArgumentException{
        if (name.isEmpty() || lineName.isEmpty()) {
            throw new MissingFormatArgumentException("Provide a MetroLine name.");
        }
        if (id.isEmpty() || !Character.isDigit(id.charAt(id.length() - 1)) || !Character.isLetter(id.charAt(0))) {
            throw new IllegalArgumentException("Invalid Station ID. (" + id + ")");
        }

        this.id = id;
        this.name = name;
        this.lineName = lineName;
        //this.stationNo = stationNo;
        this.x = x;
        this.y = y;
        this.selected = false;
        this.dragging = false;
    }

    //specialised for interchange: will have empty id
    public Station(double x, double y, String name) throws MissingFormatArgumentException {
        if (name.isEmpty()) throw new MissingFormatArgumentException("Provide a MetroLine name.");

        this.id = "";
        this.name = name;
        this.lineName = "";
        //this.stationNo = stationNo;
        this.x = x;
        this.y = y;
        this.selected = false;
        this.dragging = false;
    }

   /*
    public Station(Station other) {
        this(other.getX(), other.getY(), other.getStationID(), other.getLineName(), other.getName());
    }
    */

    /*
    //useless
    public abstract Group draw();
    public abstract Group setHighlighted(boolean highlighted);
    //javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);
     */

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setSelected(boolean tf) {
        this.selected = tf;
    }
    public void setDragging(boolean tf) {
        this.dragging = tf;
        this.selected = true;
    }

    //TODO
    public String getStationLineColour() {
        return MetroLine.lineNameToColourMap.get(this.lineName);
    }
    public String getStationID() {
        if (this.id.isEmpty() && this instanceof Interchange) {
            throw new UnsupportedOperationException("Station is Interchange: use getInterchangeIDs() instead");
        }
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getLineName() {
        if (this instanceof Interchange) {
            throw new UnsupportedOperationException("Station is Interchange: use getLineNames() instead");
        }
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