package com.example.controlmaterial11;

public class Reporte {
    private String idTicket;
    private String colonia;
    private String direccion;

    public Reporte(String idTicket, String colonia, String direccion) {
        this.idTicket = idTicket;
        this.colonia = colonia;
        this.direccion = direccion;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public String getColonia() {
        return colonia;
    }

    public String getDireccion() {
        return direccion;
    }
}
