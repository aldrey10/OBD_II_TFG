package com.tfg.obdTFG.ui.verdatos.VerVisores;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.db.DatoOBDHelper;

import java.util.HashMap;

public class VerDatosVisoresViewModel extends ViewModel {
    private MutableLiveData<String> miDato;
    private DatoOBDHelper contactarBD;
    private Bluetooth bluetooth;


    public VerDatosVisoresViewModel(DatoOBDHelper contactarBD, Bluetooth bluetooth) {
        this.miDato = new MutableLiveData<>();
        this.contactarBD = contactarBD;
        this.bluetooth = bluetooth;
    }

    public LiveData getMiDato(){
        return miDato;
    }

    public void setResultadoParDatoValor(String mensaje){
        miDato.postValue(mensaje);
    }

    public void writeVisores(byte[] send){
        bluetooth.writeVisores(send);
    }

    public void setEstamosEnViewModelVisores(boolean estado){
        bluetooth.setEstamosEnViewModelVisores(estado);
    }

    public void setViewModelVisores(VerDatosVisoresViewModel viewModel){
        bluetooth.setViewModelVisores(viewModel);
    }

    public boolean getBluetoothEstado(){
        if (bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
            return true;
        }
        return false;
    }

    public void insertDBExport(String nombreDato, float valor){
        contactarBD.insertDBExport(nombreDato, valor);
    }

    public void insertValuesEstadisticasDB(String nombreDato, float valor){
        contactarBD.insertValuesEstadisticasDB(nombreDato, valor);
    }

    public HashMap<String, Boolean> consultarPreferenciasMotor(){
        return contactarBD.consultarPreferenciasMotor();
    }

    public HashMap<String, Boolean> consultarPreferenciasPresion(){
        return contactarBD.consultarPreferenciasPresion();
    }

    public HashMap<String, Boolean> consultarPreferenciasCombustible(){
        return contactarBD.consultarPreferenciasCombustible();
    }

    public HashMap<String, Boolean> consultarPreferenciasTemperatura(){
        return contactarBD.consultarPreferenciasTemperatura();
    }

}
