package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Sale;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SalesController implements Initializable {

    public Label totalIncomeLabel;
    public Label monthIncomeLabel;
    public Label yearIncomeLabel;
    StockDAO stockDAO = new StockDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        totalIncomeLabel.setText(currencyFormatter.format(stockDAO.totalIncome()));
        monthIncomeLabel.setText(currencyFormatter.format(stockDAO.monthIncome()));
        yearIncomeLabel.setText(currencyFormatter.format(stockDAO.yearIncome()));


    }
}
