package com.project.meongcare

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.project.meongcare.databinding.ActivityMainBinding
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.login.view.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    @Inject
    lateinit var userPreferences: UserPreferences

    companion object{
        val LOGIN_FRAGMENT = "LoginFragment"
        val HOME_FRAGMENT = "HomeFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.run {
            replaceFragment(LOGIN_FRAGMENT, false, true, null)
//            autoLogin()

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

            overlayLayout.setOnClickListener {
                it.visibility = View.GONE
            }
        }
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name:String, addToBackStack:Boolean, animate:Boolean, bundle:Bundle?){
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // 새로운 Fragment를 담을 변수
        var newFragment = when(name){
            LOGIN_FRAGMENT -> LoginFragment()
            HOME_FRAGMENT -> HomeFragment()
            else -> Fragment()
        }

        newFragment.arguments = bundle

        if(newFragment != null) {
            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.fragmentContainerView, newFragment)

            if (animate == true) {
                // 애니메이션을 설정한다.
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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

    fun detachBottomNav(){
        activityMainBinding.bottomNavLayout.visibility = View.GONE
    }
    fun attachBottomNav(){
        activityMainBinding.bottomNavLayout.visibility = View.VISIBLE
    }

    fun autoLogin(){
        lifecycleScope.launch {
            userPreferences.email.collect { email ->
                if(email == null){
                    replaceFragment(LOGIN_FRAGMENT, false, true, null)
                }
                else{
                    replaceFragment(HOME_FRAGMENT, false, true, null)
                }
            }
        }
    }
}
