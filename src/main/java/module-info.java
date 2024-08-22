module com.guilherme.stockcontrol.stockcontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.guilherme.stockcontrol.stockcontrol to javafx.fxml;
    exports com.guilherme.stockcontrol.stockcontrol;
}