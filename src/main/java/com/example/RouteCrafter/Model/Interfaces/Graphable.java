package com.example.RouteCrafter.Model.Interfaces;

import com.example.RouteCrafter.Model.GraphTheoryHandler.GraphHandler;
import com.example.RouteCrafter.Model.StationTypes.Station;

import java.util.HashMap;
import java.util.HashSet;

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
