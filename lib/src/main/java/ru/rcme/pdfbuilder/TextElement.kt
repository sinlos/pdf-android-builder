package ru.rcme.pdfbuilder

import ru.rcme.pdfbuilder.style.Alignment
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream

class TextElement(parent: Element, val value: String = "Hello, world!") : Element(parent) {

    override fun instanceHeight(width: Float, startY: Float): Float {
        return fontHeight * wrapText(width).size
    }

    override fun renderInstance(
      pdDocument: PDDocument,
      startX: Float,
      endX: Float,
      startY: Float,
      minHeight: Float
    ) {
        val pdPage = getPage(document, pdDocument, startY)
        wrapText(endX - startX).forEachIndexed { i, line ->
            val startX = when (inheritedHorizontalAlignment) {
              Alignment.LEFT -> startX
              Alignment.RIGHT -> endX - line.width()
              Alignment.CENTER -> startX + (endX - startX - line.width()) / 2f
            }

            PDPageContentStream(pdDocument, pdPage, true, true).use { stream ->
                stream.beginText()
                stream.setFont(inheritedPdFont, inheritedFontSize)
                stream.setNonStrokingColor(inheritedFontColor)
                stream.newLineAtOffset(
                  startX,
                  (transformY(document, startY) - fontHeight) - fontHeight * i
                )
                stream.showText(line)
                stream.endText()
            }
        }
    }

    fun wrapText(width: Float): List<String> {
        val lines = mutableListOf<String>()
        value.splitToSequence(" ").forEach { word ->
            if (word.width() > width) {
                // skip word for now
//                throw UnsupportedOperationException("TODO: wrap word by character for this case")
            } else if (lines.isEmpty()) {
                lines.add(word)
            } else if ((lines.last() + ' ' + word).width() > width) {
                lines.add(word)
            } else {
                lines[lines.size - 1] = lines.last() + ' ' + word
            }
        }
        return lines
    }

    private fun String.width() = inheritedPdFont.getStringWidth(this) * inheritedFontSize / 1000

    private val fontHeight: Float
        get() = inheritedPdFont.boundingBox.height * inheritedFontSize / 1000f

}
