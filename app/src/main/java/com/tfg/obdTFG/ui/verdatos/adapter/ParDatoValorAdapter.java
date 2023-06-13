package com.tfg.obdTFG.ui.verdatos.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.obdTFG.R;
import com.tfg.obdTFG.ui.verdatos.ParDatoValor;

import java.util.ArrayList;

public class ParDatoValorAdapter extends RecyclerView.Adapter<ParDatoValorViewHolder> {
    private ArrayList<ParDatoValor> listaParDatoValor;

    public ParDatoValorAdapter(ArrayList<ParDatoValor> listaParDatoValor) {
        this.listaParDatoValor = listaParDatoValor;
    }

    @NonNull
    @Override
    public ParDatoValorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ParDatoValorViewHolder holder = new ParDatoValorViewHolder(inflater.inflate(R.layout.recyclerview_par_dato_valor, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParDatoValorViewHolder holder, int position) {
        ParDatoValor variable = listaParDatoValor.get(position);
        holder.render(variable);
    }

    @Override
    public int getItemCount() {
        return listaParDatoValor.size();
    }
}
