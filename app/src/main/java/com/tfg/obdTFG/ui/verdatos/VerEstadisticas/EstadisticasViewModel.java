package com.tfg.obdTFG.ui.verdatos.VerEstadisticas;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.db.DatoOBDHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EstadisticasViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> miDato;
    private MutableLiveData<BarData> miBarData;
    private MutableLiveData<ArrayList> miDataDeHistograma;
    private Bluetooth bluetooth;
    private DatoOBDHelper database;

    public static final int[] COLORES_TABLA_ACELERACIONES = {
            Color.rgb(255, 255, 255), Color.rgb(255, 255, 255), Color.rgb(255, 255, 255),
            Color.rgb(255, 255, 255), Color.rgb(255, 169, 169), Color.rgb(255, 169, 169),
            Color.rgb(255, 0, 0)
    };


    public EstadisticasViewModel(Bluetooth bluetooth, DatoOBDHelper database) {
        this.miDato = new MutableLiveData<>();
        this.miBarData = new MutableLiveData<>();
        this.miDataDeHistograma = new MutableLiveData<>();
        this.bluetooth = bluetooth;
        this.database = database;
    }

    public LiveData getMiDataDeHistograma(){
        return miDataDeHistograma;
    }

    public Runnable setResultadoMiDataDeHistograma(){
        return new Runnable() {
            @Override
            public void run() {
                miDataDeHistograma.postValue(getData());
            }
        };
    }

    public void startTaskHistograma(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(setResultadoMiDataDeHistograma(), 2, TimeUnit.SECONDS);

    }

    public LiveData getMiDato(){
        return miDato;
    }

    public void setResultadoParDatoValor(String mensaje){
        miDato.postValue(mostrarDatos(mensaje));
    }

    public LiveData getMiBarData(){
        return miBarData;
    }

    public void setResultadoBarData(){
        ArrayList misDatos = getData();
        BarDataSet barDataSet = new BarDataSet(misDatos, "Histograma de aceleraciones");
        BarData barData = new BarData(barDataSet);

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        miBarData.setValue(barData);
    }

    public ArrayList getData() {
        ArrayList listHistograma = new ArrayList();
        listHistograma.add(new BarEntry(1f, database.getCantidadAceleracionesPorGrupo(0)));
        listHistograma.add(new BarEntry(2f, database.getCantidadAceleracionesPorGrupo(1)));
        listHistograma.add(new BarEntry(3f, database.getCantidadAceleracionesPorGrupo(2)));
        listHistograma.add(new BarEntry(4f, database.getCantidadAceleracionesPorGrupo(3)));
        listHistograma.add(new BarEntry(5f, database.getCantidadAceleracionesPorGrupo(4)));
        listHistograma.add(new BarEntry(6f, database.getCantidadAceleracionesPorGrupo(5)));
        listHistograma.add(new BarEntry(7f, database.getCantidadAceleracionesPorGrupo(6)));

        return listHistograma;

    }

    public void writeVisores(byte[] send){
        bluetooth.writeVisores(send);
    }

    public void setEstamosEnViewModelEstadisticas(boolean estado){
        if(bluetooth!=null){
            bluetooth.setEstamosEnViewModelEstadisticas(estado);
        }
    }

    public void setViewModelEstadisticas(EstadisticasViewModel viewModel){
        if(bluetooth!=null) {
            bluetooth.setViewModelEstadisticas(viewModel);
        }
    }

    public boolean getBluetoothEstado(){
        if(bluetooth!=null){
            if (bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
                return true;
            }
        }
        return false;
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

    private float calculateAverage(ArrayList<Float> listavg) {
        float sum = 0;
        for (float val : listavg) {
            sum += val;
        }
        return sum / listavg.size();
    }

    public void resetValores(){
        database.resetValores();
    }

    public void insertValuesEstadisticasDB(String nombreDato, float valor){
        database.insertValuesEstadisticasDB(nombreDato, valor);
    }

    public float getTiempoTotalEstadisticas(){
        return database.getTiempoTotalEstadisticas();
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
            if (mensaje.substring(4, 6).equals("41"))
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
        }

        String send;
        switch (msgTemporal) {
            case "410D": {
                float valor = (float) obdval;
                database.insertValuesEstadisticasDB("Velocidad", valor);
                ArrayList<Float> lista = database.consultarMediaValores("Velocidad");
                float media = calculateAverage(lista);
                DecimalFormat formato2 = new DecimalFormat("#.##");
                String texto = formato2.format(media) + " Km/h";
                miArray.add("Velocidad media del viaje");
                miArray.add(texto);

                float max = 0;
                for (float val : lista){
                    if (max<val){
                        max = val;
                    }
                }
                System.out.println("\n\nESTE VALOR ES:"+ max);
                texto = max + " Km/h";
                miArray.add("Velocidad máxima del viaje");
                miArray.add(texto);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad del vehículo", obdval);
                }

                break;
            }
            case "410C": {
                float valor = (float) obdval;
                database.insertValuesEstadisticasDB("Revoluciones", valor/4);
                ArrayList<Float> lista = database.consultarMediaValores("Revoluciones");
                float media = calculateAverage(lista);
                DecimalFormat formato2 = new DecimalFormat("#.##");
                String texto = formato2.format(media) + " RPM";
                miArray.add("Media de revoluciones");
                miArray.add(texto);

                float max = 0;
                for (float val : lista){
                    if (max<val){
                        max = val;
                    }
                }
                texto = max + " RPM";

                miArray.add("Máxima revolución");
                miArray.add(texto);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Revoluciones por minuto", valor);
                }
                break;
            }
            case "415E": {
                float valor = (float) obdval;
                database.insertValuesEstadisticasDB("Consumo", valor);
                ArrayList<Float> lista = database.consultarMediaValores("Consumo");
                float media = calculateAverage(lista);
                String texto = media + " L/h";
                miArray.add("Consumo medio del viaje");
                miArray.add(texto);

                float max = 0;
                for (float val : lista){
                    if (max<val){
                        max = val;
                    }
                }
                texto = max + " L/h";

                miArray.add("Máximo consumo");
                miArray.add(texto);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad consumo de combustible", valor);
                }
                break;
            }
            case "411F": {
                ArrayList<Float> lista = database.consultarMediaValores("Velocidad");
                float media = calculateAverage(lista);
                media=media/3600;

                float tiempo = (float) obdval;
                database.modifyTiempoTotalEncendido(tiempo);
                float tiempoEncendidoMotorDesdeReset = database.getTiempoTrasResetUsuario();

                tiempo = tiempo - tiempoEncendidoMotorDesdeReset;
                String texto = tiempo + " Segundos";

                miArray.add("Tiempo con el motor encendido");
                miArray.add(texto);

                texto = media * tiempo + " Km";

                miArray.add("Distancia recorrida (aprox)");
                miArray.add(texto);

                if (MainActivity.estamosCapturando) {
                    database.insertDBExport("Tiempo con el motor encendido", obdval);
                }
                break;
            }
            case "4110": {
                float val = (float) (obdval / 100);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tº del aire del colector de admisión", val);
                }
                break;
            }
            case "4104": {
                float val = (float) (obdval / 2.55);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Carga calculada del motor", val);
                }break;
            }
            case "415C": {
                float val = (float) (obdval - 40);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Temperatura del aceite del motor", val);
                }break;
            }
            case "4111": {
                float val = (float) (obdval/2.55);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Posición del acelerador", val);
                }break;
            }
            case "4161": {
                float val = (float) (obdval - 125);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Porcentaje torque solicitado", val);
                }break;
            }
            case "4162": {
                float val = (float) (obdval - -125);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Porcentaje torque actual", val);
                }break;
            }
            case "4163": {
                float val = (float) (obdval);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Torque referencia motor", val);
                }break;
            }
            case "4142": {
                float val = (float) (obdval/1000);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Voltaje módulo control", val);
                }break;
            }
            case "4133": {

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión barométrica absoluta", obdval);
                }break;
            }
            case "410A": {
                float val = (float) (obdval * 3);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión del combustible", val);
                }break;
            }
            case "4123": {
                float val = (float) (obdval * 10);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión medidor tren combustible", val);
                }break;
            }
            case "410B": {
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presion absoluta colector admisión", obdval);
                }break;
            }
            case "4132": {
                float val = (float) ((obdval / 4) - 8192);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión del vapor del sistema evaporativo", val);
                }break;
            }
            case "412F": {
                float val = (float) (obdval / 2.55);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Nivel de combustible %", val);
                }break;
            }
            case "4151": {
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tipo de combustible", obdval);
                }
                break;
            }
            case "4144": {
                float val = (float) (obdval / 32768);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Relación combustible-aire", val);
                }break;
            }
            case "4121": {
                float val = (float) (obdval);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Distancia con luz fallas encendida", val);
                }break;
            }
            case "412C": {
                float val = (float) (obdval / 2.55);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("EGR comandado", val);
                }break;
            }
            case "412D": {
                float val = (float) ((obdval / 1.28) -100);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Falla EGR", val);
                }break;
            }
            case "412E": {
                float val = (float) (obdval / 2.55);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Purga evaporativa comandada", val);
                }break;
            }
            case "4130": {
                float val = (float) (obdval);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Cant. calentamiento sin fallas", val);
                }break;
            }
            case "4131": {
                float val = (float) (obdval);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Distancia sin luz fallas encendida", val);
                }break;
            }
            case "415D": {
                float val = (float) ((obdval / 128) - 210);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Sincronización inyección combustible", val);
                }break;
            }
            case "4146": {
                float val = (float) (obdval - 40);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Temperatura del aire ambiente", val);
                }break;
            }
            case "4105": {
                float val = (float) (obdval - 40);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tº del líquido de enfriamiento", val);
                }break;
            }
            case "410F": {
                float val = (float) (obdval - 40);

                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tº del aire del colector de admisión", val);
                }break;
            }
            case "413C": {
                float val = (float) ((obdval / 10) - 40);

                if (MainActivity.estamosCapturando) {
                    database.insertDBExport("Temperatura del catalizador", val);
                }
                break;
            }

        }
        return miArray;

    }


}
