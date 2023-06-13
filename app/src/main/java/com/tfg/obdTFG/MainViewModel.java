package com.tfg.obdTFG;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.db.DatoOBDHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class MainViewModel {
    private MutableLiveData<ArrayList<String>> miDato;
    private DatoOBDHelper database;
    private String nombreDispositivo;
    private BluetoothSocket btSocket;
    private Bluetooth bluetooth;


    public MainViewModel(DatoOBDHelper database) {
        this.miDato = new MutableLiveData<>();
        this.database = database;
    }

    public LiveData getMiDato(){
        return miDato;
    }

    public void setResultadoParDatoValor(ArrayList<String> mensaje){
        miDato.postValue(mensaje);
    }

    public void setBluetooth(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public void setEstamosEnViewModelMain(boolean estado){
        if(bluetooth!=null){
            bluetooth.setEstamosEnViewModelMain(estado);
        }
    }

    public void iniciarConexion(){
        bluetooth.iniciarConexion();
    }

    public void iniciarHiloConnect(BluetoothDevice bluetoothDevice){
        bluetooth.iniciarHiloConnect(bluetoothDevice);
    }

    public void iniciarTransferenciaDatosVisores(){
        bluetooth.iniciarTransferenciaDatosVisores();
    }

    public boolean getBluetoothEstado(){
        if(bluetooth!=null){
            if (bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
                return true;
            }
        }
        return false;
    }

    public void writeVisores(byte[] send){
        bluetooth.writeVisores(send);
    }

    public void cancelarHiloConnect(){
        bluetooth.cancelarHiloConnect();
    }


    //database
    public void deleteTablaBachesInit (){
        database.deleteTablaBachesInit();
    }

    public boolean existenDatosAExportar(){
        return database.existenDatosAExportar();
    }

    public void insertDBExport (String nombreDato, float valor){
        database.insertDBExport(nombreDato, valor);
    }

    public void insertValuesEstadisticasDB (String nombreDato, float valor){
        database.insertValuesEstadisticasDB(nombreDato, valor);
    }

    public void crearNuevoViaje(){
        database.crearNuevoViaje();
    }

    public String cargarConfiguracionCoche (){
        return database.cargarConfiguracionCoche();
    }

    public String cargarMarcaCoche(){
        return database.cargarMarcaCoche();
    }

    public String cargarModeloCoche(){
        return database.cargarModeloCoche();
    }

    public String cargarYearCoche(){
        return database.cargarYearCoche();
    }

    public ArrayList<String> consultarTodasLasConfiguraciones (){
        return database.consultarTodasLasConfiguraciones();
    }

    public boolean consultarConfiguracionActiva(String nombre){
        return database.consultarConfiguracionActiva(nombre);
    }

    public void desactivarConfiguracionActiva (String nombre){
        database.desactivarConfiguracionActiva(nombre);
    }

    public void activarConfiguracion (String nombre){
        database.activarConfiguracion(nombre);
    }

    public void resetValoresTablaEstadisticas(){
        database.resetValores();
    }

    public HashMap<String, Boolean> consultarPreferenciasMotor(){
        return database.consultarPreferenciasMotor();
    }

    public HashMap<String, Boolean> consultarPreferenciasPresion(){
        return database.consultarPreferenciasPresion();
    }

    public HashMap<String, Boolean> consultarPreferenciasCombustible(){
        return database.consultarPreferenciasCombustible();
    }

    public HashMap<String, Boolean> consultarPreferenciasTemperatura(){
        return database.consultarPreferenciasTemperatura();
    }
}
