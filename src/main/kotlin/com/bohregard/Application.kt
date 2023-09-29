package com.bohregard

import com.bohregard.plugins.configureMonitoring
import com.bohregard.plugins.configureSerialization
import com.bohregard.web.SkuttApiImpl
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    SkuttApiImpl().startSkuttApi()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
}
