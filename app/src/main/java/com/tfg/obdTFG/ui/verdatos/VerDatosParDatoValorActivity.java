package com.tfg.obdTFG.ui.verdatos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tfg.obdTFG.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.configuracion.opcionesconf.PreferenciasActivity;
import com.tfg.obdTFG.ui.verdatos.adapter.ParDatoValorAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class VerDatosParDatoValorActivity extends AppCompatActivity {

    private String msgTemporal;
    private Bluetooth bluetooth;
    private Menu menu;

    public static final int MESSAGE_READ = 1;
    public static final int PEDIR_COMANDOS = 2;
    public static final int MESSAGE_WRITE = 3;

    private ArrayList<Float> velocidadMedia;
    private ArrayList<Float> consumoMedio;
    private TextView RPM, TempAceiteMotor, VelocidadFlujoAire, CargaCalculadaMotor, PresionBarometricaAbsoluta, PresionCombustible, PresionMedidorTrenCombustible,
            PresionAbsColectorAdmision, PresionVaporSisEvaporativo, NivelCombustible, TipoCombustibleNombre, VelocidadConsumoCombustible, RelacionCombustibleAire,
            TempLiquidoEnfriamiento, TempAireAmbiente, TempAireColectorAdmision, TempCatalizador, VelocidadVehiculo, TiempoMotorEncendido, VelocidadMedia, ConsumoMedio,
            PosicionAcelerador, DistanciaLuzEncendidaFalla,  EGRComandado, FallaEGR, PurgaEvaporativaComand, CantidadCalentamientosDesdeNoFallas, DistanciaRecorridadSinLuzFallas,
            VoltajeModuloControl, SincroInyeccionCombustible, PorcentajeTorqueSolicitado, PorcentajeTorqueActual, TorqueReferenciaMotor;


    private final String[] tiposComandos = new String[]{"ATDP", "ATS0", "ATL0", "ATAT0", "ATST10", "ATSPA0", "ATE0"};

    private ArrayList<String> comandos = new ArrayList<>();
    private int comandoAElegir = 0;
    private Handler handler;

    private DatoOBDHelper contactarBD;
    private HashMap<String, Boolean> motor;
    private HashMap<String, Boolean> presion;
    private HashMap<String, Boolean> combustible;
    private HashMap<String, Boolean> temperatura;
    private HashMap<String, Boolean> datosViaje;


    private RecyclerView recyclerView;
    private ParDatoValorAdapter adaptador;
    private ArrayList<ParDatoValor> listaDatosValor = new ArrayList<>();
    private boolean primeraVezAnhadirRecycler = true;

    private boolean primeraVezVelocidad = true;
    private boolean primeraVezRPM = true;
    private boolean primeraVezVelocidadFlujoAire = true;
    private boolean primeraVezCargaCalculadaMotor = true;
    private boolean primeraVezTempAceiteMotor = true;
    private boolean primeraVezPosicionAcelerador = true;
    private boolean primeraVezPorcentajeTorqueSolicitado = true;
    private boolean primeraVezPorcentajeTorqueActual = true;
    private boolean primeraVezTorqueReferenciaMotor = true;
    private boolean primeraVezVoltajeModuloControl = true;
    private boolean primeraVezPresionBarometricaAbsoluta = true;
    private boolean primeraVezPresionCombustible = true;
    private boolean primeraVezPresionMedidorTrenCombustible = true;
    private boolean primeraVezPresionAbsColectorAdmision = true;
    private boolean primeraVezPresionVaporSisEvaporativo = true;
    private boolean primeraVezNivelCombustible = true;
    private boolean primeraVezTipoCombustibleNombre = true;
    private boolean primeraVezConsumo = true;
    private boolean primeraVezRelacionCombustibleAire = true;
    private boolean primeraVezDistanciaLuzEncendidaFalla = true;
    private boolean primeraVezEGRComandado = true;
    private boolean primeraVezFallaEGR = true;
    private boolean primeraVezPurgaEvaporativaComand = true;
    private boolean primeraVezCantidadCalentamientosDesdeNoFallas = true;
    private boolean primeraVezDistanciaRecorridadSinLuzFallas = true;
    private boolean primeraVezSincroInyeccionCombustible = true;
    private boolean primeraVezTempAireAmbiente = true;
    private boolean primeraVezTempLiquidoEnfriamiento = true;
    private boolean primeraVezTempAireColectorAdmision = true;
    private boolean primeraVezTempCatalizador = true;
    private boolean primerVezTiempoMotorEncendido = true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos_par_dato_valor);

        ParDatoValor prueba = new ParDatoValor("Estado:", "No hay datos");
        listaDatosValor.add(prueba);
        iniciarRecyclerView();
        comandos = MainActivity.comandos;

        // Variables para los datos del coche
        VelocidadVehiculo = (TextView) findViewById(R.id.txtValor2);
        RPM = (TextView) findViewById(R.id.txtValor1);
        VelocidadFlujoAire = (TextView) findViewById(R.id.txtValor4);
        CargaCalculadaMotor = (TextView) findViewById(R.id.txtValor5);
        TempAceiteMotor = (TextView) findViewById(R.id.txtValor3);
        PosicionAcelerador = (TextView) findViewById(R.id.txtValor6);
        PorcentajeTorqueSolicitado = (TextView) findViewById(R.id.txtValor7);
        PorcentajeTorqueActual = (TextView) findViewById(R.id.txtValor8);
        TorqueReferenciaMotor = (TextView) findViewById(R.id.txtValor9);
        VoltajeModuloControl = (TextView) findViewById(R.id.txtValor10);

        PresionBarometricaAbsoluta = (TextView) findViewById(R.id.txtValor11);
        PresionCombustible = (TextView) findViewById(R.id.txtValor12);
        PresionMedidorTrenCombustible = (TextView) findViewById(R.id.txtValor13);
        PresionAbsColectorAdmision = (TextView) findViewById(R.id.txtValor14);
        PresionVaporSisEvaporativo = (TextView) findViewById(R.id.txtValor15);

        NivelCombustible = (TextView) findViewById(R.id.txtValor16);
        TipoCombustibleNombre = (TextView) findViewById(R.id.txtValor17);
        VelocidadConsumoCombustible= (TextView) findViewById(R.id.txtValor18);
        RelacionCombustibleAire= (TextView) findViewById(R.id.txtValor19);
        DistanciaLuzEncendidaFalla = (TextView) findViewById(R.id.txtValor20);
        EGRComandado = (TextView) findViewById(R.id.txtValor21);
        FallaEGR = (TextView) findViewById(R.id.txtValor22);
        PurgaEvaporativaComand = (TextView) findViewById(R.id.txtValor23);
        CantidadCalentamientosDesdeNoFallas = (TextView) findViewById(R.id.txtValor24);
        DistanciaRecorridadSinLuzFallas = (TextView) findViewById(R.id.txtValor25);
        SincroInyeccionCombustible = (TextView) findViewById(R.id.txtValor26);

        TempAireAmbiente = (TextView) findViewById(R.id.txtValor28);
        TempLiquidoEnfriamiento = (TextView) findViewById(R.id.txtValor27);
        TempAireColectorAdmision = (TextView) findViewById(R.id.txtValor29);
        TempCatalizador = (TextView) findViewById(R.id.txtValor30);

        TiempoMotorEncendido = (TextView) findViewById(R.id.txtValor31);
        VelocidadMedia= (TextView) findViewById(R.id.txtValor32);
        ConsumoMedio= (TextView) findViewById(R.id.txtValor33);


        velocidadMedia = new ArrayList<Float>();
        consumoMedio = new ArrayList<Float>();


        contactarBD = new DatoOBDHelper(this);
        //establecerCodigos();

        //establecerDisponibilidad();

        bluetooth = MainActivity.bluetooth;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mainActivity = true;
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(MainActivity.listComands));
        MainActivity.comandos = stringList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icons, menu);

        // change color for icon 0
        Drawable yourdrawable = menu.getItem(0).getIcon();
        if (bluetooth!=null){
            if(bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
                yourdrawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
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
            if(bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
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

    public void cambiarMenuCocheEncendido(){
        Drawable yourdrawable1 = menu.getItem(1).getIcon();
        if(MainActivity.cocheEncendido){
            VerDatosParDatoValorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                }
            });
        }
    }


    public void iniciarRecyclerView(){
        recyclerView = findViewById(R.id.recyclerParDatoValor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new ParDatoValorAdapter(listaDatosValor);
        recyclerView.setAdapter(adaptador);
    }

    public void anhadirARecyclerView(String nombreDato, String valorDato){
        VerDatosParDatoValorActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (primeraVezAnhadirRecycler){
                    listaDatosValor.clear();
                    adaptador.notifyItemRemoved(0);
                    primeraVezAnhadirRecycler = false;
                }
                ParDatoValor var = new ParDatoValor(nombreDato, valorDato);
                listaDatosValor.add(var);
                adaptador.notifyItemInserted(listaDatosValor.size()-1);

            }
        });

    }

    public void actualizarRecyclerView(String nombreDato, String valorDato){
        VerDatosParDatoValorActivity.this.runOnUiThread(new Runnable() {
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

    public void mandarPrimerMensaje(){
        String send = "010C";
        enviarMensajeADispositivo(send);
    }


    public void mostrarDatos(String mensaje){
        mensaje = mensaje.replace("null", "");
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        mensaje = mensaje.replaceAll("\n", "");
        mensaje = mensaje.replaceAll("\r", "");
        mensaje = mensaje.replaceAll(" ", "");

        if (mensaje.length() > 35){
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
                    if (mensaje.length()>12){
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }else{
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }
                } catch (NumberFormatException nFE) {
                }
        }

        String send;
        switch (msgTemporal) {
            case "410D": {
                String texto = String.valueOf(obdval) + " Km/h";
                velocidadMedia.add((float)obdval);
                float val = calculateAverage(velocidadMedia);
                String texto2 = String.valueOf(val) + " Km/h";
                if(!primeraVezVelocidad){
                    actualizarRecyclerView("Velocidad del vehículo", texto);
                    actualizarRecyclerView("Velocidad media del viaje", texto2);
                }else{
                    anhadirARecyclerView("Velocidad del vehículo", texto);
                    anhadirARecyclerView("Velocidad media del viaje", texto2);
                    primeraVezVelocidad = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Velocidad del vehículo", obdval);
                }
                contactarBD.insertValuesEstadisticasDB("Velocidad del vehículo", (float) obdval);

                /*VelocidadVehiculo.setText(texto);
                velocidadMedia.add((float)obdval);
                texto = String.valueOf(val) + " Km/h";
                VelocidadMedia.setText(texto);*/
                break;
            }
            case "410C": {
                float val = (float) (obdval / 4);
                String texto = String.valueOf(val) + " RPM";
                if(!primeraVezRPM){
                    actualizarRecyclerView("Revoluciones por minuto", texto);
                }else{
                    anhadirARecyclerView("Revoluciones por minuto", texto);
                    primeraVezRPM = false;
                }
                MainActivity.cocheEncendido = true;
                cambiarMenuCocheEncendido();
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Revoluciones por minuto", val);
                }
                contactarBD.insertValuesEstadisticasDB("Revoluciones por minuto", val);
                //RPM.setText(texto);
                break;
            }
            case "4110": {
                float val = (float) (obdval / 100);
                String texto = String.valueOf(val) + " gr/sec";
                if(!primeraVezVelocidadFlujoAire){
                    actualizarRecyclerView("Velocidad del flujo del aire MAF", texto);
                }else{
                    anhadirARecyclerView("Velocidad del flujo del aire MAF", texto);
                    primeraVezVelocidadFlujoAire = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Velocidad del flujo del aire MAF", val);
                }//VelocidadFlujoAire.setText(texto);
                break;
            }
            case "4104": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %";
                if(!primeraVezCargaCalculadaMotor){
                    actualizarRecyclerView("Carga calculada del motor", texto);
                }else{
                    anhadirARecyclerView("Carga calculada del motor", texto);
                    primeraVezCargaCalculadaMotor = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Carga calculada del motor", val);
                }//CargaCalculadaMotor.setText(texto);
                break;
            }
            case "415C": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                if(!primeraVezTempAceiteMotor){
                    actualizarRecyclerView("Temperatura del aceite del motor", texto);
                }else{
                    anhadirARecyclerView("Temperatura del aceite del motor", texto);
                    primeraVezTempAceiteMotor = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Temperatura del aceite del motor", val);
                }//TempAceiteMotor.setText(texto);
                break;
            }
            case "4111": {
                float val = (float) (obdval/2.55);
                String texto = String.valueOf(val) + " %";
                if(!primeraVezPosicionAcelerador){
                    actualizarRecyclerView("Posición del acelerador", texto);
                }else{
                    anhadirARecyclerView("Posición del acelerador", texto);
                    primeraVezPosicionAcelerador = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Posición del acelerador", val);
                }//PosicionAcelerador.setText(texto);
                break;
            }
            case "4161": {
                float val = (float) (obdval - 125);
                String texto = String.valueOf(val) + " %";
                if(!primeraVezPorcentajeTorqueSolicitado){
                    actualizarRecyclerView("Porcentaje torque solicitado", texto);
                }else{
                    anhadirARecyclerView("Porcentaje torque solicitado", texto);
                    primeraVezPorcentajeTorqueSolicitado = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Porcentaje torque solicitado", val);
                }//PorcentajeTorqueSolicitado.setText(texto);
                break;
            }
            case "4162": {
                float val = (float) (obdval - -125);
                String texto = String.valueOf(val) + " %";
                if(!primeraVezPorcentajeTorqueActual){
                    actualizarRecyclerView("Porcentaje torque actual", texto);
                }else{
                    anhadirARecyclerView("Porcentaje torque actual", texto);
                    primeraVezPorcentajeTorqueActual = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Porcentaje torque actual", val);
                }//PorcentajeTorqueActual.setText(texto);
                break;
            }
            case "4163": {
                float val = (float) (obdval);
                String texto = String.valueOf(val) + " Nm";
                if(!primeraVezTorqueReferenciaMotor){
                    actualizarRecyclerView("Torque referencia motor", texto);
                }else{
                    anhadirARecyclerView("Torque referencia motor", texto);
                    primeraVezTorqueReferenciaMotor = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Torque referencia motor", val);
                }//TorqueReferenciaMotor.setText(texto);
                break;
            }
            case "4142": {
                float val = (float) (obdval/1000);
                String texto = String.valueOf(val) + " V";
                if(!primeraVezVoltajeModuloControl){
                    actualizarRecyclerView("Voltaje módulo control", texto);
                }else{
                    anhadirARecyclerView("Voltaje módulo control", texto);
                    primeraVezVoltajeModuloControl = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Voltaje módulo control", val);
                }//VoltajeModuloControl.setText(texto);
                break;
            }

            case "4133": {
                String texto = String.valueOf(obdval) + " kPa";
                if(!primeraVezPresionBarometricaAbsoluta){
                    actualizarRecyclerView("Presión barométrica absoluta", texto);
                }else{
                    anhadirARecyclerView("Presión barométrica absoluta", texto);
                    primeraVezPresionBarometricaAbsoluta = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión barométrica absoluta", obdval);
                }//PresionBarometricaAbsoluta.setText(texto);
                break;
            }
            case "410A": {
                float val = (float) (obdval * 3);
                String texto = String.valueOf(val) + " kPa";
                if(!primeraVezPresionCombustible){
                    actualizarRecyclerView("Presión del combustible", texto);
                }else{
                    anhadirARecyclerView("Presión del combustible", texto);
                    primeraVezPresionCombustible = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión del combustible", val);
                }//PresionCombustible.setText(texto);
                break;
            }
            case "4123": {
                float val = (float) (obdval * 10);
                String texto = String.valueOf(val) + " kPa";
                if(!primeraVezPresionMedidorTrenCombustible){
                    actualizarRecyclerView("Presión medidor tren combustible", texto);
                }else{
                    anhadirARecyclerView("Presión medidor tren combustible", texto);
                    primeraVezPresionMedidorTrenCombustible = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión medidor tren combustible", val);
                }//PresionMedidorTrenCombustible.setText(texto);
                break;
            }
            case "410B": {
                String texto = String.valueOf(obdval) + " kPa";
                if(!primeraVezPresionAbsColectorAdmision){
                    actualizarRecyclerView("Presion absoluta colector admisión", texto);
                }else{
                    anhadirARecyclerView("Presion absoluta colector admisión", texto);
                    primeraVezPresionAbsColectorAdmision = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presion absoluta colector admisión", obdval);
                }//PresionAbsColectorAdmision.setText(texto);
                break;
            }
            case "4132": {
                float val = (float) ((obdval / 4) - 8192);
                String texto = String.valueOf(obdval) + " Pa";
                if(!primeraVezPresionVaporSisEvaporativo){
                    actualizarRecyclerView("Presión del vapor del sistema evaporativo", texto);
                }else{
                    anhadirARecyclerView("Presión del vapor del sistema evaporativo", texto);
                    primeraVezPresionVaporSisEvaporativo = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Presión del vapor del sistema evaporativo", val);
                }//PresionVaporSisEvaporativo.setText(texto);
                break;
            }

            case "412F": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(obdval) + " %";
                if(!primeraVezNivelCombustible){
                    actualizarRecyclerView("Nivel de combustible %", texto);
                }else{
                    anhadirARecyclerView("Nivel de combustible %", texto);
                    primeraVezNivelCombustible = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Nivel de combustible %", val);
                }//NivelCombustible.setText(texto);
                break;
            }
            case "4151": {
                String texto = TipoCombustible.fromValue(obdval).getDescription();
                if(!primeraVezTipoCombustibleNombre){
                    actualizarRecyclerView("Tipo de combustible", texto);
                }else{
                    anhadirARecyclerView("Tipo de combustible", texto);
                    primeraVezTipoCombustibleNombre = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tipo de combustible", obdval);
                }//TipoCombustibleNombre.setText(texto);
                break;
            }
            case "415E": {
                float val = (float) (obdval / 20);
                String texto = String.valueOf(val) + " L/h";
                consumoMedio.add(val);
                val = calculateAverage(consumoMedio);
                String texto2 = String.valueOf(val) + " L/h";
                if(!primeraVezConsumo){
                    actualizarRecyclerView("Velocidad consumo de combustible", texto);
                    actualizarRecyclerView("Consumo medio del viaje", texto2);
                }else{
                    anhadirARecyclerView("Velocidad consumo de combustible", texto);
                    anhadirARecyclerView("Consumo medio del viaje", texto2);
                    primeraVezConsumo = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Velocidad consumo de combustible", val);
                }
                contactarBD.insertValuesEstadisticasDB("Velocidad consumo de combustible", (float) val);
                /*VelocidadConsumoCombustible.setText(texto);
                consumoMedio.add(val);
                val = calculateAverage(consumoMedio);
                texto = String.valueOf(val) + " L/h";
                ConsumoMedio.setText(texto);*/
                break;
            }
            case "4144": {
                float val = (float) (obdval / 32768);
                String texto = String.valueOf(obdval) + " prop.";
                if(!primeraVezRelacionCombustibleAire){
                    actualizarRecyclerView("Relación combustible-aire", texto);
                }else{
                    anhadirARecyclerView("Relación combustible-aire", texto);
                    primeraVezRelacionCombustibleAire = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Relación combustible-aire", val);
                }//RelacionCombustibleAire.setText(texto);
                break;
            }
            case "4121": {
                float val = (float) (obdval);
                String texto = String.valueOf(val) + " km.";
                if(!primeraVezDistanciaLuzEncendidaFalla){
                    actualizarRecyclerView("Distancia con luz fallas encendida", texto);
                }else{
                    anhadirARecyclerView("Distancia con luz fallas encendida", texto);
                    primeraVezDistanciaLuzEncendidaFalla = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Distancia con luz fallas encendida", val);
                }//DistanciaLuzEncendidaFalla.setText(texto);
                break;
            }
            case "412C": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %.";
                if(!primeraVezEGRComandado){
                    actualizarRecyclerView("EGR comandado", texto);
                }else{
                    anhadirARecyclerView("EGR comandado", texto);
                    primeraVezEGRComandado = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("EGR comandado", val);
                }//EGRComandado.setText(texto);
                break;
            }
            case "412D": {
                float val = (float) ((obdval / 1.28) -100);
                String texto = String.valueOf(val) + " %.";
                if(!primeraVezFallaEGR){
                    actualizarRecyclerView("Falla EGR", texto);
                }else{
                    anhadirARecyclerView("Falla EGR", texto);
                    primeraVezFallaEGR = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Falla EGR", val);
                }//FallaEGR.setText(texto);
                break;
            }
            case "412E": {
                float val = (float) (obdval / 2.55);
                String texto = String.valueOf(val) + " %.";
                if(!primeraVezPurgaEvaporativaComand){
                    actualizarRecyclerView("Purga evaporativa comandada\"", texto);
                }else{
                    anhadirARecyclerView("Purga evaporativa comandada\"", texto);
                    primeraVezPurgaEvaporativaComand = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Purga evaporativa comandada", val);
                }//PurgaEvaporativaComand.setText(texto);
                break;
            }
            case "4130": {
                float val = (float) (obdval);
                String texto = String.valueOf(obdval) + " calentamientos.";
                if(!primeraVezCantidadCalentamientosDesdeNoFallas){
                    actualizarRecyclerView("Cant. calentamiento sin fallas", texto);
                }else{
                    anhadirARecyclerView("Cant. calentamiento sin fallas", texto);
                    primeraVezCantidadCalentamientosDesdeNoFallas = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Cant. calentamiento sin fallas", val);
                }//CantidadCalentamientosDesdeNoFallas.setText(texto);
                break;
            }
            case "4131": {
                float val = (float) (obdval);
                String texto = String.valueOf(val) + " km.";
                if(!primeraVezDistanciaRecorridadSinLuzFallas){
                    actualizarRecyclerView("Distancia sin luz fallas encendida", texto);
                }else{
                    anhadirARecyclerView("Distancia sin luz fallas encendida", texto);
                    primeraVezDistanciaRecorridadSinLuzFallas = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Distancia sin luz fallas encendida", val);
                }//DistanciaRecorridadSinLuzFallas.setText(texto);
                break;
            }
            case "415D": {
                float val = (float) ((obdval / 128) - 210);
                String texto = String.valueOf(val) + " º.";
                if(!primeraVezSincroInyeccionCombustible){
                    actualizarRecyclerView("Sincronización inyección combustible", texto);
                }else{
                    anhadirARecyclerView("Sincronización inyección combustible", texto);
                    primeraVezSincroInyeccionCombustible = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Sincronización inyección combustible", val);
                }//SincroInyeccionCombustible.setText(texto);
                break;
            }

            case "4146": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                if(!primeraVezTempAireAmbiente){
                    actualizarRecyclerView("Temperatura del aire ambiente", texto);
                }else{
                    anhadirARecyclerView("Temperatura del aire ambiente", texto);
                    primeraVezTempAireAmbiente = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Temperatura del aire ambiente", val);
                }//TempAireAmbiente.setText(texto);
                break;
            }
            case "4105": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                if(!primeraVezTempLiquidoEnfriamiento){
                    actualizarRecyclerView("Tº del líquido de enfriamiento", texto);
                }else{
                    anhadirARecyclerView("Tº del líquido de enfriamiento", texto);
                    primeraVezTempLiquidoEnfriamiento = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tº del líquido de enfriamiento", val);
                }//TempLiquidoEnfriamiento.setText(texto);
                break;
            }
            case "410F": {
                float val = (float) (obdval - 40);
                String texto = String.valueOf(val) + " ºC";
                if(!primeraVezTempAireColectorAdmision){
                    actualizarRecyclerView("Tº del aire del colector de admisión", texto);
                }else{
                    anhadirARecyclerView("Tº del aire del colector de admisión", texto);
                    primeraVezTempAireColectorAdmision = false;
                }
                if(MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tº del aire del colector de admisión", val);
                }
                //TempAireColectorAdmision.setText(texto);
                break;
            }
            case "413C": {
                float val = (float) ((obdval / 10) - 40);
                String texto = String.valueOf(val) + " ºC";
                if(!primeraVezTempCatalizador){
                    actualizarRecyclerView("Temperatura del catalizador", texto);
                }else{
                    anhadirARecyclerView("Temperatura del catalizador", texto);
                    primeraVezTempCatalizador = false;
                }
                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Temperatura del catalizador", val);
                }//TempCatalizador.setText(texto);
                break;
            }
            case "411F": {
                String texto = String.valueOf(obdval) + " Segundos";
                if(!primerVezTiempoMotorEncendido){
                    actualizarRecyclerView("Tiempo con el motor encendido", texto);
                }else{
                    anhadirARecyclerView("Tiempo con el motor encendido", texto);
                    primerVezTiempoMotorEncendido = false;
                }
                if (MainActivity.estamosCapturando) {
                    contactarBD.insertDBExport("Tiempo con el motor encendido", obdval);
                }
                //TiempoMotorEncendido.setText(texto);
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


    public void pedirComandos() {
        String send = tiposComandos[comandoAElegir];
        System.out.println(send + "\n");
        enviarMensajeADispositivo(send);
    }

    private float calculateAverage(ArrayList<Float> listavg) {
        float sum = 0;
        for (float val : listavg) {
            sum += val;
        }
        return sum / listavg.size();
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


    //establecemos los array de codigos que solicitaremos al OBD
    /*public void establecerCodigos(){
        motor = contactarBD.consultarPreferenciasMotor();
        if(Objects.equals(motor.get("Velocidad del vehículo"), true)){
            comandos.add(CodigoDatos.VelocidadVehiculo.getCodigo());
        }
        if(Objects.equals(motor.get("Revoluciones por minuto"), true)){
            comandos.add(CodigoDatos.RPM.getCodigo());
        }
        if(Objects.equals(motor.get("Velocidad del flujo del aire MAF"), true)){
            comandos.add(CodigoDatos.VelocidadFlujoAire.getCodigo());
        }
        if(Objects.equals(motor.get("Carga calculada del motor"), true)){
            comandos.add(CodigoDatos.CargaCalculadaMotor.getCodigo());
        }
        if(Objects.equals(motor.get("Temperatura del aceite del motor"), true)){
            comandos.add(CodigoDatos.TempAceiteMotor.getCodigo());
        }
        if(Objects.equals(motor.get("Posición del acelerador"), true)){
            comandos.add(CodigoDatos.PosicionAcelerador.getCodigo());
        }
        if(Objects.equals(motor.get("Porcentaje torque solicitado"), true)){
            comandos.add(CodigoDatos.PorcentajeTorqueSolicitado.getCodigo());
        }
        if(Objects.equals(motor.get("Porcentaje torque actual"), true)){
            comandos.add(CodigoDatos.PorcentajeTorqueActual.getCodigo());
        }
        if(Objects.equals(motor.get("Torque referencia motor"), true)){
            comandos.add(CodigoDatos.TorqueReferenciaMotor.getCodigo());
        }
        if(Objects.equals(motor.get("Voltaje módulo control"), true)){
            comandos.add(CodigoDatos.VoltajeModuloControl.getCodigo());
        }

        presion = contactarBD.consultarPreferenciasPresion();
        if(Objects.equals(presion.get("Presión barométrica absoluta"), true)){
            comandos.add(CodigoDatos.PresionBarometricaAbsoluta.getCodigo());
        }
        if(Objects.equals(presion.get("Presión del combustible"), true)){
            comandos.add(CodigoDatos.PresionCombustible.getCodigo());
        }
        if(Objects.equals(presion.get("Presión medidor tren combustible"), true)){
            comandos.add(CodigoDatos.PresionMedidorTrenCombustible.getCodigo());
        }
        if(Objects.equals(presion.get("Presion absoluta colector admisión"), true)){
            comandos.add(CodigoDatos.PresionAbsColectorAdmision.getCodigo());
        }
        if(Objects.equals(presion.get("Presión del vapor del sistema evaporativo"), true)){
            comandos.add(CodigoDatos.PresionVaporSisEvaporativo.getCodigo());
        }


        combustible = contactarBD.consultarPreferenciasCombustible();
        if(Objects.equals(combustible.get("Nivel de combustible %"), true)){
            comandos.add(CodigoDatos.NivelCombustible.getCodigo());
        }
        if(Objects.equals(combustible.get("Tipo de combustible"), true)){
            comandos.add(CodigoDatos.TipoCombustibleNombre.getCodigo());
        }
        if(Objects.equals(combustible.get("Velocidad consumo de combustible"), true)){
            comandos.add(CodigoDatos.VelocidadConsumoCombustible.getCodigo());
        }
        if(Objects.equals(combustible.get("Relación combustible-aire"), true)){
            comandos.add(CodigoDatos.RelacionCombustibleAire.getCodigo());
        }
        if(Objects.equals(combustible.get("Distancia con luz fallas encendida"), true)){
            comandos.add(CodigoDatos.DistanciaLuzEncendidaFalla.getCodigo());
        }
        if(Objects.equals(combustible.get("EGR comandado"), true)){
            comandos.add(CodigoDatos.EGRComandado.getCodigo());
        }
        if(Objects.equals(combustible.get("Falla EGR"), true)){
            comandos.add(CodigoDatos.FallaEGR.getCodigo());
        }
        if(Objects.equals(combustible.get("Purga evaporativa comandada"), true)){
            comandos.add(CodigoDatos.PurgaEvaporativaComand.getCodigo());
        }
        if(Objects.equals(combustible.get("Cant. calentamiento sin fallas"), true)){
            comandos.add(CodigoDatos.CantidadCalentamientosDesdeNoFallas.getCodigo());
        }
        if(Objects.equals(combustible.get("Distancia sin luz fallas encendida"), true)){
            comandos.add(CodigoDatos.DistanciaRecorridadSinLuzFallas.getCodigo());
        }
        if(Objects.equals(combustible.get("Sincronización inyección combustible"), true)){
            comandos.add(CodigoDatos.SincroInyeccionCombustible.getCodigo());
        }


        temperatura = contactarBD.consultarPreferenciasTemperatura();
        if(Objects.equals(temperatura.get("Temperatura del aire ambiente"), true)){
            comandos.add(CodigoDatos.TempAireAmbiente.getCodigo());
        }
        if(Objects.equals(temperatura.get("Tº del líquido de enfriamiento"), true)){
            comandos.add(CodigoDatos.TempLiquidoEnfriamiento.getCodigo());
        }
        if(Objects.equals(temperatura.get("Tº del aire del colector de admisión"), true)){
            comandos.add(CodigoDatos.TempAireColectorAdmision.getCodigo());
        }
        if(Objects.equals(temperatura.get("Temperatura del catalizador"), true)){
            comandos.add(CodigoDatos.TempCatalizador.getCodigo());
        }

        datosViaje = contactarBD.consultarPreferenciasDatosViaje();
        if(Objects.equals(datosViaje.get("Tiempo con el motor encendido"), true)){
            comandos.add(CodigoDatos.TiempoMotorEncendido.getCodigo());
        }
        if(Objects.equals(datosViaje.get("Velocidad media del viaje"), true)){
            comandos.add(CodigoDatos.VelocidadMedia.getCodigo());
        }
        if(Objects.equals(datosViaje.get("Consumo medio del viaje"), true)){
            comandos.add(CodigoDatos.VelocidadMedia.getCodigo());
        }

        if (comandos.isEmpty()){
            comandos.add("010C");
        }
    }*/


    public void establecerDisponibilidad(){
        TextView txt;

        txt = findViewById(R.id.txtValor2);
        if(Objects.equals(motor.get("Velocidad del vehículo"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor1);
        if(Objects.equals(motor.get("Revoluciones por minuto"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor3);
        if(Objects.equals(motor.get("Temperatura del aceite del motor"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor4);
        if(Objects.equals(motor.get("Velocidad del flujo del aire MAF"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor5);
        if(Objects.equals(motor.get("Carga calculada del motor"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor6);
        if(Objects.equals(motor.get("Posición del acelerador"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor7);
        if(Objects.equals(motor.get("Porcentaje torque solicitado"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor8);
        if(Objects.equals(motor.get("Porcentaje torque actual"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor9);
        if(Objects.equals(motor.get("Torque referencia motor"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor10);
        if(Objects.equals(motor.get("Voltaje módulo control"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor11);
        if(Objects.equals(presion.get("Presión barométrica absoluta"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor12);
        if(Objects.equals(presion.get("Presión del combustible"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor13);
        if(Objects.equals(presion.get("Presión medidor tren combustible"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor14);
        if(Objects.equals(presion.get("Presión absoluta colector admisión"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor15);
        if(Objects.equals(presion.get("Presión del vapor del sistema evaporativo"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor16);
        if(Objects.equals(combustible.get("Nivel de combustible %"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor17);
        if(Objects.equals(combustible.get("Tipo de combustible"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor18);
        if(Objects.equals(combustible.get("Tipo de combustible"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor19);
        if(Objects.equals(combustible.get("Tipo de combustible"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor20);
        if(Objects.equals(combustible.get("Distancia con luz fallas encendida"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor21);
        if(Objects.equals(combustible.get("EGR comandado"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor22);
        if(Objects.equals(combustible.get("Falla EGR"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor23);
        if(Objects.equals(combustible.get("Purga evaporativa comandada"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor24);
        if(Objects.equals(combustible.get("Cant. calentamiento sin fallas"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor25);
        if(Objects.equals(combustible.get("Distancia sin luz fallas encendida"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor26);
        if(Objects.equals(combustible.get("Sincronización inyección combustible"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor27);
        if(Objects.equals(temperatura.get("Tº del líquido de enfriamiento"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor28);
        if(Objects.equals(temperatura.get("Temperatura del aire ambiente"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor29);
        if(Objects.equals(temperatura.get("Tº del aire del colector de admisión"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor30);
        if(Objects.equals(temperatura.get("Temperatura del catalizador"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor31);
        if(Objects.equals(datosViaje.get("Tiempo con el motor encendido"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor32);
        if(Objects.equals(datosViaje.get("Velocidad media del viaje"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }
        txt = findViewById(R.id.txtValor33);
        if(Objects.equals(datosViaje.get("Consumo medio del viaje"), false)){
            txt.setText("NO DISPONIBLE");
        }else{
            txt.setText("");
        }

    }


}