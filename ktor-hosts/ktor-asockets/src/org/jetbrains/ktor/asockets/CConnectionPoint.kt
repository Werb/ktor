package org.jetbrains.ktor.asockets

import kotlinx.http.server.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.http.HttpMethod

class CConnectionPoint(val request: HttpRequest) : RequestConnectionPoint {
    override val host: String
        get() = request.header(HttpHeaders.Host).let { when (it.size) {
            0 -> throw IllegalStateException("No Host header specified") // TODO valid state
            1 -> it.first().value(request.headersBody)
            else -> throw IllegalStateException("Multiple host headers specified") // TODO valid state
        }}

    override val method: HttpMethod
        get() = request.method.let { if (it.id == -1) HttpMethod(it.name) else methodMap[it.id] }

    override val port: Int
        get() = host.substringAfter(":", "").toIntOrNull() ?: -1 // TODO get from socket

    override val remoteHost: String
        get() = host // TODO ???

    override val scheme: String
        get() = "http"

    override val uri: String
        get() = request.uri

    override val version: String
        get() = request.version.text

    companion object {
        val methodMap = kotlinx.http.server.HttpMethod.known.sortedBy { it.id }.map { HttpMethod.parse(it.name) }.toTypedArray()
    }
}