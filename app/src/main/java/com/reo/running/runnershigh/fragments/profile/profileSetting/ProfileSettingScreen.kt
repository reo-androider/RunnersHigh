package com.reo.running.runnershigh.fragments.profile.profileSetting

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.reo.running.runnershigh.R

@Composable
fun ProfileSettingsScreen(
    context: Context,
    onNavigate: (Int) -> Unit,
    viewModel: ProfileSettingViewModel
) {
    var iconImage by rememberSaveable { mutableStateOf(0) }
    Scaffold(
        topBar = {
            ToolBar(
                context = context,
                onNavigate = onNavigate,
                onSave = { viewModel.saveProfileSetting() },
            )
        },
        content = {
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Row {
                    Column(modifier = Modifier.padding(top = 40.dp, end = 16.dp)) {
                        ProfileImage()
                    }
                    Column {
                        val lastName by viewModel.lastName.observeAsState("")
                        EditText(
                            title = stringResource(id = R.string.profile_family_name),
                            state = lastName,
                            onChange = { viewModel.onLastNameChange(it) },
                        )
                        val firstName by viewModel.firstName.observeAsState("")
                        EditText(
                            title = stringResource(id = R.string.profile_first_name),
                            state = firstName,
                            onChange = { viewModel.onFirstNameChange(it) },
                        )
                    }
                }
                val objective by viewModel.objective.observeAsState("")
                EditText(
                    title = stringResource(id = R.string.objective),
                    state = objective,
                    onChange = { viewModel.onObjectiveChange(it) },
                )
                val weight by viewModel.weight.observeAsState("")
                EditText(
                    title = stringResource(id = R.string.profile_weight),
                    state = weight,
                    onChange = { viewModel.onWeightChange(it) },
                )
                MarkerImage(R.drawable.ic_trace)
            }
        }
    )
}
