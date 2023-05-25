package com.tfg.obdTFG.ui.verdatos;

import com.tfg.obdTFG.db.DatoOBDContrato;
import com.tfg.obdTFG.db.DatoOBDHelper;

import java.util.ArrayList;

public class ParDatoValorProviderEstadisticas {
    private ArrayList<ParDatoValor> listaDatoValor;

    public ParDatoValorProviderEstadisticas() {
        listaDatoValor = new ArrayList<>();
        ParDatoValor par = new ParDatoValor("Velocidad media del viaje", "0 Km/h");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Velocidad máxima del viaje", "0 Km/h");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Media de revoluciones", "0 RPM");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Máxima revolución", "0 RPM");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Consumo medio del viaje", "0 L/h");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Máximo consumo", "0 L/h");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Tiempo con el motor encendido", "0 segundos");
        this.listaDatoValor.add(par);
        par = new ParDatoValor("Distancia recorrida (aprox)", "0 Km");
        this.listaDatoValor.add(par);
    }

    public ArrayList<ParDatoValor> getListaDatoValor() {
        return listaDatoValor;
    }

    public void setListaDatoValor(ArrayList<ParDatoValor> listaDatoValor) {
        this.listaDatoValor = listaDatoValor;
    }
}
