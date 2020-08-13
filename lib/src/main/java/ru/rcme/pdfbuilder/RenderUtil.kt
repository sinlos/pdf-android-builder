package ru.rcme.pdfbuilder

import ru.rcme.pdfbuilder.style.Orientation
import com.tom_roush.harmony.awt.AWTColor
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.util.Matrix

/**
 * Draws a line from (startX, startY) to (endX, endY) with given color and width. The line must
 * not cross page boundaries.
 */
fun drawLine(
    document: Document,
    pdDocument: PDDocument,
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    width: Float,
    color: AWTColor
) {
    if (width == 0f) {
        return
    } else if (width < 0f) {
        throw IllegalArgumentException("drawLine() called with negative width")
    }

    val pdPage = getPage(document, pdDocument, endY)
    PDPageContentStream(pdDocument, pdPage, true, true).use { stream ->
        val offset = width / 2f
        stream.setStrokingColor(color)
        stream.setLineWidth(width)
        stream.moveTo(startX, transformY(document, startY) - offset)
        stream.lineTo(endX, transformY(document, endY) - offset)
        stream.stroke()
    }
}

/**
 * Draws the box whose opposite corners are (startX, startY) and (endX, endY). The box must
 * not cross page boundaries.
 */
fun drawBox(
    document: Document,
    pdDocument: PDDocument,
    startX: Float,
    endX: Float,
    startY: Float,
    endY: Float,
    color: AWTColor
) {
    val width = endX - startX
    val height = endY - startY
    if (width <= 0 || height <= 0) {
        return
    }
    val pdPage = getPage(document, pdDocument, endY)
    PDPageContentStream(pdDocument, pdPage, false, true).use { stream ->
        stream.setNonStrokingColor(color)
        when (document.orientation) {
            Orientation.LANDSCAPE -> {
                val point = Matrix(0f, 1f, -1f, 0f, document.pdRectangle.width, 0f)
                    .transformPoint(startX.toDouble(), transformY(document, startY).toDouble())
                @Suppress("NamedArgsPositionMismatch")
                stream.addRect(point.x, point.y, height, width) // ?
            }
            Orientation.PORTRAIT -> stream.addRect(
                startX,
                transformY(document, endY),
                width,
                height
            )
        }
        stream.fill()
    }
}

fun getPageIndex(pageHeight: Float, y: Float): Int {
    var y = y
    var i = 0
    while (y >= pageHeight) {
        y -= pageHeight
        i++
    }
    return i
}

/**
 * Safely gets (or creates) the page containing endY
 */
fun getPage(document: Document, pdDocument: PDDocument, endY: Float): PDPage {
    val pageNumber: Int = getPageIndex(document.pageHeight, endY)
    var pdPage: PDPage? = pdDocument.pages.elementAtOrNull(pageNumber)
    if (pdPage == null) {
        pdPage = PDPage(document.pdRectangle)
        if (document.orientation == Orientation.LANDSCAPE) {
            pdPage.rotation = 90
            // Add transform matrix to page to keep (0, 0) as bottom left corner
            PDPageContentStream(pdDocument, pdPage).use { stream ->
                stream.transform(Matrix(0f, 1f, -1f, 0f, document.pdRectangle.width, 0f))
            }
        }
        pdDocument.addPage(pdPage)
    }
    return pdPage
}

/**
 * Transforms endY from pdf-builder space to Apache pdf-box space (relative
 * to bottom of current page).
 */
fun transformY(document: Document, y: Float): Float {
    return document.pageHeight - (y - document.pageHeight * getPageIndex(document.pageHeight, y))
}


