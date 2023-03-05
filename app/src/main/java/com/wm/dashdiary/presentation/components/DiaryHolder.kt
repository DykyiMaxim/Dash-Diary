package com.wm.dashdiary.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wm.dashdiary.mapper.toInstant
import com.wm.dashdiary.model.Diary
import com.wm.dashdiary.model.Mood
import com.wm.dashdiary.ui.theme.Elevation
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.util.*


@Composable
fun DiaryHolder(diary: Diary, onClick: (String) -> Unit) {
    var componentHeight by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current
    var GalleryOpend by remember { mutableStateOf(false)
    }
    Row(modifier = Modifier
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() })
        { onClick(diary.Id.toString()) }) {

        Spacer(modifier = Modifier.width(14.dp))
        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp), tonalElevation = Elevation.level1
        ) {

        }
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {
                    componentHeight = with(localDensity) { it.size.height.toDp() }
                },
            tonalElevation = Elevation.level1
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DiaryHeader(moodName = diary.mood, time = diary.date.toInstant())
                Text(
                    modifier = Modifier.padding(all = 14.dp),
                    text = diary.description,
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
                if(diary.Images.isNotEmpty()){
                    ShowGalleryButton(
                        galleryOpened =GalleryOpend ,
                        onCLick ={
                            GalleryOpend = !GalleryOpend
                        }
                    )
                }
                AnimatedVisibility(visible = GalleryOpend) {
                    Column(Modifier.padding(all = 14.dp)) {
                        Gallery(images = diary.Images)
                    }

                    
                }

            }

        }


    }

}

@Composable
fun DiaryHeader(moodName: String, time: Instant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Mood.valueOf(moodName).containerColor)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = Mood.valueOf(moodName).icon),
                contentDescription = "Mood Icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Mood.valueOf(moodName).name,
                color = Mood.valueOf(moodName).containerColor,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
            )
            Text(
                text = SimpleDateFormat("hh:mm a", Locale.US).format(Date.from(time)),
                color = Mood.valueOf(moodName).containerColor,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
            )
        }

    }

}


@Composable
fun DateHeader(localDate: LocalDate) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format("%02d", localDate.dayOfMonth),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
            Text(
                text = localDate.dayOfWeek.toString().take(3),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }
        Spacer(modifier = Modifier.width(14.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = localDate.month.toString().lowercase()
                    .replaceFirstChar { it.titlecase() },
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
            Text(
                text = "${localDate.year}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light
                )
            )
        }

    }
}

@Composable
fun ShowGalleryButton(
    galleryOpened: Boolean,
    onCLick: () -> Unit
) {
    TextButton(onClick = onCLick) {
        Text(
            text = if (galleryOpened) "Hide Gallery" else "Show Gallery",
            style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize)
        )


    }

}

