package com.tfg.obdTFG.ui.configuracion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.tfg.obdTFG.Bluetooth;
import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.R;
import com.tfg.obdTFG.ViewModel;

import java.util.ArrayList;

public class ConfiguracionActivity extends AppCompatActivity {
    public ViewModel viewModel;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

    }

    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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

    public void onDestroy() {
        super.onDestroy();
    }


    public void cambiarADatosVehiculo (View view){
        Intent intent = new Intent(ConfiguracionActivity.this, com.tfg.obdTFG.ui.configuracion.opcionesconf.DatosCocheActivity.class);
        startActivity(intent);
    }

    public void cambiarAPreferencias (View view){
        Intent intent = new Intent(ConfiguracionActivity.this, com.tfg.obdTFG.ui.configuracion.opcionesconf.PreferenciasActivity.class);
        startActivity(intent);
    }




}