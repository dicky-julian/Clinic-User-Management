module com.example.clinicmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.clinicmanagement to javafx.fxml;
    exports com.example.clinicmanagement;
}