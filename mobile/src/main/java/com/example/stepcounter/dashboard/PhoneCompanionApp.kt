package com.example.stepcounter.dashboard

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.stepcounter.sendStepsGoalToWatch
import com.example.stepcounter.shared.data.FirebaseRepository

@Composable
fun PhoneCompanionApp(
    repository: FirebaseRepository,
    widthSizeClass: WindowWidthSizeClass
){
    val context = LocalContext.current
    var stepsGoal by remember { mutableIntStateOf(10000) }
    var sendStatus by remember { mutableStateOf("Not sent") }

    DisposableEffect(repository) {
        val listenerRegistration = repository.listenToFitnessData(
            onDataChanged = { fitnessData ->
                stepsGoal = fitnessData.dailyGoal.toInt()
                sendStatus = "Goal received from Firebase: $stepsGoal"
            },
            onError = { exception ->
                sendStatus = "Firebase listener error: " + (exception.message ?: "Unknown error")
            }
        )
        onDispose { listenerRegistration.remove() }
    }

    val onDecrease = {
        if (stepsGoal > 500) stepsGoal -= 500
    }
    val onIncrease = { stepsGoal += 500 }
    val onSendToWatch = {
        sendStatus = "Sending..."
        sendStepsGoalToWatch(
            context = context,
            stepsGoal = stepsGoal,
            onSuccess = { sendStatus = "Sent $stepsGoal to watch" },
            onError = { message -> sendStatus = "Error: $message" }
        )
    }
    val onSaveToFirebase = {
        sendStatus = "Saving to Firebase..."
        repository.updateDailyGoal(
            dailyGoal = stepsGoal.toLong(),
            onSuccess = { sendStatus = "Saved to Firebase" },
            onError = { exception
                -> sendStatus = "Firebase error: " +
                (exception.message ?: "Unknown error")}
        )
    }

    val layoutLabel = widthSizeClass.label()

    if (widthSizeClass.isWide()){
        WideDashboard(
            stepsGoal = stepsGoal,
            sendStatus = sendStatus,
            layoutLabel = layoutLabel,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            onSendToWatch = onSendToWatch,
            onSendToFirebase = onSaveToFirebase
        )
    }
}