package com.reo.running.runnershigh.fragments.profile.setting

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.fragments.profile.ProfileSettingsType

@Composable
fun ProfileSettingScreen() {
    Column {
        ToolBar()
        BorderLine()
        Row {
            ProfileImage()
            Column {
//                Box(Modifier.padding(start =)) {
//
//                }
                EditText(content = ProfileSettingsType.LAST_NAME)
                EditText(content = ProfileSettingsType.FIRST_NAME)
            }
        }
        EditText(content = ProfileSettingsType.TARGET)
        EditText(content = ProfileSettingsType.WEIGHT)
        MarkerImage(drawable = R.drawable.ic_trace)
    }
}

@Composable
private fun ToolBar() {
    TopAppBar(
        title = { Text(text = "設定") },
        backgroundColor = Color.White,
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        },
        actions = {
            // TODO: プロフィール画面に戻る
        }
    )
}

@Composable
private fun BorderLine() {
    Divider(color = Color.Gray, thickness = 10.dp)
}

@Composable
private fun ProfileImage() {
    val paddingTop = 32.dp
    val imageSize = 132.dp
    Image(
        painter = painterResource(id = R.drawable.ic_account),
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = {})
            .size(imageSize, imageSize)
    )
}

@Composable
private fun EditText(
    content: ProfileSettingsType
) {
    var editValue by remember { mutableStateOf("") }
    Text(
        text = content.title,
        Modifier.padding(
            vertical = content.verticalMargin, horizontal = content.horizontalMargin
        )
    )
    TextField(value = editValue, onValueChange = { editValue = it })
}

@Composable
private fun MarkerImage(@DrawableRes drawable: Int) {
    Text(text = stringResource(R.string.profile_marker))
    Image(painter = painterResource(id = drawable), contentDescription = null)
}
