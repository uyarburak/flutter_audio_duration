package p32929.audio_duration.audio_duration

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** AudioDurationPlugin */
class AudioDurationPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var applicationContext: Context

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "audio_duration")
    channel.setMethodCallHandler(this)
    applicationContext = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getAudioDuration") {
      val args = call.arguments as? Map<*, *>
      val path = args?.get("path") as? String

      if (path.isNullOrEmpty()) {
        result.error("INVALID_ARGUMENT", "Argument 'path' is required", null)
        return
      }

      val uri: Uri = Uri.parse(path)
      val mmr = MediaMetadataRetriever()

      try {
        val scheme = uri.scheme

        if (scheme.isNullOrEmpty() || scheme == "file" || scheme.startsWith("http")) {
          // Plain file path or network URL (http/https)
          mmr.setDataSource(path)
        } else {
          // Likely a content:// or other URI that requires a Context
          mmr.setDataSource(applicationContext, uri)
        }

        val durationStr: String? = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val milliseconds: Long? = durationStr?.toLongOrNull()

        if (milliseconds == null || milliseconds < 0L) {
          result.error("DURATION_UNAVAILABLE", "Could not determine audio duration", null)
        } else {
          result.success(milliseconds)
        }
      } catch (e: Exception) {
        result.error("DURATION_UNAVAILABLE", "Could not determine audio duration", e.localizedMessage)
      } finally {
        mmr.release()
      }
    }
    
    else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
