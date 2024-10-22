package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

/**
 * Classe que representa a aplicação de inserção de itens.
 * Estende a classe Application do JavaFX e é responsável por criar uma janela
 * para adicionar ou editar produtos.
 */
public class InsertItemApplication extends Application {

    // Varável utilizada quando um produto é selecionado para edição, não é utilizada ao adicionar um novo item
    Product selectedItem;

    /**
     * Metodo principal que inicia a aplicação JavaFX.
     * @param stage O palco principal da aplicação
     * @throws Exception Se ocorrer um erro durante o carregamento da interface
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o arquivo FXML que define a interface de adicionar item
        FXMLLoader fxmlLoader = new FXMLLoader(InsertItemApplication.class.getResource("add-item-view.fxml"));

        fxmlLoader.setResources(getProp());

        Scene scene = new Scene(fxmlLoader.load()); // Cria a cena a partir do arquivo FXML

        // Define o título da janela com base se um item foi selecionado ou não
        if (selectedItem == null) {
            stage.setTitle(getProp().getString("insert.window.title")); // Título para inserir um novo item
        } else {
            stage.setTitle(getProp().getString("edit.window.title")); // Título para editar um item existente
        }
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(500.0);
        stage.setMinHeight(400.0);

        // Se um item foi selecionado, passa o item para o controlador
        if (selectedItem != null) {
            InsertProductController controller = fxmlLoader.getController();
            controller.getProduct(selectedItem); // Preenche os campos com os dados do item selecionado
        }

        stage.showAndWait(); // Exibe a janela e aguarda até que ela seja fechada
    }
}
