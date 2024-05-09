import 'package:flutter/services.dart';

class HeartRatePlugin {
  static const MethodChannel _methodChannel =
      MethodChannel('heart_rate_method_channel');
  static const EventChannel _eventChannel =
      EventChannel('heart_rate_event_channel');

  static Stream<double> get heartRateStream {
    return _eventChannel
        .receiveBroadcastStream()
        .map<double>((dynamic event) => event as double);
  }

  static Future<double> getHeartRate() async {
    try {
      final double heartRate =
          await _methodChannel.invokeMethod('getHeartRate');
      return heartRate;
    } on PlatformException catch (e) {
      print("Failed to get heart rate: '${e.message}'.");
      return -1.0; // or handle the error as needed
    }
  }
}
