package it.posteitaliane.dccli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {

    object : CliktCommand(name = "dccli", invokeWithoutSubcommand = true) {
        override fun run() {
            if( currentContext.invokedSubcommand == null ) {
                DailyReportCmd().main(args)
            }
        }
    }.subcommands(DailyReportCmd()).main(args)

}