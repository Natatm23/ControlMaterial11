package com.example.controlmaterial11;

public class Reporte_sincronizar {
    private String idTicket;
    private String fechaAsignacion;
    private String fechaReparacion;
    private String colonia;
    private String tipoSuelo;
    private String direccion;
    private String reportante;
    private String telefonoReportante;
    private String reparador;
    private String material;
    private byte[] imagenAntes;  // Cambiado a byte[]
    private byte[] imagenDespues; // Cambiado a byte[]

    public Reporte_sincronizar(String idTicket, String fechaAsignacion, String fechaReparacion, String colonia,
                   String tipoSuelo, String direccion, String reportante, String telefonoReportante,
                   String reparador, String material, byte[] imagenAntes, byte[] imagenDespues) {
        this.idTicket = idTicket;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaReparacion = fechaReparacion;
        this.colonia = colonia;
        this.tipoSuelo = tipoSuelo;
        this.direccion = direccion;
        this.reportante = reportante;
        this.telefonoReportante = telefonoReportante;
        this.reparador = reparador;
        this.material = material;
        this.imagenAntes = imagenAntes; // Inicializa imagenAntes
        this.imagenDespues = imagenDespues; // Inicializa imagenDespues
    }

    public String getIdTicket() {
        return idTicket;
    }

    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    public String getFechaReparacion() {
        return fechaReparacion;
    }

    public String getColonia() {
        return colonia;
    }

    public String getTipoSuelo() {
        return tipoSuelo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getReportante() {
        return reportante;
    }

    public String getTelefonoReportante() {
        return telefonoReportante;
    }

    public String getReparador() {
        return reparador;
    }

    public String getMaterial() {
        return material;
    }

    public byte[] getImagenAntes() { // Getter para imagenAntes
        return imagenAntes;
    }

    public byte[] getImagenDespues() { // Getter para imagenDespues
        return imagenDespues;
    }
}

