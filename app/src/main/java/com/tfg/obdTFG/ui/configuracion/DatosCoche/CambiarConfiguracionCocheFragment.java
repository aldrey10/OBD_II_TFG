package com.tfg.obdTFG.ui.configuracion.DatosCoche;

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

public class CambiarConfiguracionCocheFragment extends DialogFragment {
    public static String TAG = "CrearConfiguracion";
    private boolean alertReady;
    private AlertDialog alerta;
    private String nombreEscogido="";
    private ArrayList<String> list= new ArrayList<String>();
    private int configuracionSeleccionada;


    interface GestionarConfiguracion {
        public ArrayList<String> consultarTodasLasConfiguraciones();
        public boolean consultarConfiguracionActiva(String nombre);
        public void cambiarConfiguracion(String nombre);
        public void borrarConfiguracion(String nombre);
    }

    GestionarConfiguracion gestionarConfiguracion;


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
                .setNegativeButton("Borrar configuración", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alerta.dismiss();
                    }
                });

        list = new ArrayList<String>();
        list = gestionarConfiguracion.consultarTodasLasConfiguraciones();
        String[] nombresConfiguraciones = list.toArray(new String[0]);
        configuracionSeleccionada=0;
        for(String nombre : list){
            if(gestionarConfiguracion.consultarConfiguracionActiva(nombre)){
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
                            gestionarConfiguracion.cambiarConfiguracion(nombreEscogido);
                            alerta.dismiss();
                        }
                    });

                    button = alerta.getButton(DialogInterface.BUTTON_NEGATIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int indice = list.indexOf(nombreEscogido);
                            if (indice == configuracionSeleccionada) {
                                showSnackBar(getDialog().findViewById(R.id.snackbarDialogo), "No se puede borrar la configuración actualmente activa.");
                            } else {
                                gestionarConfiguracion.borrarConfiguracion(nombreEscogido);
                                dialog.dismiss();
                            }
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
            gestionarConfiguracion = (GestionarConfiguracion)getActivity();
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
