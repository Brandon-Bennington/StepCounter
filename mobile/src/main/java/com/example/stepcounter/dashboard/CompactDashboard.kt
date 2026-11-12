package com.example.stepcounter.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme.typography

@Composable
fun CompactDashboard(
    stepsGoal: Int,
    sendStatus: String,
    layoutLabel: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSendToWatch: () -> Unit,
    onSendToFirebase: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all=24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Layout: $layoutLabel",
            style = typography.labelLarge
        )
        GoalStepper(
            stepsGoal = stepsGoal,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            largeNumber = false
        )
        SyncActions(
            sendStatus = sendStatus,
            onSendToWatch = onSendToWatch,
            onSaveToFirebase = onSendToFirebase,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )
    }
}

