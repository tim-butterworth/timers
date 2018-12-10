package tester

import com.science.time.timer.ResponseTimeData
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Duration
import java.util.*

class TesterClass

fun main(args: Array<String>) {
    val webClient1 = WebClient
            .create("https://timer1.app.wtcdev2.paas.fedex.com")
    val webClient2 = WebClient
            .create("https://timer2.app.wtcdev2.paas.fedex.com")

    val stack1 = Stack<ResponseTimeData>()
    val stack2 = Stack<ResponseTimeData>()

    val flux1 = mono(webClient1)
    val flux2 = mono(webClient2)

    flux1.subscribe { time -> updateStack(time, stack1) }
    flux2.subscribe { time -> updateStack(time, stack2) }

    flux1.blockLast()
    flux2.blockLast()

    val resource = TesterClass::class.java.classLoader.getResource("")
    val path = resource.toURI().toURL().path

    println(path)

    val file = File("$path/results.txt")
    val fileWriter = FileWriter(file)

    val bufferedWriter = BufferedWriter(fileWriter)

    writeStack(stack1, bufferedWriter)

    bufferedWriter.newLine()

    bufferedWriter.write("---------------------------------------")

    bufferedWriter.newLine()
    writeStack(stack2, bufferedWriter)
    bufferedWriter.flush()
}

private fun writeStack(stack1: Stack<ResponseTimeData>, bufferedWriter: BufferedWriter) {
    while (!stack1.empty()) {
        val time = stack1.pop()

        bufferedWriter.write("$time -> [serverTime - clientTime = ${time.serverTime.time - time.clientTime.time}]")
        bufferedWriter.newLine()
    }
}

@Synchronized
fun updateStack(time: ResponseTimeData, stack: Stack<ResponseTimeData>) {
    stack.push(time)
}

private fun mono(webClient1: WebClient): Flux<ResponseTimeData> = Flux.range(0, 10000)
        .delayElements(Duration.ofMillis(100))
        .flatMap { _ -> makeNetworkCall(webClient1).toFlux() }

fun makeNetworkCall(webClient: WebClient): Mono<ResponseTimeData> {
    return webClient
            .get()
            .uri("/timer")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .flatMap { response: ClientResponse -> response.bodyToMono(ResponseTimeData::class.java) }
}
