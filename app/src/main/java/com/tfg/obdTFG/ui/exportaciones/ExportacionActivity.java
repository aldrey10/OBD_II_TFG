package com.tfg.obdTFG.ui.exportaciones;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfg.obdTFG.databinding.ActivityExportacionBinding;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.exportaciones.adapter.ExportacionAdapter;

import com.tfg.obdTFG.R;

import java.util.ArrayList;


public class ExportacionActivity extends AppCompatActivity {

    private ActivityExportacionBinding binding;

    private RecyclerView recyclerView;
    private ExportacionAdapter adaptador;
    private ArrayList<Exportacion> listaViajes = new ArrayList<>();
    private DatoOBDHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExportacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new DatoOBDHelper(this);
        database.getWritableDatabase();
        listaViajes = database.getViajes();
        iniciarRecyclerView();

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    database.exportDB(getViajesActivados());
                    actualizarRecyclerView();
                } else {
                    mostrarSnackBarMsg("Para exportar a un archivo deberá conceder este permiso.");
                }
        }
    }

    public void mostrarSnackBarMsg(String mensaje){
        Snackbar.make(findViewById(R.id.snackbarExportacion), mensaje, 6000).show();
    }

    public void iniciarRecyclerView(){
        recyclerView = findViewById(R.id.recyclerExportacion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        for(Exportacion e: listaViajes){
            e.setIsSelected(false);
        }
        adaptador = new ExportacionAdapter(listaViajes);
        recyclerView.setAdapter(adaptador);
    }

    public void actualizarRecyclerView(){
        listaViajes=database.getViajes();
        for(Exportacion e: listaViajes){
            e.setIsSelected(false);
        }
        adaptador = new ExportacionAdapter(listaViajes);
        recyclerView.setAdapter(adaptador);
    }

    public void exportarDatosViajesSeleccionados(View view){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            database.exportDB(getViajesActivados());
            actualizarRecyclerView();
        }
    }

    public void borrarDatosExportados(View view){
        database.borrarTablaExport(getViajesActivados());
        actualizarRecyclerView();
    }

    public ArrayList<String> getViajesActivados(){
        ArrayList<String> listFechasViajes = new ArrayList<>();
        for(Exportacion e : adaptador.getListaExportaciones()){
            if(e.getIsSelected()){
                listFechasViajes.add(e.getFecha());
            }
        }
        return listFechasViajes;
    }
}