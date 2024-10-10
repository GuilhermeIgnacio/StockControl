package com.guilherme.stockcontrol.stockcontrol;

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

    public void getItem(Product item) {
        detailIdLabel.setText(String.valueOf(item.getProduct_id()));

        detailNameLabel.setText(item.getProduct_name());
        detailDescriptionLabel.setText(item.getProduct_description());

        detailQuantityLabel.setText(String.valueOf(item.getStock_quantity()));
//        detailSalesLabel.setText(String.valueOf(item.getItemSales()));

        detailPurchasePriceLabel.setText(currencyFormatter.format(item.getPurchase_price()));
        detailRetailPriceLabel.setText(currencyFormatter.format(item.getRetail_price()));

        float profitMargin = ((item.getRetail_price() - item.getPurchase_price()) / item.getRetail_price()) * 100f;

        String formattedMarginProfit = String.format(getProp().getString("detail.per.unit"), profitMargin, currencyFormatter.format(item.getRetail_price() - item.getPurchase_price()));

        detailProfitMarginLabel.setText(formattedMarginProfit);

        String formattedCreatedAt = item.getCreated_at().format(dateTimeFormatter);
        String formattedUpdated = item.getUpdated_at().format(dateTimeFormatter);

        detailCreatedAtLabel.setText(formattedCreatedAt);
        detailUpdatedAtLabel.setText(formattedUpdated);

    }
}
