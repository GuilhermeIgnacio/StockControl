package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.guilherme.stockcontrol.stockcontrol.Util.currencyFormatter;

public class ItemDetailController {
    public Label detailIdLabel;
    public Label detailNameLabel;
    public Label detailDescriptionLabel;
    public Label detailQuantityLabel;
    public Label detailPriceLabel;
    public Label detailCreatedAtLabel;
    public Label detailUpdatedAtLabel;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void getItem(Item item) {
        detailIdLabel.setText(String.valueOf(item.getId()));
        detailNameLabel.setText(item.getItemName());
        detailDescriptionLabel.setText(item.getItemDescription());
        detailQuantityLabel.setText(String.valueOf(item.getItemQuantity()));
        detailPriceLabel.setText(currencyFormatter.format(item.getItemPrice()));

        String formattedCreatedAt = item.getCreatedAt().format(dateTimeFormatter);
        String formattedUpdated = item.getUpdatedAt().format(dateTimeFormatter);

        detailCreatedAtLabel.setText(formattedCreatedAt);
        detailUpdatedAtLabel.setText(formattedUpdated);

    }
}
