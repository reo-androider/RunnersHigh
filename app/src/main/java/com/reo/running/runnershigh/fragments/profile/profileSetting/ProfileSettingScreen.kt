package com.reo.running.runnershigh.fragments.profile.profileSetting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reo.running.runnershigh.R

@Composable
fun ProfileSettingsScreen() {
    Scaffold(
        topBar = { ToolBar() },
        content = {
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Row {
                    Column(modifier = Modifier.padding(top = 40.dp, end = 16.dp)) {
                        ProfileImage()
                    }
                    Column {
                        EditText(stringResource(id = R.string.profile_family_name))
                        EditText(stringResource(id = R.string.profile_first_name))
                    }
                }
                EditText(stringResource(id = R.string.objective))
                EditText(stringResource(id = R.string.profile_weight))
                MarkerImage(R.drawable.ic_trace)
            }
        }
    )
}
