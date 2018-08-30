package com.cc.csdnexport.tool

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object HttpDownloader {

    fun download(path: String): String? {

        var inStream: InputStream? = null
        var outStream: OutputStream? = null
        var conn: HttpURLConnection? = null
        try {

            val url = URL(path)
            conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5 * 1000
            conn.readTimeout = 5 * 1000
            conn.requestMethod = "GET"
            inStream = conn.inputStream

            outStream = ByteArrayOutputStream()
            outStream.write(inStream.readBytes())

            return outStream.toString()

        } catch (ex: Exception) {
            ex.printStackTrace()
            LogUtils.e("download exception: $path")
            LogUtils.e(ex)
            throw ex
        } finally {
            inStream?.close()
            outStream?.close()
            conn?.disconnect()
        }
    }


    fun downloadFile(path: String, file: File): Boolean {

        var inStream: InputStream? = null
        var outStream: OutputStream? = null
        var conn: HttpURLConnection? = null
        try {

            val url = URL(path)
            conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5 * 1000
            conn.readTimeout = 5 * 1000
            conn.requestMethod = "GET"

            conn.addRequestProperty("Host", url.host)
            conn.setRequestProperty("referer", "https://${url.host}")
            conn.addRequestProperty("Connection", "keep-alive")
            conn.addRequestProperty("Cache-Control", "no-cache")
            conn.addRequestProperty("DNT", "1")
            conn.addRequestProperty("Pragma", "no-cache");
            conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            inStream = conn.inputStream
            outStream = FileOutputStream(file)
            outStream.write(inStream.readBytes())
            return true

        } catch (ex: Exception) {
            ex.printStackTrace()

            LogUtils.e("downloadFile exception: $path->${file.absolutePath}")
            LogUtils.e(ex)
        } finally {
            inStream?.close()
            outStream?.close()
            conn?.disconnect()
        }
        return false
    }


}
