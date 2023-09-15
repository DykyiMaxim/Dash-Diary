package com.wm.dashdiary.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.wm.dashdiary.data.repository.Diaries
import com.wm.dashdiary.data.repository.RequestState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun HomeScreen(
    diaries: Diaries,
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    onSignOutClicked: () -> Unit
) {
    var padding by remember { mutableStateOf(PaddingValues()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    NavigationDrawer(
        drawerState = drawerState,
        onSignOutCliked = onSignOutClicked
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopBar(
                    scrollBehavior = scrollBehavior,
                    onMenuClicked = onMenuClicked
                )
            },
            content = {
                padding = it
                when (diaries) {
                    is RequestState.Success -> {
                        HomeContent(
                            diariesNotes = diaries.data,
                            onClick = navigateToWriteWithArgs,
                            paddingValues = it)
                    }

                    is RequestState.Error -> {
                        EmptyPage(
                            title = "Error",
                            subtitle = "${diaries.error.message}"
                        )
                    }

                    is RequestState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()

                        }
                    }

                    else -> {}
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = navigateToWrite,
                    Modifier
                        .size(80.dp)
                        .padding(end = padding.calculateEndPadding(LayoutDirection.Ltr))
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
                HorizontalDivider()
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

