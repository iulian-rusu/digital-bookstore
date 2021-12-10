package com.pos.idm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IdentityManagementApplication

fun main(args: Array<String>) {
    runApplication<IdentityManagementApplication>(*args)
}
