package com.example.georgebra.Model.StationTypes;

import com.example.georgebra.Model.Drawable;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

import java.util.MissingFormatArgumentException;
import java.util.Objects;

public abstract class Station implements Comparable<Station>{
    protected int stationNo; //3
    protected final String id; //NE3    Must have at least 2 letters, 1st denoting line, 2nd denoting station
    protected final String name; //Outram Park
    protected final String lineName; //North East Line
    protected double x;
    protected double y;
    protected boolean selected;
    protected boolean dragging;

    int estimate;

    public Station(double x, double y, String id, String name, String lineName, int stationNo) throws MissingFormatArgumentException{
        if (name.isEmpty() || lineName.isEmpty()) {
            throw new MissingFormatArgumentException("Provide a Line name.");
        }
        //
        if (id.isEmpty() || !Character.isDigit(id.charAt(id.length() - 1)) || !Character.isLetter(id.charAt(0))) {
            System.out.println(id);
            throw new IllegalArgumentException("Invalid Station ID.");
        }

        this.id = id;
        this.name = name;
        this.lineName = lineName;
        this.stationNo = stationNo;
        this.x = x;
        this.y = y;
        this.selected = false;
        this.dragging = false;
    }

    //specialised for interchange: will have empty id
    public Station(double x, double y, String name, int stationNo) throws MissingFormatArgumentException {
        if (name.isEmpty()) throw new MissingFormatArgumentException("Provide a Line name.");

        this.id = "";
        this.name = name;
        this.lineName = "";
        this.stationNo = stationNo;
        this.x = x;
        this.y = y;
        this.selected = false;
        this.dragging = false;
    }

    public Station(Station other) {
        this(other.getX(), other.getY(), other.getStationID(), other.getName(), other.lineName, other.getStationNo());
    }

    public abstract Group draw();
    public abstract Group setHighlighted();
    //javafx.scene.shape.Circle stn = new javafx.scene.shape.Circle(x, y, 8.0);

    public void setEstimate(int x) {
        this.estimate = x;
    }
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

    public int getEstimate() {
        return this.estimate;
    }
    public String getStationID() {
        if (this.id.isEmpty() && this instanceof Interchange) {
            throw new UnsupportedOperationException("Station is Interchange: use getInterchangeIDs() instead");
        }
        return this.id;
    }
    public int getStationNo() {
        return this.stationNo;
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
        return this.id + " (" + this.name + ")";
    }

    @Override
    public int compareTo(Station other) { //sort in ascending order for max heap
        return Integer.compare(this.estimate, other.estimate);
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }
    public int hashCode() {
        return Objects.hash(id);
    }

     */
}