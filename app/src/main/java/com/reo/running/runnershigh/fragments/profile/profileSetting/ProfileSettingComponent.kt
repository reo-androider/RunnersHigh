package com.reo.running.runnershigh.fragments.profile.profileSetting

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
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
import androidx.navigation.NavHostController
import com.reo.running.runnershigh.R

@Composable
fun ToolBar(context: Context, onNavigate: (Int) -> Unit) {
    TopAppBar(
        title = { Text(text = "設定") },
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = {
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_running)
                    .setTitle(R.string.profile_setting_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.profile_setting_dialog_positive_text) { _, _ ->
                        onNavigate(R.id.action_fragmentProfileSetting_to_navi_profile)
                    }
                    .setNegativeButton(R.string.profile_setting_dialog_negative_text) { _, _ -> }
                    .show()
            }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back to profile screen"
                )
            }
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
private fun SaveCheckDialog(isOpened: Boolean, navController: NavHostController) {
    if (isOpened) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "記録を保存しますか？") },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate(R.id.action_fragmentProfileSetting_to_navi_profile)
                }) { Text(text = "はい") }
            }
        )
    }
}
