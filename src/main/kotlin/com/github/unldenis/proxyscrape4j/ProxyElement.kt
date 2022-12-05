package com.github.unldenis.proxyscrape4j

data class ProxyElement(
    val ipAddress: String,
    val port: Int,
    val country: String?,
    val anonymous: Boolean?,
    val https: Boolean?,
) {
}

