package com.tfg.obdTFG.ui.verdatos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tfg.obdTFG.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.adapter.ParDatoValorAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EstadisticasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParDatoValorAdapter adaptador;
    private ParDatoValorProviderEstadisticas providerEstadisticas;
    private ArrayList<ParDatoValor> listaDatosValor = new ArrayList<>();
    private ArrayList listHistograma = new ArrayList();

    private DatoOBDHelper database;
    private Bluetooth bluetooth;
    private Menu menu;

    public static final int MESSAGE_READ = 1;
    public static final int PEDIR_COMANDOS = 2;
    public static final int MESSAGE_WRITE = 3;

    private Handler handler;

    private final String[] tiposComandos = new String[]{"ATDP", "ATS0", "ATL0", "ATAT0", "ATST10", "ATSPA0", "ATE0"};

    private ArrayList<String> comandos = new ArrayList<>();
    private int comandoAElegir = 0;
    private String msgTemporal;

    private float tiempoEncendidoMotorDesdeReset = 0;
    private float tiempo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        database = new DatoOBDHelper(this);
        database.getWritableDatabase();
        providerEstadisticas = new ParDatoValorProviderEstadisticas();
        listaDatosValor = providerEstadisticas.getListaDatoValor();
        iniciarRecyclerView();

        comandos = MainActivity.comandos;

        bluetooth = MainActivity.bluetooth;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activarHistograma();
            }
        }, 0, 2000);
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icons, menu);

        // change color for icon 0
        Drawable yourdrawable = menu.getItem(0).getIcon();
        if (bluetooth!=null){
            if(bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
                yourdrawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        activarHistograma();
                    }
                }, 0, 2000);
            }else{
                yourdrawable.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            }
        }

        Drawable yourdrawable1 = menu.getItem(1).getIcon();
        if(MainActivity.cocheEncendido){
            yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        }else{
            yourdrawable1.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
        }

        if(bluetooth!=null){
            if(bluetooth.getEstado()== Bluetooth.STATE_CONECTADOS){
                /*HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
                handlerThread.start();
                Looper looper = handlerThread.getLooper();*/
                MainActivity.handlerVerDatosVisores = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        switch (msg.what) {
                            case MESSAGE_WRITE:
                                byte[] writeBuf = (byte[]) msg.obj;
                                // construct a string from the buffer
                                String writeMessage = new String(writeBuf);
                                break;
                            case PEDIR_COMANDOS:
                                // lista de comandos que se mandan a OBD II, es decir, lista de datos que queremos saber (velocidad, RPM, etc)
                                pedirComandos();
                                break;
                            case MESSAGE_READ:
                                // interpretamos el mensaje que nos manda el OBD II (el valor)
                                mostrarDatos(msg.obj.toString());
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                handler = MainActivity.handlerVerDatosVisores;
                bluetooth.setHandlerVerDatos(handler);
                MainActivity.mainActivity = false;
                MainActivity.comandos = comandos;
                //handler = MainActivity.handlerVerDatosVisores;
                //bluetooth.setHandlerVerDatos(handler);

                //bluetooth.continuarHiloVisores();
                //mandarPrimerMensaje();
                /*if(MainActivity.primeraVezVerDatos){
                    bluetooth.iniciarTransferenciaDatosVisores();
                    MainActivity.primeraVezVerDatos = false;
                }else{
                    bluetooth.continuarHiloVisores();
                    mandarPrimerMensaje();
                }*/
            }
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mainActivity = true;
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(MainActivity.listComands));
        MainActivity.comandos = stringList;
    }

    public void cambiarMenuCocheEncendido(){
        Drawable yourdrawable1 = menu.getItem(1).getIcon();
        if(MainActivity.cocheEncendido){
            EstadisticasActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                }
            });
        }
    }


    public void resetValores(View view){
        database.resetValores();
        database.insertTiempoTrasResetUsuario(tiempo+tiempoEncendidoMotorDesdeReset);
        actualizarRecyclerView("Velocidad media del viaje", "0 Km/h");
        actualizarRecyclerView("Velocidad máxima del viaje", "0 Km/h");
        actualizarRecyclerView("Media de revoluciones", "0 RPM");
        actualizarRecyclerView("Máxima revolución", "0 RPM");
        actualizarRecyclerView("Consumo medio del viaje", "0 L/h");
        actualizarRecyclerView("Máximo consumo", "0 L/h");
        actualizarRecyclerView("Tiempo con el motor encendido", "0 segundos");
        actualizarRecyclerView("Distancia recorrida (aprox)", "0 Km");

    }

    public void iniciarRecyclerView(){
        recyclerView = findViewById(R.id.recyclerEstadisticas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new ParDatoValorAdapter(listaDatosValor);
        recyclerView.setAdapter(adaptador);
    }


    public void actualizarRecyclerView(String nombreDato, String valorDato){
        EstadisticasActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int position=0;
                for (ParDatoValor p : listaDatosValor){
                    if (nombreDato.equals(p.getNombreDato())){
                        position = listaDatosValor.indexOf(p);
                        break;
                    }
                }
                ParDatoValor p = new ParDatoValor(nombreDato, valorDato);
                listaDatosValor.set(position, p);
                adaptador.notifyItemChanged(position);
            }
        });

    }

    public void pedirComandos() {
        String send = tiposComandos[comandoAElegir];
        System.out.println(send + "\n");
        enviarMensajeADispositivo(send);
    }

    //comunicacion de mensajes con el vehiculo
    public void enviarMensajeADispositivo(String mensaje) {
        if (mensaje.length() > 0) {
            mensaje = mensaje + "\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            bluetooth.writeVisores(send);
        }
    }

    public void mandarPrimerMensaje(){
        String send = "010C";
        enviarMensajeADispositivo(send);
    }

    public void activarHistograma(){
        getData();
        BarChart barChart = findViewById(R.id.histograma);
        BarDataSet barDataSet = new BarDataSet(listHistograma, "Histograma de aceleraciones");
        BarData barData = new BarData(barDataSet);
        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                barChart.setData(barData);

                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);
                barChart.getDescription().setEnabled(true);
            }
        };*/

        barChart.setData(barData);

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);

    }

    public void getData(){
        listHistograma = new ArrayList();
        listHistograma.add(new BarEntry(1f, database.getCantidadAceleracionesPorGrupo(0)));
        listHistograma.add(new BarEntry(2f, database.getCantidadAceleracionesPorGrupo(1)));
        listHistograma.add(new BarEntry(3f, database.getCantidadAceleracionesPorGrupo(2)));
        listHistograma.add(new BarEntry(4f, database.getCantidadAceleracionesPorGrupo(3)));
        listHistograma.add(new BarEntry(5f, database.getCantidadAceleracionesPorGrupo(4)));
        listHistograma.add(new BarEntry(6f, database.getCantidadAceleracionesPorGrupo(5)));
        listHistograma.add(new BarEntry(7f, database.getCantidadAceleracionesPorGrupo(6)));

    }

    public void mostrarDatos(String mensaje) {
        mensaje = mensaje.replace("null", "");
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        mensaje = mensaje.replaceAll("\n", "");
        mensaje = mensaje.replaceAll("\r", "");
        mensaje = mensaje.replaceAll(" ", "");

        if (mensaje.length() > 35) {
            mensaje = "";
        }

        int obdval = 0;
        msgTemporal = "";
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
                ArrayList<Float> lista = database.consultarMediaValores("Velocdiad");
                float media = calculateAverage(lista);
                String texto = String.valueOf(media) + " Km/h";
                actualizarRecyclerView("Velocidad media del viaje", texto);

                float max = 0;
                for (float val : lista){
                    if (max<val){
                        max = val;
                    }
                }
                texto = String.valueOf(max) + " Km/h";
                actualizarRecyclerView("Velocidad máxima del viaje", texto);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad del vehículo", obdval);
                }

                break;
            }
            case "410C": {
                float valor = (float) obdval;
                database.insertValuesEstadisticasDB("Revoluciones", valor);
                ArrayList<Float> lista = database.consultarMediaValores("Revoluciones");
                float media = calculateAverage(lista);
                String texto = String.valueOf(media) + " RPM";
                actualizarRecyclerView("Media de revoluciones", texto);

                float max = 0;
                for (float val : lista){
                    if (max<val){
                        max = val;
                    }
                }
                texto = String.valueOf(max) + " RPM";
                actualizarRecyclerView("Máxima revolución", texto);

                MainActivity.cocheEncendido = true;
                cambiarMenuCocheEncendido();
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
                String texto = String.valueOf(media) + " L/h";
                actualizarRecyclerView("Consumo medio del viaje", texto);

                float max = 0;
                for (float val : lista){
                    if (max<val){
                        max = val;
                    }
                }
                texto = String.valueOf(max) + " L/h";
                actualizarRecyclerView("Máximo consumo", texto);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad consumo de combustible", valor);
                }
                break;
            }
            case "411F": {
                ArrayList<Float> lista = database.consultarMediaValores("Velocdiad");
                float media = calculateAverage(lista);
                media=media/3600;

                tiempo = (float) obdval;
                tiempoEncendidoMotorDesdeReset = database.getTiempoTrasResetUsuario();
                if(tiempoEncendidoMotorDesdeReset > tiempo){
                    database.insertTiempoTrasResetUsuario(0);
                    tiempoEncendidoMotorDesdeReset = database.getTiempoTrasResetUsuario();
                }
                tiempo = tiempo - tiempoEncendidoMotorDesdeReset;
                String texto = String.valueOf(obdval) + " Segundos";
                actualizarRecyclerView("Tiempo con el motor encendido", texto);

                texto = String.valueOf(media*tiempo) + " Km";
                actualizarRecyclerView("Distancia recorrida (aprox)", texto);

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
                        /*GaugeDistanciaLuzEncendidaFalla.setMinValue(0);
                        GaugeDistanciaLuzEncendidaFalla.setMaxValue(65000);
                        GaugeDistanciaLuzEncendidaFalla.setValuePerNick(5000);
                        GaugeDistanciaLuzEncendidaFalla.setTotalNicks(13);
                        GaugeDistanciaLuzEncendidaFalla.setValue(val);*/
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
        send = comandos.get(comandoAElegir);
        enviarMensajeADispositivo(send);
        if (comandoAElegir >= comandos.size() - 1) {
            comandoAElegir = 0;
        } else {
            comandoAElegir++;
        }
    }

    private float calculateAverage(ArrayList<Float> listavg) {
        float sum = 0;
        for (float val : listavg) {
            sum += val;
        }
        return sum / listavg.size();
    }

}