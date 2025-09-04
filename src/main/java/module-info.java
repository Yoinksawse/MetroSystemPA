module com.example.georgebra {
    requires javafx.controls;
    requires javafx.fxml;
    requires exp4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;


    opens com.example.georgebra to javafx.fxml;
    exports com.example.georgebra;
    exports com.example.georgebra.Controller;
    opens com.example.georgebra.Controller to javafx.fxml;
    exports com.example.georgebra.Model;
    opens com.example.georgebra.Model to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.georgebra.Model.InputHandler;
    opens com.example.georgebra.Model.InputHandler to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.georgebra.Model.TesterClasses;
    opens com.example.georgebra.Model.TesterClasses to javafx.fxml;
}



