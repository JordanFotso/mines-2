module com.example.promines {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.promines to javafx.fxml;
    opens com.example.promines.controller to javafx.fxml;
    
    exports com.example.promines;
    exports com.example.promines.model;
    exports com.example.promines.view;
    exports com.example.promines.controller;
}