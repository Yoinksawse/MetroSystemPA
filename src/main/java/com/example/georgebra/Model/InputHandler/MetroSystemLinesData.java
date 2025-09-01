package com.example.georgebra.Model.InputHandler;

import com.example.georgebra.Model.LineTypes.MetroLine;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MetroSystemLinesData {
    @JsonProperty("metroLines")
    private List<MetroLine> metroLines = new ArrayList<>();

    public List<MetroLine> getMetroLines() { return metroLines; }
    public void setMetroLines(List<MetroLine> metroLines) { this.metroLines = metroLines; }
}
