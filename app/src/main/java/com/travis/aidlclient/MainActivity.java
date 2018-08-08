package com.travis.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;

    private MessageCenter messageCenter = null;

    private boolean mBound = false;

    private List<Info> mInfoList;



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageCenter = MessageCenter.Stub.asInterface(iBinder);

            mBound = true;

            if (messageCenter != null){
                try {
                    mInfoList = messageCenter.getInfo();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        findViewById(R.id.button).setOnClickListener(this);
    }

    public void addMessage(String content){
        if (!mBound){
            attempToBindService();
            Toast.makeText(this, "正在尝试连接，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (messageCenter == null) return;
        Info info = new Info();

        info.setContent(content);

        try{
            messageCenter.addInfo(info);
        }catch (RemoteException e){
            e.printStackTrace();
        }

    }

    private void attempToBindService(){
        Intent intent = new Intent();
        intent.setAction("com.travis.aidlservice.service");
        intent.setPackage("com.travis.aidlservice");
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                String content = editText.getText().toString();
                addMessage(content);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound){
            attempToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
    }
}






















