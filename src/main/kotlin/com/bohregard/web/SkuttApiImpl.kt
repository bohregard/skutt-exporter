package com.bohregard.web

import com.auth0.jwt.JWT
import com.bohregard.plugins.appMicrometerRegistry
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.micrometer.core.instrument.Tag
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class SkuttApiImpl {

    protected val log = LoggerFactory.getLogger(this::class.java)
    private var authResponse: AuthResponse? = null

    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val api by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(60.seconds.toJavaDuration())
            .readTimeout(60.seconds.toJavaDuration())
            .build()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://kiln.bartinst.com/")
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()

        retrofit.create(SkuttApi::class.java)
    }

    fun startSkuttApi() {
        GlobalScope.launch {
            while (true) {
                val expiration = authResponse?.let {
                    val decodedToken = JWT.decode(it.token)
                    decodedToken.expiresAtAsInstant
                }
                log.debug("Expiration: ${expiration == null || Instant.now() > expiration}")
                if (expiration == null || Instant.now() > expiration) {
                    authResponse = fetchAuthToken()
                }
                fetchKilnData(authResponse!!)
                delay(60_000)
            }
        }
    }

    suspend fun fetchAuthToken(): AuthResponse {
        log.debug("Fetching Auth Token")
        return api.getAuth(System.getenv("SKUTT_USERNAME"), System.getenv("SKUTT_PASSWORD"))
    }

    private val temp = AtomicInteger(0)
    private val fires = AtomicInteger(0)
    private val updated = AtomicLong(0)
    private val v1 = AtomicInteger(0)
    private val v2 = AtomicInteger(0)
    private val v3 = AtomicInteger(0)
    private val vs = AtomicInteger(0)
    private val a1 = AtomicInteger(0)
    private val a2 = AtomicInteger(0)
    private val a3 = AtomicInteger(0)
    private val currentRampTemp = AtomicInteger(0)
    private val currentRampMaxTemp = AtomicInteger(0)
    private val boardTemp = AtomicInteger(0)

    suspend fun fetchKilnData(authResponse: AuthResponse) {
        log.debug("Fetching Kilns")
        val response = api.getKilns(authResponse.token, KilnRequest(authResponse.kilnInfo.map { it.id }))
        log.debug("{}", json.encodeToString(response))
        log.debug("Fetched Response for Kiln")
        response.kilns.forEach { kiln ->
            temp.set(kiln.status.temp)
            fires.set(kiln.status.numFires)
            updated.set(kiln.updatedAt.toEpochMilliseconds())
            v1.set(kiln.status.diagnostic.volt1)
            v2.set(kiln.status.diagnostic.volt2)
            v3.set(kiln.status.diagnostic.volt3)
            vs.set(kiln.status.diagnostic.voltS)
            a1.set(kiln.status.diagnostic.amp1)
            a2.set(kiln.status.diagnostic.amp2)
            a3.set(kiln.status.diagnostic.amp3)
            boardTemp.set(kiln.status.diagnostic.boardTemp)
            currentRampTemp.set(kiln.status.firing.setPoint)

            val currentRamp = kiln.status.firing.step.replace("Ramp (\\d+) of (\\d+)".toRegex(), "$1")
                .toInt() - 1

            currentRampMaxTemp.set(kiln.program.steps[currentRamp].temp)

            val tags = listOf(
                Tag.of("kiln", authResponse.kilnInfo.first { it.id == kiln.serialNumber }.name),
                Tag.of("firmware", kiln.status.firmware),
            )

            appMicrometerRegistry.gauge(
                "kiln_temp",
                tags,
                temp
            )
            appMicrometerRegistry.gauge(
                "kiln_firings",
                tags,
                fires
            )
            appMicrometerRegistry.gauge(
                "kiln_updated",
                tags,
                updated
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_volt1",
                tags,
                v1
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_volt2",
                tags,
                v2
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_volt3",
                tags,
                v3
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_volts",
                tags,
                vs
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_amp1",
                tags,
                a1
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_amp2",
                tags,
                a2
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_amp3",
                tags,
                a3
            )
            appMicrometerRegistry.gauge(
                "kiln_diag_board_temp",
                tags,
                boardTemp
            )
            appMicrometerRegistry.gauge(
                "kiln_current_ramp_temp",
                tags,
                currentRampTemp
            )
            appMicrometerRegistry.gauge(
                "kiln_current_ramp_temp_max",
                tags,
                currentRampMaxTemp
            )
        }
    }
}