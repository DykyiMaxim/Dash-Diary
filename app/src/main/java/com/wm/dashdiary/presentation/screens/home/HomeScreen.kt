package com.wm.dashdiary.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun HomeScreen(
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    onSignOutClicked: () -> Unit
) {
    NavigationDrawer(
        drawerState = drawerState,
        onSignOutCliked = onSignOutClicked
    ) {
        Scaffold(
            topBar = { HomeTopBar(onMenuClicked = onMenuClicked) },
            content = {},
            floatingActionButton = {
                FloatingActionButton(
                    onClick = navigateToWrite,
                    Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Screen"
                    )

                }
            }
        )

    }
}


@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOutCliked: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(250.dp),
                        painter = painterResource(id = com.wm.dashdiary.R.drawable.logo),
                        contentDescription = "Logo",

                        )
                }
                Divider()
                Spacer(modifier = Modifier.padding(12.dp))
                NavigationDrawerItem(
                    label = {
                        Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Icon(
                                painter = painterResource(id = com.wm.dashdiary.R.drawable.google_logo),
                                contentDescription = "Log out",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Sign Out", color = MaterialTheme.colorScheme.onSurface)

                        }
                    },
                    selected = false,
                    onClick = onSignOutCliked
                )
            })
        },
        content = content,
    )
}