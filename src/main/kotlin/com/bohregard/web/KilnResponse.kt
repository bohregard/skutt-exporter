package com.bohregard.web

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KilnResponse(
    val kilns: List<Kiln>
)

@Serializable
data class Kiln(
    val config: KilnConfig,
    val createdAt: Instant,
    @SerialName("latest_firing") val latestFiring: KilnLatestFiring,
    @SerialName("mac_address") val macAddress: String,
    val program: KilnProgram,
    @SerialName("serial_number") val serialNumber: String,
    val status: KilnStatus,
    val updatedAt: Instant,
)

@Serializable
data class KilnStatus(
    @SerialName("diag") val diagnostic: KilnStatusDiagnostic,
    @SerialName("fw") val firmware: String,
    val mode: String,
    @SerialName("num_fire") val numFires: Int,
    @SerialName("t2") val temp: Int
)

@Serializable
data class KilnStatusDiagnostic(
    @SerialName("a1") val amp1: Int,
    @SerialName("a2") val amp2: Int,
    @SerialName("a3") val amp3: Int,
    @SerialName("board_t") val boardTemp: Int,
    @SerialName("date") val date: Instant,
    @SerialName("fl") val loadFull: Int,
    @SerialName("last_err") val lastError: Int,
    @SerialName("nl") val loadNo: Int,
    @SerialName("v1") val volt1: Int,
    @SerialName("v2") val volt2: Int,
    @SerialName("v3") val volt3: Int,
    @SerialName("vs") val voltS: Int,
)

@Serializable
data class KilnProgram(
    @SerialName("alarm_t") val alarmT: Int,
    val cone: String,
    @SerialName("_id") val id: String,
    val name: String,
    @SerialName("num_steps") val numOfSteps: Int,
    val speed: String,
    val steps: List<KilnProgramStep>,
    val type: String,
)

@Serializable
data class KilnProgramStep(
    @SerialName("hr") val hourHold: Int,
    @SerialName("_id") val id: String,
    @SerialName("mn") val minuteHold: Int,
    @SerialName("rt") val rate: Int,
    @SerialName("num") val step: Int,
    @SerialName("t") val temp: Int,
)

@Serializable
data class KilnLatestFiring(
    val ended: Boolean,
    @SerialName("ended_time") val endedTime: String?,
    @SerialName("just_ended") val justEnded: Boolean,
    @SerialName("start_time") val startTime: String,
    @SerialName("update_time") val updateTime: String,
)

@Serializable
data class KilnConfig(
    @SerialName("err_codes") val errorCodes: KilnErrorCodes,
    @SerialName("full_load") val loadFull: Int,
    @SerialName("no_load") val loadNo: Int,
    @SerialName("_id") val id: String,
    @SerialName("num_zones") val numZones: Int,
    @SerialName("t_scale") val tempScale: KilnTempConfig,
)

@Serializable
enum class KilnTempConfig {
    @SerialName("F")
    FAHRENHEIT,
    @SerialName("C")
    CELSIUS
}

@Serializable
enum class KilnErrorCodes {
    @SerialName("On")
    ON,
    @SerialName("Off")
    OFF
}