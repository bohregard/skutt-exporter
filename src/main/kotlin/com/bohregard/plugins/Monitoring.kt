package com.bohregard.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.Clock
import io.micrometer.prometheus.*
import io.prometheus.client.CollectorRegistry
import java.util.concurrent.atomic.AtomicInteger


val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun Application.configureMonitoring() {
//    appMicrometerRegistry.config().commonTags("kiln", "calcifer")
//
//    appMicrometerRegistry.gauge("kiln_temp", listOf(), temp)

    install(CustomMetrics) {
        registry = appMicrometerRegistry
        meterBinders = listOf()
    }
    routing {
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
