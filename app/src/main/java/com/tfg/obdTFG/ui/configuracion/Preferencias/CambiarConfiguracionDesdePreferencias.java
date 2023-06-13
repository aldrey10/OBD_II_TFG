package com.tfg.obdTFG.ui.configuracion.Preferencias;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.tfg.obdTFG.R;

import java.util.ArrayList;

public class CambiarConfiguracionDesdePreferencias extends DialogFragment {
    public static String TAG = "CambiarConfiguracionDesdePreferencias";
    private boolean alertReady;
    private AlertDialog alerta;
    private String nombreEscogido="";
    private ArrayList<String> list= new ArrayList<String>();
    private int configuracionSeleccionada;


    interface CambiarConfiguracion {
        public ArrayList<String> consultarTodasLasConfiguraciones();
        public boolean consultarConfiguracionActiva(String nombre);
        public void cambiarConfiguracion(String nombre);
    }

    CambiarConfiguracion cambiarConfiguracion;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_cambiar_configuracion, null))
                // Add action buttons
                .setPositiveButton("Establecer configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alerta.dismiss();
                    }
                });

        list = new ArrayList<String>();
        list = cambiarConfiguracion.consultarTodasLasConfiguraciones();
        String[] nombresConfiguraciones = list.toArray(new String[0]);
        configuracionSeleccionada=0;
        for(String nombre : list){
            if(cambiarConfiguracion.consultarConfiguracionActiva(nombre)){
                configuracionSeleccionada=list.indexOf(nombre);
                break;
            }
        }
        builder.setSingleChoiceItems(nombresConfiguraciones, configuracionSeleccionada, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nombreEscogido = list.get(which);
            }
        });

        alerta = builder.create();

        this.alertReady = false;
        alerta.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (!alertReady) {
                    Button button = alerta.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cambiarConfiguracion.cambiarConfiguracion(nombreEscogido);
                            alerta.dismiss();
                        }
                    });
                    alertReady = true;
                }
            }
        });



        return alerta;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            cambiarConfiguracion = (CambiarConfiguracion)getActivity();
        }
        catch (ClassCastException e) {
            //nada
        }
    }

    public void showSnackBar(final View parent, final String text) {
        Snackbar sb = Snackbar.make(parent, text, Snackbar.LENGTH_LONG);
        sb.show();
    }
}
