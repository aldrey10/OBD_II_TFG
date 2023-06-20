package com.tfg.obdTFG.ui.verdatos.VerVisores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.CodigoDatos;
import com.tfg.obdTFG.ui.verdatos.TipoCombustible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.nitri.gauge.Gauge;

public class VerDatosVisoresActivity extends AppCompatActivity {

    private String msgTemporal;
    private Bluetooth bluetooth;
    private Menu menu;
    private VerDatosVisoresViewModel viewModel;
    private TextView RPM, TempAceiteMotor, VelocidadFlujoAire, CargaCalculadaMotor, PresionBarometricaAbsoluta, PresionCombustible, PresionMedidorTrenCombustible,
            PresionAbsColectorAdmision, PresionVaporSisEvaporativo, NivelCombustible, TipoCombustibleNombre, VelocidadConsumoCombustible, RelacionCombustibleAire,
            TempLiquidoEnfriamiento, TempAireAmbiente, TempAireColectorAdmision, TempCatalizador, VelocidadVehiculo,
            PosicionAcelerador, DistanciaLuzEncendidaFalla,  EGRComandado, FallaEGR, PurgaEvaporativaComand, CantidadCalentamientosDesdeNoFallas, DistanciaRecorridadSinLuzFallas,
            VoltajeModuloControl, SincroInyeccionCombustible, PorcentajeTorqueSolicitado, PorcentajeTorqueActual, TorqueReferenciaMotor;

    private Gauge GaugeRpm, GaugeTempAceiteMotor, GaugeVelocidadFlujoAire, GaugeCargaCalculadaMotor, GaugePresionBarometricaAbsoluta, GaugePresionCombustible,
            GaugePresionMedidorTrenCombustible, GaugePresionAbsColectorAdmision, GaugePresionVaporSisEvaporativo, GaugeNivelCombustible, GaugeTipoCombustibleNombre,
            GaugeVelocidadConsumoCombustible, GaugeRelacionCombustibleAire, GaugeTempLiquidoEnfriamiento, GaugeTempAireAmbiente, GaugeTempAireColectorAdmision,
            GaugeTempCatalizador, GaugeVelocidadVehiculo, GaugePosicionAcelerador, GaugeDistanciaLuzEncendidaFalla,
            GaugeEGRComandado, GaugeFallaEGR, GaugePurgaEvaporativaComand, GaugeCantidadCalentamientosDesdeNoFallas, GaugeDistanciaRecorridadSinLuzFallas,
            GaugeVoltajeModuloControl, GaugeSincroInyeccionCombustible, GaugePorcentajeTorqueSolicitado, GaugePorcentajeTorqueActual, GaugeTorqueReferenciaMotor;

    private ArrayList<String> comandos = new ArrayList<>();


    private static final int comandoMotor = 1;
    private static final int comandoPresion = 2;
    private static final int comandoCombustible = 3;
    private static final int comandoTemperatura = 4;

    private int comandoActivo = 1;


    private final String[] tiposComandos = new String[]{"ATDP", "ATS0", "ATL0", "ATAT0", "ATST10", "ATSPA0", "ATE0"};
    private int comandoAElegir = 0;

    private DatoOBDHelper contactarBD;
    private HashMap<String, Boolean> motor;
    private HashMap<String, Boolean> presion;
    private HashMap<String, Boolean> combustible;
    private HashMap<String, Boolean> temperatura;



    public VerDatosVisoresActivity(){
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos_visores);

        MainActivity.hiloDatosVisores = true;

        // Variables para los datos del coche
        VelocidadVehiculo = (TextView) findViewById(R.id.txtDato1);
        GaugeVelocidadVehiculo = (Gauge) findViewById(R.id.visor1);
        RPM = (TextView) findViewById(R.id.txtDato2);
        GaugeRpm = (Gauge) findViewById(R.id.visor2);
        VelocidadFlujoAire = (TextView) findViewById(R.id.txtDato3);
        GaugeVelocidadFlujoAire = (Gauge) findViewById(R.id.visor3);
        CargaCalculadaMotor = (TextView) findViewById(R.id.txtDato4);
        GaugeCargaCalculadaMotor = (Gauge) findViewById(R.id.visor4);
        TempAceiteMotor = (TextView) findViewById(R.id.txtDato5);
        GaugeTempAceiteMotor = (Gauge) findViewById(R.id.visor5);
        PosicionAcelerador = (TextView) findViewById(R.id.txtDato6);
        GaugePosicionAcelerador = (Gauge) findViewById(R.id.visor6);
        PorcentajeTorqueSolicitado = (TextView) findViewById(R.id.txtDato7);
        GaugePorcentajeTorqueSolicitado = (Gauge) findViewById(R.id.visor7);
        PorcentajeTorqueActual = (TextView) findViewById(R.id.txtDato8);
        GaugePorcentajeTorqueActual = (Gauge) findViewById(R.id.visor8);
        TorqueReferenciaMotor = (TextView) findViewById(R.id.txtDato9);
        GaugeTorqueReferenciaMotor = (Gauge) findViewById(R.id.visor9);
        VoltajeModuloControl = (TextView) findViewById(R.id.txtDato10);
        GaugeVoltajeModuloControl = (Gauge) findViewById(R.id.visor10);

