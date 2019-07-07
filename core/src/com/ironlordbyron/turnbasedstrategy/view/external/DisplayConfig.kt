package com.megacrit.cardcrawl.core

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.util.ArrayList
import java.util.HashMap
import java.util.Scanner

import com.sun.corba.se.impl.presentation.rmi.ExceptionHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class DisplayConfig (val width: Int, val height: Int, val maxFPS: Int, val isFullscreen: Boolean, val wfs: Boolean, val isVsync: Boolean) {

    override fun toString(): String {
        val hm = HashMap<String, Any>()
        hm.put("width", Integer.valueOf(this.width))
        hm.put("height", Integer.valueOf(this.height))
        hm.put("fps_limit", Integer.valueOf(this.maxFPS))
        hm.put("isFullscreen", java.lang.Boolean.valueOf(this.isFullscreen))
        hm.put("wfs", java.lang.Boolean.valueOf(this.wfs))
        hm.put("vsync", java.lang.Boolean.valueOf(this.isVsync))
        return hm.toString()
    }

    companion object {
        private val logger = LogManager.getLogger(DisplayConfig::class.java.name)
        private val DISPLAY_CONFIG_LOC = "info.displayconfig"
        private val DEFAULT_W = 1280
        private val DEFAULT_H = 720
        private val DEFAULT_FPS_LIMIT = 60
        private val DEFAULT_FS = false
        private val DEFAULT_WFS = false
        private val DEFAULT_VSYNC = true

        fun readConfig(): DisplayConfig {
            logger.info("Reading info.displayconfig")
            val configLines = readDisplayConfFile()
            if (configLines.size < 4) {
                createNewConfig()
                return readConfig()
            }
            if (configLines.size == 5) {
                appendFpsLimit(configLines)
                return readConfig()
            }
            val dc: DisplayConfig
            try {
                dc = DisplayConfig(Integer.parseInt(configLines[0].trim { it <= ' ' }), Integer.parseInt(configLines[1].trim { it <= ' ' }), Integer.parseInt(configLines[2].trim { it <= ' ' }), java.lang.Boolean.parseBoolean(configLines[3].trim { it <= ' ' }), java.lang.Boolean.parseBoolean(configLines[4].trim { it <= ' ' }), java.lang.Boolean.parseBoolean(configLines[5].trim { it <= ' ' }))
            } catch (e: Exception) {
                logger.info("Failed to parse the info.displayconfig going to recreate it with defaults.")
                createNewConfig()
                return readConfig()
            }

            logger.info("DisplayConfig successfully read.")
            return dc
        }

        private fun readDisplayConfFile(): ArrayList<String> {
            val configLines = ArrayList<String>()
            var s: Scanner? = null
            try {
                s = Scanner(File("info.displayconfig"))
                while (s.hasNextLine()) {
                    configLines.add(s.nextLine())
                }
            } catch (e: FileNotFoundException) {
                logger.info("File info.displayconfig not found, creating with defaults.")

                createNewConfig()
                return readDisplayConfFile()
            } finally {
                s?.close()
            }
            return configLines
        }

        fun writeDisplayConfigFile(w: Int, h: Int, fps: Int, fs: Boolean, wfs: Boolean, vs: Boolean) {
            var writer: PrintWriter? = null
            try {
                writer = PrintWriter("info.displayconfig", "UTF-8")
                writer.println(Integer.toString(w))
                writer.println(Integer.toString(h))
                writer.println(Integer.toString(fps))
                writer.println(java.lang.Boolean.toString(fs))
                writer.println(java.lang.Boolean.toString(wfs))
                writer.println(java.lang.Boolean.toString(vs))
            } finally {
                writer?.close()
            }
        }

        private fun createNewConfig() {
            logger.info("Creating new config with default values...")
            writeDisplayConfigFile(1280, 720, 60, false, false, true)
        }

        private fun appendFpsLimit(configLines: ArrayList<String>) {
            logger.info("Updating config...")
            try {
                writeDisplayConfigFile(
                        Integer.parseInt(configLines[0].trim { it <= ' ' }),
                        Integer.parseInt(configLines[1].trim { it <= ' ' }), 60,

                        java.lang.Boolean.parseBoolean(configLines[2].trim { it <= ' ' }),
                        java.lang.Boolean.parseBoolean(configLines[3].trim { it <= ' ' }), true)
            } catch (e: Exception) {
                logger.info("Failed to parse the info.displayconfig going to recreate it with defaults.")

                createNewConfig()
            }

        }
    }
}
