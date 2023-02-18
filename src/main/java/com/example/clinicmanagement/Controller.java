package com.example.clinicmanagement;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> nikColumn;

    @FXML
    private TableColumn<User, LocalDate> birthDateColumn;

    @FXML
    private TableColumn<User, String> addressColumn;

    //Text input
    @FXML
    private TextField nameInput;

    @FXML
    private TextField nikInput;

    @FXML
    private DatePicker birthDateInput;

    @FXML
    private TextField addressInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        nikColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nik"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<User, LocalDate>("birthDate"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<User, String>("address"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");

        birthDateColumn.setCellFactory(myDateTableCell -> {
            return new TableCell<User, LocalDate>() {
                @Override
                protected void updateItem(LocalDate date, boolean dateIsEmpty) {
                    super.updateItem(date, dateIsEmpty);
                    if (date == null || dateIsEmpty) {
                        setText(null);
                    } else {
                        setText(date.format(dateFormatter));
                    }
                }
            };
        });

        birthDateInput.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) return dateFormatter.format(date);
                else return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) return LocalDate.parse(string, dateFormatter);
                else return null;
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener(((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameInput.setText(newSelection.getName());
                nikInput.setText(newSelection.getNik());
                birthDateInput.setValue(newSelection.getBirthDate());
                addressInput.setText(newSelection.getAddress());
            }
        }));
    }

    @FXML
    void reset() {
        checkEmptyFields(true);
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    void submit() {
        if (checkEmptyFields(false)) {
            showError(Alert.AlertType.ERROR, "Invalid Entries", "Invalid Entries! Make sure the fields are not empty and valid");
            return;
        }

        if (tableView.getSelectionModel().getSelectedIndex() > -1) {

        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");

        System.out.println(birthDateInput.getValue().format(dateFormatter));

        User user = new User(nameInput.getText(),
            nikInput.getText(),
            birthDateInput.getValue(),
            addressInput.getText());
        ObservableList<User> users = tableView.getItems();
        users.add(user);
        tableView.setItems(users);

        showError(Alert.AlertType.INFORMATION, "Insert Successfully", "Data Successfully Inserted");
        reset();
    }

    @FXML
    void delete() {
        int selectedID = tableView.getSelectionModel().getSelectedIndex();
        User user = tableView.getSelectionModel().getSelectedItem();
        if (selectedID > -1) {
            tableView.getItems().remove(selectedID);
            showError(Alert.AlertType.INFORMATION, "Delete Successfully", "Data Successfully Deleted");
        }
    }

    @FXML
    void selectNext() {
        tableView.getSelectionModel().selectNext();
    }

    @FXML
    void selectPrevious() {
        System.out.println(tableView.getSelectionModel().getSelectedIndex());
        if (tableView.getSelectionModel().getSelectedIndex() < 0) {
            tableView.getSelectionModel().selectFirst();
        } else {
            tableView.getSelectionModel().selectPrevious();
        }
    }

    void showError(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    boolean checkEmptyFields(Boolean isReset) {
        boolean isError = false;
        Set<Node> textFields = anchorPane.lookupAll(".text-field-element");

        for (Node textField : textFields) {
            if (!isReset && ((textField instanceof TextField && ((TextField) textField).getText().isEmpty()) ||
                    (textField instanceof DatePicker && ((DatePicker) textField).getValue() == null)))
            {
                // set error message as visible
                Label label = (Label) anchorPane.lookup(("#" + textField.getId()).replace("Input", "Alert"));
                if (label != null) label.setText(textField.getId().replace("Input", "") + " can't be empty!");

                // show error border of text-field
                textField.setStyle("-fx-border-color: red");
                isError = true;
            } else {
                // hide error message
                Label label = (Label) anchorPane.lookup(("#" + textField.getId()).replace("Input", "Alert"));
                label.setText("");

                // hide error border of text-field
                if (isReset && textField instanceof TextField) {
                    ((TextField) textField).setText("");
                } else if (isReset) {
                    ((DatePicker) textField).setValue(null);
                }

                textField.setStyle("-fx-border-color: transparent");
            }
        }

        return isError;
    }
}