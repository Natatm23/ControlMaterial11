package com.example.controlmaterial11;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {
    private List<Reporte> reportes; // Cambiado a List<Reporte>

    public static class ReporteViewHolder extends RecyclerView.ViewHolder {
        public TextView txtIdTicket, txtColonia, txtDireccion;

        public ReporteViewHolder(View itemView) {
            super(itemView);
            txtIdTicket = itemView.findViewById(R.id.id_ticket);
            txtColonia = itemView.findViewById(R.id.colonia);
            txtDireccion = itemView.findViewById(R.id.direccion);
        }
    }

    // Cambiar el tipo de la lista en el constructor
    public ReporteAdapter(List<Reporte> reportes) {
        this.reportes = reportes;
    }

    @Override
    public ReporteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reporte_item, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {
        Reporte reporte = reportes.get(position);
        holder.txtIdTicket.setText(reporte.getIdTicket());
        holder.txtColonia.setText(reporte.getColonia());
        holder.txtDireccion.setText(reporte.getDireccion());
    }

    @Override
    public int getItemCount() {
        return reportes.size();
    }
}
