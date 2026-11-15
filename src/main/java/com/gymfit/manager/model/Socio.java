package com.gymfit.manager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Socio {
    private final StringProperty nombreCompleto;
    private final StringProperty fechaNacimiento;
    private final StringProperty provincia;
    private final StringProperty sexo;
    private final StringProperty cuota;
    private final StringProperty clase;
    private final StringProperty iban;
    private final StringProperty estado;

    public Socio(String nombreCompleto, String fechaNacimiento, String provincia,
                 String sexo, String cuota, String clase, String iban, String estado) {
        this.nombreCompleto = new SimpleStringProperty(nombreCompleto);
        this.fechaNacimiento = new SimpleStringProperty(fechaNacimiento);
        this.provincia = new SimpleStringProperty(provincia);
        this.sexo = new SimpleStringProperty(sexo);
        this.cuota = new SimpleStringProperty(cuota);
        this.clase = new SimpleStringProperty(clase);
        this.iban = new SimpleStringProperty(iban);
        this.estado = new SimpleStringProperty(estado);
    }

    public String getNombreCompleto() { return nombreCompleto.get(); }
    public StringProperty nombreCompletoProperty() { return nombreCompleto; }

    public String getFechaNacimiento() { return fechaNacimiento.get(); }
    public StringProperty fechaNacimientoProperty() { return fechaNacimiento; }

    public String getProvincia() { return provincia.get(); }
    public StringProperty provinciaProperty() { return provincia; }

    public String getSexo() { return sexo.get(); }
    public StringProperty sexoProperty() { return sexo; }

    public String getCuota() { return cuota.get(); }
    public StringProperty cuotaProperty() { return cuota; }

    public String getClase() { return clase.get(); }
    public StringProperty claseProperty() { return clase; }

    public String getIban() { return iban.get(); }
    public StringProperty ibanProperty() { return iban; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String nuevoEstado) { this.estado.set(nuevoEstado); }
    public StringProperty estadoProperty() { return estado; }
}