        PresionBarometricaAbsoluta = (TextView) findViewById(R.id.txtDato1);
        GaugePresionBarometricaAbsoluta = (Gauge) findViewById(R.id.visor1);
        PresionCombustible = (TextView) findViewById(R.id.txtDato2);
        GaugePresionCombustible = (Gauge) findViewById(R.id.visor2);
        PresionMedidorTrenCombustible = (TextView) findViewById(R.id.txtDato3);
        GaugePresionMedidorTrenCombustible = (Gauge) findViewById(R.id.visor3);
        PresionAbsColectorAdmision = (TextView) findViewById(R.id.txtDato4);
        GaugePresionAbsColectorAdmision = (Gauge) findViewById(R.id.visor4);
        PresionVaporSisEvaporativo = (TextView) findViewById(R.id.txtDato5);
        GaugePresionVaporSisEvaporativo = (Gauge) findViewById(R.id.visor5);

        NivelCombustible = (TextView) findViewById(R.id.txtDato1);
        GaugeNivelCombustible = (Gauge) findViewById(R.id.visor1);
        TipoCombustibleNombre = (TextView) findViewById(R.id.txtDato2);
        GaugeTipoCombustibleNombre = (Gauge) findViewById(R.id.visor2);
        VelocidadConsumoCombustible= (TextView) findViewById(R.id.txtDato3);
        GaugeVelocidadConsumoCombustible = (Gauge) findViewById(R.id.visor3);
        RelacionCombustibleAire= (TextView) findViewById(R.id.txtDato4);
        GaugeRelacionCombustibleAire = (Gauge) findViewById(R.id.visor4);
        DistanciaLuzEncendidaFalla = (TextView) findViewById(R.id.txtDato5);
        GaugeDistanciaLuzEncendidaFalla = (Gauge) findViewById(R.id.visor5);
        EGRComandado = (TextView) findViewById(R.id.txtDato6);
        GaugeEGRComandado = (Gauge) findViewById(R.id.visor6);
        FallaEGR = (TextView) findViewById(R.id.txtDato7);
        GaugeFallaEGR = (Gauge) findViewById(R.id.visor7);
        PurgaEvaporativaComand = (TextView) findViewById(R.id.txtDato8);
        GaugePurgaEvaporativaComand = (Gauge) findViewById(R.id.visor8);
        CantidadCalentamientosDesdeNoFallas = (TextView) findViewById(R.id.txtDato9);
        GaugeCantidadCalentamientosDesdeNoFallas = (Gauge) findViewById(R.id.visor9);
        DistanciaRecorridadSinLuzFallas = (TextView) findViewById(R.id.txtDato10);
        GaugeDistanciaRecorridadSinLuzFallas = (Gauge) findViewById(R.id.visor10);
        SincroInyeccionCombustible = (TextView) findViewById(R.id.txtDato11);
        GaugeSincroInyeccionCombustible = (Gauge) findViewById(R.id.visor11);

        TempAireAmbiente = (TextView) findViewById(R.id.txtDato1);
        GaugeTempAireAmbiente = (Gauge) findViewById(R.id.visor1);
        TempLiquidoEnfriamiento = (TextView) findViewById(R.id.txtDato2);
        GaugeTempLiquidoEnfriamiento = (Gauge) findViewById(R.id.visor2);
        TempAireColectorAdmision = (TextView) findViewById(R.id.txtDato3);
        GaugeTempAireColectorAdmision = (Gauge) findViewById(R.id.visor3);
        TempCatalizador = (TextView) findViewById(R.id.txtDato4);
        GaugeTempCatalizador = (Gauge) findViewById(R.id.visor4);



        contactarBD = new DatoOBDHelper(this);
        bluetooth = MainActivity.bluetooth;

        viewModel = new VerDatosVisoresViewModel(contactarBD, bluetooth);

        establecerCodigos();
        comandos = MainActivity.comandos;
        cambiarTitulos("Motor");


        Spinner spinnerTipoDato = findViewById(R.id.spinnerTipoDatos);
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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mainActivity = true;
        if(bluetooth!=null){
            viewModel.setEstamosEnViewModelVisores(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icons, menu);

        System.out.println("\nlaksjfdklaskldfjasdjfjasfdjkl\n\n");
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
            if(viewModel.getBluetoothEstado()){
                viewModel.setViewModelVisores(viewModel);
                viewModel.setEstamosEnViewModelVisores(true);

                final Observer<String> observer = new Observer<String>() {
                    @Override
                    public void onChanged(String misDatos) {
                        mostrarDatos(misDatos);
                    }
                };
                viewModel.getMiDato().observe(this, observer);
            }
        }

