package com.wm.dashdiary.presentation.screens.write

import DisplayAlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.wm.dashdiary.model.Diary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTopBar(
    selectDiary: Diary?,
    onBackPressed: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    CenterAlignedTopAppBar(navigationIcon = {
        IconButton(onClick = onBackPressed) {
            Icon(
                imageVector = Icons.Default.ArrowBack, contentDescription = "Back Arrow Icon"
            )
        }
    }, title = {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(), text = "Happy", style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                ), textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(), text = "Soma Date", style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,

                    ), textAlign = TextAlign.Center
            )

        }
    }, actions = {
        IconButton({}) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Back Arrow Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        if (selectDiary != null) {
            DeleteDiaryAction(selectDiary = selectDiary, onDeleteClicked = onDeleteConfirm)
        }
    }

    )
}

@Composable
fun DeleteDiaryAction(
    selectDiary: Diary?, onDeleteClicked: () -> Unit
) {
    var expandMenu by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expandMenu,
        onDismissRequest = { expandMenu = false })
    {
        DropdownMenuItem(
            text = { Text(text = "Delete") },
            onClick = {
                openDialog = true
                expandMenu = false
            }
        )
    }
    DisplayAlertDialog(
        title = "Delete",
        message = "Are you sure you want to permanently delete this note '${selectDiary?.title}'?",
        dialogOpened = openDialog,
        onDialogClosed = { openDialog = false },
        onYesClicked = onDeleteClicked
    )
    IconButton(onClick = { expandMenu = !expandMenu }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Menu Icon",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}