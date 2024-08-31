package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.currencyFormatter;
import static com.guilherme.stockcontrol.stockcontrol.Util.formatter;

public class HelloController implements Initializable {

    public Button addNewItemBtn;
    public TableView itemTableView;

    //Table Columns
    public TableColumn<Item, String> idTableColumn;
    public TableColumn<Item, String> nameTableColumn;
    public TableColumn<Item, String> descriptionTableColumn;
    public TableColumn<Item, String> quantityTableColumn;
    public TableColumn<Item, Float> priceTableColumn;
    public TableColumn<Item, LocalDateTime> createdAtTableColumn;
    public TableColumn<Item, LocalDateTime> updatedAtTableColumn;
    //Table Columns

    public Button editBtn;
    public Button deleteBtn;

    ObservableList<Item> itemList = FXCollections.observableArrayList();

    StockDAO stockDAO = new StockDAO();

    public void onAddNewItemBtnClicked(ActionEvent actionEvent) throws Exception {

        InsertItemApplication insertItemApplication = new InsertItemApplication();
        Stage stage = new Stage();

        insertItemApplication.start(stage);

        fetchItems();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fetchItems();

        //Todo: Add purchasePrice and retailPrice columns
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemQuantity"));
        priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        createdAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        priceTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Item, Float> call(TableColumn<Item, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(currencyFormatter.format(item));
//                            setText("R$" + item);
                        }
                    }
                };
            }
        });

        createdAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Item, LocalDateTime> call(TableColumn<Item, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(formatter));
                        }
                    }
                };
            }
        });

        updatedAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Item, LocalDateTime> call(TableColumn<Item, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(formatter));
                        }
                    }
                };
            }
        });

    }

    private void fetchItems() {
        try {
            itemList.clear();

            for (Item item : stockDAO.fetchItems()) {

                Item newItem = new Item();
                newItem.setId(item.getId());
                newItem.setItemName(item.getItemName());
                newItem.setItemDescription(item.getItemDescription());
                newItem.setItemQuantity(item.getItemQuantity());
                newItem.setPurchasePrice(item.getPurchasePrice());
                newItem.setRetailPrice(item.getRetailPrice());
                newItem.setCreatedAt(item.getCreatedAt());
                newItem.setUpdatedAt(item.getUpdatedAt());


                // Custom CellFactory para createdAt


                itemList.add(newItem);
                itemTableView.setItems(itemList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEditBtnClicked(ActionEvent actionEvent) throws IOException {

        if (itemTableView.getSelectionModel().getSelectedItem() != null) {

            Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-item-view.fxml"));

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(new Scene(loader.load()));
            stage.setMinWidth(500.0);
            stage.setMinHeight(400.0);

            EditItemController controller = loader.getController();
            controller.getItem(selectedItem);

            stage.showAndWait();

            fetchItems();

        }

    }

    public void onDeleteBtnClicked(ActionEvent actionEvent) {
        if (itemTableView.getSelectionModel().getSelectedItem() != null) {

            Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Item Delete");
            alert.setHeaderText("Are You Sure You Want to Delete This Item? This Action Cannot be Undone");

            ButtonType buttonTypeOne = new ButtonType("Confirm");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {

                stockDAO.deleteItemById(selectedItem.getId());
                fetchItems();

            } else {
                //User Cancelled Item Deletion
                System.out.println("Item Deletion Canceled");
            }
        }
    }

    public void onRefreshBtnClicked(ActionEvent actionEvent) {
        fetchItems();
    }


    public void onItemClicked(MouseEvent mouseEvent) throws IOException {

        if (mouseEvent.getClickCount() == 2) {

            if (itemTableView.getSelectionModel().getSelectedItem() != null) {
                Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("item-detail-view.fxml"));

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(loader.load()));
                stage.setTitle(selectedItem.getItemName());
                stage.setMinWidth(720);
                stage.setMinHeight(520);

                ItemDetailController controller = loader.getController();
                controller.getItem(selectedItem);

                stage.showAndWait();
            }

        }


    }
}