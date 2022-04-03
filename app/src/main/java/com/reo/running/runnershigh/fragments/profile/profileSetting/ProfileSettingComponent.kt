package com.reo.running.runnershigh.fragments.profile.profileSetting

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.reo.running.runnershigh.R

@Composable
fun ToolBar() {
    val navController = rememberNavController()
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
    Text(text = title, color = Color.Gray)
    TextField(
        modifier = Modifier.padding(bottom = 32.dp),
        value = editValue,
        onValueChange = { editValue = it },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Gray,
            backgroundColor = Color.White
        )
    )
}

@Composable
fun MarkerImage(@DrawableRes drawable: Int) {
    Text(text = stringResource(R.string.profile_marker))
    Image(painter = painterResource(id = drawable), contentDescription = null)
}

@Composable
private fun SaveCheckDialog() {
    val navController = rememberNavController()
    val isOpened = remember { mutableStateOf(true) }
    if (isOpened.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "記録を保存しますか？") },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate(R.id.action_profileFragment_to_fragmentProfileSetting)
                }) { Text(text = "はい") }
            }
        )
    }
}

