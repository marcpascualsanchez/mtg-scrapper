package com.marcpascualsanchez.mtgscrapper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MtgScrapperApplication

fun main(args: Array<String>) {
	runApplication<MtgScrapperApplication>(*args)
}
