package sensorManager

import util.toDegrees

data class DeviceOrientation(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float,
) {
    val azimuthDegrees = azimuth.toDegrees()
    val pitchDegrees = pitch.toDegrees()
    val rollDegrees = roll.toDegrees()

    fun prettyPrint(degrees: Boolean = false) {
        println(
            "Device Orientation ${if (degrees) "Degrees" else "Radians"}: |" +
                    "Azimuth: ${if (degrees) azimuthDegrees else azimuth} | " +
                    "Pitch: ${if (degrees) pitchDegrees else pitch} | " +
                    "Roll: ${if (degrees) rollDegrees else roll} |"
        )
    }
}