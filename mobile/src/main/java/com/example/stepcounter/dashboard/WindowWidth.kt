package com.example.stepcounter.dashboard

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

fun WindowWidthSizeClass.isWide(): Boolean {
    return this != WindowWidthSizeClass.Compact
}

fun WindowWidthSizeClass.label(): String {
    return when (this) {
        WindowWidthSizeClass.Compact -> "Compact"
        WindowWidthSizeClass.Medium -> "Medium"
        else -> "Expanded"
    }
}