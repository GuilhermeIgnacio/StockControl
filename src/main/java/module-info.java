module com.guilherme.stockcontrol.stockcontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mariadb.jdbc;
    requires java.desktop;

    opens com.guilherme.stockcontrol.stockcontrol to javafx.fxml;
    opens com.guilherme.stockcontrol.stockcontrol.model;
    exports com.guilherme.stockcontrol.stockcontrol;

}