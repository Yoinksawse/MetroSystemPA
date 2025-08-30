module com.example.georgebra {
    requires javafx.controls;
    requires javafx.fxml;
    requires exp4j;
    requires java.sql;


    opens com.example.georgebra to javafx.fxml;
    exports com.example.georgebra;
    exports com.example.georgebra.Controller;
    opens com.example.georgebra.Controller to javafx.fxml;
    exports com.example.georgebra.Model;
    opens com.example.georgebra.Model to javafx.fxml;
}