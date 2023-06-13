package com.tfg.obdTFG.ui.verdatos.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tfg.obdTFG.R;
import com.tfg.obdTFG.ui.verdatos.ParDatoValor;

public class ParDatoValorViewHolder extends RecyclerView.ViewHolder {
    private View vistaRecycler;
    private TextView txtNombreDato;
    private TextView txtValorDato;

    public ParDatoValorViewHolder(View vistaRecycler) {
        super(vistaRecycler);
        this.vistaRecycler = vistaRecycler;
        txtNombreDato = vistaRecycler.findViewById(R.id.txtNombreDatoRecycler);
        txtValorDato = vistaRecycler.findViewById(R.id.txtValorDatoRecycler);
    }

    public void render(ParDatoValor parDatoValor){
        txtNombreDato.setText(parDatoValor.getNombreDato());
        txtValorDato.setText(parDatoValor.getValorDato());
    }

    public View getVistaRecycler() {
        return vistaRecycler;
    }

    public void setVistaRecycler(View vistaRecycler) {
        this.vistaRecycler = vistaRecycler;
    }
}
