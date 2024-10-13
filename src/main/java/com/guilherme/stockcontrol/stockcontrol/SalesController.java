package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.guilherme.stockcontrol.stockcontrol.Util.currencyFormatter;
import static com.guilherme.stockcontrol.stockcontrol.Util.dateTimeFormatter;

public class SalesController implements Initializable {

    public Label totalIncomeLabel;
    public Label monthIncomeLabel;
    public Label yearIncomeLabel;
    public VBox vBox;

    public TableView tableView;

    public TableColumn<SaleProduct, Date> saleDateColumn;
    public TableColumn<SaleProduct, String> productNameColumn;
    public TableColumn<SaleProduct, Float> salePriceColumn;
    public DatePicker fromDatePicker;
    public DatePicker toDatePicker;
    public ChoiceBox productsChoiceBox;

    ObservableList<SaleProduct> saleProductsList = FXCollections.observableArrayList();

    StockDAO stockDAO = new StockDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        createOrdersTable();

        fillProductsChoiceBox();

        totalIncomeLabel.setText(currencyFormatter.format(stockDAO.totalIncome()));
        monthIncomeLabel.setText(currencyFormatter.format(stockDAO.monthIncome()));
        yearIncomeLabel.setText(currencyFormatter.format(stockDAO.yearIncome()));

    }

    private void fetchSales() {

        String productName = (String) productsChoiceBox.getValue();

        String startDate = fromDatePicker.getEditor().getText();

        String endDate = toDatePicker.getEditor().getText();

        try {
            saleProductsList.clear();

            for (SaleProduct saleProduct : stockDAO.fetchSaleProduct(productName, startDate, endDate)) {

                SaleProduct newSaleProduct = new SaleProduct();

                newSaleProduct.setSaleId(saleProduct.getSaleId());
                newSaleProduct.setProductName(saleProduct.getProductName());
                newSaleProduct.setSalePrice(saleProduct.getSalePrice());
                newSaleProduct.setSaleDate(saleProduct.getSaleDate());

                saleProductsList.add(newSaleProduct);
                tableView.setItems(saleProductsList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void createOrdersTable() {
        fetchSales();

        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

        saleDateColumn.setCellFactory(_ -> new TableCell<>() {
            private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        salePriceColumn.setCellFactory(_ -> new TableCell<>() {

            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(item));
                }
            }
        });
    }

    private void fillProductsChoiceBox() {

        for (String productName : saleProductsList.stream().map(SaleProduct::getProductName).collect(Collectors.toSet())) {
            productsChoiceBox.getItems().add(productName);
        }

    }


    public void onSearchBtnClicked(ActionEvent actionEvent) {
        createOrdersTable();
    }
}
