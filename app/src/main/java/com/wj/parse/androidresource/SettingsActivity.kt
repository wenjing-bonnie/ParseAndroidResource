package com.wj.parse.androidresource

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wj.parse.androidresource.ui.theme.ParseAndroidResourceTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParseAndroidResourceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp, top = 30.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Settings("Settings")
                }
            }
        }
    }
}

@Composable
fun Settings(
    name: String,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(id = R.string.settings_content, "1"),
            modifier = modifier
        )
        Text(
            text = stringResource(id = R.string.settings_sub_content),
            modifier = modifier
        )
        Text(
            text = stringResource(id = R.string.settings_sub_content_2),
            modifier = modifier
        )
        Text(
            text = stringResource(id = R.string.settings_sub_content_3),
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ParseAndroidResourceTheme {
        Settings("Android")
    }
}