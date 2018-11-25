package com.ironlordbyron.turnbasedstrategy.view.tiledutils.xml

import com.badlogic.gdx.Gdx
import com.fasterxml.jackson.xml.JacksonXmlModule
import com.fasterxml.jackson.xml.XmlMapper
import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty
import com.fasterxml.jackson.xml.annotate.JacksonXmlElementWrapper
import org.w3c.dom.NodeList
import java.io.FileInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


/**
 * Created by Aaron on 3/26/2018.
 */
class TilemapXmlProcessor {

    val sourceNameToFile = mapOf("Player0" to "Player0.tsx",
            "red_tile" to "tilehighlights/red_tile.tsx",
            "blue_tile" to "tilehighlights/blue_tile.tsx",
            "green_tile" to "tilehighlights/green_tile.tsx",
            "Player1" to "Player1.tsx")

    fun getTilesetFirstgid(tilemapPath: String, tilesetName: String): String {
        val tilesets = getTilemapXmlAttributes(tilemapPath)

        val sourceFileName = sourceNameToFile[tilesetName] ?: throw IllegalStateException("Attempted to pull nonexistent tile set name: $tilesetName , " +
                "please make sure it's added to XmlParser.kt" )
        return tilesets.first { it.source == sourceFileName }.firstgid // TODO
    }

    fun getTilemapXmlAttributes(tilemapPath: String): ArrayList<TilesetXmlAttributes> {
        val file = Gdx.files.internal(tilemapPath)
        val fileIS = file.read();
        val builderFactory = DocumentBuilderFactory.newInstance();
        val builder = builderFactory.newDocumentBuilder();
        val xmlDocument = builder.parse(fileIS);
        val xPath = XPathFactory.newInstance().newXPath();
        val expression = "//tileset";
        val nodeList = xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET) as NodeList;
        val tilesetsXml = ArrayList<TilesetXmlAttributes>();
        for (nodeIndex in 0 until nodeList.length) {
            val node = nodeList.item(nodeIndex)
            val firstgid = node.attributes.getNamedItem("firstgid")
            val source = node.attributes.getNamedItem("source")
            tilesetsXml.add(TilesetXmlAttributes(firstgid.nodeValue, source.nodeValue))
        }
        return tilesetsXml
    }
}


data class TilesetXmlAttributes(
        var firstgid: String,
        var source: String)

