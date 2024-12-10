package com.capstone.nutrisee.helper

import android.content.Context
import android.graphics.Bitmap
import com.capstone.nutrisee.ml.Detector
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category

class ImageClassifierHelper(private val context: Context) {

    private val model: Detector = Detector.newInstance(context)

    fun analyzeFood(bitmap: Bitmap): List<Category> {
        val image = TensorImage.fromBitmap(bitmap)

        val outputs = model.process(image)
        val detectedFoods = outputs.outputAsCategoryList

        model.close()

        return detectedFoods
    }
}
