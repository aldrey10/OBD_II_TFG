package com.tfg.obdTFG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.tfg.obdTFG.databinding.ActivityMainBinding;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.configuracion.opcionesconf.CrearConfiguracionCocheFragment;
import com.tfg.obdTFG.ui.verdatos.EstadisticasActivity;
import com.tfg.obdTFG.ui.verdatos.VerDatosParDatoValorActivity;
import com.tfg.obdTFG.ui.verdatos.VerDatosVisoresActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MostrarConfiguracionesFragment.MostrarConf, ResumenConfiguracionFragment.GestionConfiguracion, PreguntarGrabacionFragment.PreguntarGrabacion {

    public ViewModel viewmodel;
    public static Menu menu;
    private ActivityMainBinding binding;

    public static boolean mainActivity = true;
    public static boolean estamosCapturando = false;
    private boolean existeGrabacionEnBD = false;

    private static boolean bluetoothEstaHabilitado = false;
    private static int REQUEST_ENABLE_BT = 1;
    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    public static Bluetooth bluetooth;
    public static boolean primeraVezVerDatos = true;
    public static boolean cocheEncendido = false;
    //public static boolean primeraVezPreferencias = true;
    //public static boolean primeraVezVerParDatoValor = true;
    public static boolean pidiendoPreferencias = false;
    private boolean firstTime=true;
    private boolean esVisores = false;
    private boolean esParDatoValor = false;

    public static boolean hiloDatosVisores = false;
    public static boolean hiloDatosPreferencias = false;

    public static final Object GUI_INITIALIZATION_MONITOR = new Object();
    //public static final Object GUI_INITIALIZATION_MONITOR2 = new Object();
    //public static final Object GUI_INITIALIZATION_MONITOR3 = new Object();

    public static Handler handlerVerDatosVisores;
    //public static Handler handlerVerParDatoValor;
    //public static Handler handlerPreferencias;

    //public static HandlerThread handlerThreadPreferencias;
    //public static HandlerThread handlerThreadVerParDatoValor;
    public static HandlerThread handlerThreadVerDatosVisores;

    //public static Looper looperPreferencias;
    //public static Looper looperVerParDatoValor;
    public static Looper looperVerDatosVisores;


    private BluetoothAdapter btAdapter;
    private transient BluetoothSocket btSocket;
    private String nombreDispositivoConectado;
    private Intent enableBtIntent;
    private Intent enableWriteFiles;
    private ArrayList<String> listaDispositivosBluetoothNombre = new ArrayList<String>();
    private ListView listaDispositivosBluetooth;
    private boolean conexionActiva = false;

    private static final String[] Android_Permissions = new String[]{
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
    };


    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_DEVICE_NAME = 2;
    public static final int BLUETOOTH_ACTIVADO = 3;
    public static final int ASK_BLUETOOTH = 4;
    public static final int SNACKBAR_MSG = 5;
    public static final int PEDIR_COMANDOS = 6;
    public static final int SNACKBAR_MSG_CONEXION_FALLIDA = 7;
    public static final int MESSAGE_WRITE = 8;


    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String MENSAJE_SNACKBAR = "mensaje_Snackbar";
    public static final String COMANDOS = "mis_comandos";


    public static final String[] listComands = new String[]{"010C", "010D", "0110", "0104", "015C", "0133", "010A", "0123", "010B", "0132", "012F", "0151", "015E", "0144",
            "0146", "0105", "010F", "013C", "011F", "0111", "0121", "012C", "012D", "012E", "0130", "0131", "0142", "015D", "0161", "0162", "0163"};
    public static ArrayList<String> comandos = new ArrayList<>();
    private final String[] tiposComandos = new String[]{"ATDP", "ATS0", "ATL0", "ATAT0", "ATST10", "ATSPA0", "ATE0"};
    private int comandoAElegir = 0;

    private String msgTemporal;

    private int rpmval = 0, currenttemp = 0, Enginedisplacement = 1500, Enginetype = 0, FaceColor = 0;
    private ArrayList<Double> avgconsumption;
    private TextView Speed, RPM, Load, Fuel, Volt, Temp, Loadtext, Volttext, Temptext, Centertext;

    private DatoOBDHelper database;

    private ServiceStates srvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewmodel = new ViewModel();

        //Creamos o obtenemos la base de datos
        database = new DatoOBDHelper(this);
        database.getWritableDatabase();
        database.deleteTablaBachesInit();
        //database.hola();
        //database.establecerForeignKeyON();
        // cogemos el adaptador Bluetooth de nuestro dispositivo
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(listComands));
        comandos = stringList;


        // Titulo de Dispositivos Bluetooth
        TextView textView = new TextView(this);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setText("Dispositivos Bluetooth");
        textView.setTextColor(Color.parseColor("#FFFFFF"));


        listaDispositivosBluetooth = (ListView) findViewById(R.id.listaDispositivosBluetooth);
        //listaDispositivosBluetooth.addHeaderView(textView);
        listaDispositivosBluetooth.addHeaderView(textView, null, false);
        listaDispositivosBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //listener para cuando se selecciona un dispositivo Bluetooth
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String itemValue = (String) listaDispositivosBluetooth.getItemAtPosition(position);
                    String MAC = itemValue.substring(itemValue.length() - 17);
                    System.out.println(MAC);
                    BluetoothDevice bluetoothDevice = btAdapter.getRemoteDevice(MAC);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        //nada
                    }
                    btAdapter.cancelDiscovery();

                    // iniciamos la fase de conexion de Bluetooth con el dispositivo seleccionado
                    bluetooth.iniciarHiloConnect(bluetoothDevice);
                    View botonConectarse = (View) findViewById(R.id.btnConectarse);
                    botonConectarse.setVisibility(View.INVISIBLE);
                    TextView conectandonos = (TextView) findViewById(R.id.txtConectandonos);
                    conectandonos.setText("Estableciendo conexión...");
                    conectandonos.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });

        /*handlerThreadPreferencias = new HandlerThread("MyHandlerThread");
        handlerThreadPreferencias.start();
        looperPreferencias = handlerThreadPreferencias.getLooper();*/

        handlerThreadVerDatosVisores = new HandlerThread("MyHandlerThread2");
        handlerThreadVerDatosVisores.start();
        looperVerDatosVisores = handlerThreadVerDatosVisores.getLooper();

        /*handlerThreadVerParDatoValor = new HandlerThread("MyHandlerThread3");
        handlerThreadVerParDatoValor.start();
        looperVerParDatoValor = handlerThreadVerParDatoValor.getLooper();*/


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
        if(cocheEncendido){
            yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        }else{
            yourdrawable1.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
        }

        return true;
    }

    public void comprobarCocheEncendido(){
        if(menu!=null){
            Drawable yourdrawable1 = menu.getItem(1).getIcon();
            if(cocheEncendido){
                yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
            }else{
                yourdrawable1.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        existeGrabacionEnBD = database.existenDatosAExportar();
        if(existeGrabacionEnBD){
            ImageButton btnExportar = findViewById(R.id.btnExportacion);
            btnExportar.setBackgroundTintList(getResources().getColorStateList(R.color.opcionMenuActiva));
            btnExportar.setClickable(true);
        }else{
            ImageButton btnExportar = findViewById(R.id.btnExportacion);
            btnExportar.setBackgroundTintList(getResources().getColorStateList(R.color.opcionMenuDesactiva));
            btnExportar.setClickable(false);
        }
        this.btSocket = viewmodel.getConexionBluetooth();
        if (btSocket != null) {
            //this.btAdapter = viewmodel.getBtAdapter();
            this.bluetooth = viewmodel.getBluetooth();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //requestPermissions(Android_Permissions, 1);
                }
            }
            this.nombreDispositivoConectado = viewmodel.getNombreDispositivo();
        }
        if (this.nombreDispositivoConectado != null) {
            mostrarDispositivoConectado(nombreDispositivoConectado);
            View botonConectarse = (View) findViewById(R.id.btnConectarse);
            botonConectarse.setVisibility(View.INVISIBLE);
        }
        comprobarCocheEncendido();
        ImageButton btnRegistroDatos = findViewById(R.id.btnGrabacionDatos);
        if(bluetooth!=null) {
            if (bluetooth.getEstado() == Bluetooth.STATE_CONECTADOS) {
                btnRegistroDatos.setVisibility(View.VISIBLE);
            }
        }

        startService();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //iniciamos la conexion
                iniciarConexion(new View(this));
            }
            if (resultCode == RESULT_CANCELED) {
                //notificamos de la necesidad del bluetooth
                mostrarSnackBarMsg("Para el uso de la aplicación será necesario activar el Bluetooth.");
            }
        }
    } //onActivityResult


    private final Handler handler = new Handler(new Handler.Callback() {
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
                    interpretarMensaje(msg.obj.toString());
                    break;
                case MESSAGE_DEVICE_NAME:
                    // guardamos el nombre del dispositivo conectado
                    nombreDispositivoConectado = msg.getData().getString(DEVICE_NAME);
                    break;
                case BLUETOOTH_ACTIVADO:
                    // mostrar los dispositivos Bluetooth cercanos a los que podemos conectarnos
                    activarListaDispositivosBluetooth();
                    break;
                case ASK_BLUETOOTH:
                    // solicitar activar el Bluetooth
                    askForBluetooth();
                    break;
                case SNACKBAR_MSG:
                    // mostrar el nombre del dispositivo conectado como un mensaje de Snackbar
                    mostrarSnackBarMsg(msg.getData().getString(MENSAJE_SNACKBAR));
                    mostrarDispositivoConectado(nombreDispositivoConectado);
                    mostrarMenu();
                    View botonCancelar = (View) findViewById(R.id.btnCancelar);
                    botonCancelar.setVisibility(View.INVISIBLE);
                    bluetooth.iniciarTransferenciaDatosVisores();
                    pedirComandos();
                    ImageButton btnRegistroDatos = findViewById(R.id.btnGrabacionDatos);
                    btnRegistroDatos.setClickable(true);
                    btnRegistroDatos.setBackgroundTintList(getResources().getColorStateList(R.color.opcionMenuActiva));

                    viewmodel.setConexionBluetooth(bluetooth.getMmSocket());
                    viewmodel.setNombreDispositivo(nombreDispositivoConectado);
                    break;
                case SNACKBAR_MSG_CONEXION_FALLIDA:
                    mostrarSnackBarMsg(msg.getData().getString(MENSAJE_SNACKBAR));
                    View listaDispositivos = (View) findViewById(R.id.listaDispositivosBluetooth);
                    listaDispositivos.setVisibility(View.INVISIBLE);
                    TextView conectandonos = (TextView) findViewById(R.id.txtConectandonos);
                    conectandonos.setVisibility(View.INVISIBLE);
                    View botonConectarse = (View) findViewById(R.id.btnConectarse);
                    botonConectarse.setVisibility(View.VISIBLE);
                    View botonCancel = (View) findViewById(R.id.btnCancelar);
                    botonCancel.setVisibility(View.INVISIBLE);
                    mostrarMenu();
                    break;
            }
            return false;
        }
    });

    public void pedirComandos() {
        String send = tiposComandos[comandoAElegir];
        System.out.println(send+"\n");
        enviarMensajeADispositivo(send);
    }


    public void interpretarMensaje(String mensaje) {
        //verDatos.mostrarDatos(mensaje);
        /*mensaje = mensaje.replace("null", "");
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        mensaje = mensaje.replaceAll("\n", "");
        mensaje = mensaje.replaceAll("\r", "");
        mensaje = mensaje.replaceAll(" ", "");
        if (mensaje.contains("ELM327")) {
            mensaje = mensaje.replaceAll("ATZ", "");
            mensaje = mensaje.replaceAll("ATI", "");
        }
        if (mensaje.contains("ATDP")) {

        }


        int obdval = 0;
        msgTemporal = "";
        if (mensaje.length() > 4) {
            if (mensaje.substring(4, 6).equals("41"))
                try {
                    msgTemporal = mensaje.substring(4, 8);
                    msgTemporal = msgTemporal.trim();
                    System.out.println("MI MENSAJE TEMPORAL ES: " + msgTemporal);
                    obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                } catch (NumberFormatException nFE) {
                }
        }
        if (msgTemporal.equals("410C")) {
            int val = (int) (obdval / 4);
            rpmval = val;
            String texto = String.valueOf(val) + " RPM";
            RPM.setText(texto);
        }
                /*else if (msgTemporal.equals("410D"))
                {

                    Speed.setText((int)(obdval));
                }
                else if (msgTemporal.equals("4105")){
                    int tempC = obdval - 40;
                    currenttemp=tempC;
                    Temp.setText(Integer.toString(tempC) + " C°");
                }
                else if (msgTemporal.contains("4104"))
                {
                    int calcLoad = obdval * 100 / 255;
                    Load.setText(Integer.toString(calcLoad) + " %");
                    String avg = null;
                    if(Enginetype==0)
                    {
                        if(currenttemp<=55)
                        {
                            avg=String.format("%10.1f", (0.001*0.004*4*Enginedisplacement*rpmval*60*calcLoad/20)).trim();
                            avgconsumption.add((0.001*0.004*4*Enginedisplacement*rpmval*60*calcLoad/20));
                        }
                        else if(currenttemp>55)
                        {
                            avg=String.format("%10.1f", (0.001*0.003*4*Enginedisplacement*rpmval*60*calcLoad/20)).trim();
                            avgconsumption.add((0.001*0.003*4*Enginedisplacement*rpmval*60*calcLoad/20));
                        }
                    }else if(Enginetype==1)
                    {
                        if(currenttemp<=55)
                        {
                            avg=String.format("%10.1f", (0.001*0.004*4*1.35*Enginedisplacement*rpmval*60*calcLoad/20)).trim();
                            avgconsumption.add((0.001*0.004*4*1.35*Enginedisplacement*rpmval*60*calcLoad/20));
                        }
                        else if(currenttemp>55)
                        {
                            avg=String.format("%10.1f", (0.001*0.003*4*1.35*Enginedisplacement*rpmval*60*calcLoad/20)).trim();
                            avgconsumption.add((0.001*0.003*4*1.35*Enginedisplacement*rpmval*60*calcLoad/20));
                        }
                    }
                    Fuel.setText(avg  + " / " + String.format("%10.1f",calculateAverage(avgconsumption)).trim() + " L/h");
                }
                else if (mensaje.indexOf("V") != -1)//battery voltage
                {
                    Volt.setText(mensaje);
                }*/
        ////commands/////////////
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
                if(estamosCapturando) {
                    database.insertDBExport("Velocidad del vehículo", obdval);
                }
                database.insertValuesEstadisticasDB("Velocidad del vehículo", (float) obdval);
                break;
            }
            case "410C": {
                float val = (float) (obdval / 4);
                cocheEncendido = true;
                comprobarCocheEncendido();
                if(estamosCapturando) {
                    database.insertDBExport("Revoluciones por minuto", val);
                }
                database.insertValuesEstadisticasDB("Revoluciones por minuto", val);
                break;
            }
            case "4110": {
                float val = (float) (obdval / 100);
                if(estamosCapturando) {
                    database.insertDBExport("Velocidad del flujo del aire MAF", val);
                }
                break;
            }
            case "4104": {
                float val = (float) (obdval / 2.55);
                if(estamosCapturando) {
                    database.insertDBExport("Carga calculada del motor", val);
                }
                break;
            }
            case "415C": {
                float val = (float) (obdval - 40);
                if(estamosCapturando) {
                    database.insertDBExport("Temperatura del aceite del motor", val);
                }
                break;
            }
            case "4111": {
                float val = (float) (obdval/2.55);
                if(estamosCapturando) {
                    database.insertDBExport("Posición del acelerador", val);
                }
                break;
            }
            case "4161": {
                float val = (float) (obdval - 125);
                if(estamosCapturando) {
                    database.insertDBExport("Porcentaje torque solicitado", val);
                }
                break;
            }
            case "4162": {
                float val = (float) (obdval - -125);
                if(estamosCapturando) {
                    database.insertDBExport("Porcentaje torque actual", val);
                }
                break;
            }
            case "4163": {
                float val = (float) (obdval);
                if(estamosCapturando) {
                    database.insertDBExport("Torque referencia motor", val);
                }
                break;
            }
            case "4142": {
                float val = (float) (obdval/1000);
                if(estamosCapturando) {
                    database.insertDBExport("Voltaje módulo control", val);
                }
                break;
            }

            case "4133": {
                if(estamosCapturando) {
                    database.insertDBExport("Presión barométrica absoluta", obdval);
                }
                break;
            }
            case "410A": {
                float val = (float) (obdval * 3);
                if(estamosCapturando) {
                    database.insertDBExport("Presión del combustible", val);
                }
                break;
            }
            case "4123": {
                float val = (float) (obdval * 10);
                if(estamosCapturando) {
                    database.insertDBExport("Presión medidor tren combustible", val);
                }
                break;
            }
            case "410B": {
                if(estamosCapturando) {
                    database.insertDBExport("Presion absoluta colector admisión", obdval);
                }
                break;
            }
            case "4132": {
                float val = (float) ((obdval / 4) - 8192);
                if(estamosCapturando) {
                    database.insertDBExport("Presión del vapor del sistema evaporativo", val);
                }
                break;
            }

            case "412F": {
                float val = (float) (obdval / 2.55);
                if(estamosCapturando) {
                    database.insertDBExport("Nivel de combustible %", val);
                }
                break;
            }
            case "4151": {
                if(estamosCapturando) {
                    database.insertDBExport("Tipo de combustible", obdval);
                }
                break;
            }
            case "415E": {
                float val = (float) (obdval / 20);
                if(estamosCapturando) {
                    database.insertDBExport("Velocidad consumo de combustible", val);
                }
                database.insertValuesEstadisticasDB("Velocidad consumo de combustible", (float) val);
                break;
            }
            case "4144": {
                float val = (float) (obdval / 32768);
                if(estamosCapturando) {
                    database.insertDBExport("Relación combustible-aire", val);
                }
                break;
            }
            case "4121": {
                float val = (float) (obdval);
                if(estamosCapturando) {
                    database.insertDBExport("Distancia con luz fallas encendida", val);
                }
                break;
            }
            case "412C": {
                float val = (float) (obdval / 2.55);
                if(estamosCapturando) {
                    database.insertDBExport("EGR comandado", val);
                }
                break;
            }
            case "412D": {
                float val = (float) ((obdval / 1.28) - 100);
                if(estamosCapturando) {
                    database.insertDBExport("Falla EGR", val);
                }
                break;
            }
            case "412E": {
                float val = (float) (obdval / 2.55);
                if(estamosCapturando) {
                    database.insertDBExport("Purga evaporativa comandada", val);
                }
                break;
            }
            case "4130": {
                float val = (float) (obdval);
                if(estamosCapturando) {
                    database.insertDBExport("Cant. calentamiento sin fallas", val);
                }
                break;
            }
            case "4131": {
                float val = (float) (obdval);
                if(estamosCapturando) {
                    database.insertDBExport("Distancia sin luz fallas encendida", val);
                }
                break;
            }
            case "415D": {
                float val = (float) ((obdval / 128) - 210);
                if(estamosCapturando) {
                    database.insertDBExport("Sincronización inyección combustible", val);
                }
                break;
            }

            case "4146": {
                float val = (float) (obdval - 40);
                if(estamosCapturando) {
                    database.insertDBExport("Temperatura del aire ambiente", val);
                }
                break;
            }
            case "4105": {
                float val = (float) (obdval - 40);
                if(estamosCapturando) {
                    database.insertDBExport("Tº del líquido de enfriamiento", val);
                }
                break;
            }
            case "410F": {
                float val = (float) (obdval - 40);
                if(estamosCapturando) {
                    database.insertDBExport("Tº del aire del colector de admisión", val);
                }
                break;
            }
            case "413C": {
                float val = (float) ((obdval / 10) - 40);
                if (estamosCapturando) {
                    database.insertDBExport("Temperatura del catalizador", val);
                }
                break;
            }
            case "411F": {
                if (estamosCapturando) {
                    database.insertDBExport("Tiempo con el motor encendido", obdval);
                }
                database.insertValuesEstadisticasDB("Tiempo con el motor encendido", (float) obdval);
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

    private double calculateAverage(ArrayList<Double> listavg) {
        Double sum = 0.0;
        for (Double val : listavg) {
            sum += val;
        }
        return sum.doubleValue() / listavg.size();
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


    //mostramos los dispositivos que estan a nuestro alcance de conexion
    public void activarListaDispositivosBluetooth() {
        listaDispositivosBluetooth.setVisibility(View.VISIBLE);
        listaDispositivosBluetooth.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listaDispositivosBluetoothNombre));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, Android_Permissions, 1);
            }
        }
        btAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
    }


    public void iniciarConexion(View view) {
        if (btAdapter != null) {
            setMenuInvisible();
            bluetoothEstaHabilitado = btAdapter.isEnabled();
            if (bluetoothEstaHabilitado) {
                bluetooth = new Bluetooth(this, handler, btAdapter, viewmodel);
                bluetooth.iniciarConexion();
                viewmodel.setBluetooth(bluetooth);
                //verDatos.setBluetooth(bluetooth);
                View botonCancelar = (View) findViewById(R.id.btnCancelar);
                botonCancelar.setVisibility(View.VISIBLE);
            } else {
                askForBluetooth();
            }
        }

    }

    public void cancelarConexion(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, Android_Permissions, 1);
        }
        bluetooth.cancelarHiloConnect();
        btAdapter.cancelDiscovery();
        View botonConectarse = (View) findViewById(R.id.btnConectarse);
        botonConectarse.setVisibility(View.VISIBLE);
        View botonCancelar = (View) findViewById(R.id.btnCancelar);
        botonCancelar.setVisibility(View.INVISIBLE);
        View listaDispositivos = (View) findViewById(R.id.listaDispositivosBluetooth);
        listaDispositivos.setVisibility(View.INVISIBLE);
        TextView conectandonos = (TextView) findViewById(R.id.txtConectandonos);
        conectandonos.setVisibility(View.INVISIBLE);
        mostrarMenu();
    }


    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(mReceiver);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            super.onDestroy();
        }
        catch (Exception e){
            System.out.println(e);;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                String dispositivo = device.getName() + "\n" + device.getAddress();
                System.out.println(dispositivo);
                if (!listaDispositivosBluetoothNombre.contains(dispositivo) && (device.getName()!= null)) {
                    listaDispositivosBluetoothNombre.add(dispositivo);
                }
                ;
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                listaDispositivosBluetooth.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, listaDispositivosBluetoothNombre));
            }
        }
    };

    public void askForBluetooth() {
        enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }



    public void mostrarSnackBarMsg(String mensaje){
        Snackbar.make(findViewById(R.id.snackbar), mensaje, 6000).show();
    }

    public void mostrarDispositivoConectado(String mensaje){
        TextView textView = (TextView) findViewById(R.id.txtConectadosA);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setText("Conectado a " + mensaje);
        View listaDispositivos  = (View) findViewById(R.id.listaDispositivosBluetooth);
        listaDispositivos.setVisibility(View.INVISIBLE);
        TextView conectandonos = (TextView) findViewById(R.id.txtConectandonos);
        conectandonos.setVisibility(View.INVISIBLE);

        Drawable yourdrawable = menu.getItem(0).getIcon(); // change 0 with 1,2 ...
        yourdrawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);

    }

    public void setMenuInvisible(){
        ImageButton configuracion = (ImageButton) findViewById(R.id.imgConfiguracion);
        configuracion.setVisibility(View.INVISIBLE);
        ImageButton verDatos = (ImageButton) findViewById(R.id.imgVisualizarDatos);
        verDatos.setVisibility(View.INVISIBLE);
        ImageButton registros = (ImageButton) findViewById(R.id.imgRegistros);
        registros.setVisibility(View.INVISIBLE);
        ImageButton nada = (ImageButton) findViewById(R.id.imgVisualizarParDatos);
        nada.setVisibility(View.INVISIBLE);
        ImageButton grabar = (ImageButton) findViewById(R.id.btnGrabacionDatos);
        grabar.setVisibility(View.INVISIBLE);
        ImageButton exportar = (ImageButton) findViewById(R.id.btnExportacion);
        exportar.setVisibility(View.INVISIBLE);
        TextView conf = (TextView) findViewById(R.id.textConfiguracion);
        conf.setVisibility(View.INVISIBLE);
        TextView verData = (TextView) findViewById(R.id.textVisualizarDatos);
        verData.setVisibility(View.INVISIBLE);
        TextView register = (TextView) findViewById(R.id.textRegistros);
        register.setVisibility(View.INVISIBLE);
        TextView nothing = (TextView) findViewById(R.id.textNada);
        nothing.setVisibility(View.INVISIBLE);
        TextView txtGrabar = (TextView) findViewById(R.id.textGrabacion);
        txtGrabar.setVisibility(View.INVISIBLE);
        TextView txtExportar = (TextView) findViewById(R.id.textExportacion);
        txtExportar.setVisibility(View.INVISIBLE);
    }

    public void mostrarMenu(){
        ImageButton configuracion = (ImageButton) findViewById(R.id.imgConfiguracion);
        configuracion.setVisibility(View.VISIBLE);
        ImageButton verDatos = (ImageButton) findViewById(R.id.imgVisualizarDatos);
        verDatos.setVisibility(View.VISIBLE);
        ImageButton registros = (ImageButton) findViewById(R.id.imgRegistros);
        registros.setVisibility(View.VISIBLE);
        ImageButton nada = (ImageButton) findViewById(R.id.imgVisualizarParDatos);
        nada.setVisibility(View.VISIBLE);
        ImageButton grabar = (ImageButton) findViewById(R.id.btnGrabacionDatos);
        grabar.setVisibility(View.VISIBLE);
        ImageButton exportar = (ImageButton) findViewById(R.id.btnExportacion);
        exportar.setVisibility(View.VISIBLE);
        TextView conf = (TextView) findViewById(R.id.textConfiguracion);
        conf.setVisibility(View.VISIBLE);
        TextView verData = (TextView) findViewById(R.id.textVisualizarDatos);
        verData.setVisibility(View.VISIBLE);
        TextView register = (TextView) findViewById(R.id.textRegistros);
        register.setVisibility(View.VISIBLE);
        TextView nothing = (TextView) findViewById(R.id.textNada);
        nothing.setVisibility(View.VISIBLE);
        TextView txtGrabar = (TextView) findViewById(R.id.textGrabacion);
        txtGrabar.setVisibility(View.VISIBLE);
        TextView txtExportar = (TextView) findViewById(R.id.textExportacion);
        txtExportar.setVisibility(View.VISIBLE);
        View listaDispositivos = (View) findViewById(R.id.listaDispositivosBluetooth);
        listaDispositivos.setVisibility(View.INVISIBLE);
    }

    public void gestionarGrabarDatos(View view){
        TextView txtGrabar = findViewById(R.id.textGrabacion);
        if(cocheEncendido){
            if(txtGrabar.getText().equals("Grabar")){
                if(!existeGrabacionEnBD){
                    estamosCapturando = true;
                    txtGrabar.setText("Parar Grabación");
                    database.crearNuevoViaje();
                    mostrarSnackBarMsg("Se ha comenzado la grabación de datos en Base de Datos. Pulse en \"Parar Grabacion\" para parar.");
                }else{
                    new PreguntarGrabacionFragment().show(getSupportFragmentManager(), PreguntarGrabacionFragment.TAG);
                }
            }else{
                estamosCapturando = false;
                if(!existeGrabacionEnBD){
                    ImageButton btnExportar = findViewById(R.id.btnExportacion);
                    btnExportar.setClickable(true);
                    btnExportar.setBackgroundTintList(getResources().getColorStateList(R.color.opcionMenuActiva));
                }
                existeGrabacionEnBD=true;
                txtGrabar.setText("Grabar");
                mostrarSnackBarMsg("Grabación finalizada. Ahora puede exportar los datos a un fichero en formato .csv.");
            }
        }else{
            mostrarSnackBarMsg("Por favor, encienda el coche para poder grabar datos.");
        }

    }

    public void exportarDatos(View view){
        Intent intent = new Intent(MainActivity.this, com.tfg.obdTFG.ui.ExportacionActivity.class);
        startActivity(intent);
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            enableWriteFiles = new Intent(Manifest.permission.WRITE_EXTERNAL_STORAGE, Uri.parse("package:" + getPackageName()));
            startActivityForResult(enableWriteFiles, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            database.exportDB();
        }*/


        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            database.exportDB();
        }*/


    }

    @Override
    public void iniciarNuevaGrabacionTrasDialog() {
        //database.borrarTablaExport();
        //existeGrabacionEnBD=false;
        estamosCapturando=true;
        TextView txtExportar = findViewById(R.id.textGrabacion);
        txtExportar.setText("Parar Grabación");
        database.crearNuevoViaje();
        mostrarSnackBarMsg("Se ha comenzado la grabación de datos en Base de Datos. Pulse en \"Parar Grabacion\" para parar.");
    }

    //funcion para cambiar al panel de configuracion
    public void cambiarAConfiguracion(View view){
        Intent intent = new Intent(MainActivity.this, com.tfg.obdTFG.ui.configuracion.ConfiguracionActivity.class);
        startActivity(intent);
    }

    //funcion para cambiar al panel de registros
    public void cambiarARegistros(View view){
        if(firstTime){
            new MostrarConfiguracionesFragment().show(getSupportFragmentManager(), CrearConfiguracionCocheFragment.TAG);
            firstTime=false;
        } else{
            Intent intent = new Intent(MainActivity.this, EstadisticasActivity.class);
            startActivity(intent);
        }
    }

    //funcion para cambiar al panel de visualizar datos
    public void cambiarAVerDatos(View view){
        esVisores = true;
        if(firstTime){
            new MostrarConfiguracionesFragment().show(getSupportFragmentManager(), CrearConfiguracionCocheFragment.TAG);
            firstTime=false;
        } else{
            Intent intent = new Intent(MainActivity.this, VerDatosVisoresActivity.class);
            startActivity(intent);
        }
    }

    //funcion para cambiar al panel de ver datos en forma de par dato-valor
    public void cambiarAVerParDatoValor(View view){
        esParDatoValor = true;
        if(firstTime){
            new MostrarConfiguracionesFragment().show(getSupportFragmentManager(), CrearConfiguracionCocheFragment.TAG);
            firstTime=false;
        } else{
            Intent intent = new Intent(MainActivity.this, VerDatosParDatoValorActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void abrirVentanaVisualizacion() {
        Intent intent;
        if(esVisores){
            intent = new Intent(MainActivity.this, VerDatosVisoresActivity.class);
        }else if(esParDatoValor){
            intent = new Intent(MainActivity.this, VerDatosParDatoValorActivity.class);
        }else{
            intent = new Intent(MainActivity.this, EstadisticasActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public void abrirPreferencias() {
        Intent intent = new Intent(MainActivity.this, com.tfg.obdTFG.ui.configuracion.opcionesconf.PreferenciasActivity.class);
        startActivity(intent);
    }

    @Override
    public String consultarNombreConfiguracionActual() {
        return database.cargarConfiguracionCoche();
    }

    @Override
    public String consultarMarcaActual() {
        return database.cargarMarcaCoche();
    }

    @Override
    public String consultarModeloActual() {
        return database.cargarModeloCoche();
    }

    @Override
    public String consultarYearActual() {
        return database.cargarYearCoche();
    }

    @Override
    public ArrayList<String> consultarTodasLasConfiguraciones() {
        ArrayList<String> lista = new ArrayList<>();
        lista = database.consultarTodasLasConfiguraciones();
        return lista;
    }

    @Override
    public boolean consultarConfiguracionActiva(String nombre) {
        return database.consultarConfiguracionActiva(nombre);
    }

    @Override
    public void continuarConConfiguracion(String nombre) {
        String confAntigua = database.cargarConfiguracionCoche();
        database.desactivarConfiguracionActiva(confAntigua);
        database.activarConfiguracion(nombre);
        new ResumenConfiguracionFragment().show(getSupportFragmentManager(), CrearConfiguracionCocheFragment.TAG);
    }

    @Override
    public void ponerTruePrimeraVez() {
        firstTime=true;
    }

    public final void stopService() {
        Intent var1 = new Intent((Context)this, ServicioAcelerometros.class);
        var1.setAction(ServiceStates.STOP.name());
        this.stopService(var1);
        this.srvStatus = ServiceStates.STOP;
    }

    public final void startService() {
        /*Intent var1 = new Intent((Context)this, ServicioAcelerometros.class);
        var1.setAction(ServiceStates.START.name());
        this.startService(var1);
        this.srvStatus = ServiceStates.START;*/

        Intent serviceIntent = new Intent(this, ServicioAcelerometros.class);
        startService(serviceIntent);
    }


}