package com.wm.dashdiary.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun HomeScreen(
    onMenuClicked:()->Unit,
    navigateToWrite:()->Unit
) {
Scaffold(
    topBar = { HomeTopBar (onMenuClicked = onMenuClicked)},
    content = {},
    floatingActionButton = {
        FloatingActionButton(
            onClick = navigateToWrite,
            Modifier.size(80.dp)) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Screen"
            )
            
        }
    }
)
}