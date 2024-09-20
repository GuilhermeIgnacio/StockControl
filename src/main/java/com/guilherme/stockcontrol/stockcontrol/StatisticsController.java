package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    public BorderPane borderPane;
    StockDAO stockDAO = new StockDAO();

    public ObservableList<PieChart.Data> fetchItems() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        List<Item> items = stockDAO.fetchItems();

        for (Item item : items) {
            pieChartData.add(new PieChart.Data(item.getItemName(), item.getItemSales()));
        }

        return pieChartData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<PieChart.Data> pieChartData = fetchItems();

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Title");
        pieChart.setClockwise(true);
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);

        borderPane.setCenter(pieChart);
    }
}
