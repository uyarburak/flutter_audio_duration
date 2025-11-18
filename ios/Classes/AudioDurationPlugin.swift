import Flutter
import UIKit
import AVFoundation

public class AudioDurationPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "audio_duration", binaryMessenger: registrar.messenger())
    let instance = AudioDurationPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)

    case "getAudioDuration":
      guard
        let args = call.arguments as? [String: Any],
        let path = args["path"] as? String,
        !path.isEmpty
      else {
        result(FlutterError(code: "INVALID_ARGUMENT", message: "Argument 'path' is required", details: nil))
        return
      }

      let url: URL
      if let parsedUrl = URL(string: path), parsedUrl.scheme != nil {
        // Treat as full URL when a scheme is present (e.g., file://, http://)
        url = parsedUrl
      } else {
        // Otherwise treat as a local file path
        url = URL(fileURLWithPath: path)
      }

      let asset = AVURLAsset(url: url)
      let durationSeconds = CMTimeGetSeconds(asset.duration)

      if durationSeconds.isNaN || durationSeconds.isInfinite || durationSeconds < 0 {
        result(FlutterError(code: "DURATION_UNAVAILABLE", message: "Could not determine audio duration", details: nil))
        return
      }

      let milliseconds = Int64(durationSeconds * 1000.0)
      result(milliseconds)

    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
