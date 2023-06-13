package com.tfg.obdTFG.ui.exportaciones.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.obdTFG.R;
import com.tfg.obdTFG.ui.exportaciones.Exportacion;

import java.util.ArrayList;

public class ExportacionAdapter extends RecyclerView.Adapter<ExportacionAdapter.ExportViewHolder>{

    public static ArrayList<Exportacion> listaExportaciones;

    public class ExportViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCoche;
        private TextView txtFecha;
        private CheckBox checkBoxExport;

        public ExportViewHolder(View vistaRecycler) {
            super(vistaRecycler);
            txtCoche = vistaRecycler.findViewById(R.id.txtConfigCocheExport);
            txtFecha = vistaRecycler.findViewById(R.id.txtFechaViajeExport);
            checkBoxExport = vistaRecycler.findViewById(R.id.checkBoxExport);

            checkBoxExport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean isChecked = b;

                    if(isChecked){
                        listaExportaciones.get(getAdapterPosition()).setIsSelected(true);
                    }else{
                        listaExportaciones.get(getAdapterPosition()).setIsSelected(false);
                    }
                }
            });
        }
    }

    public ExportacionAdapter(ArrayList<Exportacion> listaExportaciones) {
        this.listaExportaciones = listaExportaciones;
    }

    @NonNull
    @Override
    public ExportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_exportacion, parent, false);
        return new ExportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExportViewHolder holder, int position) {
        Exportacion exportacion = listaExportaciones.get(position);
        render(exportacion, holder);
    }

    @Override
    public int getItemCount() {
        return listaExportaciones.size();
    }

    public ArrayList<Exportacion> getListaExportaciones(){
        return listaExportaciones;
    }

    public void render(Exportacion exportacion, ExportViewHolder holder){
        holder.txtCoche.setText(exportacion.getCoche());
        holder.txtFecha.setText(exportacion.getFecha());
        holder.checkBoxExport.setChecked(false);
    }
}
