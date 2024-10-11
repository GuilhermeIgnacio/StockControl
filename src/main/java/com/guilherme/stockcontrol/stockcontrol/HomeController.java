package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

public class HomeController implements Initializable {

    public TableView productTableView;

    //Table Columns
    public TableColumn<Product, String> productIdTableColumn;
    public TableColumn<Product, String> productNameTableColumn;
    public TableColumn<Product, String> productDescriptionTableColumn;
    public TableColumn<Product, Float> purchasePriceTableColumn;
    public TableColumn<Product, Float> retailPriceTableColumn;
    public TableColumn<Product, String> stockQuantityTableColumn;
    public TableColumn<Product, LocalDateTime> createdAtTableColumn;
    public TableColumn<Product, LocalDateTime> updatedAtTableColumn;
    //Table Columns

    public Button editBtn;
    public Button deleteBtn;
    public Button registerSaleBtn;
    public VBox contentArea;

    ObservableList<Product> itemList = FXCollections.observableArrayList();

    StockDAO stockDAO = new StockDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fetchItems();

        productIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        productNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        productDescriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("product_description"));
        stockQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("stock_quantity")); // Move to under retail_price
        purchasePriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
        retailPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("retail_price"));
        createdAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        updatedAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("updated_at"));

        purchasePriceTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Float> call(TableColumn<Product, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(currencyFormatter.format(item));
                        }
                    }
                };
            }
        });

        retailPriceTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Float> call(TableColumn<Product, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(currencyFormatter.format(item));
                        }
                    }
                };
            }
        });

        createdAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, LocalDateTime> call(TableColumn<Product, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(dateTimeFormatter));
                        }
                    }
                };
            }
        });

        updatedAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, LocalDateTime> call(TableColumn<Product, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(dateTimeFormatter));
                        }
                    }
                };
            }
        });

        productTableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

    }

    public void onAddNewItemBtnClicked(ActionEvent actionEvent) throws Exception {

        InsertItemApplication insertItemApplication = new InsertItemApplication();
        Stage stage = new Stage();

        insertItemApplication.start(stage);

        fetchItems();

    }

    private void fetchItems() {
        try {
            itemList.clear();

            for (Product product : stockDAO.fetchItems()) {

                Product newProduct = new Product();
                newProduct.setProduct_id(product.getProduct_id());
                newProduct.setProduct_name(product.getProduct_name());
                newProduct.setProduct_description(product.getProduct_description());
                newProduct.setStock_quantity(product.getStock_quantity());
                newProduct.setPurchase_price(product.getPurchase_price());
                newProduct.setRetail_price(product.getRetail_price());
                newProduct.setCreated_at(product.getCreated_at());
                newProduct.setUpdated_at(product.getUpdated_at());


                // Custom CellFactory para createdAt


                itemList.add(newProduct);
                productTableView.setItems(itemList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEditBtnClicked(ActionEvent actionEvent) throws Exception {

        if (productTableView.getSelectionModel().getSelectedItem() != null) {

            Product selectedItem = (Product) productTableView.getSelectionModel().getSelectedItem();

            InsertItemApplication application = new InsertItemApplication();
            Stage stage = new Stage();

            application.selectedItem = selectedItem;

            application.start(stage);

            fetchItems();

        }

    }

    public void onDeleteBtnClicked(ActionEvent actionEvent) {

        if (!productTableView.getSelectionModel().getSelectedItems().isEmpty()) {

            ObservableList<Product> selectedProducts = productTableView.getSelectionModel().getSelectedItems();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getProp().getString("delete.confirm.message"));
            alert.setHeaderText(getProp().getString("delete.warning"));

            ButtonType buttonTypeOne = new ButtonType(getProp().getString("delete.confirm.button"));
            ButtonType buttonTypeCancel = new ButtonType(getProp().getString("delete.cancel.button"), ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {

                selectedProducts.forEach(product ->
                        stockDAO.deleteItemById(product.getProduct_id())
                );

                fetchItems();

            }
        }
    }

    public void onRefreshBtnClicked(ActionEvent actionEvent) {
        fetchItems();
    }

    public void onItemClicked(MouseEvent mouseEvent) throws IOException {

        if (mouseEvent.getClickCount() == 2) {

            if (productTableView.getSelectionModel().getSelectedItem() != null) {
                Product selectedItem = (Product) productTableView.getSelectionModel().getSelectedItem();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("item-detail-view.fxml"));
                loader.setResources(getProp());

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(loader.load()));
                stage.setTitle(selectedItem.getProduct_name());
                stage.setMinWidth(720);
                stage.setMinHeight(520);

                ItemDetailController controller = loader.getController();
                controller.getItem(selectedItem);

                stage.showAndWait();
            }

        }


    }

    public void onRegisterSaleClicked(ActionEvent actionEvent) {

        if (!productTableView.getSelectionModel().getSelectedItems().isEmpty()) {

            ObservableList<Product> selectedProducts = productTableView.getSelectionModel().getSelectedItems();


            Dialog<Map<Product, String>> dialog = new Dialog<>();
            dialog.setWidth(300);
            dialog.setHeaderText(getProp().getString("register.sale.header.text"));

            ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

            VBox vBox = new VBox();
            vBox.setSpacing(10);

            Map<Product, TextField> productQuantityMap = new HashMap<>();

            String prompt = getProp().getString("register.sale.content.text");

            for (Product product : selectedProducts) {

                TextField soldQuantityField = new TextField();
                soldQuantityField.setPrefWidth(300);
                soldQuantityField.setPromptText(prompt + " " + product.getProduct_name());

                TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^\\d*$"));
                soldQuantityField.setTextFormatter(intTextFormatter);

                vBox.getChildren().add(soldQuantityField);
                productQuantityMap.put(product, soldQuantityField);
            }

            dialog.getDialogPane().setContent(vBox);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButton) {
                    Map<Product, String> productQuantities = new HashMap<>();
                    for (Product product : selectedProducts) {
                        String quantity = productQuantityMap.get(product).getText();
                        if (!quantity.isEmpty()) {
                            productQuantities.put(product, quantity);
                        }
                    }
                    return productQuantities;
                }
                return null;
            });

            Optional<Map<Product, String>> result = dialog.showAndWait();
            result.ifPresent(productQuantities -> {
                for (Map.Entry<Product, String> entry : productQuantities.entrySet()) {
                    Product product = entry.getKey();
                    int quantity = Integer.parseInt(entry.getValue());

                    stockDAO.insertSale(product, quantity);
                }

            });

        }

    }

    public void onAlertsClicked(ActionEvent actionEvent) throws Exception {
        loadContent("alerts-view.fxml", contentArea);
    }
}
