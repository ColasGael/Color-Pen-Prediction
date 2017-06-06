package com.example.sylvestre.colorpenprediction2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigDecimal;

public class DetectionActivity extends AppCompatActivity {
    public static final String EXTRA_MEQUAL = "com.example.sylvestre.colorpenprediction2.mEqual" ;
    public static final String EXTRA_MMIN = "com.example.sylvestre.colorpenprediction2.mMin" ;
    public static final String EXTRA_DELTA = "com.example.sylvestre.colorpenprediction2.delta" ;
    public static final String EXTRA_TMES = "com.example.sylvestre.colorpenprediction2.tMes" ;
    public static final String EXTRA_NMES = "com.example.sylvestre.colorpenprediction2.nMes" ;
    public static final String EXTRA_NFEUTRES = "com.example.sylvestre.colorpenprediction2.nFeutres" ;

    private TextView textMEqual ;
    private TextView textMMin ;
    private TextView textDelta ;
    private TextView textTMes ;
    private TextView textNMes ;
    private TextView textNFeutres ;

    private SeekBar seekMEqual ;
    private SeekBar seekMMin ;
    private SeekBar seekDelta ;
    private SeekBar seekTMes ;
    private SeekBar seekNMes ;
    private SeekBar seekNFeutres ;

    private BigDecimal bd ;

    private int mEqual = 5 ;
    private int mMin = 5 ;
    private int delta = 5 ;
    private int tMes = 5 ;
    private int nMes = 10 ;
    private int nFeutres = 5 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        Bundle intentExtra = getIntent().getExtras() ;
        mEqual = intentExtra.getInt(DetectionActivity.EXTRA_MEQUAL);
        mMin = intentExtra.getInt(DetectionActivity.EXTRA_MMIN);
        delta = intentExtra.getInt(DetectionActivity.EXTRA_DELTA);
        tMes = intentExtra.getInt(DetectionActivity.EXTRA_TMES);
        nMes = intentExtra.getInt(DetectionActivity.EXTRA_NMES);
        nFeutres = intentExtra.getInt(DetectionActivity.EXTRA_NFEUTRES);

        textMEqual = (TextView)findViewById(R.id.textViewMEqual) ;
        textMMin = (TextView)findViewById(R.id.textViewMMin) ;
        textDelta = (TextView)findViewById(R.id.textViewDelta) ;
        textTMes = (TextView)findViewById(R.id.textViewTMes) ;
        textNMes = (TextView)findViewById(R.id.textViewNMes) ;
        textNFeutres = (TextView)findViewById(R.id.textViewNFeutres) ;

        seekMEqual = (SeekBar)findViewById(R.id.seekBarMEqual);
        seekMMin = (SeekBar)findViewById(R.id.seekBarMMin);
        seekDelta = (SeekBar)findViewById(R.id.seekBarDelta);
        seekTMes = (SeekBar)findViewById(R.id.seekBarTMes);
        seekNMes = (SeekBar)findViewById(R.id.seekBarNMes);
        seekNFeutres = (SeekBar)findViewById(R.id.seekBarNFeutres);

        bd = BigDecimal.valueOf(((double) mEqual) / 50) ;
        bd.setScale(1,BigDecimal.ROUND_DOWN);
        textMEqual.setText(bd.toString());
        seekMEqual.setProgress(mEqual-1);

        textMMin.setText(Integer.toString(mMin));
        seekMMin.setProgress(mMin-1) ;

        bd = BigDecimal.valueOf(((double) delta) / 10) ;
        bd.setScale(1, BigDecimal.ROUND_DOWN);
        textDelta.setText(bd.toString());
        seekDelta.setProgress(delta-1);

        textTMes.setText(Integer.toString(tMes * 10));
        seekTMes.setProgress(tMes-1);

        textNMes.setText(Integer.toString(nMes));
        seekNMes.setProgress(nMes-1);

        textNFeutres.setText(Integer.toString(nFeutres));
        seekNFeutres.setProgress(nFeutres-2) ;

        seekMEqual.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bd = BigDecimal.valueOf(((double) (progress+1)) / 50) ;
                bd.setScale(1,BigDecimal.ROUND_DOWN);
                textMEqual.setText(bd.toString());
                mEqual = progress ;
            }

        } );

        seekMMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textMMin.setText(Integer.toString(progress+1));
                mMin = progress+1 ;
            }

        } );

        seekDelta.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bd = BigDecimal.valueOf(((double) (progress+1)) / 10) ;
                bd.setScale(1, BigDecimal.ROUND_DOWN);
                textDelta.setText(bd.toString());
                delta = progress+1 ;
            }

        } );

        seekTMes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textTMes.setText(Integer.toString((progress+1)*10));
                tMes = progress+1 ;
            }

        } );

        seekNMes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textNMes.setText(Integer.toString(progress+1));
                nMes = progress+1 ;
            }

        } );

        seekNFeutres.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textNFeutres.setText(Integer.toString(progress+2));
                nFeutres = progress+2 ;
            }

        } );


    }

    public void validerDetection(View view){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MEQUAL, mEqual);
        intent.putExtra(EXTRA_MMIN, mMin);
        intent.putExtra(EXTRA_DELTA, delta);
        intent.putExtra(EXTRA_TMES, tMes);
        intent.putExtra(EXTRA_NMES, nMes);
        intent.putExtra(EXTRA_NFEUTRES, nFeutres);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void parametresParDefaut(View view){
        mEqual = 5 ;
        mMin = 5 ;
        delta = 5 ;
        tMes = 5 ;
        nMes = 10 ;
        nFeutres = 3 ;

        bd = BigDecimal.valueOf(((double) mEqual) / 50) ;
        bd.setScale(1,BigDecimal.ROUND_DOWN);
        textMEqual.setText(bd.toString());
        seekMEqual.setProgress(mEqual-1);

        textMMin.setText(Integer.toString(mMin));
        seekMMin.setProgress(mMin-1) ;

        bd = BigDecimal.valueOf(((double) delta) / 10) ;
        bd.setScale(1, BigDecimal.ROUND_DOWN);
        textDelta.setText(bd.toString());
        seekDelta.setProgress(delta-1);

        textTMes.setText(Integer.toString(tMes * 10));
        seekTMes.setProgress(tMes-1);

        textNMes.setText(Integer.toString(nMes));
        seekNMes.setProgress(nMes-1);

        textNFeutres.setText(Integer.toString(nFeutres));
        seekNFeutres.setProgress(nFeutres-2) ;
    }


}
