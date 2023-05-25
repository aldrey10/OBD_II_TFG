package com.tfg.obdTFG.ui;

import android.widget.CheckBox;

public class Exportacion {
    private String coche;
    private String fecha;
    private Boolean isSelected;

    public Exportacion(String coche, String fecha, Boolean isSelected) {
        this.coche = coche;
        this.fecha = fecha;
        this.isSelected = isSelected;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCoche() {
        return coche;
    }

    public void setCoche(String coche) {
        this.coche = coche;
    }

}
