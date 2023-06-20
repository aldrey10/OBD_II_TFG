package com.tfg.obdTFG.ui.verdatos.VerEstadisticas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.CodigoDatos;
import com.tfg.obdTFG.ui.verdatos.ParDatoValor;
import com.tfg.obdTFG.ui.verdatos.ParDatoValorProvider;
import com.tfg.obdTFG.ui.verdatos.VerDatoParValor.VerParDatoValorViewModel;
import com.tfg.obdTFG.ui.verdatos.adapter.ParDatoValorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class EstadisticasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParDatoValorAdapter adaptador;
    private ParDatoValorProvider providerEstadisticas;
    private ArrayList<ParDatoValor> listaDatosValor = new ArrayList<>();
    private ArrayList listHistograma = new ArrayList();
    private EstadisticasViewModel viewModel;

    private DatoOBDHelper database;
    private Bluetooth bluetooth;
    private Menu menu;

    private HashMap<String, Boolean> motor;
    private HashMap<String, Boolean> presion;
    private HashMap<String, Boolean> combustible;
    private HashMap<String, Boolean> temperatura;

    public static ArrayList<String> comandos = new ArrayList<>();
    public static int comandoAElegir = 0;
    private String msgTemporal;

    private float tiempoEncendidoMotorDesdeReset = 0;
    private float tiempo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        database = new DatoOBDHelper(this);
        database.getWritableDatabase();
        providerEstadisticas = new ParDatoValorProvider();
        listaDatosValor = providerEstadisticas.getListaDatoValor();
        iniciarRecyclerView();

        comandos = MainActivity.comandos;
        bluetooth = MainActivity.bluetooth;

        viewModel = new EstadisticasViewModel(bluetooth, database);

        establecerCodigos();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icons, menu);

        // change color for icon 0
        Drawable yourdrawable = menu.getItem(0).getIcon();
        if (bluetooth!=null){
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

        final Observer<ArrayList> observerTabla = new Observer<ArrayList>() {
            @Override
            public void onChanged(ArrayList misDatos) {
                activarHistograma(misDatos);
                viewModel.startTaskHistograma();
            }
        };
        viewModel.getMiDataDeHistograma().observe(this, observerTabla);
        viewModel.startTaskHistograma();

        if(bluetooth!=null){
            if(viewModel.getBluetoothEstado()){
                viewModel.setViewModelEstadisticas(viewModel);
                viewModel.setEstamosEnViewModelEstadisticas(true);

                final Observer<ArrayList<String>> observer = new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> misDatos) {
                        if(misDatos.size()>2) {
                            gestionarRecyclerView(misDatos.get(0), misDatos.get(1), misDatos.get(2), misDatos.get(3));
                        }
                        String send = comandos.get(comandoAElegir);
                        enviarMensajeADispositivo(send);
                        if (comandoAElegir >= comandos.size() - 1) {
                            comandoAElegir = 0;
                        } else {
                            comandoAElegir++;
                        }
                    }
                };
                viewModel.getMiDato().observe(this, observer);
            }
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mainActivity = true;
        if(bluetooth!=null){
            viewModel.setEstamosEnViewModelEstadisticas(false);
        }
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

    public void gestionarRecyclerView(String nombreDato1, String valorDato1, String nombreDato2, String valorDato2){
        if(nombreDato1.equals("Velocidad media del viaje")){
            actualizarRecyclerView(nombreDato1, valorDato1);
            actualizarRecyclerView(nombreDato2, valorDato2);
        } else if(nombreDato1.equals("Media de revoluciones")){
            MainActivity.cocheEncendido = true;
            cambiarMenuCocheEncendido();
            actualizarRecyclerView(nombreDato1, valorDato1);
            actualizarRecyclerView(nombreDato2, valorDato2);
        } else if(nombreDato1.equals("Consumo medio del viaje")){
            actualizarRecyclerView(nombreDato1, valorDato1);
            actualizarRecyclerView(nombreDato2, valorDato2);
        } else if(nombreDato1.equals("Tiempo con el motor encendido")){
            actualizarRecyclerView(nombreDato1, valorDato1);
            actualizarRecyclerView(nombreDato2, valorDato2);
        }

    }


    public void resetValores(View view){
        tiempo = viewModel.getTiempoTotalEstadisticas();
        viewModel.resetValores();
        viewModel.insertValuesEstadisticasDB("tiempoTotal", tiempo);
        viewModel.insertValuesEstadisticasDB("tiempoMotorEncendido", tiempo);
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

    //comunicacion de mensajes con el vehiculo
    public void enviarMensajeADispositivo(String mensaje) {
        if (mensaje.length() > 0) {
            mensaje = mensaje + "\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            viewModel.writeVisores(send);
        }
    }

    public void activarHistograma(ArrayList miLista){
        BarChart barChart = findViewById(R.id.histograma);

        BarDataSet barDataSet = new BarDataSet(miLista, "Histograma de aceleraciones");
        barDataSet.setColors(viewModel.COLORES_TABLA_ACELERACIONES);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        Description des = new Description();
        des.setText("Nivel 1: carretera llana. Nivel 7: Bache.");
        barChart.setDescription(des);
        barChart.getDescription().setEnabled(true);

        barChart.notifyDataSetChanged();
        barChart.invalidate();

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

        if (comandos.isEmpty()){
            comandos.add("010C");
        }

    }

}