package com.tfg.obdTFG;

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

import com.tfg.obdTFG.ui.verdatos.VerDatosVisoresActivity;

/**
 * Esta clase sirve para realizar la conexión a Bluetooth
 * y gestionar toda la información que se obtiene a través del mismo.
 */

public class Bluetooth implements Serializable {
    private static Context contexto;
    private static Handler handlerMainActivity;
    private static Handler handlerVerDatos;
    private ViewModel viewModel;
    public static BluetoothSocket mmSocket;
    private boolean notInVerDatos = true;

    //private final Object GUI_INITIALIZATION_MONITOR = new Object();



    //private static BluetoothAdapter btAdapter;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String ETIQUETA = "ConexionBluetooth";

    private ConnectedThread hiloConectado;
    private ConnectedThread hiloMotorEncendido;
    private ConnectedThread hiloParDatoValor;
    private ConnectThread hiloConexion;

    // constantes para indicar el estado de la conexion bluetooth
    public static final int STATE_NO_CONEXION = 0;       // no estamos conectados
    public static final int STATE_CONECTANDO = 1;
    public static final int STATE_CONECTADOS = 2;        // estamos conectados
    private int estado;
    private boolean yaTenemosHilo= false;



    public Bluetooth (Context contexto, Handler handlerMainActivity, BluetoothAdapter btAdapter, ViewModel viewModel){
        Bluetooth.contexto = contexto;
        Bluetooth.handlerMainActivity = handlerMainActivity;
        //Bluetooth.btAdapter = btAdapter;
        estado = STATE_NO_CONEXION;
        this.viewModel = viewModel;
    }

    public Handler getHandlerVerDatos() {
        return handlerVerDatos;
    }

    public void setHandlerVerDatos(Handler handlerVerdatos) {
        handlerVerDatos = handlerVerdatos;
    }

    public BluetoothSocket getMmSocket() {
        return mmSocket;
    }

    public void setMmSocket(BluetoothSocket mmSocket) {
        this.mmSocket = mmSocket;
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

    public void cancelarHiloDatosVisores() throws InterruptedException {
        hiloConectado.setActivo(false);
        hiloConectado.pararHilo();
    }

    /*public void cancelarHiloParDatoValor() throws InterruptedException {
        hiloParDatoValor.setActivo(false);
        hiloParDatoValor.pararHilo();
    }

    public void cancelarHiloMotorEncendido() throws InterruptedException {
        hiloMotorEncendido.pararHilo();
    }*/

    public void continuarHiloVisores(){
        hiloConectado.continuarHilo();
        hiloConectado.setActivo(true);
        //hiloConectado = new ConnectedThread(0);
        //hiloConectado.start();
    }

    /*public void continuarHiloParDatoValor(){
        hiloParDatoValor.continuarHilo();
        hiloParDatoValor.setActivo(true);
    }

    public void continuarHiloMotorEncendido(){
        hiloMotorEncendido.continuarHilo();
        hiloMotorEncendido.setActivo(true);
    }*/

    public synchronized void conectados(String device){
        // cancelamos el hilo que realizo la conexion
        if (hiloConexion != null) {hiloConexion.cancel(); hiloConexion = null;}

        Message msg = handlerMainActivity.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, device);
        msg.setData(bundle);
        handlerMainActivity.sendMessage(msg);

        msg = handlerMainActivity.obtainMessage(MainActivity.SNACKBAR_MSG);
        bundle = new Bundle();
        bundle.putString(MainActivity.MENSAJE_SNACKBAR, "Te has conectado al dispositivo " + device + " con éxito.");
        msg.setData(bundle);
        handlerMainActivity.sendMessage(msg);

        setEstado(STATE_CONECTADOS);

    }

    public void iniciarTransferenciaDatosVisores(){
        // Cancelamos cualquier hilo que haya conectado
        if (hiloConectado != null) {hiloConectado.cancel(); hiloConectado = null; }


        //hiloConectado = new ConnectedThread(mmSocket);
        hiloConectado = new ConnectedThread(0);
        hiloConectado.start();
    }

    /*public void iniciarTransferenciaParDatoValor(){
        // Cancelamos cualquier hilo que haya conectado
        if (hiloParDatoValor != null) {hiloParDatoValor.cancel(); hiloParDatoValor = null; }


        //hiloConectado = new ConnectedThread(mmSocket);
        hiloParDatoValor = new ConnectedThread(1);
        hiloParDatoValor.start();
    }

    public void iniciarTransferenciaDatosPreferencias(){
        if (hiloMotorEncendido != null) {hiloMotorEncendido.cancel(); hiloMotorEncendido = null; }


        //hiloConectado = new ConnectedThread(mmSocket);
        hiloMotorEncendido = new ConnectedThread(2);
        hiloMotorEncendido.start();
    }*/

    public void iniciarConexion() {
        Message msg = handlerMainActivity.obtainMessage(MainActivity.BLUETOOTH_ACTIVADO);
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.DEVICE_NAME, null);
        msg.setData(bundle);
        handlerMainActivity.sendMessage(msg);
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

