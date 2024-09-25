package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class StatisticsController implements Initializable {

    public BorderPane borderPane;
    StockDAO stockDAO = new StockDAO();

    public XYChart.Series<String, Number> fetchItems() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<Item> items = stockDAO.fetchItems();

        for (Item item : items) {
            series.getData().add(new XYChart.Data<>(item.getItemName(), item.getItemSales()));
        }

//        series.setName("Products Sales"); Legend
        return series;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel(getProp().getString("x.axis.label"));
        yAxis.setLabel(getProp().getString("y.axis.label"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        //barChart.setTitle("Chart Title");

        XYChart.Series<String, Number> series = fetchItems();

        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        borderPane.setCenter(barChart);

    }
}
