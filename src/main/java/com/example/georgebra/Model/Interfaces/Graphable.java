package com.example.georgebra.Model.Interfaces;

import com.example.georgebra.Model.GraphTheoryHandler.GraphHandler;
import com.example.georgebra.Model.StationTypes.Interchange;
import com.example.georgebra.Model.StationTypes.Station;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public interface Graphable {
    void addStation(Station newStation);
    void addEdge(Station u, Station v, int weight);
    GraphHandler getGraphHandler();
    HashSet<Station> getStationList();

    HashMap<String, Station> getStationNameToStationMap();
    HashMap<Integer, String> getIndexToStationNameMap();
    HashMap<String, String> getIdToStationNameMap();

    default Station getStationByName(String name) {
        HashMap<String, Station> stationNameToStationMap = getStationNameToStationMap();
        if (stationNameToStationMap == null) return null;
        return stationNameToStationMap.get(name.toLowerCase());
    }
}
