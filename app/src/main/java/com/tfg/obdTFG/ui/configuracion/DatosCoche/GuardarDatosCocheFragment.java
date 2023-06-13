package com.tfg.obdTFG.ui.configuracion.DatosCoche;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.tfg.obdTFG.R;


public class GuardarDatosCocheFragment extends DialogFragment {

    public static String TAG = "GuardarDatos";

    interface GestionarRespuesta {
        public void gestionarRespuesta(int respuesta);
    }

    GestionarRespuesta gestionarRespuesta = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("¿Qué desea hacer?")
                .setItems(R.array.opciones_guardar_datos_coche, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        gestionarRespuesta.gestionarRespuesta(which);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            gestionarRespuesta = (GestionarRespuesta)getActivity();
        }
        catch (ClassCastException e) {
            //nada
        }
    }
}