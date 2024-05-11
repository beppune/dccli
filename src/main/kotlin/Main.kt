package it.posteitaliane.dccli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellAddress
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import kotlin.io.path.Path

class DailyReportCmd : CliktCommand(name = "daily") {
    override fun run() {val content:String = Jsoup::class.java.classLoader.getResource("post_body.txt")?.readText()!!

        /*Jsoup.connect("http://10.194.137.36/ACCESSIDC/ReportGiornaliero.aspx")
            .auth { it.credentials("rete\\manzogi9", "6Krum1r1") }
            .requestBody(content)
            .method(Connection.Method.POST)
            .execute()*/

        Jsoup
            .parse(File("index.html")).select("table#ADC_ContenutoSpecificoPagina_gvGiornaliero>tbody>tr").also { trs ->

                val colmap = mapOf(
                    0 to 0, 1 to 1, //COGNOME, NOME
                    2 to 8, // SOCIETA'
                    3 to 5, 4 to 6, 5 to 7, //TIPO, NUMERO, SCADENZA DOC
                    6 to 13, 7 to 14, // DECORRENZA, SCADENZA
                    8 to 16, 9 to 11, 10 to 15, //BADGE, GRUPPO, NOTE
                    11 to 9, 12 to 12,    // STRUTTURA, PROFILO
                    13 to 4, 14 to 3, 15 to 2,    // CF, DATA NASCITA, NAZIONALITA'
                    20 to 10, 21 to 12 //DATACENTER, LOCALI
                )

                val headers = trs.first()

                val rows = headers.nextElementSiblings()

                val wb = WorkbookFactory.create( this::class.java.classLoader.getResourceAsStream("report_template.xls") )

                val sheet = wb.getSheetAt(0)

                var currentColumn = 1
                rows.forEach { tr ->
                    tr.select("td")
                        .eachText().also { row ->
                            val col = currentColumn

                            colmap.forEach { from, to ->
                                val cell = sheet.getRow(from).getCell(col)

                                cell.setCellValue( row[to] )
                            }

                            sheet.getRow(16).getCell(col).setCellValue("VERO")

                            currentColumn++
                        }
                }

                val output = FileOutputStream("result.xls")
                wb.write(output)
                wb.close()

            }
    }
}

fun main(args: Array<String>) {

    object : CliktCommand(name = "dccli") {
        override fun run() = Unit
    }.subcommands(DailyReportCmd()).main(args)

}