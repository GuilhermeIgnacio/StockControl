package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Sale;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.currencyFormatter;

public class SalesController implements Initializable {

    public Label totalIncomeLabel;
    public Label monthIncomeLabel;
    public Label yearIncomeLabel;
    public VBox vBox;
    StockDAO stockDAO = new StockDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        totalIncomeLabel.setText(currencyFormatter.format(stockDAO.totalIncome()));
        monthIncomeLabel.setText(currencyFormatter.format(stockDAO.monthIncome()));
        yearIncomeLabel.setText(currencyFormatter.format(stockDAO.yearIncome()));

        for (SaleProduct saleProduct : stockDAO.fetchSaleProduct("", "", "")) {

            HBox hBox = new HBox();
            hBox.setSpacing(10);

            Label productNameLabel = new Label(saleProduct.getProductName());
            Label salePriceLabel = new Label(currencyFormatter.format(saleProduct.getSalePrice()));
            Label saleDateLabel = new Label(saleProduct.getSaleDate().toString());

            Label dotLabel = new Label("•");
            Label dotLabel2 = new Label("•");

            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(productNameLabel, dotLabel, salePriceLabel, dotLabel2, saleDateLabel);

            vBox.getChildren().add(hBox);

        }


    }
}
