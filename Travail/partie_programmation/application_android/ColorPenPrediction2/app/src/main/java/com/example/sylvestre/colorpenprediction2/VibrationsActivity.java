package com.example.sylvestre.colorpenprediction2;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class VibrationsActivity extends AppCompatActivity {

    public static final String EXTRA_SIGNAL_COURT = "com.example.sylvestre.colorpenprediction2.extraSignalCourt";
    public static final String EXTRA_SIGNAL_LONG = "com.example.sylvestre.colorpenprediction2.extraSignalLong";
    public static final String EXTRA_PAUSE = "com.example.sylvestre.colorpenprediction2.extraPause";
    public static final String EXTRA_PERIODE = "com.example.sylvestre.colorpenprediction2.extraPeriode";

    private EditText textNombre;
    private Vibrator vibrator ;

    private TextView textCourt ;
    private TextView textLong ;
    private TextView textPause ;
    private TextView textPeriode ;

    private SeekBar seekCourt ;
    private SeekBar seekLong ;
    private SeekBar seekPause ;
    private SeekBar seekPeriode ;


    private int signalCourt = 150 ;
    private int signalLong = 600 ;
    private int periode = 2000 ;
    private int pause = 150 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrations);

        vibrator = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);

        textNombre = (EditText)findViewById(R.id.entreeNombre) ;

        Bundle intentExtra = getIntent().getExtras() ;
        signalCourt = intentExtra.getInt(VibrationsActivity.EXTRA_SIGNAL_COURT);
        signalLong = intentExtra.getInt(VibrationsActivity.EXTRA_SIGNAL_LONG);
        pause = intentExtra.getInt(VibrationsActivity.EXTRA_PAUSE);
        periode = intentExtra.getInt(VibrationsActivity.EXTRA_PERIODE);

        textCourt = (TextView)findViewById(R.id.textViewCourt) ;
        textLong = (TextView)findViewById(R.id.textViewLong) ;
        textPause = (TextView)findViewById(R.id.textViewPause) ;
        textPeriode = (TextView)findViewById(R.id.textViewPeriode) ;

        seekCourt = (SeekBar)findViewById(R.id.seekBarCourt);
        seekLong = (SeekBar)findViewById(R.id.seekBarLong);
        seekPause = (SeekBar)findViewById(R.id.seekBarPause);
        seekPeriode = (SeekBar)findViewById(R.id.seekBarPeriode);

        textCourt.setText(Integer.toString(signalCourt));
        seekCourt.setProgress(signalCourt-1) ;

        textLong.setText(Integer.toString(signalLong));
        seekLong.setProgress(signalLong-1) ;

        textPause.setText(Integer.toString(pause));
        seekPause.setProgress(pause-1) ;

        textPeriode.setText(Integer.toString(periode));
        seekPeriode.setProgress(periode-1) ;

        seekCourt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                textCourt.setText(Integer.toString(progress+1));
                if(progress+1>signalLong){
                    seekBar.setProgress(signalLong-1);
                    signalCourt = signalLong ;
                }else{
                    signalCourt = progress+1;
                }
            }

        } );

        seekLong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                textLong.setText(Integer.toString(progress+1));
                if(periode<2*progress+2+pause) {
                    seekBar.setProgress((Integer)((periode-pause)/2)-2);
                    signalLong = (Integer)((periode-pause)/2)-1 ;
                }else if(progress+1<signalCourt){
                    seekBar.setProgress(signalCourt-1);
                    signalLong = signalCourt ;
                }else{
                    signalLong = progress+1;
                }
            }

        } );

        seekPause.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                textPause.setText(Integer.toString(progress+1));
                if(periode<2*signalLong+progress+1) {
                    seekBar.setProgress(periode - 2 * signalLong-1);
                    pause = periode-2 * signalLong ;
                }else {
                    pause = progress+1;
                }
            }

        } );

        seekPeriode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                textPeriode.setText(Integer.toString(progress+1));
                if(progress+1<2*signalLong+pause){
                    seekBar.setProgress(2*signalLong+pause-1);
                    periode = 2*signalLong+pause ;
                }else {
                    periode = progress+1;
                }
            }

        } );

    }

    public void faireVibrer(View view){
        int entree = 0;
        if(textNombre.getText().length()!=0) {
            entree = Integer.parseInt(textNombre.getText().toString());
        }
        long[][] motif = {  {0,signalCourt,periode-signalCourt,signalCourt},
                {0,signalLong,periode-signalLong,signalLong},
                {0,signalCourt,pause,signalCourt,periode-2*signalCourt-pause,signalCourt,pause,signalCourt},
                {0,signalCourt,pause,signalLong,periode-signalCourt-pause-signalLong,signalCourt,pause,signalLong},
                {0,signalLong,pause,signalCourt,periode-signalCourt-pause-signalLong,signalLong,pause,signalCourt},
                {0,signalLong,pause,signalLong,periode-pause-2*signalLong,signalLong,pause,signalLong}} ;

        if((entree<=6)&&(entree>=1)){
            vibrator.vibrate(motif[entree - 1], -1);
        }else{
            vibrator.vibrate(1000) ;
            Toast.makeText(getApplicationContext(), "Entrez un nombre entre 1 et 6", Toast.LENGTH_LONG).show();
        }
    }

    public void valider(View view){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SIGNAL_COURT, signalCourt);
        intent.putExtra(EXTRA_SIGNAL_LONG, signalLong);
        intent.putExtra(EXTRA_PAUSE, pause);
        intent.putExtra(EXTRA_PERIODE, periode);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void parametresParDefaut(View view){
        signalCourt = 150 ;
        signalLong = 600 ;
        periode = 2000 ;
        pause = 150 ;
        textCourt.setText(Integer.toString(signalCourt));
        seekCourt.setProgress(signalCourt-1) ;
        textLong.setText(Integer.toString(signalLong));
        seekLong.setProgress(signalLong-1) ;
        textPause.setText(Integer.toString(pause));
        seekPause.setProgress(pause-1) ;
        textPeriode.setText(Integer.toString(periode));
        seekPeriode.setProgress(periode-1) ;
    }




}
