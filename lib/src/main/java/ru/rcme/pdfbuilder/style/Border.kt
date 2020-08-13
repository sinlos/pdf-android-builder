package ru.rcme.pdfbuilder.style

import ru.rcme.pdfbuilder.Document
import ru.rcme.pdfbuilder.drawLine
import com.tom_roush.harmony.awt.AWTColor
import com.tom_roush.pdfbox.pdmodel.PDDocument

data class Border(
    val top: Float = 0f,
    val right: Float = 0f,
    val bottom: Float = 0f,
    val left: Float = 0f,
    val topColor: AWTColor = AWTColor.BLACK,
    val rightColor: AWTColor = AWTColor.BLACK,
    val bottomColor: AWTColor = AWTColor.BLACK,
    val leftColor: AWTColor = AWTColor.BLACK) {

    companion object {
        @JvmStatic
        val ZERO = Border()

        @JvmStatic
        val HALF = Border(.5f, .5f, .5f, .5f)

        @JvmStatic
        val ONE = Border(1f, 1f, 1f, 1f)
    }

    constructor(
        top: Float = 0f,
        right: Float = 0f,
        bottom: Float = 0f,
        left: Float = 0f,
        color: AWTColor = AWTColor.BLACK
    ) : this(top, right, bottom, left, color, color, color, color)

    constructor(
        width: Float = 0f,
        color: AWTColor = AWTColor.BLACK
    ) : this(width, width, width, width, color)

    /**
     * Given a box defined by the opposite corners (startX, startY) and (endX, endY), draws
     * the surrounding border.
     */
    @Suppress("NamedArgsPositionMismatch")
    fun drawBorder(
        document: Document,
        pdDocument: PDDocument,
        startX: Float,
        endX: Float,
        startY: Float,
        endY: Float
    ) {
        drawLine(document, pdDocument, startX, startY, endX, startY, top, topColor)
        drawLine(document, pdDocument, endX, startY, endX, endY, right, rightColor)
        drawLine(document, pdDocument, endX, endY, startX, endY, bottom, bottomColor)
        drawLine(document, pdDocument, startX, endY, startX, startY, left, leftColor)
    }

}
