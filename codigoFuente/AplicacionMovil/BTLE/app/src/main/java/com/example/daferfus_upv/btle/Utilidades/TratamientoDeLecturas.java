// ----------------------------------------------------------
// David Fernández Fuster
// 2020-10-18
// ----------------------------------------------------------

package com.example.daferfus_upv.btle.Utilidades;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.TextView;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.daferfus_upv.btle.R;
import com.example.daferfus_upv.btle.Workers.BDWorker;
import com.example.daferfus_upv.btle.Workers.GeolocalizacionWorker;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class TratamientoDeLecturas {

    public static int valor;

    // --------------------------------------------------------------
    //                  constructor()
    //
    // Función: Inicializa y configura el TratamientoDeLecturas.
    // --------------------------------------------------------------
    public TratamientoDeLecturas() {
    }

    // --------------------------------------------------------------
    //              haLlegadoUnBeacon() <-
    //              <- TramaIBeacon
    //
    // Invocado desde: EscaneadoWorker
    // Función: Avisa de que ha encontrado el dispositivo BTLE del usuario y se prepara para
    //          extraer datos esenciales.
    // --------------------------------------------------------------
    public static void haLlegadoUnBeacon(TramaIBeacon trama) {
        if (Utilidades.bytesToString(trama.getUUID()).equals("EPSG-GTI-PROY-G2")) {
            Log.d("Tratamiento Datos", "¡¡Ha llegado un beacon!! ");
            
            // GeolocalicionWorker: Tarea centrada en la geolocalización.
            WorkRequest geolocalizacionWorkRequest =
                    new OneTimeWorkRequest.Builder(GeolocalizacionWorker.class)
                            .build();
            WorkManager
                    .getInstance()
                    .enqueue(geolocalizacionWorkRequest);
            
            
            // Se extraen las mediciones para su posterior inserción.
            extraerMediciones(trama);

            // BDWorker: Tarea centrada en la llamada de métodos de la base de datos.
            WorkRequest envioMedicionWorkRequest =
                    new OneTimeWorkRequest.Builder(BDWorker.class)
                            .build();
            WorkManager
                    .getInstance()
                    .enqueue(envioMedicionWorkRequest);

            /*Data.Builder comandoBorrado = new Data.Builder();
            comandoBorrado.putString("Acción", "Borrar");

            WorkRequest borradoMedicionesWorkRequest =
                    new OneTimeWorkRequest.Builder(BDWorker.class)
                            .setInputData(comandoBorrado.build())
                            .build();
            WorkManager
                    .getInstance()
                    .enqueue(borradoMedicionesWorkRequest);*/
            //enviarMedicion();
            /*mDBHelper.borrarLecturasSincronizadas();*/
        } // if()
    } // ()

    // ---------------------------------------------------------------------------
    //                  extraerMediciones() ->
    //                  <- TramaIBeacon
    //
    // Invocado desde: haLlegadoUnBeacon()
    // Función: Extrae las mediciones de la trama de la baliza para su tratamiento.
    // ----------------------------------------------------------------------------
    public static void extraerMediciones(TramaIBeacon trama) {
        byte[] contador = trama.getMajor();
        byte[] valorSO2 = trama.getMinor();
        valor = Utilidades.bytesToInt(valorSO2);
        Log.d("Tratamiento Datos", "Contador: " + Utilidades.bytesToInt(contador));
        Log.d("Tratamiento Datos", "SO2: " + Utilidades.bytesToInt(valorSO2));
    } // ()


    // --------------------------------------------------------------
    //              mostrarInformacionDispositivoBTLE() ->
    //              <- BluetoothDevice, N, byte[]
    //
    // Invocado desde: EscaneadoWorker
    // Función: Muestra datos de los dispositivos BTLE detectados.
    // --------------------------------------------------------------
    public static void mostrarInformacionDispositivoBTLE(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
        String ETIQUETA_LOG = ">>>>";

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");
    } // ()
} // class
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------