package com.guilherme.stockcontrol.stockcontrol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Classe principal do aplicativo JavaFX.
 * Extende a classe Application e inicializa a interface gráfica.
 */

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o arquivo FXML da interface do usuário
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // Cria uma nova cena com o layout carregado e define o tamanho da janela
        Scene scene = new Scene(fxmlLoader.load(), 720, 480);

        // Define o título da janela principal usando uma propriedade localizada
        stage.setTitle("Stock Control");
        stage.setScene(scene);
        stage.show(); //Exibe a janela
    }

    public static void main(String[] args) {
        launch(); // Inicia o aplicativo JavaFX
    }
}