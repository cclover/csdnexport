package com.cc.csdnexport

import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.csdn.CSDNExporter

fun main(args: Array<String>) {

    if (ExportConfigManager.parserConfig()) {
        CSDNExporter().main()
    } else {
        println("Failed to parse the config file!!")
    }
}