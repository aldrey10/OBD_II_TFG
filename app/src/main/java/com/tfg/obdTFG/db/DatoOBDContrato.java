package com.tfg.obdTFG.db;

import android.provider.BaseColumns;

public class DatoOBDContrato {

    public static abstract class EntradaDatoOBD implements BaseColumns {
        public static final String OBD_PREFERENCIAS_TABLE ="DatoOBD";

        public static final String ID_NUMBER_OBD = "idNumber";
        public static final String NOMBRE_DATO = "nombreDato";
        public static final String ACTIVADO = "activado";
        public static final String CODIGO_DATO = "codigoDato";
        public static final String TIPO_DATO = "tipoDato";
        public static final String DISPONIBLE = "disponible";
        public static final String NOMBRE_CONFIGURACION = "nombreConfiguracion";


        public static final String DATOS_COCHE_TABLE ="CocheDatos";

        public static final String CONFIGURACION_ACTIVA = "activada";
        public static final String MARCA = "marca";
        public static final String MODELO = "modelo";
        public static final String YEAR = "año";
        public static final String ID_CONFIGURACION = "idConfiguracion";


        public static final String ESTADISTICAS_TABLA ="EstadisticasTabla";

        public static final String NOMBRE_ESTADISTICA = "nombreEstadistica";
        public static final String VALOR = "valor";
        public static final String ID_VALOR = "idValor";


        public static final String DATOS_EXPORT ="ExportTabla";

        public static final String NOMBRE_DATO_EXPORT = "nombreDatoExport";
        public static final String VALUE_EXPORT = "valorExport";
        public static final String FECHAHORA = "fecha";
        public static final String ID_VALOR_EXPORT= "idValorExport";
        public static final String ID_VIAJE = "idValorViaje";


        public static final String TABLE_VIAJES = "TableViajes";

        public static final String ID_VIAJE_TABLE_VIAJE = "idViaje";
        public static final String FECHA_VIAJE = "fechaViaje";
        public static final String COCHE_VIAJE = "configuracionViaje";


        public static final String TABLE_BACHES = "tableBaches";

        public static final String ID_ACELARACION_BACHE = "idAceleracionBache";
        public static final String GRUPO_ACELERACION_BACHE = "grupoAceleracionBache";

    }

}
