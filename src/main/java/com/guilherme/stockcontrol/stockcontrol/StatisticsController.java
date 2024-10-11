package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.MonthlySales;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class StatisticsController implements Initializable {

    public BorderPane borderPane;
    public DatePicker fromDatePicker;
    public DatePicker toDatePicker;
    public VBox vBox;
    private BarChart<String, Number> barChart;
    StockDAO stockDAO = new StockDAO();

    public XYChart.Series<String, Number> fetchMonthlySales(String startDate, String endDate) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<MonthlySales> monthlySales = stockDAO.fetchMonthlySales(startDate, endDate);

        for (MonthlySales sale : monthlySales) {
            String label = sale.getItemName() + " - " + sale.getFormattedMonth();
            series.getData().add(new XYChart.Data<>(label, sale.getTotalSales()));
        }

        return series;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        createMonthlySalesBarChart("", "");

    }

    private void createMonthlySalesBarChart(String startDate, String endDate) {
        if (barChart == null) {

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setLabel(getProp().getString("x.axis.label"));
            yAxis.setLabel(getProp().getString("y.axis.label"));

            barChart = new BarChart<>(xAxis, yAxis);

            vBox.getChildren().add(barChart);
        }
        XYChart.Series<String, Number> series = fetchMonthlySales(startDate, endDate);

        barChart.getData().clear();
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
    }

    public void onDateChanged(ActionEvent actionEvent) {

        String startDate = fromDatePicker.getEditor().getText();
        String endDate = toDatePicker.getEditor().getText();

        if (!startDate.isEmpty() && !endDate.isEmpty()) {

            //Todo: The date format output in date picker may change depending on user`s system
            //May need a different formater(or discard the use of one)
            //Try Catch may be a solution -> Try to format, if it fails send a default formatted date

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate localStartDate = LocalDate.parse(startDate, formatter);
            LocalDate localEndDate = LocalDate.parse(endDate, formatter);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedStartDate = localStartDate.format(dateTimeFormatter);
            String formattedEndDate = localEndDate.format(dateTimeFormatter);

            createMonthlySalesBarChart(formattedStartDate, formattedEndDate);
        } else {
            createMonthlySalesBarChart("", "");
        }
    }

    public void onClearButtonClicked(ActionEvent actionEvent) {

        //Evitar recriação desnecessária do gráfico

        if (!fromDatePicker.getEditor().getText().isEmpty() && !toDatePicker.getEditor().getText().isEmpty()) {

            fromDatePicker.getEditor().clear();
            toDatePicker.getEditor().clear();

            createMonthlySalesBarChart("", "");
        }

    }
}
