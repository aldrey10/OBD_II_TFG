package com.tfg.obdTFG.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.MainViewModel;
import com.tfg.obdTFG.db.DatoOBDHelper;
import com.tfg.obdTFG.ui.configuracion.Preferencias.PreferenciasViewModel;
import com.tfg.obdTFG.ui.verdatos.VerDatoParValor.VerParDatoValorViewModel;
import com.tfg.obdTFG.ui.verdatos.VerEstadisticas.EstadisticasViewModel;
import com.tfg.obdTFG.ui.verdatos.VerVisores.VerDatosVisoresActivity;
import com.tfg.obdTFG.ui.verdatos.VerVisores.VerDatosVisoresViewModel;

/**
 * Esta clase sirve para realizar la conexión a Bluetooth
 * y gestionar toda la información que se obtiene a través del mismo.
 */

public class Bluetooth implements Serializable {
    private static Context contexto;
    private static Handler handlerMainActivity;
    private static Handler handlerVerDatos;
    public static BluetoothSocket mmSocket;


    public static final String MESSAGE_READ_STRING = "1";
    public static final String MESSAGE_DEVICE_NAME_STRING = "2";
    public static final String BLUETOOTH_ACTIVADO_STRING = "3";
    public static final String SNACKBAR_MSG_STRING = "5";
    public static final String PEDIR_COMANDOS_STRING = "6";
    public static final String SNACKBAR_MSG_CONEXION_FALLIDA_STRING = "7";


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String ETIQUETA = "ConexionBluetooth";

    private ConnectedThread hiloConectado;
    private ConnectThread hiloConexion;

    // constantes para indicar el estado de la conexion bluetooth
    public static final int STATE_NO_CONEXION = 0;       // no estamos conectados
    public static final int STATE_CONECTANDO = 1;
    public static final int STATE_CONECTADOS = 2;        // estamos conectados
    private int estado;

    private VerParDatoValorViewModel viewModelParDato;
    private boolean estamosEnViewModelParDatos = false;
    private EstadisticasViewModel viewModelEstadisticas;
    private boolean estamosEnViewModelEstadisticas = false;
    private VerDatosVisoresViewModel viewModelVisores;
    private boolean estamosEnViewModelVisores = false;
    private PreferenciasViewModel viewModelPreferencias;
    private boolean estamosEnViewModelPreferencias = false;
    private MainViewModel viewModel;
    private boolean estamosEnViewModelMain = false;

    private HelperDB helper;
    private DatoOBDHelper database;

    private ArrayList<String> miDatoDelMainActivity;

    public Bluetooth (Context contexto, Handler handlerMainActivity, BluetoothAdapter btAdapter, MainViewModel viewModel, DatoOBDHelper database){
        Bluetooth.contexto = contexto;
        Bluetooth.handlerMainActivity = handlerMainActivity;
        //Bluetooth.btAdapter = btAdapter;
        estado = STATE_NO_CONEXION;
        this.viewModel = viewModel;
        miDatoDelMainActivity = new ArrayList<>();
        this.database = database;
        helper = new HelperDB(database, this);
    }

    public void setEstamosEnViewModelMain(boolean estamosEnViewModelMain) {
        this.estamosEnViewModelMain = estamosEnViewModelMain;
        if(!estamosEnViewModelMain){
            String mensaje = "010D\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            writeVisores(send);
        }
    }

    public void setViewModelPreferencias(PreferenciasViewModel viewModelPreferencias) {
        this.viewModelPreferencias = viewModelPreferencias;
    }

    public void setEstamosEnViewModelPreferencias(boolean estamosEnViewModelPreferencias) {
        this.estamosEnViewModelPreferencias = estamosEnViewModelPreferencias;
        if(!estamosEnViewModelPreferencias){
            String mensaje = "010D\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            writeVisores(send);
        }
    }

    public void setViewModelVisores(VerDatosVisoresViewModel viewModelVisores) {
        this.viewModelVisores = viewModelVisores;
    }

    public void setEstamosEnViewModelVisores(boolean estamosEnViewModelVisores) {
        this.estamosEnViewModelVisores = estamosEnViewModelVisores;
    }

    public void setViewModelEstadisticas(EstadisticasViewModel viewModelEstadisticas) {
        this.viewModelEstadisticas = viewModelEstadisticas;
    }

    public void setEstamosEnViewModelEstadisticas(boolean estamosEnViewModelEstadisticas) {
        this.estamosEnViewModelEstadisticas = estamosEnViewModelEstadisticas;
    }

    public void setEstamosEnViewModelParDatos(boolean estamosEnViewModelParDatos) {
        this.estamosEnViewModelParDatos = estamosEnViewModelParDatos;
    }

    public void setViewModelParDato(VerParDatoValorViewModel viewModelParDato) {
        this.viewModelParDato = viewModelParDato;
    }

    public void setEstado(int estado){
        this.estado = estado;
    }

    public int getEstado(){
        return this.estado;
    }

    public synchronized void iniciarHiloConnect(BluetoothDevice bluetoothDevice){
        // Cancelamos cualquier hilo intentando realizar una conexion
        if ( estado== STATE_CONECTANDO) {
            if (hiloConexion != null) {hiloConexion.cancel(); hiloConexion = null;}
        }

        // cancelamos cualquier hilo conectado
        if (hiloConectado != null) {hiloConectado.cancel(); hiloConectado = null;}
        hiloConexion = new ConnectThread(bluetoothDevice);
        hiloConexion.start();
        setEstado(STATE_CONECTANDO);
    }

