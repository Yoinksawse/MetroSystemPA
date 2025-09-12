package com.example.georgebra.Model.GraphTheoryHandler;

public class Node implements Comparable<Node>{
    int index;
    int estimate = Integer.MAX_VALUE;

    public Node(int index) {
        this.index = index;
    }
    public Node(int index, int estimate) {
        this.index = index;
        this.estimate = estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }
    public int getEstimate() {
        return this.estimate;
    }

    @Override
    public int compareTo(Node other) {
        return Long.compare(this.estimate, other.estimate);
    }
}
