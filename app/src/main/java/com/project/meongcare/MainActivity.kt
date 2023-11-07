package com.project.meongcare

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.project.meongcare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.run {
            bottomNavigationViewMain.background = null
            bottomNavigationViewMain.menu.getItem(1).isEnabled = false

            bottomNavigationViewMain.run {
                setOnItemSelectedListener {
                    if (it.itemId == selectedItemId) {
                        return@setOnItemSelectedListener false
                    }

                    when (it.itemId) {
                        R.id.menuMainBottomNavHome -> {
                            Log.d("Test", "Home")
                        }

                        R.id.menuMainBottomNavMedicalRecord -> {
                            Log.d("Test", "Record")
                        }

                        else -> Log.d("Test", "else")
                    }

                    true
                }
            }

            fabMain.setOnClickListener {
                if (overlayLayout.visibility == View.GONE) {
                    overlayLayout.visibility = View.VISIBLE
                    floatingButtonOutAnimation(activityMainBinding)
                }
            }

            fabCancel.setOnClickListener {
                floatingButtonInAnimation(activityMainBinding)
                overlayLayout.visibility = View.GONE
            }
        }
    }

    // fab menu 나타나는 애니메이션
    private fun floatingButtonOutAnimation(binding: ActivityMainBinding) {
        // 체중 fab
        val weightTxOut = PropertyValuesHolder.ofFloat("translationX", -330f)
        val weightTyOut = PropertyValuesHolder.ofFloat("translationY", -150f)
        ObjectAnimator.ofPropertyValuesHolder(binding.fabWeight, weightTxOut, weightTyOut)
            .setDuration(500).start()

        // 체중 txt
        ObjectAnimator.ofPropertyValuesHolder(
            binding.textViewWeight,
            weightTxOut,
            PropertyValuesHolder.ofFloat("translationY", -70f),
        ).setDuration(500).start()

        // 사료 fab
        val foodTxOut = PropertyValuesHolder.ofFloat("translationX", 330f)
        val foodTyOut = PropertyValuesHolder.ofFloat("translationY", -150f)
        ObjectAnimator.ofPropertyValuesHolder(binding.fabFood, foodTxOut, foodTyOut)
            .setDuration(500).start()

        // 사료 txt
        ObjectAnimator.ofPropertyValuesHolder(
            binding.textViewFood,
            foodTxOut,
            PropertyValuesHolder.ofFloat("translationY", -70f),
        ).setDuration(500).start()

        // 이상증상 fab
        ObjectAnimator.ofFloat(binding.fabSymptom, "translationY", -550f).setDuration(500).start()

        // 이상증상 txt
        ObjectAnimator.ofFloat(binding.textViewSymptom, "translationY", -470f).setDuration(500)
            .start()

        // 영양제 fab
        val nutritionTxOut = PropertyValuesHolder.ofFloat("translationX", -250f)
        val nutritionTyOut = PropertyValuesHolder.ofFloat("translationY", -400f)
        ObjectAnimator.ofPropertyValuesHolder(binding.fabNutrition, nutritionTxOut, nutritionTyOut)
            .setDuration(500).start()

        // 영양제 txt
        ObjectAnimator.ofPropertyValuesHolder(
            binding.textViewNutrition,
            nutritionTxOut,
            PropertyValuesHolder.ofFloat("translationY", -320f),
        ).setDuration(500).start()

        // 대소변 fab
        val fecesTxOut = PropertyValuesHolder.ofFloat("translationX", 250f)
        val fecesTyOut = PropertyValuesHolder.ofFloat("translationY", -400f)
        ObjectAnimator.ofPropertyValuesHolder(binding.fabFeces, fecesTxOut, fecesTyOut)
            .setDuration(500).start()

        // 대소변 txt
        ObjectAnimator.ofPropertyValuesHolder(
            binding.textViewFeces,
            fecesTxOut,
            PropertyValuesHolder.ofFloat("translationY", -320f),
        ).setDuration(500).start()
    }

    // fab menu 없어지는 애니메이션
    private fun floatingButtonInAnimation(binding: ActivityMainBinding) {
        val txIn = PropertyValuesHolder.ofFloat("translationX", 0f)
        val tyIn = PropertyValuesHolder.ofFloat("translationY", 0f)

        // 체중 fab
        ObjectAnimator.ofPropertyValuesHolder(binding.fabWeight, txIn, tyIn).setDuration(500)
            .start()

        // 체중 txt
        ObjectAnimator.ofPropertyValuesHolder(binding.textViewWeight, txIn, tyIn).setDuration(500)
            .start()

        // 사료 fab
        ObjectAnimator.ofPropertyValuesHolder(binding.fabFood, txIn, tyIn).setDuration(500).start()

        // 사료 txt
        ObjectAnimator.ofPropertyValuesHolder(binding.textViewFood, txIn, tyIn).setDuration(500)
            .start()

        // 이상증상 fab
        ObjectAnimator.ofFloat(binding.fabSymptom, "translationY", 0f).setDuration(500).start()

        // 이상증상 txt
        ObjectAnimator.ofFloat(binding.textViewSymptom, "translationY", 0f).setDuration(500).start()

        // 영양제 fab
        ObjectAnimator.ofPropertyValuesHolder(binding.fabNutrition, txIn, tyIn).setDuration(500)
            .start()

        // 영양제 txt
        ObjectAnimator.ofPropertyValuesHolder(binding.textViewNutrition, txIn, tyIn)
            .setDuration(500).start()

        // 대소변 fab
        ObjectAnimator.ofPropertyValuesHolder(binding.fabFeces, txIn, tyIn).setDuration(500).start()

        // 대소변 txt
        ObjectAnimator.ofPropertyValuesHolder(binding.textViewFeces, txIn, tyIn).setDuration(500)
            .start()
    }
}
