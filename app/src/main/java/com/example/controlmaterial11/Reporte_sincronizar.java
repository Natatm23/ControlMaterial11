package com.example.controlmaterial11;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private byte[] imagenAntes;
    private byte[] imagenDespues;

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
        this.imagenAntes = imagenAntes;
        this.imagenDespues = imagenDespues;
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

    public byte[] getImagenAntes() {
        return imagenAntes;
    }

    public byte[] getImagenDespues() {
        return imagenDespues;
    }

    // Método para convertir la fecha de asignación a java.util.Date
    public Date getFechaAsignacionDate() {
        return parseFecha(fechaAsignacion);
    }

    // Método para convertir la fecha de reparación a java.util.Date
    public Date getFechaReparacionDate() {
        return parseFecha(fechaReparacion);
    }

    private Date parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) {
            return null; // Retorna null si la cadena de fecha es nula o vacía
        }

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy"); // Ajustar formato al esperado
        try {
            return formatoFecha.parse(fechaStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Manejo de errores
        }
    }


}
