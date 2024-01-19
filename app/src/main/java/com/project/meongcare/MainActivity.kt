package com.project.meongcare

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.project.meongcare.databinding.ActivityMainBinding
import com.project.meongcare.login.model.data.local.UserPreferences
import com.project.meongcare.symptom.viewmodel.SymptomViewModel
import com.project.meongcare.toolbar.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var toolbarViewModel: ToolbarViewModel
    companion object {
        const val BASE_URL = "https://dev.meongcare.com/"
    }

    @Inject
    lateinit var userPreferences: UserPreferences

    val permissionList =
        arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        requestPermissions(permissionList, 0)
        initNavController()

        toolbarViewModel = ViewModelProvider(this)[ToolbarViewModel::class.java]

        activityMainBinding.run {
            autoLogin()

            bottomNavigationViewMain.background = null
            bottomNavigationViewMain.menu.getItem(1).isEnabled = false

            bottomNavigationViewMain.run {
                setOnItemSelectedListener {
                    if (it.itemId == selectedItemId) {
                        return@setOnItemSelectedListener false
                    }

                    when (it.itemId) {
                        R.id.menuMainBottomNavHome -> {
                            fragmentContainerView.findNavController().navigate(R.id.homeFragment)
                        }
                        else -> {
                            includeMedicalRecordDialog.root.visibility = View.VISIBLE
                        }
                    }

                    true
                }
                setOnItemReselectedListener { menuItem ->
                    if (menuItem.itemId == R.id.menuMainBottomNavHome) {
                        fragmentContainerView.findNavController().navigate(R.id.homeFragment)
                    } else {
                        includeMedicalRecordDialog.root.visibility = View.VISIBLE
                    }
                }
            }

            includeMedicalRecordDialog.run {
                constraintlayoutBg.setOnClickListener {
                    includeMedicalRecordDialog.root.visibility = View.VISIBLE
                }
                buttonOk.setOnClickListener {
                    includeMedicalRecordDialog.root.visibility = View.GONE
                }
            }

            fabMain.setOnClickListener {
                if (overlayLayout.visibility == View.GONE) {
                    overlayLayout.visibility = View.VISIBLE
                    floatingButtonMenuOut(activityMainBinding)
                }
            }

            fabCancel.setOnClickListener {
                CoroutineScope(Main).launch {
                    floatingButtonMenuIn(activityMainBinding)
                    delay(550L)
                    overlayLayout.visibility = View.GONE
                }
            }

            menuWeightEdit.setOnClickListener {
                CoroutineScope(Main).launch {
                    floatingButtonMenuIn(activityMainBinding)
                    delay(550L)
                    overlayLayout.visibility = View.GONE
                    fragmentContainerView.findNavController().navigate(R.id.weightFragment)
                }
            }

            menuNutritionAdd.setOnClickListener {
                CoroutineScope(Main).launch {
                    floatingButtonMenuIn(activityMainBinding)
                    delay(550L)
                    overlayLayout.visibility = View.GONE
                    fragmentContainerView.findNavController().navigate(R.id.supplementFragment)
                }
            }

            menuSymptomAdd.setOnClickListener {
                CoroutineScope(Main).launch {
                    floatingButtonMenuIn(activityMainBinding)
                    delay(550L)
                    overlayLayout.visibility = View.GONE
                    fragmentContainerView.findNavController().navigate(R.id.symptomFragment)
                }
            }

            menuFecesAdd.setOnClickListener {
                CoroutineScope(Main).launch {
                    floatingButtonMenuIn(activityMainBinding)
                    delay(550L)
                    overlayLayout.visibility = View.GONE
                    includeMedicalRecordDialog.run {
                        root.visibility = View.VISIBLE
                        constraintlayoutBg.setOnClickListener { includeMedicalRecordDialog.root.visibility = View.GONE }
                        buttonOk.setOnClickListener { includeMedicalRecordDialog.root.visibility = View.GONE }
                    }
                    // fragmentContainerView.findNavController().navigate(R.id.excretaFragment)
                }
            }

            menuDogFoodAdd.setOnClickListener {
                CoroutineScope(Main).launch {
                    floatingButtonMenuIn(activityMainBinding)
                    delay(550L)
                    overlayLayout.visibility = View.GONE
                    fragmentContainerView.findNavController().navigate(R.id.feedFragment)
                }
            }

            overlayLayout.setOnClickListener {
                it.visibility = View.GONE
            }
        }
    }

    // fab menu 생기는 애니메이션
    fun floatingButtonMenuOut(binding: ActivityMainBinding) {
        val animationOut1 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_dog_food_animator).apply {
                setTarget(binding.menuDogFoodAdd)
            }
        val animationOut2 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_feces_animator).apply {
                setTarget(binding.menuFecesAdd)
            }
        val animationOut3 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_symptom_animator).apply {
                setTarget(binding.menuSymptomAdd)
            }
        val animationOut4 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_nutrition_animator).apply {
                setTarget(binding.menuNutritionAdd)
            }
        val animationOut5 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_weight_animator).apply {
                setTarget(binding.menuWeightEdit)
            }
        val animationOutSet =
            AnimatorSet().apply {
                play(animationOut1).with(animationOut2).with(animationOut3).with(animationOut4).with(animationOut5)
            }

        animationOutSet.start()
    }

    // fab menu 없어지는 애니메이션
    fun floatingButtonMenuIn(binding: ActivityMainBinding) {
        val animationIn1 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_fade_out_animator).apply {
                setTarget(binding.menuDogFoodAdd)
            }
        val animationIn2 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_fade_out_animator).apply {
                setTarget(binding.menuFecesAdd)
            }
        val animationIn3 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_fade_out_animator).apply {
                setTarget(binding.menuSymptomAdd)
            }
        val animationIn4 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_fade_out_animator).apply {
                setTarget(binding.menuNutritionAdd)
            }
        val animationIn5 =
            AnimatorInflater.loadAnimator(this@MainActivity, R.animator.menu_fade_out_animator).apply {
                setTarget(binding.menuWeightEdit)
            }
        val animationInSet =
            AnimatorSet().apply {
                play(animationIn1).with(animationIn2).with(animationIn3).with(animationIn4).with(animationIn5)
            }

        animationInSet.start()
    }

    fun autoLogin() {
        lifecycleScope.launch {
            lifecycleScope.launch {
                val accessToken = userPreferences.getAccessToken()
                val refreshToken = userPreferences.getRefreshToken()

                if (accessToken.isNullOrEmpty() && refreshToken.isNullOrEmpty()) {
                    activityMainBinding.fragmentContainerView.findNavController().navigate(R.id.onBoardingFragment)
                } else if (accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                    activityMainBinding.fragmentContainerView.findNavController().navigate(R.id.loginFragment)
                } else {
                    activityMainBinding.fragmentContainerView.findNavController().navigate(R.id.homeFragment)
                }
            }
        }
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // 바텀 네비게이션의 표시 여부를 한 번에 관리
        activityMainBinding.bottomNavigationViewMain.apply {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment,
                    R.id.medicalRecordFragment,
                    R.id.excretaFragment,
                    R.id.weightFragment,
                    R.id.feedFragment,
                    R.id.symptomFragment,
                    R.id.supplementFragment,
                    -> {
                        activityMainBinding.bottomNavLayout.apply {
                            alpha = 0f
                            visibility = View.VISIBLE
                            // 바텀 네비게이션 UI가 갑자기 나타나고 사라지는 현상을 부드럽게 처리하기 위한 애니메이션
                            animate().alpha(1f).setDuration(100).start()
                        }
                    }

                    else -> {
                        activityMainBinding.bottomNavLayout.visibility = View.GONE
                    }
                }
            }
        }
    }
}
