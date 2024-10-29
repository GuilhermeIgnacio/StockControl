package com.guilherme.stockcontrol.stockcontrol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

    @FXML
    public VBox contentArea; // Área de conteúdo onde as views são carregadas

    /**
     * Inicializa o controlador após seu elemento raiz tiver sido completamente processado
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            loadContent("home-view.fxml", contentArea); // Carrega a view inicial
        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);

            // Exibe um alerta de erro para o usuário, informando sobre a falha no carregamento
            genericAlertDialog(Alert.AlertType.ERROR, "", "Error Loading Home View", runtimeException.getMessage());

            //Lança a exceção
            throw runtimeException;
        }

    }

    /**
     * Manipulador de eventos para o botão home.
     * Carrega a view home na área de conteúdo.
     *
     * @param actionEvent O evento gerado pelo clique do botão.
     */
    public void onHomeBtnClicked(ActionEvent actionEvent) {

        try {
            loadContent("home-view.fxml", contentArea);
        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);

            // Exibe um alerta de erro para o usuário, informando sobre a falha no carregamento
            genericAlertDialog(Alert.AlertType.ERROR, "", "Error Loading Home View", runtimeException.getMessage());

            // Lança a exceção
            throw runtimeException;
        }

    }

    /**
     * Carrega a view de compras
     */
    public void onBuysButtonClicked(ActionEvent actionEvent) {
        try {
            loadContent("buys-view.fxml", contentArea);
        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);

            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao carregar compras", e.getMessage());

            throw runtimeException;
        }
    }

    /**
     * Manipulador de eventos para o botão de vendas.
     * Carrega a view de vendas na área de conteúdo.
     *
     * @param actionEvent O evento gerado pelo clique do botão.
     */
    public void onSalesButtonClicked(ActionEvent actionEvent) {

        try {
            loadContent("sales-view.fxml", contentArea);
        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);

            // Exibe um alerta de erro para o usuário, informando sobre a falha no carregamento
            genericAlertDialog(Alert.AlertType.ERROR, "", "Error Loading Sales View", runtimeException.getMessage());

            // Lança a exceção
            throw runtimeException;
        }

    }


}