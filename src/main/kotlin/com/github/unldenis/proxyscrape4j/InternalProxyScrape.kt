package com.github.unldenis.proxyscrape4j

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL


fun main() {
    val proxyElements = ArrayList<ProxyElement>()

    val typeA = arrayOf(
        "https://free-proxy-list.net/",
        "https://free-proxy-list.net/anonymous-proxy.html#",
        "https://www.sslproxies.org/",
        "https://free-proxy-list.net/uk-proxy.html",
        "https://www.us-proxy.org/"
    )

    runBlocking {
        for(t in typeA) {
            launch { scrapeTypeA(proxyElements, t) }
        }
        launch { proxy_daily_com(proxyElements) }
    }


    println("Total proxies: ${proxyElements.size}")
    var count = 0

    proxyElements
        .filter { it.https != null && it.https == true }
        .forEach{
            try {
                val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(it.ipAddress, it.port))
                URL("https://www.google.it/").openConnection(proxy)
                count++;
            } catch (_: Exception) {
            }
        }

    println("Working: $count")

}

fun scrapeTypeA(proxyElements: ArrayList<ProxyElement>, site: String) {
    val select =
        Jsoup.connect(site)
            .get()
            .select(".table-striped > tbody:nth-child(2)")[0]

    for (e in select.children()) {
        val values = e.children()
            .map { it.html() }

        proxyElements.add(
            ProxyElement(
                values[0],
                values[1].toInt(),
                values[3],
                values[4] == "anonymous",
                values[6] == "yes"
            )
        )
    }
}

fun proxy_daily_com(proxyElements: ArrayList<ProxyElement>) {
    val select =
        Jsoup.connect("https://proxy-daily.com/")
            .get()
            .select("div.centeredProxyList:nth-child(7)")[0].html()

    select.split(" ").forEach { line ->
        line.split(":").let {
            proxyElements.add(ProxyElement(it[0], it[1].toInt(), null, null, null))
        }
    }
}

