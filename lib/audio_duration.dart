import 'audio_duration_platform_interface.dart';

class AudioDuration {
  static Future<Duration?> getAudioDuration(String path) {
    return AudioDurationPlatform.instance.getAudioDuration(path);
  }
}
