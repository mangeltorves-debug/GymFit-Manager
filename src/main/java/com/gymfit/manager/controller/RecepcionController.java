package com.gymfit.manager.controller;

import com.gymfit.manager.model.Socio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RecepcionController {

    @FXML private TextField campoNombre, campoApellido1, campoApellido2, campoIban, campoFiltro;
    @FXML private DatePicker selectorNacimiento;
    @FXML private ComboBox<String> comboProvincia, comboClase;
    @FXML private ToggleGroup grupoSexo, grupoCuota;
    @FXML private RadioButton radioMujer, radioHombre, radioOtro;
    @FXML private RadioButton radioBasica, radioPlus, radioPremium, radioJoven, radioSenior;

    @FXML private VBox cajaAlertaError;
    @FXML private Label etiquetaErrorTitulo, etiquetaErrorSolucion, etiquetaTotalSocios, etiquetaBarraEstado;
    @FXML private CheckMenuItem menuSoloActivos;

    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, String> colNombre, colNacimiento, colProvincia, colCuota, colClase, colEstado;

    private final ObservableList<Socio> datosSocios = FXCollections.observableArrayList();
    private FilteredList<Socio> vistaFiltrada;
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        comboProvincia.setItems(FXCollections.observableArrayList(
                "41 - Sevilla", "29 - Málaga", "11 - Cádiz", "14 - Córdoba",
                "18 - Granada", "21 - Huelva", "23 - Jaén", "04 - Almería", "28 - Madrid"
        ));

        comboClase.setItems(FXCollections.observableArrayList(
                "Body Pump", "Boxeo", "Cross Training", "GAP",
                "Natación", "Pilates", "Spinning", "Yoga", "Zumba"
        ));


        colNombre.setCellValueFactory(c -> c.getValue().nombreCompletoProperty());
        colNacimiento.setCellValueFactory(c -> c.getValue().fechaNacimientoProperty());
        colProvincia.setCellValueFactory(c -> c.getValue().provinciaProperty());
        colCuota.setCellValueFactory(c -> c.getValue().cuotaProperty());
        colClase.setCellValueFactory(c -> c.getValue().claseProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());

        vistaFiltrada = new FilteredList<>(datosSocios, s -> true);
        tablaSocios.setItems(vistaFiltrada);
        actualizarContador();

        campoFiltro.textProperty().addListener((obs, ant, act) -> filtrarTabla());
    }

    @FXML
    private void guardarNuevoSocio() {
        ocultarAlerta();

        String nombre = campoNombre.getText().trim();
        String ap1 = campoApellido1.getText().trim();
        String ap2 = campoApellido2.getText().trim();
        LocalDate nacimiento = selectorNacimiento.getValue();
        String provincia = comboProvincia.getValue();
        RadioButton sexoBtn = (RadioButton) grupoSexo.getSelectedToggle();
        RadioButton cuotaBtn = (RadioButton) grupoCuota.getSelectedToggle();
        String clase = comboClase.getValue();
        String iban = campoIban.getText().replaceAll("\\s+", "").toUpperCase();

        if (nombre.isEmpty() || ap1.isEmpty()) {
            mostrarAlerta("Faltan datos personales obligatorios",
                    "Escribe el nombre y el primer apellido del socio antes de guardar.");
            return;
        }
        if (nacimiento == null || nacimiento.isAfter(LocalDate.now().minusYears(14))) {
            mostrarAlerta("Fecha de nacimiento incorrecta",
                    "Selecciona una fecha válida en el calendario (edad mínima de inscripción: 14 años).");
            return;
        }
        if (provincia == null) {
            mostrarAlerta("Provincia sin seleccionar",
                    "Abre el desplegable de provincia y elige una opción (ej. 41 - Sevilla).");
            return;
        }
        if (sexoBtn == null) {
            mostrarAlerta("Falta indicar el sexo",
                    "Marca una de las casillas de sexo (Mujer, Hombre u Otro) para continuar.");
            return;
        }
        if (cuotaBtn == null) {
            mostrarAlerta("No has elegido tipo de cuota",
                    "Selecciona una tarifa (Básica, Plus, Premium, Joven o Senior) antes de guardar.");
            return;
        }
        if (clase == null) {
            mostrarAlerta("Falta seleccionar la clase dirigida",
                    "Elige una actividad del listado (por ejemplo, Spinning).");
            return;
        }
        if (!iban.isEmpty() && (!iban.startsWith("ES") || iban.length() != 24)) {
            mostrarAlerta("Formato de IBAN no válido",
                    "El IBAN español debe empezar por ES seguido de 22 dígitos (ej. ES91 2100 0418 4502 0005 1332).");
            return;
        }

        String nombreFinal = (nombre + " " + ap1 + " " + ap2).trim();
        String provinciaLimpia = provincia.contains(" - ") ? provincia.split(" - ")[1] : provincia;
        String cuotaLimpia = cuotaBtn.getText().split(" ")[0];

        datosSocios.add(0, new Socio(
                nombreFinal,
                nacimiento.format(formatoFecha),
                provinciaLimpia,
                sexoBtn.getText(),
                cuotaLimpia,
                clase,
                iban.isEmpty() ? "Pendiente" : iban,
                "Activo"
        ));

        actualizarContador();
        vaciarCampos();
        etiquetaBarraEstado.setText("Alta completada: " + nombreFinal + " añadido al listado.");
    }

    @FXML
    private void confirmarBorradoFormulario() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText("¿Seguro que quieres borrar todo el formulario?");
        confirmacion.setContentText("Se perderán los datos introducidos que aún no se han guardado.");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            vaciarCampos();
            etiquetaBarraEstado.setText("Formulario limpiado.");
        }
    }

    @FXML
    private void cancelarEdicion() {
        vaciarCampos();
        etiquetaBarraEstado.setText("Operación cancelada.");
    }

    @FXML
    private void prepararNuevo() {
        vaciarCampos();
        campoNombre.requestFocus();
        etiquetaBarraEstado.setText("Formulario listo para un nuevo alta.");
    }

    @FXML
    private void editarSocio() {
        Socio sel = tablaSocios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Ningún socio seleccionado", "Haz clic en una fila de la tabla antes de pulsar Editar.");
            return;
        }
        String[] partes = sel.getNombreCompleto().split(" ", 3);
        campoNombre.setText(partes.length > 0 ? partes[0] : "");
        campoApellido1.setText(partes.length > 1 ? partes[1] : "");
        campoApellido2.setText(partes.length > 2 ? partes[2] : "");
        comboClase.setValue(sel.getClase());
        etiquetaBarraEstado.setText("Editando ficha de " + sel.getNombreCompleto());
    }

    @FXML
    private void darDeBajaSocio() {
        Socio sel = tablaSocios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Ningún socio seleccionado", "Selecciona primero al socio en la tabla para tramitar su baja.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar baja");
        confirmacion.setHeaderText("¿Dar de baja a " + sel.getNombreCompleto() + "?");
        confirmacion.setContentText("Esta acción retirará su ficha del listado de socios.");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            datosSocios.remove(sel);
            actualizarContador();
            etiquetaBarraEstado.setText("Baja tramitada para " + sel.getNombreCompleto());
        }
    }

    @FXML private void renovarCuota() {
        Socio sel = tablaSocios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            sel.setEstado("Activo");
            tablaSocios.refresh();
            etiquetaBarraEstado.setText("Cuota renovada para " + sel.getNombreCompleto());
        }
    }

    @FXML private void congelarAlta() {
        Socio sel = tablaSocios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            sel.setEstado("Congelado");
            tablaSocios.refresh();
            etiquetaBarraEstado.setText("Suscripción congelada para " + sel.getNombreCompleto());
        }
    }

    @FXML private void verFichaSocio() {
        Socio sel = tablaSocios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            Alert ficha = new Alert(Alert.AlertType.INFORMATION);
            ficha.setTitle("Ficha del socio");
            ficha.setHeaderText(sel.getNombreCompleto());
            ficha.setContentText("Nacimiento: " + sel.getFechaNacimiento() + "\nProvincia: " + sel.getProvincia()
                    + "\nCuota: " + sel.getCuota() + " · Clase: " + sel.getClase() + "\nEstado: " + sel.getEstado());
            ficha.showAndWait();
        }
    }

    @FXML private void copiarIban() {
        Socio sel = tablaSocios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            ClipboardContent contenido = new ClipboardContent();
            contenido.putString(sel.getIban());
            Clipboard.getSystemClipboard().setContent(contenido);
            etiquetaBarraEstado.setText("IBAN copiado al portapapeles.");
        }
    }

    @FXML private void filtrarTabla() {
        String texto = campoFiltro.getText() == null ? "" : campoFiltro.getText().toLowerCase().trim();
        boolean soloActivos = menuSoloActivos != null && menuSoloActivos.isSelected();
        vistaFiltrada.setPredicate(s -> {
            boolean matchTexto = texto.isEmpty()
                    || s.getNombreCompleto().toLowerCase().contains(texto)
                    || s.getProvincia().toLowerCase().contains(texto);
            boolean matchEstado = !soloActivos || "Activo".equals(s.getEstado());
            return matchTexto && matchEstado;
        });
        actualizarContador();
    }

    @FXML private void mostrarAtajos() {
        Alert ayuda = new Alert(Alert.AlertType.INFORMATION);
        ayuda.setTitle("Atajos de teclado");
        ayuda.setHeaderText("Guía rápida de recepción");
        ayuda.setContentText("Ctrl+N: Nuevo alta\nCtrl+S: Guardar socio\nCtrl+E: Editar seleccionado\nSupr: Dar de baja");
        ayuda.showAndWait();
    }

    @FXML private void salirAplicacion() { System.exit(0); }

    private void actualizarContador() {
        etiquetaTotalSocios.setText("Total visibles: " + vistaFiltrada.size() + " socios");
    }

    private void mostrarAlerta(String titulo, String solucion) {
        etiquetaErrorTitulo.setText(titulo);
        etiquetaErrorSolucion.setText(solucion);
        cajaAlertaError.setVisible(true);
        cajaAlertaError.setManaged(true);
    }

    private void ocultarAlerta() {
        cajaAlertaError.setVisible(false);
        cajaAlertaError.setManaged(false);
    }

    private void vaciarCampos() {
        campoNombre.clear();
        campoApellido1.clear();
        campoApellido2.clear();
        selectorNacimiento.setValue(null);
        comboProvincia.setValue(null);
        grupoSexo.selectToggle(null);
        grupoCuota.selectToggle(null);
        comboClase.setValue(null);
        campoIban.clear();
        ocultarAlerta();
    }
}