package com.example.heart_rate_plugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class HeartRatePlugin implements FlutterPlugin, MethodCallHandler {
    private Context context;
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private EventChannel.EventSink eventSink;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        context = binding.getApplicationContext();
        MethodChannel methodChannel = new MethodChannel(binding.getBinaryMessenger(), "heart_rate_method_channel");
        methodChannel.setMethodCallHandler(this);

        EventChannel eventChannel = new EventChannel(binding.getBinaryMessenger(), "heart_rate_event_channel");
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                eventSink = events;
                startListeningForHeartRate();
            }

            @Override
            public void onCancel(Object arguments) {
                eventSink = null;
                stopListeningForHeartRate();
            }
        });
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getHeartRate")) {
            float heartRate = getCurrentHeartRate();
            result.success(heartRate);
        } else {
            result.notImplemented();
        }
    }

    private void startListeningForHeartRate() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            if (heartRateSensor != null) {
                SensorEventListener sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                            float heartRate = event.values[0];
                            if (eventSink != null) {
                                eventSink.success(heartRate);
                            }
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        // Not needed for heart rate sensor
                    }
                };
                sensorManager.registerListener(sensorEventListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    private void stopListeningForHeartRate() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private float getCurrentHeartRate() {
        // This could return a cached value or the latest value received from the sensor
        // For simplicity, I'm just returning a hardcoded value here
        return 70.0f;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        context = null;
        stopListeningForHeartRate();
    }
}
