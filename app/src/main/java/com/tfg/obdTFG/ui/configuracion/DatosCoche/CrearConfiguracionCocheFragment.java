package com.tfg.obdTFG.ui.configuracion.DatosCoche;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.tfg.obdTFG.R;

import java.util.Objects;

public class CrearConfiguracionCocheFragment extends DialogFragment {
    public static String TAG = "CrearConfiguracion";
    private boolean alertReady;
    private AlertDialog alerta;

    interface CrearConfiguracion {
        public void crearConfiguracion(String nombreConfiguracion, String marca, String modelo, String year, Boolean activado);
    }

    CrearConfiguracion crearConfiguracion;


    CrearNombreConfCocheFragment.GestionarCrearNombreConfiguracion chequearNombreConfiguracionExiste;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_crear_configuracion, null))
                // Add action buttons
                .setPositiveButton("Crear configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alerta.dismiss();
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
                            EditText editText = (EditText) Objects.requireNonNull(getDialog()).findViewById(R.id.idNombreConfiguracion);
                            String nombre = editText.getText().toString();

                            EditText editText1 = (EditText) Objects.requireNonNull(getDialog()).findViewById(R.id.modelo);
                            String modelo = editText1.getText().toString();

                            Spinner spinnerYear = getDialog().findViewById(R.id.spinnerYearCrear);
                            String year = spinnerYear.getSelectedItem().toString();

                            Spinner spinnerMarca = getDialog().findViewById(R.id.spinnerMarcaCrear);
                            String marca = spinnerMarca.getSelectedItem().toString();

                            if (!nombre.equals("") && !nombre.equals(" ") && !nombre.equals("  ") && !nombre.equals("   ") && !modelo.equals("") && !modelo.equals(" ") && !modelo.equals("  ") && !modelo.equals("   ")) {
                                CheckBox checkBox = (CheckBox) getDialog().findViewById(R.id.checkboxActivarConfiguracion);
                                Boolean activado = checkBox.isChecked();

                                if(!chequearNombreConfiguracionExiste.chequearNombreConfiguracionExiste(nombre)){
                                    crearConfiguracion.crearConfiguracion(nombre, marca, modelo, year, activado);
                                    alerta.dismiss();
                                }else{
                                    showSnackBar(getDialog().findViewById(R.id.snackbarDialogo),"Este nombre de configuración ya existe. Elija otro.");
                                }
                            } else {
                                showSnackBar(getDialog().findViewById(R.id.snackbarDialogo),"Complete los campos vacíos");
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
            crearConfiguracion = (CrearConfiguracion)getActivity();
            chequearNombreConfiguracionExiste = (CrearNombreConfCocheFragment.GestionarCrearNombreConfiguracion)getActivity();
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
