package com.zeafen.petwalker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.presentation.standard.navigation.NavStop
import com.zeafen.petwalker.presentation.standard.navigation.NavigationRoutes
import com.zeafen.petwalker.ui.standard.elements.PetWalkerBottomBar
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import org.jetbrains.compose.resources.painterResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_error
import petwalker.composeapp.generated.resources.ic_save

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}