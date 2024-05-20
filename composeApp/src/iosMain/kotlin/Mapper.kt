import platform.Foundation.NSTimeInterval
import sensorManager.SamplingPeriod


/**
 * 1.0 / 50.0 == 50 times per second for iOS sensor monitor sampling interval
 * Android uses delays in microseconds instead, converting those to the iOS counterpart are:
 * 1.0 [ second ] / (1000 [second in millis] / 200 [delay in millis])  => 200 / 1000
 */
fun SamplingPeriod.toUpdateInterval() : NSTimeInterval =
    when (this) {
        SamplingPeriod.NORMAL -> 200.0 / 1000.0
        SamplingPeriod.UI -> 60.0 / 1000.0
        SamplingPeriod.GAME -> 20.0 / 1000.0
        SamplingPeriod.FASTEST -> 0.0
    }