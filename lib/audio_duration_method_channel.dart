import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'audio_duration_platform_interface.dart';

/// An implementation of [AudioDurationPlatform] that uses method channels.
class MethodChannelAudioDuration extends AudioDurationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('audio_duration');

  @override
  Future<Duration?> getAudioDuration(String path) async {
    var duration = await methodChannel.invokeMethod<int>('getAudioDuration', {
      "path": path,
    });
    if (duration != null) {
      return Duration(milliseconds: duration);
    }
    return null;
  }
}
