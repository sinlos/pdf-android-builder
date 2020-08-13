package ru.rcme.pdfbuilder

import android.graphics.Bitmap
import ru.rcme.pdfbuilder.style.Alignment
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject

class ImageElement(
    parent: Element,
    private val imagePath: String = "",
    private val bufferedImage: Bitmap? = null
) :
    Element(parent) {

    var imgHeight: Int? = null
    var imgWidth: Int? = null

    override fun instanceHeight(width: Float, startY: Float): Float {
        return imgHeight?.toFloat() ?: 0F
    }

    override fun renderInstance(
      pdDocument: PDDocument,
      startX: Float,
      endX: Float,
      startY: Float,
      minHeight: Float
    ) {

        val pdImage = if (bufferedImage != null) JPEGFactory.createFromImage(
          pdDocument, bufferedImage
        )
        else PDImageXObject.createFromFile(imagePath, pdDocument)

        imgHeight = imgHeight ?: pdImage.height
        imgWidth = imgWidth ?: pdImage.width

        val pdPage = getPage(document, pdDocument, startY + imgHeight!!)

        val realStartX = when (inheritedHorizontalAlignment) {
          Alignment.LEFT -> startX
          Alignment.RIGHT -> endX - imgWidth!!
          Alignment.CENTER -> startX + (endX - startX - imgWidth!!) / 2f
        }
        val transformedY = transformY(document, startY) - imgHeight!!
        PDPageContentStream(pdDocument, pdPage, true, true).use { stream ->
            stream.drawImage(
              pdImage,
              realStartX,
              transformedY,
              imgWidth!!.toFloat(),
              imgHeight!!.toFloat()
            )
        }
    }
}
