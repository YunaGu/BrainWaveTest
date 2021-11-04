package com.geekb.brainwavetest;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.EEGPower;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

public class BlueService extends Service {
    int attention;
    BluetoothAdapter bluetoothAdapter;
    int delta;
    int highAlpha;
    int highBeta;
    int lowAlpha;
    int lowBeta;
    int lowGamma;
    int meditation;
    int midGamma;
    boolean notStar = true;
    int raw;
    public int signal = -1000;
    TgStreamReader tgStreamReader;
    int theta;
    private boolean threadDisable;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            tgStreamReader = new TgStreamReader(bluetoothAdapter, tgStreamHandler);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!threadDisable) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (notStar && !threadDisable) {
                            tgStreamReader.connect();
                        }
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("lowAlpha", lowAlpha);
                        bundle.putInt("highAlpha", highAlpha);
                        bundle.putInt("lowBeta", lowBeta);
                        bundle.putInt("highBeta", highBeta);
                        bundle.putInt("midGamma", midGamma);
                        bundle.putInt("lowGamma", lowGamma);
                        bundle.putInt("delta", delta);
                        bundle.putInt("theta", theta);
                        bundle.putInt("attention", attention);
                        bundle.putInt("meditation", meditation);
                        bundle.putInt("sinal", signal);
                        bundle.putInt("raw", raw);
                        intent.putExtras(bundle);
                        intent.setAction("com.geekb.brainwavetest.BlueService");
                        sendBroadcast(intent);
                    }
                }
            }).start();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private TgStreamHandler tgStreamHandler = new TgStreamHandler() {
        @Override
        public void onDataReceived(int i, int i1, Object o) {
            Message msg = linkDetectedHandler.obtainMessage();
            msg.what = i;
            msg.arg1 = i1;
            msg.obj = o;
            linkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onStatesChanged(int i) {
            switch (i) {
                case ConnectionStates.STATE_CONNECTED:
                    tgStreamReader.start();
                    notStar = false;
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                case ConnectionStates.STATE_ERROR:
                    signal = -1000;
                    notStar = true;
                    break;
                default:
            }
        }

        @Override
        public void onChecksumFail(byte[] bytes, int i, int i1) {

        }

        @Override
        public void onRecordFail(int i) {

        }
    };

    private Handler linkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MindDataType.CODE_FILTER_TYPE:

                    break;
                case MindDataType.CODE_RAW:
                    break;
                case MindDataType.CODE_MEDITATION:
                    meditation = msg.arg1;
                    break;
                case MindDataType.CODE_ATTENTION:
                    attention = msg.arg1;
                    break;
                case MindDataType.CODE_EEGPOWER:
                    EEGPower power = (EEGPower) msg.obj;
                    if (power.isValidate()) {
                        theta = power.theta;
                        delta = power.delta;
                        lowAlpha = power.lowAlpha;
                        highAlpha = power.highAlpha;
                        lowBeta = power.lowBeta;
                        highBeta = power.highBeta;
                        lowGamma = power.lowGamma;
                        midGamma = power.middleGamma;
                    }
                    break;
                case MindDataType.CODE_POOR_SIGNAL:
                    signal = msg.arg1;
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        attention = 0;
        meditation = 0;
        signal = 200;
        lowAlpha = 0;
        lowBeta = 0;
        highAlpha = 0;
        highBeta = 0;
        midGamma = 0;
        lowGamma = 0;
        delta = 0;
        theta = 0;
        tgStreamReader.close();
        threadDisable = true;
    }
}