    /*public synchronized void writeParDatoValor(byte[] mensaje){
        // Create temporary object
        ConnectedThread r;
        // sincronizamos una copia del hilo conectado
        synchronized (this) {
            if (estado != STATE_CONECTADOS) return;
            r = hiloParDatoValor;
        }
        // Perform the write unsynchronized
        if(r!=null){
            r.write(mensaje);
        }
    }

    public synchronized void writePreferencias (byte[] mensaje){
        // Create temporary object
        ConnectedThread r;
        // sincronizamos una copia del hilo conectado
        synchronized (this) {
            if (estado != STATE_CONECTADOS) return;
            r = hiloMotorEncendido;
        }
        // Perform the write unsynchronized
        if(r!=null){
            r.write(mensaje);
        }
    }*/


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
            //dejamos de buscar y cancelamos el discovery
            //btAdapter.cancelDiscovery();

            try {
                // nos conectamos
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Message msg = handlerMainActivity.obtainMessage(MainActivity.SNACKBAR_MSG_CONEXION_FALLIDA);
                    Bundle bundle = new Bundle();
                    bundle.putString(MainActivity.MENSAJE_SNACKBAR, "No se ha podido conectar al dispositivo.");
                    msg.setData(bundle);
                    handlerMainActivity.sendMessage(msg);
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
            viewModel.setConexionBluetooth(mmSocket);

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
        //private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private boolean pauseThreadFlag = false;
        private int conectado;
        private boolean activo;

        //public ConnectedThread(BluetoothSocket socket) {
        //constructor para la transferencia de datos normal
        public ConnectedThread(int conectado) {
            //mmSocket = socket;
            //viewModel.setConexionBluetooth(socket);
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

            Message msg = handlerMainActivity.obtainMessage(VerDatosVisoresActivity.PEDIR_COMANDOS);
            Bundle bundle = new Bundle();
            bundle.putString(VerDatosVisoresActivity.COMANDOS, "010D\r");
            msg.setData(bundle);
            handlerMainActivity.sendMessage(msg);

            /*msg = handlerVerDatos.obtainMessage(VerDatosVisoresActivity.PEDIR_COMANDOS);
            bundle = new Bundle();
            bundle.putString(VerDatosVisoresActivity.COMANDOS, "010D\r");
            msg.setData(bundle);
            handlerVerDatos.sendMessage(msg);*/
        }

        public void setActivo(boolean activo){
            this.activo = activo;
        }

        String s,msg;
        public synchronized void run() {
            while (true) {
                try {
                    checkForPaused();
                    if(activo){
                        byte[] buffer = new byte[1];
                        int bytes = mmInStream.read(buffer, 0, buffer.length);
                        s = new String(buffer);
                        for(int i = 0; i < s.length(); i++){
                            char x = s.charAt(i);
                            msg = msg + x;
                            if (x == 0x3e) {
                                try{
                                    if(MainActivity.mainActivity){
                                        handlerMainActivity.obtainMessage(VerDatosVisoresActivity.MESSAGE_READ, buffer.length, -1, msg).sendToTarget();
                                    }else{
                                        handlerVerDatos.obtainMessage(VerDatosVisoresActivity.MESSAGE_READ, buffer.length, -1, msg).sendToTarget();
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

        private void checkForPaused() {
            if(conectado==0){
                synchronized (MainActivity.GUI_INITIALIZATION_MONITOR) {
                    while (pauseThreadFlag) {
                        try {
                            MainActivity.GUI_INITIALIZATION_MONITOR.wait();
                        } catch (Exception e) {}
                    }
                }
            }/* else if(conectado == 1){
                synchronized (MainActivity.GUI_INITIALIZATION_MONITOR2) {
                    while (pauseThreadFlag) {
                        try {
                            MainActivity.GUI_INITIALIZATION_MONITOR2.wait();
                        } catch (Exception e) {}
                    }
                }
            } else {
                synchronized (MainActivity.GUI_INITIALIZATION_MONITOR3) {
                    while (pauseThreadFlag) {
                        try {
                            MainActivity.GUI_INITIALIZATION_MONITOR3.wait();
                        } catch (Exception e) {}
                    }
                }
            }*/
        }

        public void pararHilo() throws InterruptedException {
            pauseThreadFlag = true;
        }

        public void continuarHilo() {
            if(conectado==0){
                synchronized(MainActivity.GUI_INITIALIZATION_MONITOR) {
                    pauseThreadFlag = false;
                    MainActivity.GUI_INITIALIZATION_MONITOR.notify();
                }
            }/*else if(conectado==1){
                synchronized(MainActivity.GUI_INITIALIZATION_MONITOR2) {
                    pauseThreadFlag = false;
                    MainActivity.GUI_INITIALIZATION_MONITOR2.notify();
                }
            } else{
                synchronized(MainActivity.GUI_INITIALIZATION_MONITOR3) {
                    pauseThreadFlag = false;
                    MainActivity.GUI_INITIALIZATION_MONITOR3.notify();
                }
            }*/

        }


        public void write(byte[] buffer) {
            try {
                /*if(this.activo){
                    mmOutStream.write(buffer);
                    handlerVerDatos.obtainMessage(VerDatosVisoresActivity.MESSAGE_WRITE, -1, -1, buffer)
                            .sendToTarget();
                }*/
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

