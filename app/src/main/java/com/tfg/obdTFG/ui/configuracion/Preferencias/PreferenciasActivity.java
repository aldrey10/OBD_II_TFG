package com.tfg.obdTFG.ui.configuracion.Preferencias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import com.google.android.material.snackbar.Snackbar;
import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.CodigoDatos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PreferenciasActivity extends AppCompatActivity implements CambiarConfiguracionDesdePreferencias.CambiarConfiguracion {
    public PreferenciasViewModel viewModel;
    private Menu menu;
    private DatoOBDHelper contactarBD;
    private HashMap<String, Boolean> motor;
    private HashMap<String, Boolean> presion;
    private HashMap<String, Boolean> combustible;
    private HashMap<String, Boolean> temperatura;

    private HashMap<String, Boolean> motorDisponibilidad;
    private HashMap<String, Boolean> presionDisponibilidad;
    private HashMap<String, Boolean> combustibleDisponibilidad;
    private HashMap<String, Boolean> temperaturaDisponibilidad;

    private Boolean seHanHechoCambios = false;
    private Boolean primeraVezSeCreaObservador = true;
    boolean motorON;
    boolean primeraVez = true;
    boolean podemosActualizarDisponibilidadPreferencias = false;

    private int comandoAElegir = 0;
    private final String[] comandosString = new String[]{"010C", "010D", "0110", "0104", "015C", "0133", "010A", "0123", "010B", "0132", "012F", "0151", "015E", "0144",
            "0146", "0105", "010F", "013C", "011F", "0111", "0121", "012C", "012D", "012E", "0130", "0131", "0142", "015D", "0161", "0162", "0163"};
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

    private ProgressDialog dialog;


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

        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(comandosString));
        comandos = stringList;
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
        viewModel = new PreferenciasViewModel(contactarBD, MainActivity.bluetooth);

        checkearDisponibilidad();
        iniciarPreferencias();

        mostrarDisponibilidadDatos();
        activarListenerCheckBoxes();

        configuracionActiva = viewModel.cargarConfiguracionCoche();
        TextView txt = findViewById(R.id.txtConfiguracionName);
        txt.setText(configuracionActiva);

    }

    public void onDestroy() {
        super.onDestroy();
        if(seHanHechoCambios){
            viewModel.guardarPreferencias(motor, presion, combustible, temperatura);

            comandos = new ArrayList<>();
            establecerCodigos();
            MainActivity.comandos = comandos;
            MainActivity.huboCambiosPreferencias = true;
            try{
                MainActivity.hiloDatosPreferencias = false;
            } catch (Exception e){
                System.out.println(e);
            }
        }
        if(podemosActualizarDisponibilidadPreferencias){
            viewModel.actualizarDisponibilidad(motorDisponibilidad, presionDisponibilidad, combustibleDisponibilidad, temperaturaDisponibilidad);
        }
        MainActivity.mainActivity = true;
        if(MainActivity.bluetooth!=null){
            viewModel.setEstamosEnViewModelPreferencias(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icons, menu);

        // change color for icon 0
        Drawable yourdrawable = menu.getItem(0).getIcon();
        if (MainActivity.bluetooth!=null){
            if(viewModel.getBluetoothEstado()){
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

    public void establecerCodigos(){
        motor = viewModel.consultarPreferenciasMotor();
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

        presion = viewModel.consultarPreferenciasPresion();
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


        combustible = viewModel.consultarPreferenciasCombustible();
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


        temperatura = viewModel.consultarPreferenciasTemperatura();
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

    }

    public void pedirMotorEncendido() {
        if (MainActivity.bluetooth != null) {
            if(viewModel.getBluetoothEstado()){

                resetearDisponibilidades();
                mostrarDisponibilidadDatos();
                MainActivity.comandoAElegir = 0;

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                MainActivity.pidiendoPreferencias = true;

                if(primeraVezSeCreaObservador){
                    primeraVezSeCreaObservador=false;
                    viewModel.setViewModelPreferencias(viewModel);

                    final Observer<String> observer = new Observer<String>() {
                        @Override
                        public void onChanged(String misDatos) {
                            interpretarDatos(misDatos);
                        }
                    };
                    viewModel.getMiDato().observe(this, observer);
                }
                viewModel.setEstamosEnViewModelPreferencias(true);
                enviarMensajeADispositivo(comandos.get(0));


                dialog = ProgressDialog.show(PreferenciasActivity.this, "",
                        "Buscando disponibilidad de datos. Por favor espere...", true);

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


    public void pedirQueSeConecteABluetooth(){
        mostrarSnackBarMsg("En primer lugar, conéctese con el dispositivo OBD II través de Bluetooth.");
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

        String msgTemporal = "";
        if (mensaje.length() > 4) {
            if (mensaje.substring(4, 6).equals("41"))
                try {
                    msgTemporal = mensaje.substring(4, 8);
                    msgTemporal = msgTemporal.trim();
                    System.out.println("MI MENSAJE TEMPORAL ES: " + msgTemporal);
                } catch (NumberFormatException nFE) {
                }
        }


        String send;
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
                        viewModel.setEstamosEnViewModelPreferencias(false);
                        comandoAElegir=0;
                        contadorIter=0;
                        dialog.dismiss();
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
        seHanHechoCambios=true;

        Map<String, Boolean> motorMap = motor;
        for (Map.Entry<String, Boolean> set : motorMap.entrySet()) {
            set.setValue(false);
        }
        Map<String, Boolean> presionMap = presion;
        for (Map.Entry<String, Boolean> set : presionMap.entrySet()) {
            set.setValue(false);
        }
        Map<String, Boolean> combustibleMap = combustible;
        for (Map.Entry<String, Boolean> set : combustibleMap.entrySet()) {
            set.setValue(false);
        }
        Map<String, Boolean> temperaturaMap = temperatura;
        for (Map.Entry<String, Boolean> set : temperaturaMap.entrySet()) {
            set.setValue(false);
        }

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
        lista = viewModel.consultarTodasLasConfiguraciones();
        return lista;
    }

    @Override
    public boolean consultarConfiguracionActiva(String nombre) {
        return viewModel.consultarConfiguracionActiva(nombre);
    }

    @Override
    public void cambiarConfiguracion(String nombre) {
        viewModel.desactivarConfiguracionActiva(configuracionActiva);
        viewModel.activarConfiguracion(nombre);

        TextView txt = (TextView) findViewById(R.id.txtConfiguracionName);
        txt.setText(nombre);

        checkearDisponibilidad();
        cambiarTitulos("Motor");
        iniciarPreferencias();
        mostrarDisponibilidadDatos();

    }

    public void checkearDisponibilidad(){
        motor = viewModel.consultarPreferenciasMotor();
        presion = viewModel.consultarPreferenciasPresion();
        combustible = viewModel.consultarPreferenciasCombustible();
        temperatura = viewModel.consultarPreferenciasTemperatura();

        motorDisponibilidad = viewModel.consultarPreferenciasMotorDisponibilidad();
        presionDisponibilidad = viewModel.consultarPreferenciasPresionDisponibilidad();
        combustibleDisponibilidad = viewModel.consultarPreferenciasCombustibleDisponibilidad();
        temperaturaDisponibilidad = viewModel.consultarPreferenciasTemperaturaDisponibilidad();

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

    }

    public void mostrarSnackBarMsg(String mensaje){
        Snackbar.make(findViewById(R.id.snackbar_preferencias), mensaje, 6000).show();
    }
}

