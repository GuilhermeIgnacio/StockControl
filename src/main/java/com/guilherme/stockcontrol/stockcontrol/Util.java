package com.guilherme.stockcontrol.stockcontrol;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.io.FileInputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class Util {

    public static ResourceBundle getProp() throws Exception {
        FileInputStream file = new FileInputStream("src/main/resources/com/guilherme/stockcontrol/stockcontrol/strings.properties");
        return new PropertyResourceBundle(file);
    }

    public static void addTextLimiter(final TextField textField, final int maxLength) {
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (textField.getText().length() > maxLength) {
                String s = textField.getText().substring(0, maxLength);
                textField.setText(s);
            }
        });
    }

    public static UnaryOperator<TextFormatter.Change> getChangeUnaryOperator(String regex) {
        return change -> {

            String newText = change.getControlNewText();

            if (newText.matches(regex)) {
                return change;
            }
            return null;
        };
    }

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
    public static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

}
