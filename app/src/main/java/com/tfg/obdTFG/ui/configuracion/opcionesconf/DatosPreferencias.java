package com.tfg.obdTFG.ui.configuracion.opcionesconf;

import java.util.HashMap;

public class DatosPreferencias {
    private HashMap<String, Boolean> motor;
    private HashMap<String, Boolean> presion;
    private HashMap<String, Boolean> combustible;
    private HashMap<String, Boolean> temperatura;
    private HashMap<String, Boolean> datosViaje;

    public DatosPreferencias(HashMap<String, Boolean> motor, HashMap<String, Boolean> presion, HashMap<String, Boolean> combustible, HashMap<String, Boolean> temperatura, HashMap<String, Boolean> datosViaje) {
        this.motor = motor;
        this.presion = presion;
        this.combustible = combustible;
        this.temperatura = temperatura;
        this.datosViaje = datosViaje;
    }

    public HashMap<String, Boolean> getMotor() {
        return motor;
    }

    public void setMotor(HashMap<String, Boolean> motor) {
        this.motor = motor;
    }

    public HashMap<String, Boolean> getPresion() {
        return presion;
    }

    public void setPresion(HashMap<String, Boolean> presion) {
        this.presion = presion;
    }

    public HashMap<String, Boolean> getCombustible() {
        return combustible;
    }

    public void setCombustible(HashMap<String, Boolean> combustible) {
        this.combustible = combustible;
    }

    public HashMap<String, Boolean> getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(HashMap<String, Boolean> temperatura) {
        this.temperatura = temperatura;
    }

    public HashMap<String, Boolean> getDatosViaje() {
        return datosViaje;
    }

    public void setDatosViaje(HashMap<String, Boolean> datosViaje) {
        this.datosViaje = datosViaje;
    }
}
