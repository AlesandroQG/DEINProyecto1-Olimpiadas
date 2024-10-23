package com.alesandro.olimpiadas.controller;

import com.alesandro.olimpiadas.dao.DaoDeporte;
import com.alesandro.olimpiadas.model.Deporte;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Clase que controla los eventos de la ventana deportes
 */
public class DeportesController implements Initializable {
    private Deporte deporte;
    private Deporte crear;

    @FXML // fx:id="btnEliminar"
    private Button btnEliminar; // Value injected by FXMLLoader

    @FXML // fx:id="cbDeporte"
    private ComboBox<Deporte> cbDeporte; // Value injected by FXMLLoader

    @FXML // fx:id="txtNombre"
    private TextField txtNombre; // Value injected by FXMLLoader

    @FXML
    private ResourceBundle resources; // ResourceBundle injected automatically by FXML loader

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        this.deporte = null;
        crear = new Deporte();
        crear.setId_deporte(0);
        crear.setNombre(resources.getString("cb.new"));
        cargarDeportes();
        // Listener ComboBox
        cbDeporte.getSelectionModel().selectedItemProperty().addListener(this::cambioDeporte);
    }

    /**
     * Función que carga los deportes de la base de datos al ComboBox
     */
    public void cargarDeportes() {
        cbDeporte.getItems().clear();
        cbDeporte.getItems().add(crear);
        ObservableList<Deporte> deportes = DaoDeporte.cargarListado();
        cbDeporte.getItems().addAll(deportes);
        cbDeporte.getSelectionModel().select(0);
    }

    /**
     * Listener del cambio del ComboBox
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    public void cambioDeporte(ObservableValue<? extends Deporte> observable, Deporte oldValue, Deporte newValue) {
        if (newValue != null) {
            btnEliminar.setDisable(true);
            if (newValue.equals(crear)) {
                deporte = null;
                txtNombre.setText(null);
            } else {
                deporte = newValue;
                txtNombre.setText(deporte.getNombre());
                if (DaoDeporte.esEliminable(deporte)) {
                    btnEliminar.setDisable(false);
                }
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar". Cierra la ventana
     *
     * @param event
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage)txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Eliminar". Elimina un deporte
     *
     * @param event
     */
    @FXML
    void eliminar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(txtNombre.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Estás seguro que quieres eliminar este deporte?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (DaoDeporte.eliminar(deporte)) {
                confirmacion("Deporte eliminado correctamente");
                cargarDeportes();
            } else {
                alerta("Ha habido un error eliminando ese deporte. Por favor vuelva a intentarlo");
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar". Válida y procesa los datos
     *
     * @param event
     */
    @FXML
    void guardar(ActionEvent event) {
        if (txtNombre.getText().isEmpty()) {
            alerta("El campo nombre no puede estar vacío");
        } else {
            Deporte nuevo = new Deporte();
            nuevo.setNombre(txtNombre.getText());
            if (this.deporte == null) {
                int id = DaoDeporte.insertar(nuevo);
                if (id == -1) {
                    alerta("Ha habido un error almacenando los datos. Por favor vuelva a intentarlo");
                } else {
                    confirmacion("Deporte añadido correctamente");
                    cargarDeportes();
                }
            } else {
                if (DaoDeporte.modificar(this.deporte, nuevo)) {
                    confirmacion("Deporte actualizado correctamente");
                    cargarDeportes();
                } else {
                    alerta("Ha habido un error almacenando los datos. Por favor vuelva a intentarlo");
                }
            }
        }
    }

    /**
     * Función que muestra un mensaje de alerta al usuario
     *
     * @param texto contenido de la alerta
     */
    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("ERROR");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param texto contenido del mensaje
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

}