package com.tfg.obdTFG.bluetooth;

import com.tfg.obdTFG.MainActivity;
import com.tfg.obdTFG.db.DatoOBDHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class HelperDB {
    private DatoOBDHelper database;
    private Bluetooth bluetooth;
    public static final String[] listComands = new String[]{"010C", "010D", "0110", "0104", "015C", "0133", "010A", "0123", "010B", "0132", "012F", "0151", "015E", "0144",
            "0146", "0105", "010F", "013C", "011F", "0111", "0121", "012C", "012D", "012E", "0130", "0131", "0142", "015D", "0161", "0162", "0163"};
    public static ArrayList<String> comandos = new ArrayList<>();
    private int comandoAElegir = 0;

    public HelperDB(DatoOBDHelper database, Bluetooth bluetooth) {
        this.database = database;
        comandos = MainActivity.comandos;
        this.bluetooth = bluetooth;
    }

    public void guardarValorBD(String mensaje){
        mensaje = mensaje.replace("null", "");
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        mensaje = mensaje.replaceAll("\n", "");
        mensaje = mensaje.replaceAll("\r", "");
        mensaje = mensaje.replaceAll(" ", "");

        if (mensaje.length() > 35){
            mensaje = "";
        }

        int obdval = 0;
        String msgTemporal = "";
        if (mensaje.length() > 4) {
            if (mensaje.substring(4, 6).equals("41"))
                try {
                    msgTemporal = mensaje.substring(4, 8);
                    msgTemporal = msgTemporal.trim();
                    System.out.println("MI MENSAJE TEMPORAL ES: " + msgTemporal);
                    if (mensaje.length()>12){
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }else{
                        obdval = Integer.parseInt(mensaje.substring(8, mensaje.length()), 16);
                    }
                } catch (NumberFormatException nFE) {
                }
        }

        String send;
        switch (msgTemporal) {
            case "410D": {
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad del vehículo", obdval);
                }
                database.insertValuesEstadisticasDB("Velocidad", (float) obdval);
                break;
            }
            case "410C": {
                float val = (float) (obdval / 4);
                MainActivity.cocheEncendido = true;
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Revoluciones por minuto", val);
                }
                database.insertValuesEstadisticasDB("Revoluciones", val);
                break;
            }
            case "4110": {
                float val = (float) (obdval / 100);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad del flujo del aire MAF", val);
                }
                break;
            }
            case "4104": {
                float val = (float) (obdval / 2.55);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Carga calculada del motor", val);
                }
                break;
            }
            case "415C": {
                float val = (float) (obdval - 40);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Temperatura del aceite del motor", val);
                }
                break;
            }
            case "4111": {
                float val = (float) (obdval/2.55);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Posición del acelerador", val);
                }
                break;
            }
            case "4161": {
                float val = (float) (obdval - 125);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Porcentaje torque solicitado", val);
                }
                break;
            }
            case "4162": {
                float val = (float) (obdval - -125);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Porcentaje torque actual", val);
                }
                break;
            }
            case "4163": {
                float val = (float) (obdval);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Torque referencia motor", val);
                }
                break;
            }
            case "4142": {
                float val = (float) (obdval/1000);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Voltaje módulo control", val);
                }
                break;
            }

            case "4133": {
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión barométrica absoluta", obdval);
                }
                break;
            }
            case "410A": {
                float val = (float) (obdval * 3);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión del combustible", val);
                }
                break;
            }
            case "4123": {
                float val = (float) (obdval * 10);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión medidor tren combustible", val);
                }
                break;
            }
            case "410B": {
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presion absoluta colector admisión", obdval);
                }
                break;
            }
            case "4132": {
                float val = (float) ((obdval / 4) - 8192);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Presión del vapor del sistema evaporativo", val);
                }
                break;
            }

            case "412F": {
                float val = (float) (obdval / 2.55);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Nivel de combustible %", val);
                }
                break;
            }
            case "4151": {
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tipo de combustible", obdval);
                }
                break;
            }
            case "415E": {
                float val = (float) (obdval / 20);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Velocidad consumo de combustible", val);
                }
                database.insertValuesEstadisticasDB("Consumo", (float) val);
                break;
            }
            case "4144": {
                float val = (float) (obdval / 32768);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Relación combustible-aire", val);
                }
                break;
            }
            case "4121": {
                float val = (float) (obdval);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Distancia con luz fallas encendida", val);
                }
                break;
            }
            case "412C": {
                float val = (float) (obdval / 2.55);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("EGR comandado", val);
                }
                break;
            }
            case "412D": {
                float val = (float) ((obdval / 1.28) - 100);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Falla EGR", val);
                }
                break;
            }
            case "412E": {
                float val = (float) (obdval / 2.55);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Purga evaporativa comandada", val);
                }
                break;
            }
            case "4130": {
                float val = (float) (obdval);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Cant. calentamiento sin fallas", val);
                }
                break;
            }
            case "4131": {
                float val = (float) (obdval);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Distancia sin luz fallas encendida", val);
                }
                break;
            }
            case "415D": {
                float val = (float) ((obdval / 128) - 210);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Sincronización inyección combustible", val);
                }
                break;
            }

            case "4146": {
                float val = (float) (obdval - 40);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Temperatura del aire ambiente", val);
                }
                break;
            }
            case "4105": {
                float val = (float) (obdval - 40);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tº del líquido de enfriamiento", val);
                }
                break;
            }
            case "410F": {
                float val = (float) (obdval - 40);
                if(MainActivity.estamosCapturando) {
                    database.insertDBExport("Tº del aire del colector de admisión", val);
                }
                break;
            }
            case "413C": {
                float val = (float) ((obdval / 10) - 40);
                if (MainActivity.estamosCapturando) {
                    database.insertDBExport("Temperatura del catalizador", val);
                }
                break;
            }
            case "411F": {
                if (MainActivity.estamosCapturando) {
                    database.insertDBExport("Tiempo con el motor encendido", obdval);
                }
                break;
            }
        }

        if(comandos.size()>0){
            send = comandos.get(comandoAElegir);
            enviarMensajeADispositivo(send);
        }

        if(MainActivity.huboCambiosPreferencias){
            comandos = MainActivity.comandos;
            MainActivity.huboCambiosPreferencias = false;
        }

        if (comandoAElegir >= comandos.size() - 1) {
            comandoAElegir = 0;
        } else {
            comandoAElegir++;
        }
    }

    public void enviarMensajeADispositivo(String mensaje) {
        if (mensaje.length() > 0) {
            mensaje = mensaje + "\r";
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = mensaje.getBytes();
            bluetooth.writeVisores(send);
        }
    }
}
