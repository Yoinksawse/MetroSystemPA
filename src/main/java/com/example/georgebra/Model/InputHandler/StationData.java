package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StationData {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("x")
    private int x;

    @JsonProperty("y")
    private int y;

    @JsonProperty("isInterchange")
    private boolean isInterchange;

    @JsonProperty("lineNames")
    private List<String> lineNames;

    @JsonProperty("lineIDs")
    private List<String> lineIDs;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public boolean isInterchange() { return isInterchange; }
    public void setInterchange(boolean interchange) { isInterchange = interchange; }

    //stuff for interchanges
    public List<String> getLineNames() { return lineNames; }
    public void setLineNames(List<String> lineNames) { this.lineNames = lineNames; }

    public List<String> getLineIDs() { return lineIDs; }
    public void setLineIDs(List<String> lineIDs) { this.lineIDs = lineIDs; }
}