package com.example.sylvestre.colorpenprediction2;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.UUID;

/**
 * Created by Sylvestre on 10/05/2017.
 */

//Service qui permet d'établir la communication entre le téléphone et le périphérique Bluetooth Low Energy
public class BluetoothLeService extends Service {

    private BluetoothGatt mBluetoothGatt ;
    private BluetoothGattService mGattService ;
    private Context context ;

    private int mEqual = 5 ;
    private int mMin = 5 ;
    private int delta = 5 ;
    private int tMes = 5 ;
    private int nMes = 10 ;
    private int nFeutres = 5 ;

    private Handler mHandler ;

    private Runnable rNotification1 = new Runnable() {
        @Override
        public void run() {
            runNotificationScript();
        }
    };

    private Runnable rParameter = new Runnable() {
        @Override
        public void run() {
            runParametersScript();
        }
    };

    private String notification = "Aucune Notification activée" ;
    private String parameter = "Aucun paramètre envoyé" ;
    private int valueToSend = 0 ;

    public final static String INT_CHARACTERISTIC_VALUE = "com.example.sylvestre.colorpenprediction.INT_CHARACTERISTIC_VALUE" ;
    public final static String ACTION_GATT_CONNECTED = "com.example.sylvestre.colorpenprediction.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.sylvestre.colorpenprediction.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.sylvestre.colorpenprediction.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_NOTIFICATION_ENABLED = "com.example.sylvestre.colorpenprediction.ACTION_GATT_NOTIFICATION_ENABLED";
    public final static String ACTION_PARAMETERS_SENT = "com.example.sylvestre.colorpenprediction.ACTION_PARAMETERS_SENT";
    public final static String ACTION_NEW_PEN = "com.example.sylvestre.colorpenprediction.ACTION_NEW_PEN";
    public final static String ACTION_ERROR_PEN = "com.example.sylvestre.colorpenprediction.ACTION_ERROR_PEN";
    public final static String ACTION_DETECTED_PEN = "com.example.sylvestre.colorpenprediction.ACTION_DETECTED_PEN";


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                final Intent intent = new Intent(ACTION_GATT_CONNECTED) ;
                context.sendBroadcast(intent);
                mBluetoothGatt.discoverServices();
            }
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                final Intent intent = new Intent(ACTION_GATT_DISCONNECTED) ;
                context.sendBroadcast(intent);
            }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                final Intent intent = new Intent(ACTION_GATT_DISCONNECTED) ;
                context.sendBroadcast(intent);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final Intent intent = new Intent(ACTION_GATT_SERVICES_DISCOVERED);
                mGattService = gatt.getService(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834bea"));
                context.sendBroadcast(intent);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if(parameter.equals("Paramètres envoyés")) {
                if(characteristic.getUuid().equals(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834bec")))
                {
                    byte val[] = characteristic.getValue();
                    int intVal = Integer.parseInt(Byte.toString(val[0]));
                    if (intVal < 0) {
                        intVal = 256 + intVal;
                    }
                    if (valueToSend == 0) {
                        valueToSend = intVal;
                    } else {
                        if (intVal == 1) {
                            final Intent intent = new Intent(ACTION_NEW_PEN);
                            intent.putExtra(INT_CHARACTERISTIC_VALUE, valueToSend);
                            context.sendBroadcast(intent);
                        } else if (intVal == 0) {
                            final Intent intent = new Intent(ACTION_ERROR_PEN);
                            intent.putExtra(INT_CHARACTERISTIC_VALUE, valueToSend);
                            context.sendBroadcast(intent);
                        }
                        valueToSend = 0;
                    }
                }else if(characteristic.getUuid().equals(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834bed"))){
                    final Intent intent = new Intent(ACTION_DETECTED_PEN);
                    byte val[] = characteristic.getValue();
                    int intVal = Integer.parseInt(Byte.toString(val[0]));
                    if (intVal < 0) {
                        intVal = 256 + intVal;
                    }
                    intent.putExtra(INT_CHARACTERISTIC_VALUE, intVal);
                    context.sendBroadcast(intent);
                }
            }else{
                if(characteristic.getUuid().equals(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834bed"))){
                    parameter+="K" ;
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor descriptor,int status){
            if(status == BluetoothGatt.GATT_SUCCESS){
                switch(notification){
                    case "Notification 1 en attente" :
                        notification = "Notification 1 activée" ;
                        break ;
                    case "Notification 2 en attente" :
                        notification = "Notification 2 activée" ;
                        final Intent intent = new Intent(ACTION_GATT_NOTIFICATION_ENABLED) ;
                        intent.putExtra("NOTIF",notification);
                        context.sendBroadcast(intent);
                        break ;
                }
            }else{
                final Intent intent = new Intent(ACTION_GATT_NOTIFICATION_ENABLED) ;
                intent.putExtra("NOTIF","ERREUR");
                context.sendBroadcast(intent);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status){
            if(status == BluetoothGatt.GATT_SUCCESS){
                parameter += "O" ;
            }
        }

    };

    BluetoothLeService(Context context,BluetoothDevice device){
        super() ;
        this.context = context ;
        mBluetoothGatt = device.connectGatt(context,false,mGattCallback);
        mHandler = new Handler() ;
    }

    public BluetoothGattService getGattService(){
        return mGattService ;
    }

    public int readCharacteristicValue(String valueLabel){
        byte[] val = null ;
        switch(valueLabel){
            case "nombreDeFeutres" :
                val = mGattService.getCharacteristic(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834beb")).getValue() ;
                break ;
            case "masse" :
                val = mGattService.getCharacteristic(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834bec")).getValue() ;
                break ;
            case "idFeutre" :
                val = mGattService.getCharacteristic(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834bed")).getValue() ;
                break ;
        }
        int intVal = Integer.parseInt(Byte.toString(val[0])) ;
        if(intVal<0){
            intVal = 256+intVal ;
        }
        return intVal ;
    }

    public void sendParameters(int mEqual, int mMin, int delta, int tMes, int nMes, int nFeutres){
        this.mEqual = mEqual ;
        this.mMin = mMin ;
        this.delta = delta ;
        this.tMes = tMes ;
        this.nMes = nMes ;
        this.nFeutres = nFeutres ;
        rParameter.run() ;
    }

    public void runParametersScript(){
        switch(parameter){
            case "Aucun paramètre envoyé" :
                parameter = "mEqual" ;
                writeCharacteristicValue(mEqual);
                mHandler.postDelayed(rParameter,200) ;
                break ;
            case "mEqualOK" :
                parameter = "mMin" ;
                writeCharacteristicValue(mMin);
                mHandler.postDelayed(rParameter,200) ;
                break ;
            case "mMinOK" :
                parameter = "delta" ;
                writeCharacteristicValue(delta);
                mHandler.postDelayed(rParameter,200) ;
                break ;
            case "deltaOK" :
                parameter = "tMes" ;
                writeCharacteristicValue(tMes);
                mHandler.postDelayed(rParameter,200) ;
                break ;
            case "tMesOK" :
                parameter = "nMes" ;
                writeCharacteristicValue(nMes);
                mHandler.postDelayed(rParameter,200) ;
                break ;
            case "nMesOK" :
                parameter = "nFeutres" ;
                writeCharacteristicValue(nFeutres);
                mHandler.postDelayed(rParameter,200) ;
                break ;
            case "nFeutresOK" :
                parameter = "Paramètres envoyés" ;
                final Intent intent = new Intent(ACTION_PARAMETERS_SENT) ;
                context.sendBroadcast(intent);
                break ;
        }
    }

    public void writeCharacteristicValue(int value){
        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString("a7b2fe81-25c5-4ca5-adfc-77e4f3834beb")) ;
        characteristic.setValue(value,BluetoothGattCharacteristic.FORMAT_UINT16,0);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void setNotification(){
        mGattService.getCharacteristics() ;
        rNotification1.run() ;
    }

    public void runNotificationScript(){
        switch(notification){
            case "Aucune Notification activée" :
                notification = "Notification 1 en attente" ;
                setNotificationId("a7b2fe81-25c5-4ca5-adfc-77e4f3834bec");
                mHandler.postDelayed(rNotification1,500) ;
                break ;
            case "Notification 1 en attente" :
                mHandler.postDelayed(rNotification1,500) ;
                break ;
            case "Notification 1 activée" :
                notification = "Notification 2 en attente" ;
                setNotificationId("a7b2fe81-25c5-4ca5-adfc-77e4f3834bed");
                mHandler.postDelayed(rNotification1,500) ;
                break ;
            case "Notification 2 en attente" :
                mHandler.postDelayed(rNotification1,500) ;
                break ;
        }
    }

    public void setNotificationId(String uuid){
        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(uuid)); //a7b2fe81-25c5-4ca5-adfc-77e4f3834bec
        mBluetoothGatt.setCharacteristicNotification(characteristic,true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

