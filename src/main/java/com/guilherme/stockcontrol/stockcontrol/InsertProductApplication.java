package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Classe que representa a aplicação de inserção de itens.
 * Estende a classe Application do JavaFX e é responsável por criar uma janela
 * para adicionar ou editar produtos.
 */
public class InsertProductApplication extends Application {

    // Varável utilizada quando um produto é selecionado para edição, não é utilizada ao adicionar um novo produto
    Product selectedProduct;

    /**
     * Metodo principal que inicia a aplicação JavaFX.
     * @param stage O palco principal da aplicação
     * @throws Exception Se ocorrer um erro durante o carregamento da interface
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o arquivo FXML que define a interface de adicionar produto
        FXMLLoader fxmlLoader = new FXMLLoader(InsertProductApplication.class.getResource("add-item-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load()); // Cria a cena a partir do arquivo FXML

        // Define o título da janela com base se um produto foi selecionado ou não
        if (selectedProduct == null) {
            stage.setTitle("Adicionar Produto"); // Título para inserir um novo produto
        } else {
            stage.setTitle("Editar Produto"); // Título para editar um produto existente
        }
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(500.0);
        stage.setMinHeight(400.0);

        // Se um produto foi selecionado, passa o produto para o controlador
        if (selectedProduct != null) {
            InsertProductController controller = fxmlLoader.getController();
            controller.getProduct(selectedProduct); // Preenche os campos com os dados do produto selecionado
        }

        stage.showAndWait(); // Exibe a janela e aguarda até que ela seja fechada
    }
}