    public void cancelarHiloConnect(){
        if (hiloConexion != null) {hiloConexion.cancel(); hiloConexion = null;}
    }

    public synchronized void conectados(String device){
        // cancelamos el hilo que realizo la conexion
        if (hiloConexion != null) {hiloConexion.cancel(); hiloConexion = null;}

        miDatoDelMainActivity = new ArrayList<>();
        miDatoDelMainActivity.add(MESSAGE_DEVICE_NAME_STRING);
        miDatoDelMainActivity.add(device);
        viewModel.setResultadoParDatoValor(miDatoDelMainActivity);

        miDatoDelMainActivity = new ArrayList<>();
        miDatoDelMainActivity.add(SNACKBAR_MSG_STRING);
        miDatoDelMainActivity.add("Te has conectado al dispositivo " + device + " con éxito.");
        viewModel.setResultadoParDatoValor(miDatoDelMainActivity);

        setEstado(STATE_CONECTADOS);

    }

    public void iniciarTransferenciaDatosVisores(){
        // Cancelamos cualquier hilo que haya conectado
        if (hiloConectado != null) {hiloConectado.cancel(); hiloConectado = null; }

        hiloConectado = new ConnectedThread(0);
        hiloConectado.start();
    }

    public void iniciarConexion() {
        miDatoDelMainActivity = new ArrayList<>();
        miDatoDelMainActivity.add(BLUETOOTH_ACTIVADO_STRING);
        viewModel.setResultadoParDatoValor(miDatoDelMainActivity);
    }

    public synchronized void writeVisores(byte[] mensaje){
        // Create temporary object

        ConnectedThread r;
        // sincronizamos una copia del hilo conectado
        synchronized (this) {
            if (estado != STATE_CONECTADOS) return;
            r = hiloConectado;
        }
        // Perform the write unsynchronized
        if(r!=null){
            r.write(mensaje);
        }
    }


    public class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // usamos un objeto temporal y el socket
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                if (ActivityCompat.checkSelfPermission( contexto,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(ETIQUETA, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public synchronized void run() {
            if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            }
            try {
                // nos conectamos
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();

                    miDatoDelMainActivity = new ArrayList<>();
                    miDatoDelMainActivity.add(SNACKBAR_MSG_CONEXION_FALLIDA_STRING);
                    miDatoDelMainActivity.add("No se ha podido conectar al dispositivo.");
                    viewModel.setResultadoParDatoValor(miDatoDelMainActivity);
                } catch (IOException closeException) {
                    Log.e(ETIQUETA, "Could not close the client socket", closeException);
                }
                return;
            }


            // hemos acabado con el hilo conexion asi que lo reseteamos a null
            synchronized (this) {
                hiloConexion = null;
            }

            conectados(mmDevice.getName());
            viewModel.setBtSocket(mmSocket);

        }

        // cerramos el socket
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(ETIQUETA, "Could not close the client socket", e);
            }
        }
    }

    public class ConnectedThread extends Thread{
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private boolean pauseThreadFlag = false;
        private int conectado;
        private boolean activo;

        //constructor para la transferencia de datos normal
        public ConnectedThread(int conectado) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.conectado = conectado;
            this.activo = true;

            // cogemos el input y output streams
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(ETIQUETA, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            miDatoDelMainActivity = new ArrayList<>();
            miDatoDelMainActivity.add(PEDIR_COMANDOS_STRING);
            viewModel.setResultadoParDatoValor(miDatoDelMainActivity);
        }

        String s,msg;
        public synchronized void run() {
            while (true) {
                try {
                    if(activo){
                        byte[] buffer = new byte[1];
                        int bytes = mmInStream.read(buffer, 0, buffer.length);
                        s = new String(buffer);
                        for(int i = 0; i < s.length(); i++){
                            char x = s.charAt(i);
                            msg = msg + x;
                            if (x == 0x3e) {
                                try{
                                    if(estamosEnViewModelParDatos){
                                        viewModelParDato.setResultadoParDatoValor(msg);
                                    } else if(estamosEnViewModelEstadisticas){
                                        viewModelEstadisticas.setResultadoParDatoValor(msg);
                                    } else if(estamosEnViewModelVisores){
                                        viewModelVisores.setResultadoParDatoValor(msg);
                                    } else if(estamosEnViewModelPreferencias){
                                        viewModelPreferencias.setResultadoParDatoValor(msg);
                                    } else if(estamosEnViewModelMain){
                                        miDatoDelMainActivity = new ArrayList<>();
                                        miDatoDelMainActivity.add(MESSAGE_READ_STRING);
                                        miDatoDelMainActivity.add(msg);
                                        viewModel.setResultadoParDatoValor(miDatoDelMainActivity);
                                    } else{
                                        helper.guardarValorBD(msg);
                                    }
                                }catch (Exception e){
                                    System.out.println(e);
                                }
                                System.out.println(msg+"\n");
                                msg="";
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e);
                    connectionLost();
                    break;
                }
            }
        }


        public void write(byte[] buffer) {
            try {
                if(this.activo){
                    mmOutStream.write(buffer);
                }
            } catch (IOException e) {
                Log.e(ETIQUETA, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(ETIQUETA, "close() of connect socket failed", e);
            }
        }

    }



    private void connectionLost() {
        // mandamos al activity un mensaje de que la conexion ha fallado
        Message msg = handlerMainActivity.obtainMessage(MainActivity.SNACKBAR_MSG_CONEXION_FALLIDA);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handlerMainActivity.sendMessage(msg);

    }



}

