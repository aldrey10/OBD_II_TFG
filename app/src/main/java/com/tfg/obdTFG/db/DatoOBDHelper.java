package com.tfg.obdTFG.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.tfg.obdTFG.ui.exportaciones.Exportacion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class DatoOBDHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataBaseOBD.db";

    public DatoOBDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        sqLiteDatabase = getWritableDatabase();
        /*sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");*/
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS CocheDatos");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS DatoOBD");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS EstadisticasTabla");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ExportTabla");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TableViajes");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tableBaches");


        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION + " TEXT PRIMARY KEY,"
                + DatoOBDContrato.EntradaDatoOBD.MARCA + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.MODELO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.YEAR + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION + "))");


        //insercion de datos
        ContentValues values= new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.MARCA, "Alfa Romeo");
        values.put(DatoOBDContrato.EntradaDatoOBD.MODELO, "Giulia");
        values.put(DatoOBDContrato.EntradaDatoOBD.YEAR, "2018");
        values.put(DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, null, values);



        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_NUMBER_OBD + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.ACTIVADO + " INTEGER NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.TIPO_DATO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.DISPONIBLE + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_NUMBER_OBD + "))");


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad del vehículo");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Revoluciones por minuto");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad del flujo del aire MAF");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0110");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Carga calculada del motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0104");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del aceite del motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Posición del acelerador");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0111");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Porcentaje torque solicitado");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0161");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Porcentaje torque actual");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0162");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Torque referencia motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0163");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Voltaje módulo control");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0142");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión barométrica absoluta");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0133");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión del combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010A");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión medidor tren combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0123");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión absoluta colector admisión");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010B");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión del vapor del sistema evaporativo");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0132");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Nivel de combustible %");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tipo de combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0151");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad consumo de combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Relación combustible-aire");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0144");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Distancia con luz fallas encendida");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0121");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "EGR comandado");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Falla EGR");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Purga evaporativa comandada");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Cant. calentamiento sin fallas");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0130");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Distancia sin luz fallas encendida");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0131");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Sincronización inyección combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del aire ambiente");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0146");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tº del líquido de enfriamiento");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0105");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tº del aire del colector de admisión");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del catalizador");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "013C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tiempo con el motor encendido");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "011F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad media del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Consumo medio del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Revolucion media del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_VALOR + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.VALOR + " FLOAT NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_VALOR + "))");

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA, "tiempoMotorEncendido");
        values.put(DatoOBDContrato.EntradaDatoOBD.VALOR, 0);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA, null, values);


        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.DATOS_EXPORT + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_VALOR_EXPORT + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO_EXPORT + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.VALUE_EXPORT + " FLOAT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.FECHAHORA + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.ID_VIAJE + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_VALOR_EXPORT + "))");

        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.FECHA_VIAJE + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.COCHE_VIAJE + " TEXT NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE + "))");

        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.TABLE_BACHES + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_ACELARACION_BACHE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.GRUPO_ACELERACION_BACHE + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_ACELARACION_BACHE + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void hola(){
        desactivarConfiguracionActiva("Predeterminada");
    }

    public void establecerForeignKeyON(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        /*sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");*/
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS CocheDatos");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS DatoOBD");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS EstadisticasTabla");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ExportTabla");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TableViajes");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tableBaches");


        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION + " TEXT PRIMARY KEY,"
                + DatoOBDContrato.EntradaDatoOBD.MARCA + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.MODELO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.YEAR + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION + "))");


        //insercion de datos
        ContentValues values= new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.MARCA, "Alfa Romeo");
        values.put(DatoOBDContrato.EntradaDatoOBD.MODELO, "Giulia");
        values.put(DatoOBDContrato.EntradaDatoOBD.YEAR, "2018");
        values.put(DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, null, values);



        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_NUMBER_OBD + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.ACTIVADO + " INTEGER NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.TIPO_DATO + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.DISPONIBLE + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_NUMBER_OBD + "))");


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad del vehículo");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Revoluciones por minuto");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad del flujo del aire MAF");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0110");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Carga calculada del motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0104");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del aceite del motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Posición del acelerador");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0111");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Porcentaje torque solicitado");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0161");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Porcentaje torque actual");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0162");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Torque referencia motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0163");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Voltaje módulo control");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0142");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión barométrica absoluta");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0133");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión del combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010A");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión medidor tren combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0123");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión absoluta colector admisión");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010B");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión del vapor del sistema evaporativo");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0132");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Nivel de combustible %");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tipo de combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0151");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad consumo de combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Relación combustible-aire");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0144");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Distancia con luz fallas encendida");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0121");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "EGR comandado");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Falla EGR");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Purga evaporativa comandada");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Cant. calentamiento sin fallas");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0130");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Distancia sin luz fallas encendida");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0131");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Sincronización inyección combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del aire ambiente");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0146");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tº del líquido de enfriamiento");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0105");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tº del aire del colector de admisión");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del catalizador");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "013C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tiempo con el motor encendido");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "011F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad media del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Consumo medio del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Revolucion media del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, "Predeterminada");
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_VALOR + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.VALOR + " FLOAT NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_VALOR + "))");

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA, "tiempoMotorEncendido");
        values.put(DatoOBDContrato.EntradaDatoOBD.VALOR, 0);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA, null, values);


        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.DATOS_EXPORT + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_VALOR_EXPORT + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO_EXPORT + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.VALUE_EXPORT + " FLOAT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.FECHAHORA + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.ID_VIAJE + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_VALOR_EXPORT + "))");

        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.FECHA_VIAJE + " TEXT NOT NULL,"
                + DatoOBDContrato.EntradaDatoOBD.COCHE_VIAJE + " TEXT NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE + "))");

        //creacion de tabla
        sqLiteDatabase.execSQL("CREATE TABLE " + DatoOBDContrato.EntradaDatoOBD.TABLE_BACHES + " ("
                + DatoOBDContrato.EntradaDatoOBD.ID_ACELARACION_BACHE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatoOBDContrato.EntradaDatoOBD.GRUPO_ACELERACION_BACHE + " INTEGER NOT NULL,"
                + "UNIQUE (" + DatoOBDContrato.EntradaDatoOBD.ID_ACELARACION_BACHE + "))");
    }




    public HashMap<String, Boolean> consultarPreferenciasMotor() {
        HashMap<String, Boolean> motor =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"motor",configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Boolean activado = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ACTIVADO)).equals("1");
            motor.put(nombreDato, activado);
        }
        return motor;
    }

    public HashMap<String, Boolean> consultarPreferenciasPresion() {
        HashMap<String, Boolean> presion =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"presion", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Boolean activado = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ACTIVADO)).equals("1");
            presion.put(nombreDato, activado);
        }
        return presion;
    }

    public HashMap<String, Boolean> consultarPreferenciasCombustible() {
        HashMap<String, Boolean> combustible =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"combustible", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Boolean activado = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ACTIVADO)).equals("1");
            combustible.put(nombreDato, activado);
        }
        return combustible;
    }

    public HashMap<String, Boolean> consultarPreferenciasTemperatura() {
        HashMap<String, Boolean> temperatura =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"temperatura", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Boolean activado = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ACTIVADO)).equals("1");
            temperatura.put(nombreDato, activado);
        }
        return temperatura;
    }

    public HashMap<String, Boolean> consultarPreferenciasDatosViaje() {
        HashMap<String, Boolean> datosViaje =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"datosViaje", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Boolean activado = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ACTIVADO)).equals("1");
            datosViaje.put(nombreDato, activado);
        }
        return datosViaje;
    }

    public HashMap<String, Boolean> consultarPreferenciasMotorDisponibilidad() {
        HashMap<String, Boolean> motor =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"motor", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Integer disponibilidad = Integer.parseInt(consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE)));
            Boolean disponibilidad1 = disponibilidad.equals(1);
            motor.put(nombreDato, disponibilidad1);
        }
        return motor;
    }

    public HashMap<String, Boolean> consultarPreferenciasPresionDisponibilidad() {
        HashMap<String, Boolean> presion =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"presion", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Integer disponibilidad = Integer.parseInt(consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE)));
            Boolean disponibilidad1 = disponibilidad.equals(1);
            presion.put(nombreDato, disponibilidad1);
        }
        return presion;
    }

    public HashMap<String, Boolean> consultarPreferenciasCombustibleDisponibilidad() {
        HashMap<String, Boolean> combustible =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"combustible", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Integer disponibilidad = Integer.parseInt(consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE)));
            Boolean disponibilidad1 = disponibilidad.equals(1);
            combustible.put(nombreDato, disponibilidad1);
        }
        return combustible;
    }

    public HashMap<String, Boolean> consultarPreferenciasTemperaturaDisponibilidad() {
        HashMap<String, Boolean> temperatura =  new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracion = cargarConfiguracionCoche();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE + " WHERE tipoDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?";
        Cursor consulta = sqLiteDatabase.rawQuery(query, new String[]{"temperatura", configuracion});
        while(consulta.moveToNext()){
            @SuppressLint("Range") String nombreDato = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO));
            @SuppressLint("Range") Integer disponibilidad = Integer.parseInt(consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE)));
            Boolean disponibilidad1 = disponibilidad.equals(1);
            temperatura.put(nombreDato, disponibilidad1);
        }
        return temperatura;
    }


    public void guardarPreferencias(HashMap<String, Boolean> motor, HashMap<String, Boolean> presion, HashMap<String, Boolean> combustible, HashMap<String, Boolean> temperatura){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean activado;
        int valor;

        String configuracion = cargarConfiguracionCoche();
        for (String clave:motor.keySet()) {
            ContentValues cont = new ContentValues();
            activado = motor.get(clave);
            if(activado){
                valor = 1;
            } else{
                valor = 0;
            }
            cont.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, valor);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

        for (String clave:presion.keySet()) {
            ContentValues cont = new ContentValues();
            activado = presion.get(clave);
            if(activado){
                valor = 1;
            } else{
                valor = 0;
            }
            cont.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, valor);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

        for (String clave:combustible.keySet()) {
            ContentValues cont = new ContentValues();
            activado = combustible.get(clave);
            if(activado){
                valor = 1;
            } else{
                valor = 0;
            }
            cont.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, valor);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

        for (String clave:temperatura.keySet()) {
            ContentValues cont = new ContentValues();
            activado = temperatura.get(clave);
            if(activado){
                valor = 1;
            } else{
                valor = 0;
            }
            cont.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, valor);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }


    }

    public void actualizarDisponibilidad(HashMap<String, Boolean> motor, HashMap<String, Boolean> presion, HashMap<String, Boolean> combustible, HashMap<String, Boolean> temperatura){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean valor;
        int disp=0;
        String configuracion = cargarConfiguracionCoche();

        for (String clave:motor.keySet()) {
            ContentValues cont = new ContentValues();
            valor = motor.get(clave);
            if(valor){
                disp = 1;
            }else{
                disp = 0;
            }

            cont.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, disp);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

        for (String clave:presion.keySet()) {
            ContentValues cont = new ContentValues();
            valor = presion.get(clave);
            if(valor){
                disp = 1;
            }else{
                disp = 0;
            }

            cont.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, disp);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

        for (String clave:combustible.keySet()) {
            ContentValues cont = new ContentValues();
            valor = combustible.get(clave);
            if(valor){
                disp = 1;
            }else{
                disp = 0;
            }

            cont.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, disp);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

        for (String clave:temperatura.keySet()) {
            ContentValues cont = new ContentValues();
            valor = temperatura.get(clave);
            if(valor){
                disp = 1;
            }else{
                disp = 0;
            }
            cont.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, disp);
            int consulta = sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, cont, "nombreDato=? and "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION+"=?", new String[] {clave, configuracion});
        }

    }


    @SuppressLint("Range")
    public String cargarMarcaCoche(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String marca="";

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " WHERE activada=1";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            marca = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.MARCA));
        }
        return marca;
    }

    @SuppressLint("Range")
    public String cargarModeloCoche(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String modelo="";

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " WHERE activada=1";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            modelo = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.MODELO));
        }
        return modelo;
    }

    @SuppressLint("Range")
    public String cargarYearCoche(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String year="";

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " WHERE activada=1";
        Cursor consulta = sqLiteDatabase.rawQuery(query,null);
        while(consulta.moveToNext()){
            year = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.YEAR));
        }
        System.out.println("\n\ndskjfkasdjfk");
        System.out.println(year);
        return year;
    }

    @SuppressLint("Range")
    public String cargarConfiguracionCoche(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String configuracionCoche="";

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " WHERE activada=1";
        Cursor consulta = sqLiteDatabase.rawQuery(query,null);
        while(consulta.moveToNext()){
            configuracionCoche = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION));
        }
        return configuracionCoche;
    }



    @SuppressLint("Range")
    public void actualizarDatosCoche(String nombreConfiguracion, String marca, String modelo, String year){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cont = new ContentValues();
        cont.put(DatoOBDContrato.EntradaDatoOBD.MARCA, marca);
        cont.put(DatoOBDContrato.EntradaDatoOBD.MODELO, modelo);
        cont.put(DatoOBDContrato.EntradaDatoOBD.YEAR, year);

        sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, cont, DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"='"+nombreConfiguracion+"'", null);
    }

    public void desactivarConfiguracionActiva(String nombreConfiguracion){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put(DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA, 0);
        sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, cont, DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"='"+nombreConfiguracion+"'", null);
    }

    public void activarConfiguracion(String nombreConfiguracion){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put(DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA, 1);
        sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, cont, DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"='"+nombreConfiguracion+"'", null);
    }

    public void borrarConfiguracion(String nombreConfiguracion){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"=?", new String[]{nombreConfiguracion});
        sqLiteDatabase.delete(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"=?", new String[]{nombreConfiguracion});

    }

    @SuppressLint("Range")
    public boolean comprobarSiExisteConfiguracion(String nombreConfiguracion){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String name=null;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " WHERE "+DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"='"+nombreConfiguracion+"'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            name = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION));
        }
        if(name==null){
            return false;
        }
        return true;
    }

    @SuppressLint("Range")
    public ArrayList<String> consultarTodasLasConfiguraciones(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ArrayList<String> lista = new ArrayList<>();
        String nombreConfiguracion=null;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE;
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            nombreConfiguracion = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION));
            lista.add(nombreConfiguracion);
        }
        return lista;
    }

    public boolean consultarConfiguracionActiva(String nombreConfiguracion){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean activo = false;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE + " WHERE "+DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION +"='"+nombreConfiguracion+"'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            @SuppressLint("Range") int activado = consulta.getInt(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA));
            if (activado==1){
                activo=true;
            }
        }
        return activo;
    }

    public void insertValuesEstadisticasDB(String nombreDato, Float valor){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //insercion de datos
        ContentValues values= new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA, nombreDato);
        values.put(DatoOBDContrato.EntradaDatoOBD.VALOR, valor);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA, null, values);
    }

    public void modifyTiempoTotalEncendido(float valor){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put(DatoOBDContrato.EntradaDatoOBD.VALOR, valor);
        sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA, cont, DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA+"='tiempoTotal'", null);
    }

    @SuppressLint("Range")
    public float getTiempoTotalEstadisticas(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        float tiempo = 0;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA + " WHERE "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA+"='tiempoTotal'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            tiempo = consulta.getFloat(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.VALOR));
        }
        return tiempo;
    }

    public void insertTiempoTrasResetUsuario(float valor){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put(DatoOBDContrato.EntradaDatoOBD.VALOR, valor);
        sqLiteDatabase.update(DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA, cont, DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA+"='tiempoMotorEncendido'", null);
    }

    @SuppressLint("Range")
    public float getTiempoTrasResetUsuario(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        float tiempo = 0;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA + " WHERE "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA+"='tiempoMotorEncendido'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            tiempo = consulta.getFloat(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.VALOR));
        }
        return tiempo;
    }

    @SuppressLint("Range")
    public ArrayList<Float> consultarMediaValores(String nombreDato){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ArrayList<Float> lista = new ArrayList<>();
        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA + " WHERE "+DatoOBDContrato.EntradaDatoOBD.NOMBRE_ESTADISTICA+"='"+nombreDato+"'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            lista.add(consulta.getFloat(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.VALOR)));
        }
        return lista;
    }

    public void resetValores(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(DatoOBDContrato.EntradaDatoOBD.ESTADISTICAS_TABLA, null, null);
    }

    public void insertDBExport(String nombreDato, float valor){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //insercion de datos
        ContentValues values= new ContentValues();

        /*Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);*/

        DateTimeFormatter dtf = null;
        LocalDateTime now = null;
        String strDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            now = LocalDateTime.now();
            strDate = dtf.format(now);
        }


        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO_EXPORT, nombreDato);
        values.put(DatoOBDContrato.EntradaDatoOBD.VALUE_EXPORT, valor);
        values.put(DatoOBDContrato.EntradaDatoOBD.FECHAHORA, strDate);
        values.put(DatoOBDContrato.EntradaDatoOBD.ID_VIAJE, getUltimoIdViaje());

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.DATOS_EXPORT, null, values);
    }

    @SuppressLint("Range")
    public int getUltimoIdViaje(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int idViaje = 0;
        int lastViaje = 0;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES;
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            idViaje = consulta.getInt(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE));
            if(idViaje>=lastViaje){
                lastViaje = idViaje;
            }
        }
        return lastViaje;
    }

    public void crearNuevoViaje(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //insercion de datos
        ContentValues values= new ContentValues();


        /*Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);*/

        DateTimeFormatter dtf = null;
        LocalDateTime now = null;
        String strDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            now = LocalDateTime.now();
            strDate = dtf.format(now);
        }
        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.FECHA_VIAJE, strDate);
        values.put(DatoOBDContrato.EntradaDatoOBD.COCHE_VIAJE, cargarConfiguracionCoche());

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES, null, values);
    }

    public ArrayList<Exportacion> getViajes(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ArrayList<Exportacion> listaViajes = new ArrayList<>();

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES;
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            @SuppressLint("Range") String conf = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.COCHE_VIAJE));
            @SuppressLint("Range") String fecha = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.FECHA_VIAJE));
            Exportacion exportacion = new Exportacion(conf, fecha, null);
            listaViajes.add(exportacion);
        }
        return listaViajes;
    }

    @SuppressLint("Range")
    public int getIdViajePorFecha(String fecha){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int idViaje = 0;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES + " WHERE "+DatoOBDContrato.EntradaDatoOBD.FECHA_VIAJE+"='"+fecha+"'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            idViaje = consulta.getInt(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE));
        }
        return idViaje;
    }

    @SuppressLint("Range")
    public String getConfCocheViajePorFecha(String fecha){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String confCoche = null;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES + " WHERE "+DatoOBDContrato.EntradaDatoOBD.FECHA_VIAJE+"='"+fecha+"'";
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            confCoche = consulta.getString(consulta.getColumnIndex(DatoOBDContrato.EntradaDatoOBD.COCHE_VIAJE));
        }
        return confCoche;
    }

    public void borrarTablaExport(ArrayList<String> listFechaViajes){
        for(String fechaViaje : listFechaViajes){
            int idViaje = getIdViajePorFecha(fechaViaje);
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(DatoOBDContrato.EntradaDatoOBD.DATOS_EXPORT, DatoOBDContrato.EntradaDatoOBD.ID_VIAJE + "='"+idViaje+"'", null);
            borrarViaje(idViaje);
        }
    }

    public void borrarViaje(int idViaje){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(DatoOBDContrato.EntradaDatoOBD.TABLE_VIAJES, DatoOBDContrato.EntradaDatoOBD.ID_VIAJE_TABLE_VIAJE + "='"+idViaje+"'", null);
    }

    public boolean existenDatosAExportar(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.DATOS_EXPORT;
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        if(consulta.getCount()>0){
            return true;
        }
        return false;
    }

    public void exportDB(ArrayList<String> listViajesPorFecha) {

        for(String fecha : listViajesPorFecha){
            int idViaje = getIdViajePorFecha(fecha);
            String confCoche = getConfCocheViajePorFecha(fecha);
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, fecha+"_"+confCoche+".csv");
            try
            {
                file.createNewFile();
                EscribirCSV csvWrite = null;
                csvWrite = new EscribirCSV(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

                SQLiteDatabase sqLiteDatabase = getReadableDatabase();
                Cursor curCSV = sqLiteDatabase.rawQuery("SELECT * FROM ExportTabla where idValorViaje ='" + idViaje+"'",null);
                String tituloColumnas[] ={curCSV.getColumnName(0), curCSV.getColumnName(1), curCSV.getColumnName(2), curCSV.getColumnName(3)};
                csvWrite.writeNext(tituloColumnas);
                while(curCSV.moveToNext())
                {
                    //Which column you want to export
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2), curCSV.getString(3)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
            }
            catch(Exception sqlEx)
            {
                Log.e("Escritura en CSV", sqlEx.getMessage(), sqlEx);
            }


        }
        //borrar datos de bbdd
        borrarTablaExport(listViajesPorFecha);
    }

    public void deleteTablaBachesInit(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(DatoOBDContrato.EntradaDatoOBD.TABLE_BACHES,null, null);
    }

    public void guardarGrupoAceleracion(int grupo){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put(DatoOBDContrato.EntradaDatoOBD.GRUPO_ACELERACION_BACHE, grupo);
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.TABLE_BACHES, null, cont);
    }

    public int getCantidadAceleracionesPorGrupo(int grupo){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int cantidadAceleraciones=0;

        String query = "select * from " + DatoOBDContrato.EntradaDatoOBD.TABLE_BACHES + " WHERE grupoAceleracionBache=" + grupo;
        Cursor consulta = sqLiteDatabase.rawQuery(query, null);
        while(consulta.moveToNext()){
            cantidadAceleraciones++;
        }
        return cantidadAceleraciones;
    }


    public void crearConfiguracionCoche(String nombreConfiguracion, String marca, String modelo, String year, Integer activado){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatoOBDContrato.EntradaDatoOBD.ID_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.MARCA, marca);
        values.put(DatoOBDContrato.EntradaDatoOBD.MODELO, modelo);
        values.put(DatoOBDContrato.EntradaDatoOBD.YEAR, year);
        values.put(DatoOBDContrato.EntradaDatoOBD.CONFIGURACION_ACTIVA, activado);


        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.DATOS_COCHE_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad del vehículo");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Revoluciones por minuto");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad del flujo del aire MAF");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0110");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Carga calculada del motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0104");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del aceite del motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Posición del acelerador");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0111");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Porcentaje torque solicitado");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0161");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Porcentaje torque actual");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0162");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Torque referencia motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0163");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Voltaje módulo control");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0142");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "motor");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);


        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión barométrica absoluta");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0133");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión del combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010A");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión medidor tren combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0123");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión absoluta colector admisión");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010B");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Presión del vapor del sistema evaporativo");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0132");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "presion");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Nivel de combustible %");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tipo de combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0151");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad consumo de combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Relación combustible-aire");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0144");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Distancia con luz fallas encendida");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0121");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "EGR comandado");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Falla EGR");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Purga evaporativa comandada");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "012E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Cant. calentamiento sin fallas");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0130");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Distancia sin luz fallas encendida");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0131");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Sincronización inyección combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "combustible");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);


        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del aire ambiente");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0146");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tº del líquido de enfriamiento");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "0105");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tº del aire del colector de admisión");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Temperatura del catalizador");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "013C");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "temperatura");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Tiempo con el motor encendido");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "011F");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Velocidad media del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "010D");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

        //insercion de datos
        values = new ContentValues();

        // Pares clave-valor
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_DATO, "Consumo medio del viaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.NOMBRE_CONFIGURACION, nombreConfiguracion);
        values.put(DatoOBDContrato.EntradaDatoOBD.ACTIVADO, 1);
        values.put(DatoOBDContrato.EntradaDatoOBD.CODIGO_DATO, "015E");
        values.put(DatoOBDContrato.EntradaDatoOBD.TIPO_DATO, "datosViaje");
        values.put(DatoOBDContrato.EntradaDatoOBD.DISPONIBLE, 1);

        // Insertar...
        sqLiteDatabase.insert(DatoOBDContrato.EntradaDatoOBD.OBD_PREFERENCIAS_TABLE, null, values);

    }
}
