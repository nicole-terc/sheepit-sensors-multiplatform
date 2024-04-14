package sensorManager

import com.bumble.appyx.interactions.SystemClock
import util.toDegrees

// documentation: https://developer.android.com/develop/sensors-and-location/sensors/sensors_position#sensors-pos-orient
data class DeviceOrientation(
    val azimuth: Float, // Orientation[0] 0 to 360
    val pitch: Float, // Orientation[1] -90 to 90
    val roll: Float, // Orientation[2] -180 to 180
) {
    constructor(orientation: FloatArray) : this(
        azimuth = orientation[0], pitch = orientation[1], roll = orientation[2]
    )

    val azimuthDegrees = azimuth.toDegrees()
    val pitchDegrees = pitch.toDegrees()
    val rollDegrees = roll.toDegrees()

    fun prettyPrint(degrees: Boolean = false) {
        println(
            "Device Orientation ${if (degrees) "Degrees" else "Radians"}: |"
                    + "Azimuth(-z): ${if (degrees) azimuthDegrees else azimuth} | "
                    + "Pitch(x): ${if (degrees) pitchDegrees else pitch} | "
                    + "Roll(y): ${if (degrees) rollDegrees else roll} |"
        )
    }

    fun asEvent() = MultiplatformSensorEvent(
        values = floatArrayOf(azimuth, pitch, roll),
        accuracy = -1,
        timestamp = SystemClock.nanoTime(),
    )
}