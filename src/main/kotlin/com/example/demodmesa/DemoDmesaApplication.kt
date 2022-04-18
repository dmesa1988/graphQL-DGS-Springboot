package com.example.demodmesa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

data class Greeting(var id: Long, var content:String)

@SpringBootApplication
class DemoDmesaApplication

fun main(args: Array<String>) {
	runApplication<DemoDmesaApplication>(*args)
}
