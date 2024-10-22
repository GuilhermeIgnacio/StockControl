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

    // Metodo para carregar o arquivo de propriedades (strings.properties) para internacionalização (i18n)
    public static ResourceBundle getProp() {
        FileInputStream file;
        try {
            file = new FileInputStream("src/main/resources/com/guilherme/stockcontrol/stockcontrol/strings.properties");
            return new PropertyResourceBundle(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Metodo para limitar o número de caracteres de um TextField
    public static void addTextLimiter(final TextField textField, final int maxLength) {
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (textField.getText().length() > maxLength) {
                String s = textField.getText().substring(0, maxLength);
                textField.setText(s);
            }
        });
    }

    // Metodo para retornar um operador que valida a entrada de texto com base em uma expressão regular (regex)
    public static UnaryOperator<TextFormatter.Change> getChangeUnaryOperator(String regex) {
        return change -> {

            String newText = change.getControlNewText();

            if (newText.matches(regex)) {
                return change;
            }
            return null;
        };
    }

    // Formatter para datas, configurado para o padrão "dd/MM/yyyy HH:mm"
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Localização configurada para o Brasil (pt-BR)
    public static Locale locale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();

    // Formatação monetária configurada para o Brasil
    public static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    // Metodo para carregar um arquivo FXML em um VBox específico
    public static void loadContent(String fxml, VBox contentArea) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Util.class.getResource(fxml)));
        loader.setResources(getProp());
        Parent newContent = loader.load();
        contentArea.getChildren().setAll(newContent);
    }

    // Metodo para formatar uma data string (de "dd/MM/yyyy" para "yyyy-MM-dd")
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

    // Variável para controlar se um diálogo já foi exibido (para evitar repetições)
    public static boolean dialogShown = false;

    // Metodo para exibir um diálogo de alerta genérico
    public static void genericAlertDialog(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);

        if (!title.isEmpty()) {
            alert.setTitle(title);
        }

        if (!headerText.isEmpty()) {
            alert.setHeaderText(headerText);
        }

        if (!contentText.isEmpty()) {
            alert.setContentText(contentText);
        }

        alert.show();

    }

}
