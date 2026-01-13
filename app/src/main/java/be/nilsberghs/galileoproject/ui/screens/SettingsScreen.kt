package be.nilsberghs.galileoproject.ui.screens

import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import be.nilsberghs.galileoproject.R
import be.nilsberghs.galileoproject.ScoreViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ScoreViewModel,
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val currentActiveLanguage = configuration.locales[0].language
    val scrollState = rememberScrollState()
    BackHandler {
        onBackClick()
    }

    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val scrollModifier = if (isLandscape) Modifier.verticalScroll(scrollState) else Modifier

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .then(scrollModifier)
    ) {
        // --- Language Selection ---
        Text(
            text = stringResource(R.string.label_language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val languages = listOf(
            "en" to stringResource(R.string.lang_en),
            "nl" to stringResource(R.string.lang_nl)
        )

        val currentLanguageName = languages.find { it.first == currentActiveLanguage }?.second 
            ?: languages.first().second

        val coroutineScope = rememberCoroutineScope ()
        var langExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = langExpanded,
            onExpandedChange = { langExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = currentLanguageName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_language)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = langExpanded,
                onDismissRequest = { langExpanded = false }
            ) {
                languages.forEach { (tag, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            langExpanded = false
                            coroutineScope.launch {
                                delay(150L)
                                val appLocale: LocaleListCompat =
                                    LocaleListCompat.forLanguageTags(tag)
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Theme Selection ---
        Text(
            text = stringResource(R.string.label_theme),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val themes = listOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to stringResource(R.string.theme_system),
            AppCompatDelegate.MODE_NIGHT_NO to stringResource(R.string.theme_light),
            AppCompatDelegate.MODE_NIGHT_YES to stringResource(R.string.theme_dark)
        )

        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val currentThemeName = themes.find { it.first == currentNightMode }?.second 
            ?: stringResource(R.string.theme_system)

        var themeExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = themeExpanded,
            onExpandedChange = { themeExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = currentThemeName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_theme)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = themeExpanded,
                onDismissRequest = { themeExpanded = false }
            ) {
                themes.forEach { (mode, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            //close dropdown
                            themeExpanded = false
                            coroutineScope.launch {
                                delay(150L) // give ui time to close the dropdown
                                viewModel.setThemeMode(mode)
                            }

                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val backgroundOpacity by viewModel.backgroundOpacity.collectAsState()

        // Add Label
        Text(
            text = stringResource(R.string.label_background_opacity),
            style = MaterialTheme.typography.titleMedium,    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Slider(
            value = backgroundOpacity,
            onValueChange = { viewModel.setBackgroundOpacity(it) },

            modifier = Modifier.fillMaxWidth()
        )


        if (isLandscape) {
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f)) // Push About button to bottom
        }

        Button(
            onClick = onAboutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.action_about))
        }
    }
}
