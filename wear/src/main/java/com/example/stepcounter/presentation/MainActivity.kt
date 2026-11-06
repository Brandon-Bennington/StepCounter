package com.example.stepcounter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.stepcounter.presentation.theme.StepCounterTheme
import com.google.android.gms.wearable.Wearable
import android.util.Log
import com.example.stepcounter.shared.`data`.FirebaseRepository
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : ComponentActivity() {

    private var heartRate by mutableIntStateOf(72)
    private var stepsGoal by mutableIntStateOf(10000)
    private lateinit var wearDataListener: WearDataListener

    private lateinit var repository: FirebaseRepository
    private var firebaseListener: ListenerRegistration? = null

    private lateinit var heartRateSensorManager: HeartRateSensorManager


    private val heartRatePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            heartRateSensorManager.startListening()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = FirebaseRepository()

        createNotificationChannel(this)

        heartRateSensorManager = HeartRateSensorManager(
            context = this,
            onHeartRateChanged = { newHeartRate -> heartRate = newHeartRate }
        )

        if (heartRateSensorManager.hasHeartRateSensor && !heartRateSensorManager.hasPermission()) {
            heartRatePermissionLauncher.launch(heartRateSensorManager.requiredPermission)
        }

        wearDataListener = WearDataListener(onStepsGoalChanged = { newGoal ->
            Log.d("WearDataLayer", "Goal received from phone: $newGoal")
            runOnUiThread { stepsGoal = newGoal }
        })

        setContent {
            StepCounterTheme {
                WearFitnessApp(
                    heartRateSensorValue = heartRate,
                    hasHeartRateSensor = heartRateSensorManager.hasHeartRateSensor,
                    stepsGoalFromPhone = stepsGoal
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (::heartRateSensorManager.isInitialized) {
            heartRateSensorManager.startListening()
        }
        if (::wearDataListener.isInitialized) {
            Wearable.getDataClient(this).addListener(wearDataListener)
        }
        if (::repository.isInitialized) {
            startFirebaseListener()
        }
    }



    override fun onPause() {
        super.onPause()
        if (::heartRateSensorManager.isInitialized) {
            heartRateSensorManager.stopListening()
        }
        if (::wearDataListener.isInitialized) {
            Wearable.getDataClient(this).removeListener(wearDataListener)
        }
        stopFirebaseListener()
    }


    private fun startFirebaseListener() {
        if (firebaseListener != null) return
        firebaseListener = repository.listenToFitnessData(
            onDataChanged = { fitnessData ->
                Log.d("SharedFirebaseWear", "Goal received from Firebase: ${fitnessData.dailyGoal}")
                runOnUiThread { stepsGoal = fitnessData.dailyGoal.toInt() }
            },
            onError = { exception ->
                Log.e("SharedFirebaseWear", "Firebase listener error", exception)
            }
        )
    }

    private fun stopFirebaseListener() {
        firebaseListener?.remove()
        firebaseListener = null
    }

}