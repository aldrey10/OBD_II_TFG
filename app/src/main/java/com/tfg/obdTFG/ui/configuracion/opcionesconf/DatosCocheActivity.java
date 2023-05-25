package com.tfg.obdTFG.ui.configuracion.opcionesconf;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tfg.obdTFG.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.db.DatoOBDHelper;

import java.util.ArrayList;
import java.util.Objects;

public class DatosCocheActivity extends AppCompatActivity implements CambiarConfiguracionCocheFragment.GestionarConfiguracion, CrearConfiguracionCocheFragment.CrearConfiguracion, CrearNombreConfCocheFragment.GestionarCrearNombreConfiguracion, GuardarDatosCocheFragment.GestionarRespuesta{

    private DatoOBDHelper contactarBD;
    private String modelo;
    private String marca;
    private String year;
    private String nombreConfiguracion;

    private Menu menu;

    private boolean seHanHechoCambios=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_coche);

        contactarBD = new DatoOBDHelper(this);

        nombreConfiguracion = contactarBD.cargarConfiguracionCoche();
        marca = contactarBD.cargarMarcaCoche();
        modelo = contactarBD.cargarModeloCoche();
        year = contactarBD.cargarYearCoche();

        ponerDatosUI();

        EditText inputModelo = (EditText) findViewById(R.id.inputModelo);

        if(MainActivity.estamosCapturando){
            inputModelo.setEnabled(false);
            Spinner spinMarca = findViewById(R.id.spinnerMarca);
            spinMarca.setEnabled(false);
            Spinner spinYear = findViewById(R.id.spinnerYear);
            spinYear.setEnabled(false);

            Button btnCambiarconf = findViewById(R.id.btnCambiarConfiguracion);
            btnCambiarconf.setClickable(false);
            btnCambiarconf.setBackgroundColor(getResources().getColor(R.color.opcionMenuDesactiva));

            Button btnCrearconf = findViewById(R.id.btnCrearConfiguracion);
            btnCrearconf.setClickable(false);
            btnCrearconf.setBackgroundColor(getResources().getColor(R.color.opcionMenuDesactiva));
        }

        inputModelo.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                seHanHechoCambios = true;
                setGuardarDeshacerVisible();
            }
        });

        Spinner spinnerMarca = findViewById(R.id.spinnerMarca);
        spinnerMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipoDato = spinnerMarca.getSelectedItem().toString();
                if(!tipoDato.equals(marca)){
                    seHanHechoCambios=true;
                    setGuardarDeshacerVisible();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //no hacemos nada
            }
        });

        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipoDato = spinnerYear.getSelectedItem().toString();
                if(!tipoDato.equals(year)){
                    seHanHechoCambios=true;
                    setGuardarDeshacerVisible();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //no hacemos nada
            }
        });

        setGuardarDeshacerInvisible();

    }

    public void onDestroy() {
        super.onDestroy();
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


    public void ponerDatosUI(){
        Spinner spinnerMarca = findViewById(R.id.spinnerMarca);
        for (int i = 0; i < spinnerMarca.getAdapter().getCount(); i++){
            if (spinnerMarca.getItemAtPosition(i).equals(marca)) {
                spinnerMarca.setSelection(i);
                break;
            }
        }

        TextInputEditText txtModelo = findViewById(R.id.inputModelo);
        txtModelo.setText(modelo);

        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        for (int i = 0; i < spinnerYear.getAdapter().getCount(); i++){
            if (spinnerYear.getItemAtPosition(i).equals(year)) {
                spinnerYear.setSelection(i);
                break;
            }
        }

        TextView idConfiguracion = findViewById(R.id.txtNombreConfiguracion);
        idConfiguracion.setText(nombreConfiguracion);

    }

    public void setGuardarDeshacerVisible(){
        Button guardar = findViewById(R.id.btnGuardarCambios);
        guardar.setVisibility(View.VISIBLE);
        Button deshacer = findViewById(R.id.btnRestablecer);
        deshacer.setVisibility(View.VISIBLE);
    }

    public void setGuardarDeshacerInvisible(){
        Button guardar = findViewById(R.id.btnGuardarCambios);
        guardar.setVisibility(View.INVISIBLE);
        Button deshacer = findViewById(R.id.btnRestablecer);
        deshacer.setVisibility(View.INVISIBLE);
    }

    public void cambiarConfiguracion(View view){
        new CambiarConfiguracionCocheFragment().show(getSupportFragmentManager(), CrearConfiguracionCocheFragment.TAG);
    }

    public void crearConfiguracion(View view){
        new CrearConfiguracionCocheFragment().show(getSupportFragmentManager(), CrearConfiguracionCocheFragment.TAG);
    }

    public void guardarCambios(View view){
        TextInputEditText txtModelo = findViewById(R.id.inputModelo);

        if(txtModelo.getText()!=null){
            if(txtModelo.getText().toString().equals("") || txtModelo.getText().toString().equals(" ") ||txtModelo.getText().toString().equals("  ") ||txtModelo.getText().toString().equals("   ") ||txtModelo.getText().toString().equals("    ")){
                mostrarSnackBarMsg("Se necesita un modelo de vehículo.");
            }else{
                new GuardarDatosCocheFragment().show(getSupportFragmentManager(), GuardarDatosCocheFragment.TAG);
                setGuardarDeshacerInvisible();
            }
        }


    }

    public void deshacerCambios(View view){
        ponerDatosUI();
        setGuardarDeshacerInvisible();
    }

    @Override
    public void gestionarRespuesta(int respuesta) {
        if(respuesta==0){
            //sobrescribir configuracion actual
            guardarDatosBD();
        }else if(respuesta==1){
            //creamos dialog para pedirle nombre de la configuracion y si quiere establecerla como activa
            new CrearNombreConfCocheFragment().show(getSupportFragmentManager(), GuardarDatosCocheFragment.TAG);
        }else{
            setGuardarDeshacerVisible();
            //No se hace nada, boton cancelar
        }
    }

    @Override
    public void gestionarCrearConfiguracion(String nameConfiguracion, Boolean activado) {
        int activo=0;
        if(activado){
            contactarBD.desactivarConfiguracionActiva(this.nombreConfiguracion);
            activo=1;
            TextView idConfiguracion = findViewById(R.id.txtNombreConfiguracion);
            idConfiguracion.setText(nameConfiguracion);
            nombreConfiguracion = nameConfiguracion;
        }
        Spinner spinnerMarca = findViewById(R.id.spinnerMarca);
        marca = spinnerMarca.getSelectedItem().toString();
        TextInputEditText txtModelo = findViewById(R.id.inputModelo);
        modelo = Objects.requireNonNull(txtModelo.getText()).toString();
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        year = spinnerYear.getSelectedItem().toString();
        contactarBD.crearConfiguracionCoche(nameConfiguracion, marca, modelo, year, activo);

        if(!activado){
            nombreConfiguracion = contactarBD.cargarConfiguracionCoche();
            marca = contactarBD.cargarMarcaCoche();
            modelo = contactarBD.cargarModeloCoche();
            year = contactarBD.cargarYearCoche();
            ponerDatosUI();
        }

        setGuardarDeshacerInvisible();

    }

    @Override
    public boolean chequearNombreConfiguracionExiste(String nombreConfiguracion) {
        return contactarBD.comprobarSiExisteConfiguracion(nombreConfiguracion);
    }

    @Override
    public void cancelarCrearNombreConfig() {
        setGuardarDeshacerVisible();
    }

    @Override
    public void crearConfiguracion(String nombreConfiguracion, String marca, String modelo, String year, Boolean activado) {
        int activo = 0;
        if(activado){
            contactarBD.desactivarConfiguracionActiva(this.nombreConfiguracion);
            activo=1;
        }
        contactarBD.crearConfiguracionCoche(nombreConfiguracion, marca, modelo, year, activo);

        if (activado){
            this.nombreConfiguracion = contactarBD.cargarConfiguracionCoche();
            this.marca = contactarBD.cargarMarcaCoche();
            this.modelo = contactarBD.cargarModeloCoche();
            this.year = contactarBD.cargarYearCoche();

            ponerDatosUI();
        }
        setGuardarDeshacerInvisible();

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
        contactarBD.desactivarConfiguracionActiva(nombreConfiguracion);
        contactarBD.activarConfiguracion(nombre);
        this.nombreConfiguracion = contactarBD.cargarConfiguracionCoche();
        this.marca = contactarBD.cargarMarcaCoche();
        this.modelo = contactarBD.cargarModeloCoche();
        this.year = contactarBD.cargarYearCoche();

        ponerDatosUI();
        setGuardarDeshacerInvisible();
    }

    @Override
    public void borrarConfiguracion(String nombre) {
        contactarBD.borrarConfiguracion(nombre);
        mostrarSnackBarMsg("Se ha borrado la configuracion con el nombre " + nombre);
    }


    public void guardarDatosBD(){
        TextView idConfiguracion = findViewById(R.id.txtNombreConfiguracion);
        nombreConfiguracion = idConfiguracion.getText().toString();

        Spinner spinnerMarca = findViewById(R.id.spinnerMarca);
        marca = spinnerMarca.getSelectedItem().toString();
        TextInputEditText txtModelo = findViewById(R.id.inputModelo);
        modelo = Objects.requireNonNull(txtModelo.getText()).toString();
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        year = spinnerYear.getSelectedItem().toString();

        contactarBD.actualizarDatosCoche(nombreConfiguracion,marca, modelo, year);
    }

    public void mostrarSnackBarMsg(String mensaje){
        Snackbar.make(findViewById(R.id.snackbar), mensaje, 5000).show();
    }



}