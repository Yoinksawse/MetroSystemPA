package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
public class MetroLineData {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("hasInterchange")
    private boolean hasInterchange;

    @JsonProperty("stationCodes")
    private ArrayList<String> stationCodes = new ArrayList<>();

    @JsonProperty("colour")
    private String colour;

    @JsonProperty("lineType")
    private String lineType;

    @JsonProperty("stations")
    private ArrayList<StationData> stations = new ArrayList<>();

    @JsonProperty("edges")
    private ArrayList<EdgeData> edges = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getColour() { return colour; }
    public void setColour(String colourRGB) { this.colour = colourRGB; }

    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }

    public boolean hasInterchange() { return hasInterchange; }
    public void setHasInterchange(boolean hasInterchange) { this.hasInterchange = hasInterchange; }

    public ArrayList<String> getStationCodes() { return stationCodes; }
    public void setStationCodes(ArrayList<String> stationCodes) { this.stationCodes = stationCodes; }

    public ArrayList<StationData> getStations() { return stations; }
    public void setStations(ArrayList<StationData> stations) { this.stations = stations; }

    public ArrayList<EdgeData> getEdges() { return edges; }
    public void setEdges(ArrayList<EdgeData> edges) { this.edges = edges; }
}