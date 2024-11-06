package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.MonthlySales;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class StatisticsController implements Initializable {

    public BorderPane borderPane;
    StockDAO stockDAO = new StockDAO();

    /*
    public XYChart.Series<String, Number> fetchItems() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<Item> items = stockDAO.fetchItems();

        for (Item item : items) {
            series.getData().add(new XYChart.Data<>(item.getItemName(), item.getItemSales()));
        }

        return series;
    }
     */

    public XYChart.Series<String, Number> fetchMonthlySales() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<MonthlySales> monthlySales = stockDAO.fetchSales();

        for (MonthlySales sale : monthlySales) {
            String label = sale.getItemName() + " - " + sale.getFormattedMonth();
            series.getData().add(new XYChart.Data<>(label, sale.getTotalSales()));
        }

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

        XYChart.Series<String, Number> series = fetchMonthlySales();

        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        borderPane.setCenter(barChart);

    }
}
