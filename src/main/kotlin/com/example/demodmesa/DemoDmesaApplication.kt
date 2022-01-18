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

@RestController
class GreetingsController {
	val template = "Hello %s!"
	val atomicLong = AtomicLong()

	@GetMapping("/greeting")
	fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String?): Greeting {
		return Greeting(atomicLong.getAndIncrement(), String.format(template, name))
	}

	// I can directly put controllers on the HelloSpringApplication but rather not.
	@GetMapping("/hello")
	fun hello(@RequestParam(value = "name", defaultValue = "World") name: String?): String? {
		return String.format("Hello %s! Fox News - Foolandia", name)
	}
}

fun main(args: Array<String>) {
	runApplication<DemoDmesaApplication>(*args)
}
