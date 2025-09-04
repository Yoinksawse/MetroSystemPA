package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
public class InterchangeEntry {
    @JsonProperty("interchangeName")
    private String interchangeName;

    @JsonProperty("lineNames")
    private ArrayList<String> lineNames = new ArrayList<>();;

    @JsonProperty("lineIDs")
    private ArrayList<String> lineIDs = new ArrayList<>();;

    public String getInterchangeName() { return this.interchangeName;}
    public void setInterchangeName(String interchangeName) { this.interchangeName = interchangeName;}

    public ArrayList<String> getLineNames() { return this.lineNames;}
    public void setInterchangeName(ArrayList<String> lineNames) { this.lineNames = lineNames;}

    public ArrayList<String> getLineIDs() { return this.lineIDs;}
    public void setLineIDs(ArrayList<String> lineIDs) { this.lineIDs = lineIDs;}
}