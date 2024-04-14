package sensorManager

import util.toDegrees

// documentation: https://developer.android.com/develop/sensors-and-location/sensors/sensors_position#sensors-pos-orient
data class DeviceOrientation(
    val azimuth: Float, // Orientation[0]
    val pitch: Float, // Orientation[1]
    val roll: Float, // Orientation[2]
) {
    val azimuthDegrees = azimuth.toDegrees()
    val pitchDegrees = pitch.toDegrees()
    val rollDegrees = roll.toDegrees()

    fun prettyPrint(degrees: Boolean = false) {
        println(
            "Device Orientation ${if (degrees) "Degrees" else "Radians"}: |" +
                    "Azimuth(-z): ${if (degrees) azimuthDegrees else azimuth} | " +
                    "Pitch(x): ${if (degrees) pitchDegrees else pitch} | " +
                    "Roll(y): ${if (degrees) rollDegrees else roll} |"
        )
    }
}