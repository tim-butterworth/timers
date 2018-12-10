package com.science.time.timer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimerApplication

fun main(args: Array<String>) {
    runApplication<TimerApplication>(*args)
}
