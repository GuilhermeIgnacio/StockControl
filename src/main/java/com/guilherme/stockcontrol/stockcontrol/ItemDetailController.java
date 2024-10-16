package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.scene.control.Label;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

public class ItemDetailController {
    public Label detailIdLabel;
    public Label detailNameLabel;
    public Label detailDescriptionLabel;
    public Label detailQuantityLabel;
    public Label detailPurchasePriceLabel;
    public Label detailRetailPriceLabel;
    public Label detailProfitMarginLabel;
    public Label detailCreatedAtLabel;
    public Label detailUpdatedAtLabel;
    public Label detailSalesLabel;
    public Label saleAmount;

    StockDAO stockDAO = new StockDAO();

    public void getItem(Product product) {
        detailIdLabel.setText(String.valueOf(product.getProduct_id()));

        detailNameLabel.setText(product.getProduct_name());
        detailDescriptionLabel.setText(product.getProduct_description());

        detailQuantityLabel.setText(String.valueOf(product.getStock_quantity()));
        detailSalesLabel.setText(String.valueOf(stockDAO.getProductTotalSales(product.getProduct_id())));

        detailPurchasePriceLabel.setText(currencyFormatter.format(product.getPurchase_price()));
        detailRetailPriceLabel.setText(currencyFormatter.format(product.getRetail_price()));

        float profitMargin = ((product.getRetail_price() - product.getPurchase_price()) / product.getRetail_price()) * 100f;

        String formattedMarginProfit = String.format(getProp().getString("detail.per.unit"), profitMargin, currencyFormatter.format(product.getRetail_price() - product.getPurchase_price()));

        detailProfitMarginLabel.setText(formattedMarginProfit);

        saleAmount.setText(currencyFormatter.format(stockDAO.getProductSaleAmount(product.getProduct_id())));

        String formattedCreatedAt = product.getCreated_at().format(dateTimeFormatter);
        String formattedUpdated = product.getUpdated_at().format(dateTimeFormatter);

        detailCreatedAtLabel.setText(formattedCreatedAt);
        detailUpdatedAtLabel.setText(formattedUpdated);

    }
}
