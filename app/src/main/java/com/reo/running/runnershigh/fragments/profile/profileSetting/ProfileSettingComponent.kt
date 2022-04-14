package com.reo.running.runnershigh.fragments.profile.profileSetting

import android.content.Context
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reo.running.runnershigh.R

@Composable
fun ToolBar(context: Context, onNavigate: (Int) -> Unit, onSave: () -> Unit) {
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
                        onSave()
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
    ImageButton(
        painter = painterResource(id = R.drawable.ic_account),
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = {})
            .size(size = 120.dp)
            .clip(CircleShape)
    )
}

@Composable
fun EditText(
    title: String,
    state: String,
    onChange: (String) -> Unit
) {
    Text(text = title, color = Color.Gray)
    TextField(
        modifier = Modifier.padding(bottom = 32.dp),
        value = state,
        onValueChange = onChange,
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
