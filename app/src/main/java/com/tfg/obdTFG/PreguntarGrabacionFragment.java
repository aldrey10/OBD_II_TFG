package com.tfg.obdTFG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class PreguntarGrabacionFragment extends DialogFragment {
    public static String TAG = "PreguntarGrabacion";
    private AlertDialog alerta;
    private boolean alertReady;


    interface PreguntarGrabacion {
        public void iniciarNuevaGrabacionTrasDialog();

    }

    PreguntarGrabacion preguntarGrabacion;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_preguntar_grabacion_datos, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        preguntarGrabacion.iniciarNuevaGrabacionTrasDialog();
                    }
                });


        alerta = builder.create();
        return alerta;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            preguntarGrabacion = (PreguntarGrabacion) getActivity();
        }
        catch (ClassCastException e) {
            //nada
        }
    }
}
