package com.capstone.nutrisee.helper

import android.content.Context
import android.graphics.Bitmap
import com.capstone.nutrisee.ml.Detector
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class ImageClassifierHelper(private val context: Context) {

    private fun setupImageClassifier(): Detector {
        return Detector.newInstance(context)
    }

    private fun loadLabels(): List<String> {
        val labels = mutableListOf<String>()
        val inputStream = context.assets.open("labels.txt")
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { labels.add(it) }
        }
        return labels
    }

    fun classifyStaticImage(bitmap: Bitmap): List<Pair<String, Float>> {
        val model = setupImageClassifier()
        val labels = loadLabels()

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 480, 480, true)
        val image = TensorImage.fromBitmap(resizedBitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(480, 480, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        val processedImage = imageProcessor.process(image)

        val outputs = model.process(processedImage)

        val outputCategories = outputs.outputAsCategoryList

        println("Output categories size: ${outputCategories.size}")
        outputCategories.forEachIndexed { index, category ->
            println("Category $index: ${category.label} - ${category.score}")
        }

        model.close()

        return outputCategories.mapIndexed { index, category ->
            val label = if (index < labels.size) labels[index] else "Unknown"
            Pair(label, category.score)
        }.sortedByDescending { it.second }
    }

}
