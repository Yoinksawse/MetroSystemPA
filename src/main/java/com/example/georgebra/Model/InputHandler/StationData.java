package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
public class StationData {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonProperty("isInterchange")
    private boolean isInterchange;

    @JsonProperty("lineNames")
    private ArrayList<String> lineNames = new ArrayList<>();

    @JsonProperty("lineIDs")
    private ArrayList<String> lineIDs = new ArrayList<>();;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public boolean isInterchange() { return isInterchange; }
    public void setInterchange(boolean interchange) { isInterchange = interchange; }

    //stuff for interchanges
    public ArrayList<String> getLineNames() { return lineNames; }
    public void setLineNames(ArrayList<String> lineNames) { this.lineNames = lineNames; }

    public ArrayList<String> getLineIDs() { return lineIDs; }
    public void setLineIDs(ArrayList<String> lineIDs) { this.lineIDs = lineIDs; }
}