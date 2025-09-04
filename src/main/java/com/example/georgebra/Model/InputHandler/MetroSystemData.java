package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

//[metroID]_Line_[lineCode].json
//or
//[metroID]_Interchanges.json

//SGMTR.json will contain: list of strings for all line IDs

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
public class MetroSystemData {
    //the below 2 arrays contain data that will be collected by IOHandler
    //private List<InterchangeEntry> interchanges; //will be iterated through and created
    //private List<MetroLineData> metroLineDataList;

    @JsonProperty("cityName")
    private String cityName; //e.g. SGMTR

    @JsonProperty("metroLines")
    //references to system Line and system Interchange Json
    //will be iterated through and json directory scanned for the line json
    private ArrayList<String> metroLineIDs = new ArrayList<>();

    public String getCityName() { return this.cityName;}
    public void setCityName(String cityName) {this.cityName = cityName;}

    public ArrayList<String> getMetroLineIDs() { return metroLineIDs; }
    public void setMetroLineIDs(ArrayList<String> metroLineIDs) { this.metroLineIDs = metroLineIDs; }
}