        return true;
    }

    public void cambiarMenuCocheEncendido(){
        Drawable yourdrawable1 = menu.getItem(1).getIcon();
        if(MainActivity.cocheEncendido){
            VerDatosVisoresActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    yourdrawable1.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                }
            });
        }

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
            try{
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
            }catch (Exception e){

            }

        }


        switch (msgTemporal) {
            case "410D": {
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(obdval) + " Km/h";
                    VelocidadVehiculo.setText(texto);
                    GaugeVelocidadVehiculo.setValue(obdval);
                }
                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Velocidad del vehículo", obdval);
                }
                viewModel.insertValuesEstadisticasDB("Velocidad", (float) obdval);
                break;
            }
            case "410C": {
                float val = (float) (obdval / 4);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " RPM";
                    RPM.setText(texto);
                    GaugeRpm.setValue(val);
                }

                MainActivity.cocheEncendido = true;
                cambiarMenuCocheEncendido();
                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Revoluciones por minuto", val);
                }
                viewModel.insertValuesEstadisticasDB("Revoluciones", val);

                break;
            }
            case "4110": {
                float val = (float) (obdval / 100);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " gr/sec";
                    VelocidadFlujoAire.setText(texto);
                    GaugeVelocidadFlujoAire.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Tº del aire del colector de admisión", val);
                }

                break;
            }
            case "4104": {
                float val = (float) (obdval / 2.55);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " %";
                    CargaCalculadaMotor.setText(texto);
                    GaugeCargaCalculadaMotor.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Carga calculada del motor", val);
                }break;
            }
            case "415C": {
                float val = (float) (obdval - 40);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " ºC";
                    TempAceiteMotor.setText(texto);
                    GaugeTempAceiteMotor.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Temperatura del aceite del motor", val);
                }break;
            }
            case "4111": {
                float val = (float) (obdval/2.55);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " %";
                    PosicionAcelerador.setText(texto);
                    GaugePosicionAcelerador.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Posición del acelerador", val);
                }break;
            }
            case "4161": {
                float val = (float) (obdval - 125);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " %";
                    PorcentajeTorqueSolicitado.setText(texto);
                    GaugePorcentajeTorqueSolicitado.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Porcentaje torque solicitado", val);
                }break;
            }
            case "4162": {
                float val = (float) (obdval - -125);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " %";
                    PorcentajeTorqueActual.setText(texto);
                    GaugePorcentajeTorqueActual.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Porcentaje torque actual", val);
                }break;
            }
            case "4163": {
                float val = (float) (obdval);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " Nm";
                    TorqueReferenciaMotor.setText(texto);
                    GaugeTorqueReferenciaMotor.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Torque referencia motor", val);
                }break;
            }
            case "4142": {
                float val = (float) (obdval/1000);
                if(comandoActivo==comandoMotor){
                    String texto = String.valueOf(val) + " V";
                    VoltajeModuloControl.setText(texto);
                    GaugeVoltajeModuloControl.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Voltaje módulo control", val);
                }break;
            }
            case "4133": {
                if(comandoActivo==comandoPresion){
                    String texto = String.valueOf(obdval) + " kPa";
                    PresionBarometricaAbsoluta.setText(texto);
                    GaugePresionBarometricaAbsoluta.setValue(obdval);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Presión barométrica absoluta", obdval);
                }break;
            }
            case "410A": {
                float val = (float) (obdval * 3);
                if(comandoActivo==comandoPresion){
                    String texto = String.valueOf(val) + " kPa";
                    PresionCombustible.setText(texto);
                    GaugePresionCombustible.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Presión del combustible", val);
                }break;
            }
            case "4123": {
                float val = (float) (obdval * 10);
                if(comandoActivo==comandoPresion){
                    String texto = String.valueOf(val) + " kPa";
                    PresionMedidorTrenCombustible.setText(texto);
                    GaugePresionMedidorTrenCombustible.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Presión medidor tren combustible", val);
                }break;
            }
            case "410B": {
                if(comandoActivo==comandoPresion){
                    String texto = String.valueOf(obdval) + " kPa";
                    PresionAbsColectorAdmision.setText(texto);
                    GaugePresionAbsColectorAdmision.setValue(obdval);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Presion absoluta colector admisión", obdval);
                }break;
            }
            case "4132": {
                float val = (float) ((obdval / 4) - 8192);
                if(comandoActivo==comandoPresion){
                    String texto = String.valueOf(obdval) + " Pa";
                    PresionVaporSisEvaporativo.setText(texto);
                    GaugePresionVaporSisEvaporativo.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Presión del vapor del sistema evaporativo", val);
                }break;
            }
            case "412F": {
                float val = (float) (obdval / 2.55);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(obdval) + " %";
                    NivelCombustible.setText(texto);
                    GaugeNivelCombustible.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Nivel de combustible %", val);
                }break;
            }
            case "4151": {
                if(comandoActivo==comandoCombustible){
                    String texto = TipoCombustible.fromValue(obdval).getDescription();
                    TipoCombustibleNombre.setText(texto);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Tipo de combustible", obdval);
                }
                        /*GaugeDistanciaLuzEncendidaFalla.setMinValue(0);
                        GaugeDistanciaLuzEncendidaFalla.setMaxValue(65000);
                        GaugeDistanciaLuzEncendidaFalla.setValuePerNick(5000);
                        GaugeDistanciaLuzEncendidaFalla.setTotalNicks(13);
                        GaugeDistanciaLuzEncendidaFalla.setValue(val);*/
                break;
            }
            case "415E": {
                float val = (float) (obdval / 20);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " L/h";
                    VelocidadConsumoCombustible.setText(texto);
                    GaugeVelocidadConsumoCombustible.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Velocidad consumo de combustible", val);
                }
                viewModel.insertValuesEstadisticasDB("Consumo", (float) val);
                break;
            }
            case "4144": {
                float val = (float) (obdval / 32768);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(obdval) + " prop.";
                    RelacionCombustibleAire.setText(texto);
                    GaugeRelacionCombustibleAire.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Relación combustible-aire", val);
                }break;
            }
            case "4121": {
                float val = (float) (obdval);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " km.";
                    DistanciaLuzEncendidaFalla.setText(texto);
                    GaugeDistanciaLuzEncendidaFalla.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Distancia con luz fallas encendida", val);
                }break;
            }
            case "412C": {
                float val = (float) (obdval / 2.55);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " %.";
                    EGRComandado.setText(texto);
                    GaugeEGRComandado.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("EGR comandado", val);
                }break;
            }
            case "412D": {
                float val = (float) ((obdval / 1.28) -100);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " %.";
                    FallaEGR.setText(texto);
                    GaugeFallaEGR.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Falla EGR", val);
                }break;
            }
            case "412E": {
                float val = (float) (obdval / 2.55);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " %.";
                    PurgaEvaporativaComand.setText(texto);
                    GaugePurgaEvaporativaComand.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Purga evaporativa comandada", val);
                }break;
            }
            case "4130": {
                float val = (float) (obdval);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(obdval) + " calent.";
                    CantidadCalentamientosDesdeNoFallas.setText(texto);
                    GaugeCantidadCalentamientosDesdeNoFallas.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Cant. calentamiento sin fallas", val);
                }break;
            }
            case "4131": {
                float val = (float) (obdval);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " km.";
                    DistanciaRecorridadSinLuzFallas.setText(texto);
                    GaugeDistanciaRecorridadSinLuzFallas.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Distancia sin luz fallas encendida", val);
                }break;
            }
            case "415D": {
                float val = (float) ((obdval / 128) - 210);
                if(comandoActivo==comandoCombustible){
                    String texto = String.valueOf(val) + " º.";
                    SincroInyeccionCombustible.setText(texto);
                    GaugeSincroInyeccionCombustible.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Sincronización inyección combustible", val);
                }break;
            }
            case "4146": {
                float val = (float) (obdval - 40);
                if(comandoActivo==comandoTemperatura){
                    String texto = String.valueOf(val) + " ºC";
                    TempAireAmbiente.setText(texto);
                    GaugeTempAireAmbiente.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Temperatura del aire ambiente", val);
                }break;
            }
            case "4105": {
                float val = (float) (obdval - 40);
                if(comandoActivo==comandoTemperatura){
                    String texto = String.valueOf(val) + " ºC";
                    TempLiquidoEnfriamiento.setText(texto);
                    GaugeTempLiquidoEnfriamiento.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Tº del líquido de enfriamiento", val);
                }break;
            }
            case "410F": {
                float val = (float) (obdval - 40);
                if(comandoActivo==comandoTemperatura){
                    String texto = String.valueOf(val) + " ºC";
                    TempAireColectorAdmision.setText(texto);
                    GaugeTempAireColectorAdmision.setValue(val);
                }

                if(MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Tº del aire del colector de admisión", val);
                }break;
            }
            case "413C": {
                float val = (float) ((obdval / 10) - 40);
                if(comandoActivo==comandoTemperatura){
                    String texto = String.valueOf(val) + " ºC";
                    TempCatalizador.setText(texto);
                    GaugeTempCatalizador.setValue(val);
                }

                if (MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Temperatura del catalizador", val);
                }
                break;
            }
            case "411F": {
                if (MainActivity.estamosCapturando) {
                    viewModel.insertDBExport("Tiempo con el motor encendido", obdval);
                }
                break;
            }
        }

        String send = comandos.get(comandoAElegir);
        enviarMensajeADispositivo(send);
        if (comandoAElegir >= comandos.size() - 1) {
            comandoAElegir = 0;
        } else {
            comandoAElegir++;
        }

    }

    //comunicacion de mensajes con el vehiculo
    public void enviarMensajeADispositivo(String mensaje) {
        if (mensaje.length() > 0) {
            mensaje = mensaje + "\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            viewModel.writeVisores(send);
        }
    }

    //establecemos los array de codigos que solicitaremos al OBD
    public void establecerCodigos(){
        motor = viewModel.consultarPreferenciasMotor();
        presion = viewModel.consultarPreferenciasPresion();
        combustible = viewModel.consultarPreferenciasCombustible();
        temperatura = viewModel.consultarPreferenciasTemperatura();

    }

    public void cambiarTitulos (String tipoDato) {
        comandoAElegir=0;
        TextView txt;
        Gauge gaugeView;
        switch (tipoDato) {
            case "Motor":
                comandoActivo = comandoMotor;

                txt = findViewById(R.id.lblTipoDato1);
                txt.setText("Velocidad del vehículo");
                txt = findViewById(R.id.txtDato1);
                if(Objects.equals(motor.get("Velocidad del vehículo"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor1);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeVelocidadVehiculo.setMinValue(0);
                GaugeVelocidadVehiculo.setMaxValue(300);
                GaugeVelocidadVehiculo.setTotalNicks(100);
                GaugeVelocidadVehiculo.setValuePerNick(3);
                GaugeVelocidadVehiculo.setValue(0);

                txt = findViewById(R.id.lblTipoDato2);
                txt.setText("Revoluciones por minuto");
                txt = findViewById(R.id.txtDato2);
                if(Objects.equals(motor.get("Revoluciones por minuto"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor2);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeRpm.setMinValue(0);
                GaugeRpm.setMaxValue(6000);
                GaugeRpm.setTotalNicks(120);
                GaugeRpm.setValuePerNick(50);
                GaugeRpm.setValue(0);

                txt = findViewById(R.id.lblTipoDato3);
                txt.setText("Velocidad del flujo del aire MAF");
                txt = findViewById(R.id.txtDato3);
                if(Objects.equals(motor.get("Velocidad del flujo del aire MAF"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor3);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeVelocidadFlujoAire.setMinValue(0);
                GaugeVelocidadFlujoAire.setMaxValue(650);
                GaugeVelocidadFlujoAire.setTotalNicks(130);
                GaugeVelocidadFlujoAire.setValuePerNick(5);
                GaugeVelocidadFlujoAire.setValue(0);

                txt = findViewById(R.id.lblTipoDato4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Carga calculada del motor");
                txt = findViewById(R.id.txtDato4);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Carga calculada del motor"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor4);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeCargaCalculadaMotor.setMinValue(0);
                GaugeCargaCalculadaMotor.setMaxValue(100);
                GaugeCargaCalculadaMotor.setTotalNicks(100);
                GaugeCargaCalculadaMotor.setValuePerNick(1);
                GaugeCargaCalculadaMotor.setValue(0);

                txt = findViewById(R.id.lblTipoDato5);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Temperatura del aceite del motor");
                txt = findViewById(R.id.txtDato5);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Temperatura del aceite del motor"), false)){
                    txt.setText("NO DISPONIBLE");
                    txt.setVisibility(View.VISIBLE);
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor5);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTempAceiteMotor.setMinValue(-40);
                GaugeTempAceiteMotor.setMaxValue(210);
                GaugeTempAceiteMotor.setTotalNicks(50);
                GaugeTempAceiteMotor.setValuePerNick(5);
                GaugeTempAceiteMotor.setValue(0);

                txt = findViewById(R.id.lblTipoDato6);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Posición del acelerador");
                txt = findViewById(R.id.txtDato6);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Posición del acelerador"), false)){
                    txt.setText("NO DISPONIBLE");
                    txt.setVisibility(View.VISIBLE);
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor6);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePosicionAcelerador.setMinValue(0);
                GaugePosicionAcelerador.setMaxValue(100);
                GaugePosicionAcelerador.setTotalNicks(100);
                GaugePosicionAcelerador.setValuePerNick(1);
                GaugePosicionAcelerador.setValue(0);

                txt = findViewById(R.id.lblTipoDato7);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Porcentaje torque solicitado");
                txt = findViewById(R.id.txtDato7);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Porcentaje torque solicitado"), false)){
                    txt.setText("NO DISPONIBLE");
                    txt.setVisibility(View.VISIBLE);
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor7);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePorcentajeTorqueSolicitado.setMinValue(-125);
                GaugePorcentajeTorqueSolicitado.setMaxValue(125);
                GaugePorcentajeTorqueSolicitado.setTotalNicks(50);
                GaugePorcentajeTorqueSolicitado.setValuePerNick(5);
                GaugePorcentajeTorqueSolicitado.setValue(0);

                txt = findViewById(R.id.lblTipoDato8);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Porcentaje torque actual");
                txt = findViewById(R.id.txtDato8);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Porcentaje torque actual"), false)){
                    txt.setText("NO DISPONIBLE");
                    txt.setVisibility(View.VISIBLE);
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor8);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePorcentajeTorqueActual.setMinValue(-125);
                GaugePorcentajeTorqueActual.setMaxValue(125);
                GaugePorcentajeTorqueActual.setTotalNicks(50);
                GaugePorcentajeTorqueActual.setValuePerNick(5);
                GaugePorcentajeTorqueActual.setValue(0);

                txt = findViewById(R.id.lblTipoDato9);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Torque referencia motorr");
                txt = findViewById(R.id.txtDato9);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Torque referencia motor"), false)){
                    txt.setText("NO DISPONIBLE");
                    txt.setVisibility(View.VISIBLE);
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor9);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTorqueReferenciaMotor.setMinValue(0);
                GaugeTorqueReferenciaMotor.setMaxValue(70000);
                GaugeTorqueReferenciaMotor.setTotalNicks(70);
                GaugeTorqueReferenciaMotor.setValuePerNick(1000);
                GaugeTorqueReferenciaMotor.setValue(0);

                txt = findViewById(R.id.lblTipoDato10);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Voltaje módulo control");
                txt = findViewById(R.id.txtDato10);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(motor.get("Temperatura del aceite del motor"), false)){
                    txt.setText("NO DISPONIBLE");
                    txt.setVisibility(View.VISIBLE);
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor10);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeVoltajeModuloControl.setMinValue(0);
                GaugeVoltajeModuloControl.setMaxValue(70);
                GaugeVoltajeModuloControl.setTotalNicks(70);
                GaugeVoltajeModuloControl.setValuePerNick(1);
                GaugeVoltajeModuloControl.setValue(0);

                txt = findViewById(R.id.lblTipoDato11);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato11);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                gaugeView = findViewById(R.id.visor11);
                gaugeView.setVisibility(View.INVISIBLE);

                break;

            case "Presión":
                comandoActivo = comandoPresion;

                txt = findViewById(R.id.lblTipoDato1);
                txt.setText("Presión barométrica absoluta");
                txt = findViewById(R.id.txtDato1);
                if(Objects.equals(presion.get("Presión barométrica absoluta"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor1);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePresionBarometricaAbsoluta.setMinValue(0);
                GaugePresionBarometricaAbsoluta.setMaxValue(300);
                GaugePresionBarometricaAbsoluta.setTotalNicks(100);
                GaugePresionBarometricaAbsoluta.setValuePerNick(3);
                GaugePresionBarometricaAbsoluta.setValue(0);

                txt = findViewById(R.id.lblTipoDato2);
                txt.setText("Presión del combustible");
                txt = findViewById(R.id.txtDato2);
                if(Objects.equals(presion.get("Presión del combustible"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor2);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePresionCombustible.setMinValue(0);
                GaugePresionCombustible.setMaxValue(800);
                GaugePresionCombustible.setTotalNicks(80);
                GaugePresionCombustible.setValuePerNick(10);
                GaugePresionCombustible.setValue(0);

                txt = findViewById(R.id.lblTipoDato3);
                txt.setText("Presión medidor tren combustible");
                txt = findViewById(R.id.txtDato3);
                if(Objects.equals(presion.get("Presión medidor tren combustible"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor3);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePresionMedidorTrenCombustible.setMinValue(0);
                GaugePresionMedidorTrenCombustible.setMaxValue(700000);
                GaugePresionMedidorTrenCombustible.setTotalNicks(70);
                GaugePresionMedidorTrenCombustible.setValuePerNick(10000);
                GaugePresionMedidorTrenCombustible.setValue(0);

                txt = findViewById(R.id.lblTipoDato4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Presión absoluta colector admisión");
                txt = findViewById(R.id.txtDato4);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(presion.get("Presión absoluta colector admisión"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor4);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePresionAbsColectorAdmision.setMinValue(0);
                GaugePresionAbsColectorAdmision.setMaxValue(300);
                GaugePresionAbsColectorAdmision.setTotalNicks(100);
                GaugePresionAbsColectorAdmision.setValuePerNick(3);
                GaugePresionAbsColectorAdmision.setValue(0);

                txt = findViewById(R.id.lblTipoDato5);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Presión vapor sistema evaporativo");
                txt = findViewById(R.id.txtDato5);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(presion.get("Presión del vapor del sistema evaporativo"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor5);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePresionVaporSisEvaporativo.setMinValue(-8000);
                GaugePresionVaporSisEvaporativo.setMaxValue(8000);
                GaugePresionVaporSisEvaporativo.setTotalNicks(160);
                GaugePresionVaporSisEvaporativo.setValuePerNick(100);
                GaugePresionVaporSisEvaporativo.setValue(0);

                txt = findViewById(R.id.lblTipoDato6);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato6);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato7);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato7);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato8);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato8);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato9);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato9);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato10);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato10);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato11);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato10);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                gaugeView = findViewById(R.id.visor6);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor7);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor8);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor9);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor10);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor11);
                gaugeView.setVisibility(View.INVISIBLE);
                break;
            case "Combustible":
                comandoActivo = comandoCombustible;

                txt = findViewById(R.id.lblTipoDato1);
                txt.setText("Nivel de combustible %");
                txt = findViewById(R.id.txtDato1);
                if(Objects.equals(combustible.get("Nivel de combustible %"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor1);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeNivelCombustible.setMinValue(0);
                GaugeNivelCombustible.setMaxValue(100);
                GaugeNivelCombustible.setValuePerNick(1);
                GaugeNivelCombustible.setTotalNicks(100);
                GaugeNivelCombustible.setValue(0);

                txt = findViewById(R.id.lblTipoDato2);
                txt.setText("Tipo de combustible");
                txt = findViewById(R.id.txtDato2);
                if(Objects.equals(combustible.get("Tipo de combustible"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor2);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTipoCombustibleNombre.setValue(0);

                txt = findViewById(R.id.lblTipoDato3);
                txt.setText("Velocidad consumo de combustible");
                txt = findViewById(R.id.txtDato3);
                if(Objects.equals(combustible.get("Velocidad consumo de combustible"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor3);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeVelocidadConsumoCombustible.setMinValue(0);
                GaugeVelocidadConsumoCombustible.setMaxValue(3000);
                GaugeVelocidadConsumoCombustible.setValuePerNick(30);
                GaugeVelocidadConsumoCombustible.setTotalNicks(100);
                GaugeVelocidadConsumoCombustible.setValue(0);

                txt = findViewById(R.id.lblTipoDato4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Relación combustible-aire");
                txt = findViewById(R.id.txtDato4);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Relación combustible-aire"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor4);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeRelacionCombustibleAire.setMinValue(0);
                GaugeRelacionCombustibleAire.setMaxValue(2);
                GaugeRelacionCombustibleAire.setValuePerNick(1);
                GaugeRelacionCombustibleAire.setTotalNicks(2);
                GaugeRelacionCombustibleAire.setValue(0);

                txt = findViewById(R.id.lblTipoDato5);
                txt.setText("Distancia con luz fallas encendida");
                txt.setVisibility(View.VISIBLE);
                txt = findViewById(R.id.txtDato5);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Distancia con luz fallas encendida"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor5);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeDistanciaLuzEncendidaFalla.setMinValue(0);
                GaugeDistanciaLuzEncendidaFalla.setMaxValue(70000);
                GaugeDistanciaLuzEncendidaFalla.setValuePerNick(700);
                GaugeDistanciaLuzEncendidaFalla.setTotalNicks(100);
                GaugeDistanciaLuzEncendidaFalla.setValue(0);

                txt = findViewById(R.id.lblTipoDato6);
                txt.setText("EGR comandado");
                txt.setVisibility(View.VISIBLE);
                txt = findViewById(R.id.txtDato6);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("EGR comandado"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor6);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeEGRComandado.setMinValue(0);
                GaugeEGRComandado.setMaxValue(100);
                GaugeEGRComandado.setTotalNicks(100);
                GaugeEGRComandado.setValuePerNick(1);
                GaugeEGRComandado.setValue(0);

                txt = findViewById(R.id.lblTipoDato7);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Falla EGR");
                txt = findViewById(R.id.txtDato7);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Falla EGR"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor7);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeFallaEGR.setMinValue(-100);
                GaugeFallaEGR.setMaxValue(100);
                GaugeFallaEGR.setValuePerNick(2);
                GaugeFallaEGR.setTotalNicks(100);
                GaugeFallaEGR.setValue(0);

                txt = findViewById(R.id.lblTipoDato8);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Purga evaporativa comandada");
                txt = findViewById(R.id.txtDato8);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Purga evaporativa comandada"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor8);
                gaugeView.setVisibility(View.VISIBLE);
                GaugePurgaEvaporativaComand.setMinValue(0);
                GaugePurgaEvaporativaComand.setMaxValue(100);
                GaugePurgaEvaporativaComand.setTotalNicks(100);
                GaugePurgaEvaporativaComand.setValuePerNick(1);
                GaugePurgaEvaporativaComand.setValue(0);

                txt = findViewById(R.id.lblTipoDato9);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Cant. calentamiento sin fallas");
                txt = findViewById(R.id.txtDato9);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Cant. calentamiento sin fallas"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor9);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeCantidadCalentamientosDesdeNoFallas.setMinValue(0);
                GaugeCantidadCalentamientosDesdeNoFallas.setMaxValue(300);
                GaugeCantidadCalentamientosDesdeNoFallas.setValuePerNick(3);
                GaugeCantidadCalentamientosDesdeNoFallas.setTotalNicks(100);
                GaugeCantidadCalentamientosDesdeNoFallas.setValue(0);

                txt = findViewById(R.id.lblTipoDato10);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Distancia sin luz fallas encendida");
                txt = findViewById(R.id.txtDato10);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Distancia sin luz fallas encendida"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor10);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeDistanciaRecorridadSinLuzFallas.setMinValue(0);
                GaugeDistanciaRecorridadSinLuzFallas.setMaxValue(70000);
                GaugeDistanciaRecorridadSinLuzFallas.setValuePerNick(700);
                GaugeDistanciaRecorridadSinLuzFallas.setTotalNicks(100);
                GaugeDistanciaRecorridadSinLuzFallas.setValue(0);

                txt = findViewById(R.id.lblTipoDato11);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Sincronización inyección combustible");
                txt = findViewById(R.id.txtDato11);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(combustible.get("Sincronización inyección combustible"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor11);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeSincroInyeccionCombustible.setMinValue(-300);
                GaugeSincroInyeccionCombustible.setMaxValue(300);
                GaugeSincroInyeccionCombustible.setValuePerNick(10);
                GaugeSincroInyeccionCombustible.setTotalNicks(60);
                GaugeSincroInyeccionCombustible.setValue(0);


                break;
            case "Temperatura":
                comandoActivo = comandoTemperatura;

                txt = findViewById(R.id.lblTipoDato1);
                txt.setText("Temperatura del aire ambiente");
                txt = findViewById(R.id.txtDato1);
                if(Objects.equals(temperatura.get("Temperatura del aire ambiente"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor1);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTempAireAmbiente.setMinValue(-70);
                GaugeTempAireAmbiente.setMaxValue(230);
                GaugeTempAireAmbiente.setValuePerNick(3);
                GaugeTempAireAmbiente.setTotalNicks(100);
                GaugeTempAireAmbiente.setValue(0);

                txt = findViewById(R.id.lblTipoDato2);
                txt.setText("Tº del líquido de enfriamiento");
                txt = findViewById(R.id.txtDato2);
                if(Objects.equals(temperatura.get("Tº del líquido de enfriamiento"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor2);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTempLiquidoEnfriamiento.setMinValue(-70);
                GaugeTempLiquidoEnfriamiento.setMaxValue(230);
                GaugeTempLiquidoEnfriamiento.setValuePerNick(3);
                GaugeTempLiquidoEnfriamiento.setTotalNicks(100);
                GaugeTempLiquidoEnfriamiento.setValue(0);

                txt = findViewById(R.id.lblTipoDato3);
                txt.setText("Tº del aire del colector de admisión");
                txt = findViewById(R.id.txtDato3);
                if(Objects.equals(temperatura.get("Tº del aire del colector de admisión"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor3);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTempAireColectorAdmision.setMinValue(-70);
                GaugeTempAireColectorAdmision.setMaxValue(230);
                GaugeTempAireColectorAdmision.setValuePerNick(3);
                GaugeTempAireColectorAdmision.setTotalNicks(100);
                GaugeTempAireColectorAdmision.setValue(0);

                txt = findViewById(R.id.lblTipoDato4);
                txt.setVisibility(View.VISIBLE);
                txt.setText("Temperatura del catalizador");
                txt = findViewById(R.id.txtDato4);
                txt.setVisibility(View.VISIBLE);
                if(Objects.equals(temperatura.get("Temperatura del catalizador"), false)){
                    txt.setText("NO DISPONIBLE");
                }else{
                    txt.setText("Sin datos");
                }
                gaugeView = findViewById(R.id.visor4);
                gaugeView.setVisibility(View.VISIBLE);
                GaugeTempCatalizador.setMinValue(-100);
                GaugeTempCatalizador.setMaxValue(6900);
                GaugeTempCatalizador.setValuePerNick(70);
                GaugeTempCatalizador.setTotalNicks(100);
                GaugeTempCatalizador.setValue(0);

                txt = findViewById(R.id.lblTipoDato5);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato5);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato6);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato6);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato7);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato7);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato8);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato8);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato9);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato9);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato10);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato10);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                txt = findViewById(R.id.lblTipoDato11);
                txt.setVisibility(View.INVISIBLE);
                txt = findViewById(R.id.txtDato10);
                txt.setText("");
                txt.setVisibility(View.INVISIBLE);

                gaugeView = findViewById(R.id.visor5);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor6);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor7);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor8);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor9);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor10);
                gaugeView.setVisibility(View.INVISIBLE);
                gaugeView = findViewById(R.id.visor11);
                gaugeView.setVisibility(View.INVISIBLE);

                break;
        }
    }

}