package com.tfg.obdTFG.ui.configuracion.opcionesconf;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tfg.obdTFG.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.ViewModel;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.VerDatosVisoresActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PreferenciasActivity extends AppCompatActivity implements CambiarConfiguracionDesdePreferencias.CambiarConfiguracion {
    public ViewModel viewModel;
    private Menu menu;
    private DatoOBDHelper contactarBD;
    private HashMap<String, Boolean> motor;
    private HashMap<String, Boolean> presion;
    private HashMap<String, Boolean> combustible;
    private HashMap<String, Boolean> temperatura;
    private HashMap<String, Boolean> datosViaje;

    private HashMap<String, Boolean> motorDisponibilidad;
    private HashMap<String, Boolean> presionDisponibilidad;
    private HashMap<String, Boolean> combustibleDisponibilidad;
    private HashMap<String, Boolean> temperaturaDisponibilidad;
    private HashMap<String, Boolean> datosViajeDisponibilidad;

    private Boolean seHanHechoCambios = false;
    private Handler handler;
    private HandlerThread handlerThread;
    public static final int MESSAGE_READ = 1;
    public static final int PEDIR_COMANDOS = 2;
    public static final int MESSAGE_WRITE = 3;
    boolean motorON;
    boolean primeraVez = true;
    boolean podemosActualizarDisponibilidadPreferencias = false;
    boolean primeraVezPorActivity = true;
    boolean peee =true;

    private int comandoAElegir = 0;
    //private final String[] comandos = new String[]{"010C", "010D", "0110", "0104", "015C", "0133", "010A", "0123", "010B", "0132", "012F", "0151", "015E", "0144",
    //        "0146", "0105", "010F", "013C", "011F", "0111", "0121", "012C", "012D", "012E", "0130", "0131", "0142", "015D", "0161", "0162", "0163"};
    private ArrayList<String> comandos = new ArrayList<>();
    private final int ITERACIONES_DISPONIBILIDAD = 4;
    private int contadorIter = 0;

    private boolean RPM = false, TempAceiteMotor = false, VelocidadFlujoAire = false, CargaCalculadaMotor = false, PresionBarometricaAbsoluta = false,
            PresionCombustible = false, PresionMedidorTrenCombustible = false, PresionAbsColectorAdmision = false, PresionVaporSisEvaporativo = false, NivelCombustible = false,
            TipoCombustibleNombre = false, VelocidadConsumoCombustible = false, RelacionCombustibleAire = false, TempLiquidoEnfriamiento = false, TempAireAmbiente = false,
            TempAireColectorAdmision = false, TempCatalizador = false, VelocidadVehiculo = false, TiempoMotorEncendido = false, VelocidadMedia = false, ConsumoMedio = false,
            PosicionAcelerador = false, DistanciaLuzEncendidaFalla = false,  EGRComandado=false, FallaEGR = false, PurgaEvaporativaComand = false, CantidadCalentamientosDesdeNoFallas = false,
            DistanciaRecorridadSinLuzFallas = false, VoltajeModuloControl = false, SincroInyeccionCombustible = false, PorcentajeTorqueSolicitado = false, PorcentajeTorqueActual = false,
            TorqueReferenciaMotor = false;

    private String configuracionActiva;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        MainActivity.hiloDatosPreferencias = true;

        if(MainActivity.estamosCapturando){
            Button btnDisponibilidad = findViewById(R.id.btnDisponibilidad);
            btnDisponibilidad.setVisibility(View.INVISIBLE);
            ImageButton btnChangeConf = findViewById(R.id.btnChangeConf);
            btnChangeConf.setVisibility(View.INVISIBLE);
        }

        comandos = MainActivity.comandos;
        Spinner spinnerTipoDato = findViewById(R.id.spinnerTipoCampoDatos);
        spinnerTipoDato.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarTitulos(tipoDato);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //no hacemos nada
            }
        });

        contactarBD = new DatoOBDHelper(this);

        checkearDisponibilidad();

        iniciarPreferencias();

        mostrarDisponibilidadDatos();

        activarListenerCheckBoxes();

        configuracionActiva = contactarBD.cargarConfiguracionCoche();
        TextView txt = findViewById(R.id.txtConfiguracionName);
        txt.setText(configuracionActiva);


        /*if(MainActivity.bluetooth!=null) {
            if (MainActivity.bluetooth.getEstado() == Bluetooth.STATE_CONECTADOS) {
                Button btn = findViewById(R.id.btnDisponibilidad);
                btn.setVisibility(View.VISIBLE);
                TextView txt = findViewById(R.id.idTxtDisponibilidad);
                if(MainActivity.pidiendoPreferencias){
                    System.out.println("\n\nkdsjfkladsjkfjasdfjadsjlf\n\n");
                    btn.setVisibility(View.INVISIBLE);
                    txt.setVisibility(View.VISIBLE);
                    txt.setText("Buscando Disponibilidad...");
                }else{
                    txt.setVisibility(View.INVISIBLE);
                }
                try{
                    HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
                    handlerThread.start();
                    Looper looper = handlerThread.getLooper();
                    MainActivity.handlerPreferencias = new Handler(looper) {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case MESSAGE_WRITE:
                                    byte[] writeBuf = (byte[]) msg.obj;
                                    // construct a string from the buffer
                                    String writeMessage = new String(writeBuf);
                                    break;
                                case PEDIR_COMANDOS:
                                    // lista de comandos que se mandan a OBD II, es decir, lista de datos que queremos saber (velocidad, RPM, etc)
                                    pedirMotorEncendido();
                                    break;
                                case MESSAGE_READ:
                                    // interpretamos el mensaje que nos manda el OBD II (el valor)
                                    interpretarDatos(msg.obj.toString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    handler = MainActivity.handlerPreferencias;
                    MainActivity.bluetooth.setHandlerVerDatos(handler);
                    MainActivity.bluetooth.continuarHiloMotorEncendido();
                }catch(Exception e){
                    System.out.println(e);
                }
            }else{
                Button btn = findViewById(R.id.btnDisponibilidad);
                btn.setVisibility(View.INVISIBLE);
                TextView txt = findViewById(R.id.idTxtDisponibilidad);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Sin Conexión");
            }
        } else{
            Button btn = findViewById(R.id.btnDisponibilidad);
            btn.setVisibility(View.INVISIBLE);
            TextView txt = findViewById(R.id.idTxtDisponibilidad);
            txt.setVisibility(View.VISIBLE);
            txt.setText("Sin Conexión");
        }*/


    }

    public void onDestroy() {
        super.onDestroy();
        if(seHanHechoCambios){
            contactarBD.guardarPreferencias(motor, presion, combustible, temperatura, datosViaje);
            try{
                //MainActivity.bluetooth.cancelarHiloMotorEncendido();
                MainActivity.hiloDatosPreferencias = false;
            } catch (Exception e){
                System.out.println(e);
            }
        }
        if(podemosActualizarDisponibilidadPreferencias){
            contactarBD.actualizarDisponibilidad(motorDisponibilidad, presionDisponibilidad, combustibleDisponibilidad, temperaturaDisponibilidad, datosViajeDisponibilidad);
        }
        MainActivity.mainActivity = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icons, menu);

        // change color for icon 0
        Drawable yourdrawable = menu.getItem(0).getIcon();
        if (MainActivity.bluetooth!=null){
            if(MainActivity.bluetooth.getEstado()==Bluetooth.STATE_CONECTADOS){
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

        return true;
    }

    public void cambiarMenuCocheEncendido(){
        Drawable yourdrawable1 = menu.getItem(1).getIcon();
        if(MainActivity.cocheEncendido){
            PreferenciasActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                }
            });
        }
    }

    public void pedirEncender(View view){
        pedirMotorEncendido();
    }

    public void mandarPrimerMensaje(){
        String send = "010C";
        enviarMensajeADispositivo(send);
    }

    public void pedirMotorEncendido() {
        if (MainActivity.bluetooth != null) {
            if (MainActivity.bluetooth.getEstado() == Bluetooth.STATE_CONECTADOS) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                MainActivity.pidiendoPreferencias = true;
                Button btn = findViewById(R.id.btnDisponibilidad);
                btn.setVisibility(View.INVISIBLE);
                TextView txt = findViewById(R.id.idTxtDisponibilidad);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Buscando Disponibilidad...");
                resetearDisponibilidades();
                mostrarDisponibilidadDatos();
                contadorIter = 0;
                comandoAElegir = 0;
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
                                mandarPrimerMensaje();
                                break;
                            case MESSAGE_READ:
                                // interpretamos el mensaje que nos manda el OBD II (el valor)
                                interpretarDatos(msg.obj.toString());
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                handler = MainActivity.handlerVerDatosVisores;
                MainActivity.bluetooth.setHandlerVerDatos(handler);
                MainActivity.mainActivity = false;
            } else {
                pedirQueSeConecteABluetooth();
            }
        } else {
            pedirQueSeConecteABluetooth();
        }


    }

    public void enviarMensajeADispositivo(String mensaje) {
        if (mensaje.length() > 0) {
            mensaje = mensaje + "\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            MainActivity.bluetooth.writeVisores(send);
        }
    }

    /*public void pedirQueSeEnciendaMotor(){
        PreferenciasActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = findViewById(R.id.txtEstadoCoche);
                text.setText("Para saber la disponibilidad de los datos, será necesario que arranque el vehículo.");
                Button btn = findViewById(R.id.btnDisponibilidad);
                btn.setVisibility(View.VISIBLE);
                TextView txt = findViewById(R.id.idTxtDisponibilidad);
                txt.setVisibility(View.INVISIBLE);
                txt.setText("Buscando Disponibilidad...");
            }
        });
    }*/

    public void pedirQueSeConecteABluetooth(){
        TextView text = findViewById(R.id.txtEstadoCoche);
        text.setText("En primer lugar, conéctese con el dispositivo OBD II desde el menú principal.");
        Button btn = findViewById(R.id.btnDisponibilidad);
        btn.setVisibility(View.INVISIBLE);
        TextView txt = findViewById(R.id.idTxtDisponibilidad);
        txt.setText("Sin Conexión.");
        txt.setVisibility(View.VISIBLE);
    }

    public void ponerMensajeAyudaColores(){
        PreferenciasActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = findViewById(R.id.txtEstadoCoche);
                text.setText("Los datos disponibles se mostrarán con un punto verde a la derecha del dato. De lo contrario, habrá un punto rojo.");
                Button btn = findViewById(R.id.btnDisponibilidad);
                btn.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void interpretarDatos(String mensaje){
        if(!peee){
            System.out.println("\n\nESTAMOS AQUIO\n");
        }

        if(mensaje == null){
            String send;
            if(comandoAElegir < comandos.size() - 1){
                comandoAElegir++;
                send = comandos.get(comandoAElegir);
                enviarMensajeADispositivo(send);
            } else {
                mostrarDisponibilidadDatos();
                if (contadorIter < ITERACIONES_DISPONIBILIDAD) {
                    comandoAElegir = 0;
                    send = comandos.get(comandoAElegir);
                    enviarMensajeADispositivo(send);
                    contadorIter++;
                }
            }
            return;
        }

        mensaje = mensaje.replace("null", "");
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        mensaje = mensaje.replaceAll("\n", "");
        mensaje = mensaje.replaceAll("\r", "");
        mensaje = mensaje.replaceAll(" ", "");

        if (mensaje.length() > 35){
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
                    if (mensaje.length()>12){
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }else{
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }
                } catch (NumberFormatException nFE) {
                }
        }


        String send;
        System.out.println(primeraVez);
        if(primeraVez){
            if(msgTemporal.equals("410C")){
                motorON = true;
                primeraVez=false;
                enviarMensajeADispositivo(comandos.get(0));
                ponerMensajeAyudaColores();
                podemosActualizarDisponibilidadPreferencias=true;
                RPM = false;
                TempAceiteMotor = false;
                VelocidadFlujoAire = false;
                CargaCalculadaMotor = false;
                PresionBarometricaAbsoluta = false;
                PresionCombustible = false;
                PresionMedidorTrenCombustible = false;
                PresionAbsColectorAdmision = false;
                PresionVaporSisEvaporativo = false;
                NivelCombustible = false;
                TipoCombustibleNombre = false;
                VelocidadConsumoCombustible = false;
                RelacionCombustibleAire = false;
                TempLiquidoEnfriamiento = false;
                TempAireAmbiente = false;
                TempAireColectorAdmision = false;
                TempCatalizador = false;
                VelocidadVehiculo = false;
                TiempoMotorEncendido = false;
                VelocidadMedia = false;
                ConsumoMedio = false;
                PosicionAcelerador = false;
                DistanciaLuzEncendidaFalla = false;
                EGRComandado=false;
                FallaEGR = false;
                PurgaEvaporativaComand = false;
                CantidadCalentamientosDesdeNoFallas = false;
                DistanciaRecorridadSinLuzFallas = false;
                VoltajeModuloControl = false;
                SincroInyeccionCombustible = false;
                PorcentajeTorqueSolicitado = false;
                PorcentajeTorqueActual = false;
                TorqueReferenciaMotor = false;
            }
        }else{
            switch (msgTemporal) {
                case "410D": {
                    VelocidadVehiculo=true;
                    VelocidadMedia=true;
                    break;
                }
                case "410C": {
                    RPM=true;
                    MainActivity.cocheEncendido = true;
                    cambiarMenuCocheEncendido();
                    break;
                }
                case "4110": {
                    VelocidadFlujoAire=true;
                    break;
                }
                case "4104": {
                    CargaCalculadaMotor = true;
                    break;
                }
                case "415C": {
                    TempAceiteMotor=true;
                    break;
                }
                case "4133": {
                    PresionBarometricaAbsoluta=true;
                    break;
                }
                case "410A": {
                    PresionCombustible=true;
                    break;
                }
                case "4123": {
                    PresionMedidorTrenCombustible=true;
                    break;
                }
                case "410B": {
                    PresionAbsColectorAdmision=true;
                    break;
                }
                case "4132": {
                    PresionVaporSisEvaporativo=true;
                    break;
                }
                case "412F": {
                    NivelCombustible=true;
                    break;
                }
                case "4151": {
                    TipoCombustibleNombre=true;
                    break;
                }
                case "415E": {
                    VelocidadConsumoCombustible=true;
                    ConsumoMedio=true;
                    break;
                }
                case "4144": {
                    RelacionCombustibleAire=true;
                    break;
                }
                case "4146": {
                    TempAireAmbiente=true;
                    break;
                }
                case "4105": {
                    TempLiquidoEnfriamiento=true;
                    break;
                }
                case "410F": {
                    TempAireColectorAdmision=true;
                    break;
                }
                case "413C": {
                    TempCatalizador=true;
                    break;
                }
                case "411F": {
                    TiempoMotorEncendido=true;
                    break;
                }
                case "4111": {
                    PosicionAcelerador=true;
                    break;
                }
                case "4121": {
                    DistanciaLuzEncendidaFalla=true;
                    break;
                }
                case "412C": {
                    EGRComandado=true;
                    break;
                }
                case "412D": {
                    FallaEGR=true;
                    break;
                }
                case "412E": {
                    PurgaEvaporativaComand=true;
                    break;
                }
                case "4130": {
                    CantidadCalentamientosDesdeNoFallas=true;
                    break;
                }
                case "4131": {
                    DistanciaRecorridadSinLuzFallas=true;
                    break;
                }
                case "4142": {
                    VoltajeModuloControl=true;
                    break;
                }
                case "415D": {
                    SincroInyeccionCombustible=true;
                    break;
                }
                case "4161": {
                    PorcentajeTorqueSolicitado=true;
                    break;
                }
                case "4162": {
                    PorcentajeTorqueActual=true;
                    break;
                }
                case "4163": {
                    TorqueReferenciaMotor=true;
                    break;
                }
            }

        }
        if(comandoAElegir < comandos.size() - 1){
            comandoAElegir++;
            send = comandos.get(comandoAElegir);
            enviarMensajeADispositivo(send);
        } else{
            mostrarDisponibilidadDatos();
            if(contadorIter<ITERACIONES_DISPONIBILIDAD){
                comandoAElegir=0;
                send = comandos.get(comandoAElegir);
                enviarMensajeADispositivo(send);
                contadorIter++;
            }else{
                PreferenciasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Button btn = findViewById(R.id.btnDisponibilidad);
                        btn.setVisibility(View.VISIBLE);
                        TextView txt = findViewById(R.id.idTxtDisponibilidad);
                        txt.setVisibility(View.INVISIBLE);
                        txt.setText("Sin Petición");
                        MainActivity.pidiendoPreferencias = false;
                        MainActivity.mainActivity = true;
                        comandoAElegir=0;
                        String send = comandos.get(comandoAElegir);
                        enviarMensajeADispositivo(send);
                    }
                });
            }

        }

    }

    public void mostrarDisponibilidadDatos(){
        PreferenciasActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Spinner spinnerTipoDato = findViewById(R.id.spinnerTipoCampoDatos);
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                ImageView circulo;
                CheckBox checkBox;
                switch (tipoDato){
                    case "Motor":
                        if(VelocidadVehiculo){
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");
                        }else{
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");

                        }
                        if(RPM){
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");

                        }else{
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");


                        }if(VelocidadFlujoAire){
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");

                        }else{
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");


                        }if(CargaCalculadaMotor){
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");

                        }else{
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");

                        }if(TempAceiteMotor){
                            circulo = findViewById(R.id.btnVerde5);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo5);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox5);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");

                        }else{
                            circulo = findViewById(R.id.btnVerde5);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo5);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox5);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");

                        }
                        if(PosicionAcelerador){
                            circulo = findViewById(R.id.btnVerde6);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo6);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox6);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");
                        }else {
                            circulo = findViewById(R.id.btnVerde6);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo6);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox6);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");
                        }
                        if(PorcentajeTorqueSolicitado){
                            circulo = findViewById(R.id.btnVerde7);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo7);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox7);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");
                        }else {
                            circulo = findViewById(R.id.btnVerde7);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo7);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox7);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");
                        }
                        if(PorcentajeTorqueActual){
                            circulo = findViewById(R.id.btnVerde8);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo8);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox8);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");
                        }else {
                            circulo = findViewById(R.id.btnVerde8);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo8);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox8);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");
                        }
                        if(TorqueReferenciaMotor){
                            circulo = findViewById(R.id.btnVerde9);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo9);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox9);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");
                        }else {
                            circulo = findViewById(R.id.btnVerde9);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo9);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox9);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");
                        }
                        if(VoltajeModuloControl){
                            circulo = findViewById(R.id.btnVerde10);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo10);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox10);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Motor");
                        }else {
                            circulo = findViewById(R.id.btnVerde10);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo10);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox10);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Motor");
                        }

                        circulo = findViewById(R.id.btnVerde11);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo11);
                        circulo.setVisibility(View.INVISIBLE);
                        break;
                    case "Presión":
                        if(PresionBarometricaAbsoluta){
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Presión");

                        }else{
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Presión");

                        }if(PresionCombustible){
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Presión");

                        }else{
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Presión");

                        }if(PresionMedidorTrenCombustible){
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Presión");

                        }else{
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Presión");

                        }if(PresionAbsColectorAdmision){
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Presión");

                        }else{
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Presión");

                        }if(PresionVaporSisEvaporativo){
                            circulo = findViewById(R.id.btnVerde5);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo5);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox5);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Presión");

                        }else{
                            circulo = findViewById(R.id.btnVerde5);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo5);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox5);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Presión");

                        }


                        circulo = findViewById(R.id.btnVerde6);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo6);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde7);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo7);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde8);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo8);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde9);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo9);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde10);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo10);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde11);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo11);
                        circulo.setVisibility(View.INVISIBLE);
                        break;
                    case "Combustible":
                        if(NivelCombustible){
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }if(TipoCombustibleNombre){
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }if(VelocidadConsumoCombustible){
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }if(RelacionCombustibleAire){
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(DistanciaLuzEncendidaFalla){
                            circulo = findViewById(R.id.btnVerde5);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo5);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox5);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde5);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo5);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox5);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(EGRComandado){
                            circulo = findViewById(R.id.btnVerde6);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo6);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox6);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde6);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo6);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox6);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(FallaEGR){
                            circulo = findViewById(R.id.btnVerde7);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo7);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox7);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");
                        }else{
                            circulo = findViewById(R.id.btnVerde7);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo7);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox7);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(PurgaEvaporativaComand){
                            circulo = findViewById(R.id.btnVerde8);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo8);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox8);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde8);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo8);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox8);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(CantidadCalentamientosDesdeNoFallas){
                            circulo = findViewById(R.id.btnVerde9);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo9);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox9);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde9);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo9);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox9);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(DistanciaRecorridadSinLuzFallas){
                            circulo = findViewById(R.id.btnVerde10);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo10);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox10);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde10);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo10);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox10);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }
                        if(SincroInyeccionCombustible){
                            circulo = findViewById(R.id.btnVerde11);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo11);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox11);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Combustible");

                        }else{
                            circulo = findViewById(R.id.btnVerde11);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo11);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox11);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Combustible");

                        }

                        break;
                    case "Temperatura":
                        if(TempAireAmbiente){
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Temperatura");

                        }else{
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Temperatura");

                        }if(TempLiquidoEnfriamiento){
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Temperatura");

                        }else{
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Temperatura");

                        }if(TempAireColectorAdmision){
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Temperatura");

                        }else{
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Temperatura");

                        }if(TempCatalizador){
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Temperatura");

                        }else{
                            circulo = findViewById(R.id.btnVerde4);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo4);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox4);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Temperatura");

                        }
                        circulo = findViewById(R.id.btnVerde5);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo5);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde6);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo6);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde7);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo7);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde8);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo8);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde9);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo9);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde10);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo10);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde11);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo11);
                        circulo.setVisibility(View.INVISIBLE);


                        break;
                    case "Datos de Viaje":
                        if(TiempoMotorEncendido){
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Datos de Viaje");

                        }else{
                            circulo = findViewById(R.id.btnVerde1);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo1);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox1);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Datos de Viaje");

                        }if(VelocidadMedia){
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Datos de Viaje");

                        }else{
                            circulo = findViewById(R.id.btnVerde2);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo2);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox2);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Datos de Viaje");

                        }if(ConsumoMedio){
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.VISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.INVISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(true);
                            cambiarDisponibilidadDatabase(checkBox, 1, "Datos de Viaje");

                        }else{
                            circulo = findViewById(R.id.btnVerde3);
                            circulo.setVisibility(View.INVISIBLE);
                            circulo = findViewById(R.id.btnRojo3);
                            circulo.setVisibility(View.VISIBLE);
                            checkBox = findViewById(R.id.checkBox3);
                            checkBox.setClickable(false);
                            checkBox.setChecked(false);
                            cambiarDisponibilidadDatabase(checkBox, 0, "Datos de Viaje");

                        }
                        circulo = findViewById(R.id.btnVerde4);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo4);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde5);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo5);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde6);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo6);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde7);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo7);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde8);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo8);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde9);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo9);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde10);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo10);
                        circulo.setVisibility(View.INVISIBLE);

                        circulo = findViewById(R.id.btnVerde11);
                        circulo.setVisibility(View.INVISIBLE);
                        circulo = findViewById(R.id.btnRojo11);
                        circulo.setVisibility(View.INVISIBLE);

                        break;
                }

            }
        });

    }


    public void activarListenerCheckBoxes(){
        Spinner spinnerTipoDato = findViewById(R.id.spinnerTipoCampoDatos);

        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox1, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox2 = findViewById(R.id.checkBox2);
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox2, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox3 = findViewById(R.id.checkBox3);
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox3, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox4 = findViewById(R.id.checkBox4);
        checkBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox4, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox5 = findViewById(R.id.checkBox5);
        checkBox5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox5, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox6 = findViewById(R.id.checkBox6);
        checkBox6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox6, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox7 = findViewById(R.id.checkBox7);
        checkBox7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox7, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox8 = findViewById(R.id.checkBox8);
        checkBox8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox8, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox9 = findViewById(R.id.checkBox9);
        checkBox9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox9, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox10 = findViewById(R.id.checkBox10);
        checkBox10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox10, tipoDato);
                seHanHechoCambios = true;
            }
        });

        CheckBox checkBox11 = findViewById(R.id.checkBox11);
        checkBox11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String tipoDato = spinnerTipoDato.getSelectedItem().toString();
                cambiarPreferenciaCheckbox(checkBox11, tipoDato);
                seHanHechoCambios = true;
            }
        });


    }

    public void setViewModel(ViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void cambiarTitulos (String tipoDato) {
        CheckBox txt;
        switch (tipoDato) {
            case "Motor":
                txt = findViewById(R.id.checkBox1);
                txt.setText("Velocidad del vehículo");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Velocidad del vehículo")));

                txt = findViewById(R.id.checkBox2);
                txt.setText("Revoluciones por minuto");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Revoluciones por minuto")));

                txt = findViewById(R.id.checkBox3);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Velocidad del flujo del aire MAF");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Velocidad del flujo del aire MAF")));

                txt = findViewById(R.id.checkBox4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Carga calculada del motor");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Carga calculada del motor")));

                txt = findViewById(R.id.checkBox5);
                txt.setText("Temperatura del aceite del motor");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Temperatura del aceite del motor")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox6);
                txt.setText("Posición del acelerador");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Posición del acelerador")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox7);
                txt.setText("Porcentaje torque solicitado");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Porcentaje torque solicitado")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox8);
                txt.setText("Porcentaje torque actual");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Porcentaje torque actual")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox9);
                txt.setText("Torque referencia motor");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Torque referencia motor")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox10);
                txt.setText("Voltaje módulo control");
                txt.setChecked(Boolean.TRUE.equals(motor.get("Voltaje módulo control")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox11);
                txt.setVisibility(View.INVISIBLE);
                break;

            case "Presión":
                txt = findViewById(R.id.checkBox1);
                txt.setText("Presión barométrica absoluta");
                txt.setChecked(Boolean.TRUE.equals(presion.get("Presión barométrica absoluta")));

                txt = findViewById(R.id.checkBox2);
                txt.setText("Presión del combustible");
                txt.setChecked(Boolean.TRUE.equals(presion.get("Presión del combustible")));

                txt = findViewById(R.id.checkBox3);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Presión medidor tren combustible");
                txt.setChecked(Boolean.TRUE.equals(presion.get("Presión medidor tren combustible")));

                txt = findViewById(R.id.checkBox4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Presión absoluta colector admisión");
                txt.setChecked(Boolean.TRUE.equals(presion.get("Presión absoluta colector admisión")));

                txt = findViewById(R.id.checkBox5);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Presión vapor sistema evaporativo");
                txt.setChecked(Boolean.TRUE.equals(presion.get("Presión del vapor del sistema evaporativo")));

                txt = findViewById(R.id.checkBox6);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox7);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox8);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox9);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox10);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox11);
                txt.setVisibility(View.INVISIBLE);
                break;
            case "Combustible":
                txt = findViewById(R.id.checkBox1);
                txt.setText("Nivel de combustible %");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Nivel de combustible %")));

                txt = findViewById(R.id.checkBox2);
                txt.setText("Tipo de combustible");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Tipo de combustible")));

                txt = findViewById(R.id.checkBox3);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Velocidad consumo de combustible");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Velocidad consumo de combustible")));

                txt = findViewById(R.id.checkBox4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Relación combustible-aire");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Relación combustible-aire")));

                txt = findViewById(R.id.checkBox5);
                txt.setText("Distancia con luz fallas encendida");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Distancia con luz fallas encendida")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox6);
                txt.setText("EGR comandado");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("EGR comandado")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox7);
                txt.setText("Falla EGR");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Falla EGR")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox8);
                txt.setText("Purga evaporativa comandada");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Purga evaporativa comandada")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox9);
                txt.setText("Cant. calentamiento sin fallas");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Cant. calentamiento sin fallas")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox10);
                txt.setText("Distancia sin luz fallas encendida");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Distancia sin luz fallas encendida")));
                txt.setVisibility(View.VISIBLE);

                txt = findViewById(R.id.checkBox11);
                txt.setText("Sincronización inyección combustible");
                txt.setChecked(Boolean.TRUE.equals(combustible.get("Sincronización inyección combustible")));
                txt.setVisibility(View.VISIBLE);
                break;
            case "Temperatura":
                txt = findViewById(R.id.checkBox1);
                txt.setText("Temperatura del aire ambiente");
                txt.setChecked(Boolean.TRUE.equals(temperatura.get("Temperatura del aire ambiente")));

                txt = findViewById(R.id.checkBox2);
                txt.setText("Tº del líquido de enfriamiento");
                txt.setChecked(Boolean.TRUE.equals(temperatura.get("Tº del líquido de enfriamiento")));

                txt = findViewById(R.id.checkBox3);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Tº del aire del colector de admisión");
                txt.setChecked(Boolean.TRUE.equals(temperatura.get("Tº del aire del colector de admisión")));

                txt = findViewById(R.id.checkBox4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Temperatura del catalizador");
                txt.setChecked(Boolean.TRUE.equals(temperatura.get("Temperatura del catalizador")));

                txt = findViewById(R.id.checkBox5);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox6);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox7);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox8);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox9);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox10);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox11);
                txt.setVisibility(View.INVISIBLE);
                break;
            case "Datos de Viaje":
                txt = findViewById(R.id.checkBox1);
                txt.setText("Tiempo con el motor encendido");
                txt.setChecked(Boolean.TRUE.equals(datosViaje.get("Tiempo con el motor encendido")));

                txt = findViewById(R.id.checkBox2);
                txt.setText("Velocidad media del viaje");
                txt.setChecked(Boolean.TRUE.equals(datosViaje.get("Velocidad media del viaje")));

                txt = findViewById(R.id.checkBox3);
                txt.setText("Consumo medio del viaje");
                txt.setChecked(Boolean.TRUE.equals(datosViaje.get("Consumo medio del viaje")));

                txt = findViewById(R.id.checkBox4);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox5);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox6);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox7);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox8);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox9);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox10);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.checkBox11);
                txt.setVisibility(View.INVISIBLE);
                break;
        }
        mostrarDisponibilidadDatos();
    }

    public void cambiarPreferenciaCheckbox(CheckBox checkBox, String tipoDato){
        switch (tipoDato) {
            case "Motor":
                motor.put((String) checkBox.getText(), checkBox.isChecked());
                break;
            case "Presión":
                if(checkBox.getText().toString().equals("Presión vapor sistema evaporativo")){
                    presion.put("Presión del vapor del sistema evaporativo", checkBox.isChecked());
                }else{
                    presion.put((String) checkBox.getText(), checkBox.isChecked());
                }
                break;
            case "Combustible":
                combustible.put((String) checkBox.getText(), checkBox.isChecked());
                break;
            case "Temperatura":
                temperatura.put((String) checkBox.getText(), checkBox.isChecked());
                break;
            case "Datos de Viaje":
                datosViaje.put((String) checkBox.getText(), checkBox.isChecked());
                break;
        }
    }

    public void cambiarDisponibilidadDatabase(CheckBox checkBox, int disponibilidad, String tipoDato){
        boolean disp;
        if(disponibilidad==1){
            disp = true;
        }else{
            disp = false;
        }
        switch (tipoDato) {
            case "Motor":

                motorDisponibilidad.put((String) checkBox.getText(), disp);
                break;
            case "Presión":
                if(checkBox.getText().toString().equals("Presión vapor sistema evaporativo")){
                    presionDisponibilidad.put("Presión del vapor del sistema evaporativo", disp);
                }else{
                    presionDisponibilidad.put((String) checkBox.getText(), disp);
                }
                break;
            case "Combustible":
                combustibleDisponibilidad.put((String) checkBox.getText(), disp);
                break;
            case "Temperatura":
                temperaturaDisponibilidad.put((String) checkBox.getText(), disp);
                break;
            case "Datos de Viaje":
                datosViajeDisponibilidad.put((String) checkBox.getText(), disp);
                break;
        }
    }

    public void iniciarPreferencias(){
        try{
            CheckBox txt;
            txt = findViewById(R.id.checkBox1);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Velocidad del vehiculo")));
            txt = findViewById(R.id.checkBox2);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Revoluciones por minuto")));
            txt = findViewById(R.id.checkBox4);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Velocidad del flujo del aire MAF")));
            txt = findViewById(R.id.checkBox3);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Carga calculada del motor")));
            txt = findViewById(R.id.checkBox5);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Temperatura del aceite del motor")));
            txt = findViewById(R.id.checkBox6);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Posición del acelerador")));
            txt = findViewById(R.id.checkBox7);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Porcentaje torque solicitado")));
            txt = findViewById(R.id.checkBox8);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Porcentaje torque actual")));
            txt = findViewById(R.id.checkBox9);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Torque referencia motor")));
            txt = findViewById(R.id.checkBox6);
            txt.setChecked(Boolean.TRUE.equals(motor.get("Voltaje módulo control")));
            txt = findViewById(R.id.checkBox8);
            txt.setVisibility(View.INVISIBLE);
        } catch (Exception e){
            System.out.println(e);
        }

    }

    public void resetearDisponibilidades(){
        RPM = false;
        TempAceiteMotor = false;
        VelocidadFlujoAire = false;
        CargaCalculadaMotor = false;
        PresionBarometricaAbsoluta = false;
        PresionCombustible = false;
        PresionMedidorTrenCombustible = false;
        PresionAbsColectorAdmision = false;
        PresionVaporSisEvaporativo = false;
        NivelCombustible = false;
        TipoCombustibleNombre = false;
        VelocidadConsumoCombustible = false;
        RelacionCombustibleAire = false;
        TempLiquidoEnfriamiento = false;
        TempAireAmbiente = false;
        TempAireColectorAdmision = false;
        TempCatalizador = false;
        VelocidadVehiculo = false;
        TiempoMotorEncendido = false;
        VelocidadMedia = false;
        ConsumoMedio = false;
        PosicionAcelerador = false;
        DistanciaLuzEncendidaFalla = false;
        EGRComandado=false;
        FallaEGR = false;
        PurgaEvaporativaComand = false;
        CantidadCalentamientosDesdeNoFallas = false;
        DistanciaRecorridadSinLuzFallas = false;
        VoltajeModuloControl = false;
        SincroInyeccionCombustible = false;
        PorcentajeTorqueSolicitado = false;
        PorcentajeTorqueActual = false;
        TorqueReferenciaMotor = false;
    }

    public void dialogConfiguraciones(View view){
        new CambiarConfiguracionDesdePreferencias().show(getSupportFragmentManager(), CambiarConfiguracionDesdePreferencias.TAG);
    }

    @Override
    public ArrayList<String> consultarTodasLasConfiguraciones() {
        ArrayList<String> lista = new ArrayList<>();
        lista = contactarBD.consultarTodasLasConfiguraciones();
        return lista;
    }

    @Override
    public boolean consultarConfiguracionActiva(String nombre) {
        return contactarBD.consultarConfiguracionActiva(nombre);
    }

    @Override
    public void cambiarConfiguracion(String nombre) {
        contactarBD.desactivarConfiguracionActiva(configuracionActiva);
        contactarBD.activarConfiguracion(nombre);

        TextView txt = (TextView) findViewById(R.id.txtConfiguracionName);
        txt.setText(nombre);

        checkearDisponibilidad();
        cambiarTitulos("Motor");
        iniciarPreferencias();
        mostrarDisponibilidadDatos();

    }

    public void checkearDisponibilidad(){
        motor = contactarBD.consultarPreferenciasMotor();
        presion = contactarBD.consultarPreferenciasPresion();
        combustible = contactarBD.consultarPreferenciasCombustible();
        temperatura = contactarBD.consultarPreferenciasTemperatura();
        datosViaje = contactarBD.consultarPreferenciasDatosViaje();

        motorDisponibilidad = contactarBD.consultarPreferenciasMotorDisponibilidad();
        presionDisponibilidad = contactarBD.consultarPreferenciasPresionDisponibilidad();
        combustibleDisponibilidad = contactarBD.consultarPreferenciasCombustibleDisponibilidad();
        temperaturaDisponibilidad = contactarBD.consultarPreferenciasTemperaturaDisponibilidad();
        datosViajeDisponibilidad = contactarBD.consultarPreferenciasDatosViajeDisponibilidad();

        RPM = Boolean.TRUE.equals(motorDisponibilidad.get("Revoluciones por minuto"));
        TempAceiteMotor = Boolean.TRUE.equals(motorDisponibilidad.get("Temperatura del aceite del motor"));
        VelocidadVehiculo = Boolean.TRUE.equals(motorDisponibilidad.get("Velocidad del vehículo"));
        VelocidadFlujoAire = Boolean.TRUE.equals(motorDisponibilidad.get("Velocidad del flujo del aire MAF"));
        CargaCalculadaMotor = Boolean.TRUE.equals(motorDisponibilidad.get("Carga calculada del motor"));
        PosicionAcelerador = Boolean.TRUE.equals(motorDisponibilidad.get("Posición del acelerador"));
        PorcentajeTorqueSolicitado = Boolean.TRUE.equals(motorDisponibilidad.get("Porcentaje torque solicitado"));
        PorcentajeTorqueActual = Boolean.TRUE.equals(motorDisponibilidad.get("Porcentaje torque actual"));
        TorqueReferenciaMotor = Boolean.TRUE.equals(motorDisponibilidad.get("Torque referencia motor"));
        VoltajeModuloControl = Boolean.TRUE.equals(motorDisponibilidad.get("Voltaje módulo control"));

        PresionBarometricaAbsoluta = Boolean.TRUE.equals(presionDisponibilidad.get("Presión barométrica absoluta"));
        PresionCombustible = Boolean.TRUE.equals(presionDisponibilidad.get("Presión del combustible"));
        PresionMedidorTrenCombustible = Boolean.TRUE.equals(presionDisponibilidad.get("Presión medidor tren combustible"));
        PresionAbsColectorAdmision = Boolean.TRUE.equals(presionDisponibilidad.get("Presión absoluta colector admisión"));
        PresionVaporSisEvaporativo = Boolean.TRUE.equals(presionDisponibilidad.get("Presión del vapor del sistema evaporativo"));

        NivelCombustible = Boolean.TRUE.equals(combustibleDisponibilidad.get("Nivel de combustible %"));
        TipoCombustibleNombre = Boolean.TRUE.equals(combustibleDisponibilidad.get("Tipo de combustible"));
        VelocidadConsumoCombustible = Boolean.TRUE.equals(combustibleDisponibilidad.get("Velocidad consumo de combustible"));
        RelacionCombustibleAire = Boolean.TRUE.equals(combustibleDisponibilidad.get("Relación combustible-aire"));
        DistanciaLuzEncendidaFalla = Boolean.TRUE.equals(combustibleDisponibilidad.get("Distancia con luz fallas encendida"));
        EGRComandado = Boolean.TRUE.equals(combustibleDisponibilidad.get("EGR comandado"));
        FallaEGR = Boolean.TRUE.equals(combustibleDisponibilidad.get("Falla EGR"));
        PurgaEvaporativaComand = Boolean.TRUE.equals(combustibleDisponibilidad.get("Purga evaporativa comandada"));
        CantidadCalentamientosDesdeNoFallas = Boolean.TRUE.equals(combustibleDisponibilidad.get("Cant. calentamiento sin fallas"));
        DistanciaRecorridadSinLuzFallas = Boolean.TRUE.equals(combustibleDisponibilidad.get("Distancia sin luz fallas encendida"));
        SincroInyeccionCombustible = Boolean.TRUE.equals(combustibleDisponibilidad.get("Sincronización inyección combustible"));

        TempLiquidoEnfriamiento = Boolean.TRUE.equals(temperaturaDisponibilidad.get("Tº del líquido de enfriamiento"));
        TempAireAmbiente = Boolean.TRUE.equals(temperaturaDisponibilidad.get("Temperatura del aire ambiente"));
        TempAireColectorAdmision = Boolean.TRUE.equals(temperaturaDisponibilidad.get("Tº del aire del colector de admisión"));
        TempCatalizador = Boolean.TRUE.equals(temperaturaDisponibilidad.get("Temperatura del catalizador"));

        TiempoMotorEncendido = Boolean.TRUE.equals(datosViajeDisponibilidad.get("Tiempo con el motor encendido"));
        VelocidadMedia = Boolean.TRUE.equals(datosViajeDisponibilidad.get("Velocidad media del viaje"));
        ConsumoMedio = Boolean.TRUE.equals(datosViajeDisponibilidad.get("Consumo medio del viaje"));
    }
}

