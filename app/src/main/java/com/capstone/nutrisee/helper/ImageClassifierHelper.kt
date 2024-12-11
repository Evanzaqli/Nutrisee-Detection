package com.capstone.nutrisee.helper

import android.content.Context
import android.graphics.Bitmap
import com.capstone.nutrisee.ml.Detector
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class ImageClassifierHelper(context: Context) {

    private val model: Detector = Detector.newInstance(context)

    fun analyzeFood(bitmap: Bitmap): NutritionResult {
        val tensorImage = TensorImage.fromBitmap(bitmap)

        val outputs = model.process(tensorImage)
        val output = outputs.outputAsTensorBuffer

        val result = parseOutput(output)

        model.close()

        return result
    }

    private fun parseOutput(output: TensorBuffer): NutritionResult {
        val outputArray = output.floatArray

        return NutritionResult(
            protein = outputArray[0],
            carbohydrate = outputArray[1],
            fat = outputArray[2],
            fiber = outputArray[3],
            calories = outputArray[4]
        )
    }
}

data class NutritionResult(
    val protein: Float,
    val carbohydrate: Float,
    val fat: Float,
    val fiber: Float,
    val calories: Float
)