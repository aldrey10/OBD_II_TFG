package com.tfg.obdTFG.ui.verdatos.VerDatoParValor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.TipoCombustible;
import com.tfg.obdTFG.ui.verdatos.VerVisores.VerDatosVisoresViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class VerParDatoValorViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> miDato;
    private DatoOBDHelper contactarBD;
    private Bluetooth bluetooth;

    public VerParDatoValorViewModel(DatoOBDHelper contactarBD, Bluetooth bluetooth) {
        this.miDato = new MutableLiveData<>();
        this.contactarBD = contactarBD;
        this.bluetooth = bluetooth;
    }

    public LiveData getMiDato(){
        return miDato;
    }

    public void setResultadoParDatoValor(String mensaje){
        miDato.postValue(mostrarDatos(mensaje));
    }

    public void setEstamosEnViewModelParDatos(boolean estado){
        bluetooth.setEstamosEnViewModelParDatos(estado);
    }

    public void setViewModelParDato(VerParDatoValorViewModel viewModel){
        bluetooth.setViewModelParDato(viewModel);
    }

    public void writeVisores(byte[] send){
        bluetooth.writeVisores(send);
    }


    public boolean getBluetoothEstado(){
        if (bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
            return true;
        }
        return false;
    }

    public ArrayList<String> mostrarDatos(String mensaje) {
        ArrayList<String> miArray = new ArrayList<>();

        mensaje = mensaje.replace("null", "");
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        mensaje = mensaje.replaceAll("\n", "");
        mensaje = mensaje.replaceAll("\r", "");
        mensaje = mensaje.replaceAll(" ", "");

        if (mensaje.length() > 35) {
            mensaje = "";
        }

        int obdval = 0;
        String msgTemporal = "";
        if (mensaje.length() > 4) {
            if (mensaje.substring(4, 6).equals("41")) {
                try {
                    msgTemporal = mensaje.substring(4, 8);
                    msgTemporal = msgTemporal.trim();
                    System.out.println("MI MENSAJE TEMPORAL ES: " + msgTemporal);
                    if (mensaje.length() > 12) {
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    } else {
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }
                } catch (NumberFormatException nFE) {
                }
            }else{
                miArray.add("NODATA");
            }
        }

        switch (msgTemporal) {
            case "410D": {
                String texto = String.valueOf(obdval) + " Km/h";
                contactarBD.insertValuesEstadisticasDB("Velocidad", (float)obdval);

                miArray.add("Velocidad del vehículo");
                miArray.add(texto);
                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Velocidad del vehículo", obdval);
                }
                break;
            }
            case "410C": {
                float val = (float) (obdval / 4);
                String texto = String.valueOf(val) + " RPM";
                contactarBD.insertValuesEstadisticasDB("Revoluciones", (float) val);
                miArray.add("Revoluciones por minuto");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Revoluciones por minuto", val);
                }
                break;
            }
            case "4110": {
                float val = (float) (obdval / 100);
                String texto = String.valueOf(val) + " gr/sec";
                miArray.add("Velocidad del flujo del aire MAF");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Velocidad del flujo del aire MAF", val);
                }
                break;
            }
            case "4104": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %";
                miArray.add("Carga calculada del motor");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Carga calculada del motor", val);
                }
                break;
            }
            case "415C": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                miArray.add("Temperatura del aceite del motor");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Temperatura del aceite del motor", val);
                }
                break;
            }
            case "4111": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %";
                miArray.add("Posición del acelerador");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Posición del acelerador", val);
                }
                break;
            }
            case "4161": {
                float val = (float) (obdval - 125);
                String texto = String.valueOf(val) + " %";
                miArray.add("Porcentaje torque solicitado");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Porcentaje torque solicitado", val);
                }
                break;
            }
            case "4162": {
                float val = (float) (obdval - -125);
                String texto = String.valueOf(val) + " %";
                miArray.add("Porcentaje torque actual");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Porcentaje torque actual", val);
                }
                break;
            }
            case "4163": {
                float val = (float) (obdval);
                String texto = String.valueOf(val) + " Nm";
                miArray.add("Torque referencia motor");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Torque referencia motor", val);
                }
                break;
            }
            case "4142": {
                float val = (float) (obdval / 1000);
                String texto = String.valueOf(val) + " V";
                miArray.add("Voltaje módulo control");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Voltaje módulo control", val);
                }
                break;
            }

            case "4133": {
                String texto = String.valueOf(obdval) + " kPa";
                miArray.add("Presión barométrica absoluta");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión barométrica absoluta", obdval);
                }
                break;
            }
            case "410A": {
                float val = (float) (obdval * 3);
                String texto = String.valueOf(val) + " kPa";
                miArray.add("Presión del combustible");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión del combustible", val);
                }
                break;
            }
            case "4123": {
                float val = (float) (obdval * 10);
                String texto = String.valueOf(val) + " kPa";
                miArray.add("Presión medidor tren combustible");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión medidor tren combustible", val);
                }
                break;
            }
            case "410B": {
                String texto = String.valueOf(obdval) + " kPa";
                miArray.add("Presion absoluta colector admisión");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presion absoluta colector admisión", obdval);
                }
                break;
            }
            case "4132": {
                float val = (float) ((obdval / 4) - 8192);
                String texto = String.valueOf(obdval) + " Pa";
                miArray.add("Presión del vapor del sistema evaporativo");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión del vapor del sistema evaporativo", val);
                }
                break;
            }

            case "412F": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(obdval) + " %";
                miArray.add("Nivel de combustible %");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Nivel de combustible %", val);
                }
                break;
            }
            case "4151": {
                String texto = TipoCombustible.fromValue(obdval).getDescription();
                miArray.add("Tipo de combustible");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tipo de combustible", obdval);
                }
                break;
            }
            case "415E": {
                float val = (float) (obdval / 20);
                String texto = String.valueOf(val) + " L/h";
                contactarBD.insertValuesEstadisticasDB("Consumo", (float) val);

                miArray.add("Velocidad consumo de combustible");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Velocidad consumo de combustible", val);
                }

                break;
            }
            case "4144": {
                float val = (float) (obdval / 32768);
                String texto = String.valueOf(obdval) + " prop.";
                miArray.add("Relación combustible-aire");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Relación combustible-aire", val);
                }
                break;
            }
            case "4121": {
                float val = (float) (obdval);
                String texto = String.valueOf(val) + " km.";
                miArray.add("Distancia con luz fallas encendida");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Distancia con luz fallas encendida", val);
                }
                break;
            }
            case "412C": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %.";
                miArray.add("EGR comandado");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("EGR comandado", val);
                }
                break;
            }
            case "412D": {
                float val = (float) ((obdval / 1.28) - 100);
                String texto = String.valueOf(val) + " %.";
                miArray.add("Falla EGR");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Falla EGR", val);
                }
                break;
            }
            case "412E": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %.";
                miArray.add("Purga evaporativa comandada");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Purga evaporativa comandada", val);
                }
                break;
            }
            case "4130": {
                float val = (float) (obdval);
                String texto = String.valueOf(obdval) + " calentamientos.";
                miArray.add("Cant. calentamiento sin fallas");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Cant. calentamiento sin fallas", val);
                }
                break;
            }
            case "4131": {
                float val = (float) (obdval);
                String texto = String.valueOf(val) + " km.";
                miArray.add("Distancia sin luz fallas encendida");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Distancia sin luz fallas encendida", val);
                }
                break;
            }
            case "415D": {
                float val = (float) ((obdval / 128) - 210);
                String texto = String.valueOf(val) + " º.";
                miArray.add("Sincronización inyección combustible");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Sincronización inyección combustible", val);
                }
                break;
            }

            case "4146": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                miArray.add("Temperatura del aire ambiente");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Temperatura del aire ambiente", val);
                }
                break;
            }
            case "4105": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                miArray.add("Tº del líquido de enfriamiento");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tº del líquido de enfriamiento", val);
                }
                break;
            }
            case "410F": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                miArray.add("Tº del aire del colector de admisión");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tº del aire del colector de admisión", val);
                }
                break;
            }
            case "413C": {
                float val = (float) ((obdval / 10) - 40);
                String texto = String.valueOf(val) + " ºC";
                miArray.add("Temperatura del catalizador");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Temperatura del catalizador", val);
                }
                break;
            }
            case "411F": {
                String texto = String.valueOf(obdval) + " Segundos";
                miArray.add("Tiempo con el motor encendido");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tiempo con el motor encendido", obdval);
                }
                break;
            }
        }


        return miArray;
    }
}
