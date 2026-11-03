package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class Localization1To1Test {

    private fun extractKeys(file: File): Set<String> {
        assertTrue("String resource file must exist: ${file.path}", file.exists())
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(file)
        doc.documentElement.normalize()

        val stringNodes = doc.getElementsByTagName("string")
        val keys = mutableSetOf<String>()
        for (i in 0 until stringNodes.length) {
            val node = stringNodes.item(i)
            val nameAttr = node.attributes.getNamedItem("name")?.nodeValue
            if (nameAttr != null) {
                keys.add(nameAttr)
            }
        }
        return keys
    }

    @Test
    fun testLocalizationKeysAre1To1() {
        val plFile = File("src/main/res/values/strings.xml")
        val enFile = File("src/main/res/values-en/strings.xml")
        val deFile = File("src/main/res/values-de/strings.xml")

        val plKeys = extractKeys(plFile)
        val enKeys = extractKeys(enFile)
        val deKeys = extractKeys(deFile)

        val missingInEn = plKeys - enKeys
        val extraInEn = enKeys - plKeys

        val missingInDe = plKeys - deKeys
        val extraInDe = deKeys - plKeys

        val errorMessage = StringBuilder()
        if (missingInEn.isNotEmpty()) errorMessage.append("\nMissing in EN: $missingInEn")
        if (extraInEn.isNotEmpty()) errorMessage.append("\nExtra in EN: $extraInEn")
        if (missingInDe.isNotEmpty()) errorMessage.append("\nMissing in DE: $missingInDe")
        if (extraInDe.isNotEmpty()) errorMessage.append("\nExtra in DE: $extraInDe")

        assertEquals(
            "Localization keys must be exactly 1:1 matching across PL, EN, DE!$errorMessage",
            0,
            missingInEn.size + extraInEn.size + missingInDe.size + extraInDe.size
        )
    }
}
