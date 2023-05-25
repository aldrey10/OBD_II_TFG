package com.tfg.obdTFG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ViewModel implements Serializable {
    transient BluetoothSocket conexionBluetooth;
    String dispositivoBluetooth;
    //BluetoothAdapter btAdapter;
    Bluetooth bluetooth=null;

    public ViewModel() {
    }

    protected ViewModel(Parcel in) {
        dispositivoBluetooth = in.readString();
    }


    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
    }

    /*public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }

    public void setBtAdapter(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
    }*/

    public BluetoothSocket getConexionBluetooth() {
        return conexionBluetooth;
    }

    public void setConexionBluetooth(BluetoothSocket conexionBluetooth) {
        this.conexionBluetooth = conexionBluetooth;
    }

    public String getNombreDispositivo() {
        return dispositivoBluetooth;
    }

    public void setNombreDispositivo(String dispositivoBluetooth) {
        this.dispositivoBluetooth = dispositivoBluetooth;
    }

}
