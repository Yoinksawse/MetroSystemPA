package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
public class InterchangesData {
    @JsonProperty("interchanges")
    private ArrayList<InterchangeEntry> interchanges = new ArrayList<>();;

    public ArrayList<InterchangeEntry> getInterchanges() { return this.interchanges;}
    public void setLineIDs(ArrayList<InterchangeEntry> interchanges) { this.interchanges = interchanges;}
}
