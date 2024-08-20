module com.guilherme.stockcontrol.stockcontrol {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.guilherme.stockcontrol.stockcontrol to javafx.fxml;
    exports com.guilherme.stockcontrol.stockcontrol;
}