module com.gymfit.manager {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gymfit.manager.controller to javafx.fxml;
    exports com.gymfit.manager;
}