package com.cc.csdnexport.tool

import org.apache.log4j.Logger

object LogUtils {

    internal var logger = Logger.getLogger(LogUtils::class.java)

    fun d(message: Any) {

        if (logger.isDebugEnabled) {
            logger.debug(message)
        }
    }

    fun i(message: Any) {

        if (logger.isInfoEnabled) {
            logger.info(message)
        }
    }

    fun w(message: Any) {

        logger.warn(message)
    }

    fun e(message: Any) {

        logger.error(message)
    }

}