package com.tfg.obdTFG.ui.verdatos.VerDatoParValor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

import com.tfg.obdTFG.bluetooth.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.verdatos.ParDatoValor;
import com.tfg.obdTFG.ui.verdatos.TipoCombustible;
import com.tfg.obdTFG.ui.verdatos.adapter.ParDatoValorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class VerDatosParDatoValorActivity extends AppCompatActivity {

    private Bluetooth bluetooth;
    private VerParDatoValorViewModel viewModel;
    private DatoOBDHelper contactarBD;
    private Menu menu;

    private RecyclerView recyclerView;
    private ParDatoValorAdapter adaptador;
    private ArrayList<ParDatoValor> listaDatosValor = new ArrayList<>();
    private boolean primeraVezAnhadirRecycler = true;

    public static ArrayList<String> comandos = new ArrayList<>();
    public static int comandoAElegir = 0;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos_par_dato_valor);

        comandos = MainActivity.comandos;

        contactarBD = new DatoOBDHelper(this);
        bluetooth = MainActivity.bluetooth;

        ParDatoValor prueba = new ParDatoValor("Estado:", "No hay datos");
        listaDatosValor.add(prueba);
        iniciarRecyclerView();
        viewModel = new VerParDatoValorViewModel(contactarBD, bluetooth);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mainActivity = true;
        if(bluetooth!=null){
            viewModel.setEstamosEnViewModelParDatos(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu_icons, menu);

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

        if(bluetooth!=null){
            if(viewModel.getBluetoothEstado()){
                viewModel.setViewModelParDato(viewModel);
                viewModel.setEstamosEnViewModelParDatos(true);

                final Observer<ArrayList<String>> observer = new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> misDatos) {
                        if(misDatos.size()>1){
                            gestionarRecyclerView(misDatos.get(0), misDatos.get(1));
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

    //comunicacion de mensajes con el vehiculo
    public void enviarMensajeADispositivo(String mensaje) {
        if (mensaje.length() > 0) {
            mensaje = mensaje + "\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            viewModel.writeVisores(send);
        }
    }

    public void gestionarRecyclerView(String nombreDato, String valorDato){
        if(nombreDato.equals("Velocidad del vehículo")){
            if(primeraVezVelocidad){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezVelocidad = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }
        }
        if(nombreDato.equals("Revoluciones por minuto")){
            if(primeraVezRPM){
                anhadirARecyclerView(nombreDato, valorDato);
                MainActivity.cocheEncendido = true;
                cambiarMenuCocheEncendido();
                primeraVezRPM = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }
        }
        if(nombreDato.equals("Velocidad del flujo del aire MAF")){
            if(primeraVezVelocidadFlujoAire){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezVelocidadFlujoAire = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Carga calculada del motor")){
            if(primeraVezCargaCalculadaMotor){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezCargaCalculadaMotor = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Temperatura del aceite del motor")){
            if(primeraVezTempAceiteMotor){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTempAceiteMotor = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Posición del acelerador")){
            if(primeraVezPosicionAcelerador){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPosicionAcelerador = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Porcentaje torque solicitado")){
            if(primeraVezPorcentajeTorqueSolicitado){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPorcentajeTorqueSolicitado = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Porcentaje torque actual")){
            if(primeraVezPorcentajeTorqueActual){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPorcentajeTorqueActual = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Torque referencia motor")){
            if(primeraVezTorqueReferenciaMotor){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTorqueReferenciaMotor = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Voltaje módulo control")){
            if(primeraVezVoltajeModuloControl){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezVoltajeModuloControl = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Presión barométrica absoluta")){
            if(primeraVezPresionBarometricaAbsoluta){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPresionBarometricaAbsoluta = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Presión del combustible")){
            if(primeraVezPresionCombustible){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPresionCombustible = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Presión medidor tren combustible")){
            if(primeraVezPresionMedidorTrenCombustible){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPresionMedidorTrenCombustible = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Presion absoluta colector admisión")){
            if(primeraVezPresionAbsColectorAdmision){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPresionAbsColectorAdmision = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Presión del vapor del sistema evaporativo")){
            if(primeraVezPresionVaporSisEvaporativo){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPresionVaporSisEvaporativo = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }
        }
        if(nombreDato.equals("Nivel de combustible %")){
            if(primeraVezNivelCombustible){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezNivelCombustible = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Tipo de combustible")){
            if(primeraVezTipoCombustibleNombre){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTipoCombustibleNombre = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Velocidad consumo de combustible")){
            if(primeraVezConsumo){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezConsumo = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Relación combustible-aire")){
            if(primeraVezRelacionCombustibleAire){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezRelacionCombustibleAire = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Distancia con luz fallas encendida")){
            if(primeraVezDistanciaLuzEncendidaFalla){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezDistanciaLuzEncendidaFalla = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("EGR comandado")){
            if(primeraVezEGRComandado){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezEGRComandado = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Falla EGR")){
            if(primeraVezFallaEGR){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezFallaEGR = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Purga evaporativa comandada")){
            if(primeraVezPurgaEvaporativaComand){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezPurgaEvaporativaComand = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Cant. calentamiento sin fallas")){
            if(primeraVezCantidadCalentamientosDesdeNoFallas){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezCantidadCalentamientosDesdeNoFallas = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Distancia sin luz fallas encendida")){
            if(primeraVezDistanciaRecorridadSinLuzFallas){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezDistanciaRecorridadSinLuzFallas = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Sincronización inyección combustible")){
            if(primeraVezSincroInyeccionCombustible){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezSincroInyeccionCombustible = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }

        if(nombreDato.equals("Temperatura del aire ambiente")){
            if(primeraVezTempAireAmbiente){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTempAireAmbiente = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Tº del líquido de enfriamiento")){
            if(primeraVezTempLiquidoEnfriamiento){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTempLiquidoEnfriamiento = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Tº del aire del colector de admisión")){
            if(primeraVezTempAireColectorAdmision){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTempAireColectorAdmision = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }        }
        if(nombreDato.equals("Temperatura del catalizador")){
            if(primeraVezTempCatalizador){
                anhadirARecyclerView(nombreDato, valorDato);
                primeraVezTempCatalizador = false;
            }else{
                actualizarRecyclerView(nombreDato, valorDato);
            }
        }
    }

}