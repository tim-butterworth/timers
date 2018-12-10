package com.science.time.timer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

data class ResponseTimeData(val clientTime: TimeData, val serverTime: TimeData)

@Configuration
class RequestHandler {

    @Bean
    fun getRouter(configurationProvider: ConfigurationProvider): RouterFunction<ServerResponse> {
        return route(POST("/timer")
                .and(accept(APPLICATION_JSON)), HandlerFunction<ServerResponse> { request ->
            val body: Mono<TimeData> = request.body(BodyExtractors.toMono(TimeData::class.java))

            ServerResponse
                    .status(HttpStatus.OK)
                    .body(body.map { timeData ->
                        ResponseTimeData(
                                timeData,
                                TimeData(
                                        time = System.currentTimeMillis(),
                                        instanceName = configurationProvider.instanceName
                                )
                        )
                    }, ResponseTimeData::class.java)
        }).andRoute(GET("/timer"), HandlerFunction<ServerResponse> {
            val response = WebClient
                    .create(configurationProvider.url)
                    .post()
                    .uri("/timer")
                    .body(
                            Mono.just(
                                    TimeData(
                                            time = System.currentTimeMillis(),
                                            instanceName = configurationProvider.instanceName
                                    )
                            ), TimeData::class.java
                    )
                    .accept(APPLICATION_JSON)
                    .exchange()

            ServerResponse
                    .status(HttpStatus.OK)
                    .body(response.flatMap { clientResponse -> clientResponse.bodyToMono(ResponseTimeData::class.java) }, ResponseTimeData::class.java)
        })
    }
}