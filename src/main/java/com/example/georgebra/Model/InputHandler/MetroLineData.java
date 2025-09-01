package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MetroLineData {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("isInterchange")
    private boolean isInterchange;

    @JsonProperty("stationCodes")
    private List<String> stationCodes = new ArrayList<>();

    @JsonProperty("stations")
    private List<StationData> stations = new ArrayList<>();

    @JsonProperty("edges")
    private List<EdgeData> edges = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public boolean isInterchange() { return isInterchange; }
    public void setInterchange(boolean interchange) { isInterchange = interchange; }

    public List<String> getStationCodes() { return stationCodes; }
    public void setStationCodes(List<String> stationCodes) { this.stationCodes = stationCodes; }

    public List<StationData> getStations() { return stations; }
    public void setStations(List<StationData> stations) { this.stations = stations; }

    public List<EdgeData> getEdges() { return edges; }
    public void setEdges(List<EdgeData> edges) { this.edges = edges; }
}