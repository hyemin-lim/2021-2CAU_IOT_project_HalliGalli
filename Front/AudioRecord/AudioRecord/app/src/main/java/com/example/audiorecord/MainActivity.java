package com.example.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.audiorecord.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private boolean isRecording = false;
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        audioSetup();
    }

    protected boolean hasMicrophone(){
        PackageManager packageManager = this.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void audioSetup(){
        if(!hasMicrophone()){
            binding.startPlay.setEnabled(false);
            binding.stopPlay.setEnabled(false);
            binding.startRecord.setEnabled(false);
            binding.stopRecord.setEnabled(false);
        }else{
            binding.stopPlay.setEnabled(false);
            binding.startPlay.setEnabled(false);
        }

        audioFilePath = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/myaudio.aac"; // file extension과 관련 없이 aac로 인코딩 함.

        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE);
    }

    public void recordAudio(View view){
        isRecording = true;
        binding.stopRecord.setEnabled(true);
        binding.startRecord.setEnabled(false);
        binding.startPlay.setEnabled(false);
        binding.stopPlay.setEnabled(false);

        try{
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setAudioSamplingRate(96000);
            mediaRecorder.setAudioChannels(2);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
        }catch(Exception e){
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    public void stopAudio(View view){
        binding.stopRecord.setEnabled(false);
        binding.startRecord.setEnabled(true);
        binding.startPlay.setEnabled(true);
        binding.stopPlay.setEnabled(false);

        if(isRecording){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }else{
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playAudio(View view) throws IOException {
        binding.stopRecord.setEnabled(false);
        binding.startRecord.setEnabled(false);
        binding.startPlay.setEnabled(false);
        binding.stopPlay.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    protected void requestPermission(String permissionType, int requestCode){
        int permission = ContextCompat.checkSelfPermission(this, permissionType);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case RECORD_REQUEST_CODE:{
                if(grantResults.length==0 || grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    binding.startRecord.setEnabled(false);
                    Toast.makeText(this, "RECORD PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
                }else{
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE);
                }
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length==0 || grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "EXTERNAL STORAGE PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}