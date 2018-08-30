package com.cc.csdnexport

import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.csdn.CSDNExporter
import com.cc.csdnexport.tool.LogUtils


fun main(args: Array<String>) {

    LogUtils.d("Start CSDN Export to Wordpress!")

    if (ExportConfigManager.parserConfig()) {
        CSDNExporter().main()
    } else {
        LogUtils.e("Failed to parse the config file!!")
    }
}