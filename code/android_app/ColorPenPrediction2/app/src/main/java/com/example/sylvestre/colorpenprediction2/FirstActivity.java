package com.example.sylvestre.colorpenprediction2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {
    public static final int vibrationsActivityRequest = 1 ;
    public static final int detectionActivityRequest = 2 ;
    public static final int scanActivityRequest = 3 ;

    private int signalCourt = 150 ;
    private int signalLong = 600 ;
    private int periode = 2000 ;
    private int pause = 150 ;

    private int param_mEqual = 5 ;
    private int param_mMin = 5 ;
    private int param_delta = 5 ;
    private int param_tMes = 5;
    private int param_nMes = 10 ;
    private int param_nFeutres = 3 ;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void lancerScan(View view){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 5;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Intent intentScan = new Intent(getApplicationContext(), ScanActivity.class);
            intentScan.putExtra(DetectionActivity.EXTRA_MEQUAL, param_mEqual);
            intentScan.putExtra(DetectionActivity.EXTRA_MMIN, param_mMin);
            intentScan.putExtra(DetectionActivity.EXTRA_DELTA, param_delta);
            intentScan.putExtra(DetectionActivity.EXTRA_TMES, param_tMes);
            intentScan.putExtra(DetectionActivity.EXTRA_NMES, param_nMes);
            intentScan.putExtra(DetectionActivity.EXTRA_NFEUTRES, param_nFeutres);
            intentScan.putExtra(VibrationsActivity.EXTRA_SIGNAL_COURT, signalCourt);
            intentScan.putExtra(VibrationsActivity.EXTRA_SIGNAL_LONG, signalLong);
            intentScan.putExtra(VibrationsActivity.EXTRA_PAUSE, pause);
            intentScan.putExtra(VibrationsActivity.EXTRA_PERIODE, periode);
            startActivityForResult(intentScan, scanActivityRequest);
        }
    }

    public void gererVibrations(View view){
        Intent intentVibrations = new Intent(getApplicationContext(),VibrationsActivity.class);
        intentVibrations.putExtra(VibrationsActivity.EXTRA_SIGNAL_COURT, signalCourt);
        intentVibrations.putExtra(VibrationsActivity.EXTRA_SIGNAL_LONG, signalLong);
        intentVibrations.putExtra(VibrationsActivity.EXTRA_PAUSE, pause);
        intentVibrations.putExtra(VibrationsActivity.EXTRA_PERIODE, periode);
        startActivityForResult(intentVibrations, vibrationsActivityRequest);
    }

    public void gererDetection(View view){
        Intent intentDetection = new Intent(getApplicationContext(),DetectionActivity.class);
        intentDetection.putExtra(DetectionActivity.EXTRA_MEQUAL, param_mEqual);
        intentDetection.putExtra(DetectionActivity.EXTRA_MMIN, param_mMin);
        intentDetection.putExtra(DetectionActivity.EXTRA_DELTA, param_delta);
        intentDetection.putExtra(DetectionActivity.EXTRA_TMES, param_tMes);
        intentDetection.putExtra(DetectionActivity.EXTRA_NMES, param_nMes);
        intentDetection.putExtra(DetectionActivity.EXTRA_NFEUTRES, param_nFeutres);
        startActivityForResult(intentDetection,detectionActivityRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        switch(requestCode){
            case vibrationsActivityRequest :
                if(resultCode==RESULT_OK){
                    Bundle intentExtra = data.getExtras() ;
                    signalCourt = intentExtra.getInt(VibrationsActivity.EXTRA_SIGNAL_COURT);
                    signalLong = intentExtra.getInt(VibrationsActivity.EXTRA_SIGNAL_LONG);
                    pause = intentExtra.getInt(VibrationsActivity.EXTRA_PAUSE);
                    periode = intentExtra.getInt(VibrationsActivity.EXTRA_PERIODE);
                }
                break ;
            case detectionActivityRequest :
                if(resultCode==RESULT_OK){
                    Bundle intentExtra = data.getExtras() ;
                    param_mEqual = intentExtra.getInt(DetectionActivity.EXTRA_MEQUAL);
                    param_mMin = intentExtra.getInt(DetectionActivity.EXTRA_MMIN);
                    param_delta = intentExtra.getInt(DetectionActivity.EXTRA_DELTA);
                    param_tMes = intentExtra.getInt(DetectionActivity.EXTRA_TMES);
                    param_nMes = intentExtra.getInt(DetectionActivity.EXTRA_NMES);
                    param_nFeutres = intentExtra.getInt(DetectionActivity.EXTRA_NFEUTRES);
                }
                break ;
        }
    }

}
