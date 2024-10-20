package com.guilherme.stockcontrol.stockcontrol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.genericAlertDialog;
import static com.guilherme.stockcontrol.stockcontrol.Util.loadContent;

/**
 * Controlador da interface do usuário para a aplicação Hello.
 * Esta classe é responsável por gerenciar eventos e a lógica de navegação entre as diferentes telas.
 */

public class HelloController implements Initializable {

    public Button statisticsBtn; //Todo: Remove this unused reference
    public VBox contentArea; // Área de conteúdo onde as views são carregadas
    public Button homeBtn; //Todo: Remove this unused reference

    /**
     * Inicializa o controlador após seu elemento raiz tiver sido completamente processado
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            loadContent("home-view.fxml", contentArea); // Carrega a view inicial
        } catch (Exception e) {
            //Todo: Extract Error Message to string.properties
            genericAlertDialog(Alert.AlertType.ERROR, "", "Error Loadign Home View", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * Manipulador de eventos para o botão de estatísticas.
     * Carrega a view de estatísticas na área de conteúdo.
     *
     * @param actionEvent O evento gerado pelo clique do botão.
     */

    public void onStatisticsBtnClicked(ActionEvent actionEvent) throws Exception {
        loadContent("statistics-view.fxml", contentArea);
    }

    /**
     * Manipulador de eventos para o botão inicial.
     * Carrega a view inicial na área de conteúdo.
     *
     * @param actionEvent O evento gerado pelo clique do botão.
     */

    public void onHomeBtnClicked(ActionEvent actionEvent) throws Exception {
        loadContent("home-view.fxml", contentArea);
    }

    /**
     * Manipulador de eventos para o botão de vendas.
     * Carrega a view de vendas na área de conteúdo.
     *
     * @param actionEvent O evento gerado pelo clique do botão.
     */

    public void onSalesButtonClicked(ActionEvent actionEvent) throws Exception {
        loadContent("sales-view.fxml", contentArea);
    }
}