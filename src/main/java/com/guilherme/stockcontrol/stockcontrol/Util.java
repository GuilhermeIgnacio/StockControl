package com.guilherme.stockcontrol.stockcontrol;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;

public class Util {

    public static ResourceBundle getProp() {
        FileInputStream file;
        try {
            file = new FileInputStream("src/main/resources/com/guilherme/stockcontrol/stockcontrol/strings.properties");
            return new PropertyResourceBundle(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public static void loadContent(String fxml, VBox contentArea) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Util.class.getResource(fxml)));
        loader.setResources(getProp());
        Parent newContent = loader.load();
        contentArea.getChildren().setAll(newContent);
    }

    public static String formatDate(String dateString) {

        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

            return myFormat.format(fromUser.parse(dateString));

        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Parse Exception");
            alert.setContentText(e.getMessage());
            return "";
        }


    }

    public static boolean dialogShown = false;

}
