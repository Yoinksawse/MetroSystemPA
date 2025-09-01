package com.example.georgebra.Model.InputHandler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdgeData {
    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("time")
    private int time;

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }
}