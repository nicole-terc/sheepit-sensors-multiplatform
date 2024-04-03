package dev.nstv.gesturesfun.sensorManager

import sensorManager.MultiplatformSensor
import sensorManager.MultiplatformSensorType
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import kotlinx.collections.immutable.toPersistentList
import sensorManager.MultiplatformSensorEvent
import sensorManager.SamplingPeriod


fun Sensor.toMultiplatformSensor() = MultiplatformSensor(name, type.toMultiplatformSensorType())

fun Int.toMultiplatformSensorType() =
    when (this) {
        Sensor.TYPE_ACCELEROMETER -> MultiplatformSensorType.ACCELEROMETER
        Sensor.TYPE_AMBIENT_TEMPERATURE -> MultiplatformSensorType.AMBIENT_TEMPERATURE
        Sensor.TYPE_GAME_ROTATION_VECTOR -> MultiplatformSensorType.GAME_ROTATION_VECTOR
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> MultiplatformSensorType.GEOMAGNETIC_ROTATION_VECTOR
        Sensor.TYPE_GRAVITY -> MultiplatformSensorType.GRAVITY
        Sensor.TYPE_GYROSCOPE -> MultiplatformSensorType.GYROSCOPE
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> MultiplatformSensorType.GYROSCOPE_UNCALIBRATED
        Sensor.TYPE_HEART_BEAT -> MultiplatformSensorType.HEART_BEAT
        Sensor.TYPE_HEART_RATE -> MultiplatformSensorType.HEART_RATE
        Sensor.TYPE_LIGHT -> MultiplatformSensorType.LIGHT
        Sensor.TYPE_LINEAR_ACCELERATION -> MultiplatformSensorType.LINEAR_ACCELERATION
        Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> MultiplatformSensorType.LOW_LATENCY_OFFBODY_DETECT
        Sensor.TYPE_MAGNETIC_FIELD -> MultiplatformSensorType.MAGNETIC_FIELD
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> MultiplatformSensorType.MAGNETIC_FIELD_UNCALIBRATED
        Sensor.TYPE_MOTION_DETECT -> MultiplatformSensorType.MOTION_DETECT
        Sensor.TYPE_POSE_6DOF -> MultiplatformSensorType.POSE_6DOF
        Sensor.TYPE_PRESSURE -> MultiplatformSensorType.PRESSURE
        Sensor.TYPE_PROXIMITY -> MultiplatformSensorType.PROXIMITY
        Sensor.TYPE_RELATIVE_HUMIDITY -> MultiplatformSensorType.RELATIVE_HUMIDITY
        Sensor.TYPE_ROTATION_VECTOR -> MultiplatformSensorType.ROTATION_VECTOR
        Sensor.TYPE_SIGNIFICANT_MOTION -> MultiplatformSensorType.SIGNIFICANT_MOTION
        Sensor.TYPE_STATIONARY_DETECT -> MultiplatformSensorType.STATIONARY_DETECT
        Sensor.TYPE_STEP_COUNTER -> MultiplatformSensorType.STEP_COUNTER
        Sensor.TYPE_STEP_DETECTOR -> MultiplatformSensorType.STEP_DETECTOR
        else -> MultiplatformSensorType.UNKNOWN
    }

fun MultiplatformSensorType.toSensorType() =
    when (this) {
        MultiplatformSensorType.ACCELEROMETER -> Sensor.TYPE_ACCELEROMETER
        MultiplatformSensorType.AMBIENT_TEMPERATURE -> Sensor.TYPE_AMBIENT_TEMPERATURE
        MultiplatformSensorType.GAME_ROTATION_VECTOR -> Sensor.TYPE_GAME_ROTATION_VECTOR
        MultiplatformSensorType.GEOMAGNETIC_ROTATION_VECTOR -> Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
        MultiplatformSensorType.GRAVITY -> Sensor.TYPE_GRAVITY
        MultiplatformSensorType.GYROSCOPE -> Sensor.TYPE_GYROSCOPE
        MultiplatformSensorType.GYROSCOPE_UNCALIBRATED -> Sensor.TYPE_GYROSCOPE_UNCALIBRATED
        MultiplatformSensorType.HEART_BEAT -> Sensor.TYPE_HEART_BEAT
        MultiplatformSensorType.HEART_RATE -> Sensor.TYPE_HEART_RATE
        MultiplatformSensorType.LIGHT -> Sensor.TYPE_LIGHT
        MultiplatformSensorType.LINEAR_ACCELERATION -> Sensor.TYPE_LINEAR_ACCELERATION
        MultiplatformSensorType.LOW_LATENCY_OFFBODY_DETECT -> Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT
        MultiplatformSensorType.MAGNETIC_FIELD -> Sensor.TYPE_MAGNETIC_FIELD
        MultiplatformSensorType.MAGNETIC_FIELD_UNCALIBRATED -> Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED
        MultiplatformSensorType.MOTION_DETECT -> Sensor.TYPE_MOTION_DETECT
        MultiplatformSensorType.POSE_6DOF -> Sensor.TYPE_POSE_6DOF
        MultiplatformSensorType.PRESSURE -> Sensor.TYPE_PRESSURE
        MultiplatformSensorType.PROXIMITY -> Sensor.TYPE_PROXIMITY
        MultiplatformSensorType.RELATIVE_HUMIDITY -> Sensor.TYPE_RELATIVE_HUMIDITY
        MultiplatformSensorType.ROTATION_VECTOR -> Sensor.TYPE_ROTATION_VECTOR
        MultiplatformSensorType.SIGNIFICANT_MOTION -> Sensor.TYPE_SIGNIFICANT_MOTION
        MultiplatformSensorType.STATIONARY_DETECT -> Sensor.TYPE_STATIONARY_DETECT
        MultiplatformSensorType.STEP_COUNTER -> Sensor.TYPE_STEP_COUNTER
        MultiplatformSensorType.STEP_DETECTOR -> Sensor.TYPE_STEP_DETECTOR
        else -> Sensor.TYPE_ALL
    }

fun SamplingPeriod.toSamplingPeriodUs() =
    when (this) {
        SamplingPeriod.NORMAL -> SensorManager.SENSOR_DELAY_NORMAL
        SamplingPeriod.UI -> SensorManager.SENSOR_DELAY_UI
        SamplingPeriod.GAME -> SensorManager.SENSOR_DELAY_GAME
        SamplingPeriod.FASTEST -> SensorManager.SENSOR_DELAY_FASTEST
    }

fun SensorEvent.toMultiplatformSensorEvent() =
    MultiplatformSensorEvent(
        values = this.values,
        accuracy = this.accuracy,
        timestamp = this.timestamp
    )
