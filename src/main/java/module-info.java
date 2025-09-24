module com.example.RouteCrafter {
    requires javafx.controls;
    requires javafx.fxml;
    requires exp4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires java.naming;
    requires java.management;
    requires jdk.unsupported.desktop;
    requires jdk.compiler;
    requires java.sql;
    requires org.apache.commons.io;
    requires javafx.media;


    opens com.example.RouteCrafter to javafx.fxml;
    exports com.example.RouteCrafter;
    exports com.example.RouteCrafter.Controller;
    opens com.example.RouteCrafter.Controller to javafx.fxml;
    exports com.example.RouteCrafter.Model;
    opens com.example.RouteCrafter.Model to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.RouteCrafter.Model.InputHandler;
    opens com.example.RouteCrafter.Model.InputHandler to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.RouteCrafter.Model.TesterClasses;
    opens com.example.RouteCrafter.Model.TesterClasses to javafx.fxml;
    exports com.example.RouteCrafter.Model.GraphTheoryHandler;
    opens com.example.RouteCrafter.Model.GraphTheoryHandler to com.fasterxml.jackson.databind, javafx.fxml;
    exports com.example.RouteCrafter.Model.StationTypes;
    exports com.example.RouteCrafter.Model.LineTypes;
    exports com.example.RouteCrafter.Model.Interfaces;
    opens com.example.RouteCrafter.Model.Interfaces to com.fasterxml.jackson.databind, javafx.fxml;


}



