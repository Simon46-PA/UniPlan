package mx.edu.uttt.uniplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import mx.edu.uttt.uniplan.ui.UniPlanApp
import mx.edu.uttt.uniplan.ui.theme.UniPlanTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniPlanTheme {
                UniPlanApp()
            }
        }
    }
}