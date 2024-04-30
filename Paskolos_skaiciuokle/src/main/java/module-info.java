module com.example.skaic {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.skaic to javafx.fxml;
    opens Duomenys;
    exports com.example.skaic;
}