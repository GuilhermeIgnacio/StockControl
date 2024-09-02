package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.scene.control.Label;

import static com.guilherme.stockcontrol.stockcontrol.Util.currencyFormatter;
import static com.guilherme.stockcontrol.stockcontrol.Util.dateTimeFormatter;

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

    public void getItem(Item item) {
        detailIdLabel.setText(String.valueOf(item.getId()));

        detailNameLabel.setText(item.getItemName());
        detailDescriptionLabel.setText(item.getItemDescription());

        detailQuantityLabel.setText(String.valueOf(item.getItemQuantity()));

        detailPurchasePriceLabel.setText(currencyFormatter.format(item.getPurchasePrice()));
        detailRetailPriceLabel.setText(currencyFormatter.format(item.getRetailPrice()));

        float profitMargin = ((item.getRetailPrice() - item.getPurchasePrice()) / item.getRetailPrice()) * 100f;

        String formattedMarginProfit = String.format("%.1f%% (%s per unit)", profitMargin, currencyFormatter.format(item.getRetailPrice() - item.getPurchasePrice()));

        detailProfitMarginLabel.setText(formattedMarginProfit);

        String formattedCreatedAt = item.getCreatedAt().format(dateTimeFormatter);
        String formattedUpdated = item.getUpdatedAt().format(dateTimeFormatter);

        detailCreatedAtLabel.setText(formattedCreatedAt);
        detailUpdatedAtLabel.setText(formattedUpdated);

    }
}
