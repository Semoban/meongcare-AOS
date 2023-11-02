package com.project.meongcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.project.meongcare.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.run {
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(1).isEnabled = false

            bottomNavigationView.run {
                setOnItemSelectedListener {
                    if(it.itemId == selectedItemId){
                        return@setOnItemSelectedListener false
                    }

                    when(it.itemId){
                        R.id.menuMainBottomNavHome ->{
                            Log.d("Test", "Home")
                        }
                        R.id.menuMainBottomNavMedicalRecord ->{
                            Log.d("Test", "Record")
                        }
                        else -> Log.d("Test", "else")
                    }

                    true
                }
            }

            floatingActionButton.setOnClickListener {
                Log.d("Test", "Fab")
            }
        }
    }
}