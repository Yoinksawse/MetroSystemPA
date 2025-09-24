package com.example.RouteCrafter.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
public class MetroLineData {
    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    //@JsonProperty("serviceStartTimings")
    //private ArrayList<LocalTime> startTimings;
    //@JsonProperty("serviceStopTimings")
    //private ArrayList<LocalTime> stopTimings;

    @JsonProperty("colour")
    private String colour;

    @JsonProperty("lineType")
    private String lineType;

    @JsonProperty("stations")
    private ArrayList<MetroStationData> stations = new ArrayList<>();

    @JsonProperty("edges")
    private ArrayList<EdgeData> edges = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getColour() { return colour; }
    public void setColour(String colourRGB) { this.colour = colourRGB; }

    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }

    //public ArrayList<String> getStationCodes() { return stationCodes; }
    //public void setStationCodes(ArrayList<String> stationCodes) { this.stationCodes = stationCodes; }

    public ArrayList<MetroStationData> getStations() { return stations; }
    public void setStations(ArrayList<MetroStationData> stations) { this.stations = stations; }

    public ArrayList<EdgeData> getEdges() { return edges; }
    public void setEdges(ArrayList<EdgeData> edges) { this.edges = edges; }
}