package com.tfg.obdTFG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class ResumenConfiguracionFragment extends DialogFragment {
    public static String TAG = "CambiarConfiguracion";
    private boolean alertReady;
    private AlertDialog alerta;
    private String nombreConfiguracion;
    private String marca;
    private String modelo;
    private String year;


    interface GestionConfiguracion {
        public void abrirVentanaVisualizacion();
        public void abrirPreferencias();
        public String consultarNombreConfiguracionActual();
        public String consultarMarcaActual();
        public String consultarModeloActual();
        public String consultarYearActual();

    }

    GestionConfiguracion gestionarConfiguracion;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_resumen_configuracion, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Visualizar datos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Ver configuración", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alerta.dismiss();
                    }
                });

        nombreConfiguracion= gestionarConfiguracion.consultarNombreConfiguracionActual();
        marca=gestionarConfiguracion.consultarMarcaActual();
        modelo=gestionarConfiguracion.consultarModeloActual();
        year=gestionarConfiguracion.consultarYearActual();

        alerta = builder.create();

        TextView txt = (TextView) view.findViewById(R.id.nombreConfiguracionResumen);
        txt.setText(nombreConfiguracion);
        txt=(TextView)  view.findViewById(R.id.marcaResumen);
        txt.setText(marca);
        txt=(TextView) view.findViewById(R.id.modeloResumen);
        txt.setText(modelo);
        txt=(TextView) view.findViewById(R.id.yearResumen);
        txt.setText(year);

        this.alertReady = false;
        alerta.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (!alertReady) {
                    Button button = alerta.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gestionarConfiguracion.abrirVentanaVisualizacion();
                            alerta.dismiss();
                        }
                    });

                    button = alerta.getButton(DialogInterface.BUTTON_NEGATIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gestionarConfiguracion.abrirPreferencias();
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
            gestionarConfiguracion = (GestionConfiguracion)getActivity();
        }
        catch (ClassCastException e) {
            //nada
        }
    }
}
