package com.tfg.obdTFG.ui.verdatos;

//clase para el recyclerView
public class ParDatoValor {
    private String nombreDato;
    private String valorDato;

    public ParDatoValor(String nombreDato, String valorDato) {
        this.nombreDato = nombreDato;
        this.valorDato = valorDato;
    }

    public String getNombreDato() {
        return nombreDato;
    }

    public void setNombreDato(String nombreDato) {
        this.nombreDato = nombreDato;
    }

    public String getValorDato() {
        return valorDato;
    }

    public void setValorDato(String valorDato) {
        this.valorDato = valorDato;
    }
}
