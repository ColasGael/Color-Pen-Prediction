package com.example.sylvestre.colorpenprediction2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity {

    private BluetoothLeService mBLeService ;
    private BluetoothLeScanner mScanner ;

    private Vibrator vibrator ;

    private boolean etatConnexion = false ;
    private int numeroDuFeutre = 0 ;
    private int etape = 1 ;

    private int param_mEqual = 50 ;
    private int param_mMin = 1 ;
    private int param_delta = 1 ;
    private int param_tMes = 1 ;
    private int param_nMes = 1 ;
    private int param_nFeutres = 5 ;

    private int signalCourt = 150 ;
    private int signalLong = 600 ;
    private int periode = 2000 ;
    private int pause = 150 ;

    private TextView textEtape1 ;
    private TextView textEtape2 ;
    private TextView textEtape3 ;
    private TextView textEtape4 ;
    private TextView textEtape5 ;
    private TextView textEtape6 ;
    private TextView textEtape7 ;
    private TextView textPhase ;

    private ImageView imageView ;

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult scanResult){
            BluetoothDevice mBluetoothDevice = scanResult.getDevice() ;
            String deviceName = mBluetoothDevice.getName() ;
            if((deviceName!=null)&&deviceName.equals("CPP")) {
                etape = 2 ;
                textEtape1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVertValide));
                textEtape2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));
                imageView.setImageResource(R.drawable.icone2);
                mBLeService = new BluetoothLeService(getApplicationContext(),mBluetoothDevice);
                mScanner.stopScan(this) ;
            }else{
                //Un autre périphérique est détecté
            };
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                case BluetoothLeService.ACTION_GATT_CONNECTED :
                    etape = 3 ;
                    textEtape2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVertValide));
                    textEtape3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));
                    imageView.setImageResource(R.drawable.icone3);
                    etatConnexion = true ;
                    //ecrireConsole("Périphérique connecté. Chargement des services...");
                    break ;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED :
                    Toast.makeText(getApplicationContext(), "Le périphérique a été deconnecté.", Toast.LENGTH_LONG).show();
                    finish();
                    break ;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED :
                    etape = 4 ;
                    textEtape3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVertValide));
                    textEtape4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));
                    mBLeService.setNotification();
                    break;
                case BluetoothLeService.ACTION_GATT_NOTIFICATION_ENABLED :
                    etape = 5 ;
                    textEtape4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVertValide));
                    textEtape5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));
                    imageView.setImageResource(R.drawable.icone5);
                    mBLeService.sendParameters(param_mEqual, param_mMin, param_delta, param_tMes, param_nMes, param_nFeutres);
                    break ;
                case BluetoothLeService.ACTION_PARAMETERS_SENT :
                    etape = 6 ;
                    textEtape5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVertValide));
                    textEtape6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));
                    imageView.setImageResource(R.drawable.icone61);
                    numeroDuFeutre = 1 ;
                    textPhase.setText("Posez le feutre "+numeroDuFeutre+" dans le pot.");
                    break ;
                case BluetoothLeService.ACTION_NEW_PEN :
                    if(etape==6){
                        String texteAffiche = "Le feutre "+numeroDuFeutre+" pèse "+ ((float)intent.getIntExtra(mBLeService.INT_CHARACTERISTIC_VALUE,0)/10)+" g";
                        numeroDuFeutre += 1 ;
                        if(numeroDuFeutre <= param_nFeutres) {
                            texteAffiche += "\nPosez le feutre " + numeroDuFeutre + " dans le pot.";
                            textPhase.setText(texteAffiche);
                            switch(numeroDuFeutre){
                                case 2 :
                                    imageView.setImageResource(R.drawable.icone62);
                                    break ;
                                case 3 :
                                    imageView.setImageResource(R.drawable.icone63);
                                    break ;
                                case 4 :
                                    imageView.setImageResource(R.drawable.icone64);
                                    break ;
                                case 5 :
                                    imageView.setImageResource(R.drawable.icone65);
                                    break ;
                                case 6 :
                                    imageView.setImageResource(R.drawable.icone66);
                                    break ;
                            }
                        }else{
                            texteAffiche += "\nLE TOUR DE MAGIE PEUT COMMENCER !";
                            textPhase.setText(texteAffiche);
                            etape = 7 ;
                            textEtape6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorVertValide));
                            textEtape7.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));
                        }
                    }
                    break ;
                case BluetoothLeService.ACTION_ERROR_PEN :
                    if(etape==6){
                        String texteAffiche = "Le feutre "+numeroDuFeutre+" pèse "+ ((float)intent.getIntExtra(mBLeService.INT_CHARACTERISTIC_VALUE,0)/10)+" g";
                        texteAffiche += "\nCe poids est trop proche d'un feutre précédent, enlevez le et posez en un autre." ;
                        textPhase.setText(texteAffiche);
                    }
                    break ;
                case BluetoothLeService.ACTION_DETECTED_PEN :
                    if(etape==7){
                        int numero = intent.getIntExtra(mBLeService.INT_CHARACTERISTIC_VALUE,0);
                        String texteAffiche = "Le feutre "+ numero +" vient d'être pris.";
                        //texteAffiche += "\nCe poids est trop proche d'un feutre précédent, enlevez le feutre que vous venez de poser et posez en un autre." ;
                        textPhase.setText(texteAffiche);
                        if(numero!=0){
                            vibrer(numero) ;
                        }
                    }
                    break ;
                default :
                    //ecrireConsole("BUG DE RECEPTION");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        vibrator = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);

        Bundle intentExtra = getIntent().getExtras() ;
        param_mEqual = intentExtra.getInt(DetectionActivity.EXTRA_MEQUAL);
        param_mMin = intentExtra.getInt(DetectionActivity.EXTRA_MMIN);
        param_delta = intentExtra.getInt(DetectionActivity.EXTRA_DELTA);
        param_tMes = intentExtra.getInt(DetectionActivity.EXTRA_TMES);
        param_nMes = intentExtra.getInt(DetectionActivity.EXTRA_NMES);
        param_nFeutres = intentExtra.getInt(DetectionActivity.EXTRA_NFEUTRES);

        signalCourt = intentExtra.getInt(VibrationsActivity.EXTRA_SIGNAL_COURT);
        signalLong = intentExtra.getInt(VibrationsActivity.EXTRA_SIGNAL_LONG);
        pause = intentExtra.getInt(VibrationsActivity.EXTRA_PAUSE);
        periode = intentExtra.getInt(VibrationsActivity.EXTRA_PERIODE);

        textEtape1 = (TextView) findViewById(R.id.textEtape1) ;
        textEtape2 = (TextView) findViewById(R.id.textEtape2) ;
        textEtape3 = (TextView) findViewById(R.id.textEtape3) ;
        textEtape4 = (TextView) findViewById(R.id.textEtape4) ;
        textEtape5 = (TextView) findViewById(R.id.textEtape5) ;
        textEtape6 = (TextView) findViewById(R.id.textEtape6) ;
        textEtape7 = (TextView) findViewById(R.id.textEtape7) ;

        textPhase = (TextView) findViewById(R.id.textPhase) ;

        imageView = (ImageView) findViewById(R.id.imageView) ;

        textEtape1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOrangeEnCours));

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_NEW_PEN);
        filter.addAction(BluetoothLeService.ACTION_ERROR_PEN);
        filter.addAction(BluetoothLeService.ACTION_DETECTED_PEN);
        filter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BluetoothLeService.ACTION_GATT_NOTIFICATION_ENABLED);
        filter.addAction(BluetoothLeService.ACTION_PARAMETERS_SENT);
        registerReceiver(mReceiver, filter);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mScanner.startScan(mScanCallback);
    }

    public void vibrer(int numero){
        long[][] motif = {  {0,signalCourt,periode-signalCourt,signalCourt},
                {0,signalLong,periode-signalLong,signalLong},
                {0,signalCourt,pause,signalCourt,periode-2*signalCourt-pause,signalCourt,pause,signalCourt},
                {0,signalCourt,pause,signalLong,periode-signalCourt-pause-signalLong,signalCourt,pause,signalLong},
                {0,signalLong,pause,signalCourt,periode-signalCourt-pause-signalLong,signalLong,pause,signalCourt},
                {0,signalLong,pause,signalLong,periode-pause-2*signalLong,signalLong,pause,signalLong}} ;

        if((numero<=6)&&(numero>=1)){
            vibrator.vibrate(motif[numero - 1], -1);
        }else{
            vibrator.vibrate(1000) ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
