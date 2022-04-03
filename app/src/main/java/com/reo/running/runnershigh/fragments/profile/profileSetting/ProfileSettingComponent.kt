package com.reo.running.runnershigh.fragments.profile.profileSetting

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reo.running.runnershigh.R

@Composable
fun ToolBar() {
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
fun ProfileImage() {
    Image(
        painter = painterResource(id = R.drawable.ic_account),
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = {})
            .size(size = 120.dp)
    )
}

@Composable
fun EditText(
    title: String
) {
    var editValue by remember { mutableStateOf("") }
    Text(text = title)
    TextField(
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        value = editValue,
        onValueChange = { editValue = it },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(R.color.gray))
    )
}

@Composable
fun MarkerImage(@DrawableRes drawable: Int) {
    Text(text = stringResource(R.string.profile_marker))
    Image(painter = painterResource(id = drawable), contentDescription = null)
}

