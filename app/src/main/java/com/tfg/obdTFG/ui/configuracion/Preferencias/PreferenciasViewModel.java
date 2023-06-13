package com.tfg.obdTFG.ui.configuracion.Preferencias;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.VerVisores.VerDatosVisoresViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class PreferenciasViewModel extends ViewModel {
    private MutableLiveData<String> miDato;
    private DatoOBDHelper contactarBD;
    private Bluetooth bluetooth;


    public PreferenciasViewModel(DatoOBDHelper contactarBD, Bluetooth bluetooth) {
        this.miDato = new MutableLiveData<>();
        this.contactarBD = contactarBD;
        this.bluetooth = bluetooth;
    }

    public LiveData getMiDato() {
        return miDato;
    }

    public void setResultadoParDatoValor(String mensaje) {
        miDato.postValue(mensaje);
    }

    public void setEstamosEnViewModelPreferencias(boolean estado){
        bluetooth.setEstamosEnViewModelPreferencias(estado);
    }

    public void setViewModelPreferencias(PreferenciasViewModel viewModel){
        bluetooth.setViewModelPreferencias(viewModel);
    }

    public boolean getBluetoothEstado(){
        if (bluetooth.getEstado()== Bluetooth.STATE_CONECTADOS){
            return true;
        }
        return false;
    }

    public ArrayList<String> consultarTodasLasConfiguraciones() {
        return contactarBD.consultarTodasLasConfiguraciones();
    }

    public boolean consultarConfiguracionActiva(String nombre) {
        return contactarBD.consultarConfiguracionActiva(nombre);
    }

    public void desactivarConfiguracionActiva(String conguracionActiva) {
        contactarBD.desactivarConfiguracionActiva(conguracionActiva);
    }

    public void activarConfiguracion(String nombre) {
        contactarBD.activarConfiguracion(nombre);
    }

    public HashMap<String, Boolean> consultarPreferenciasMotor() {
        return contactarBD.consultarPreferenciasMotor();
    }

    public HashMap<String, Boolean> consultarPreferenciasPresion() {
        return contactarBD.consultarPreferenciasPresion();
    }

    public HashMap<String, Boolean> consultarPreferenciasCombustible() {
        return contactarBD.consultarPreferenciasCombustible();
    }

    public HashMap<String, Boolean> consultarPreferenciasTemperatura() {
        return contactarBD.consultarPreferenciasTemperatura();
    }

    public HashMap<String, Boolean> consultarPreferenciasMotorDisponibilidad() {
        return contactarBD.consultarPreferenciasMotorDisponibilidad();
    }

    public HashMap<String, Boolean> consultarPreferenciasPresionDisponibilidad() {
        return contactarBD.consultarPreferenciasPresionDisponibilidad();
    }

    public HashMap<String, Boolean> consultarPreferenciasCombustibleDisponibilidad() {
        return contactarBD.consultarPreferenciasCombustibleDisponibilidad();
    }

    public HashMap<String, Boolean> consultarPreferenciasTemperaturaDisponibilidad() {
        return contactarBD.consultarPreferenciasTemperaturaDisponibilidad();
    }


    public String cargarConfiguracionCoche() {
        return contactarBD.cargarConfiguracionCoche();
    }

    public void guardarPreferencias(HashMap<String, Boolean> motor, HashMap<String, Boolean> presion, HashMap<String, Boolean> combustible, HashMap<String, Boolean> temperatura) {
        contactarBD.guardarPreferencias(motor, presion, combustible, temperatura);
    }

    public void actualizarDisponibilidad(HashMap<String, Boolean> motor, HashMap<String, Boolean> presion, HashMap<String, Boolean> combustible, HashMap<String, Boolean> temperatura) {
        contactarBD.actualizarDisponibilidad(motor, presion, combustible, temperatura);
    }


    }
