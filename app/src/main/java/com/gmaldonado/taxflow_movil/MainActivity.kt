package com.gmaldonado.taxflow_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gmaldonado.taxflow_movil.ui.TaxflowApp
import com.gmaldonado.taxflow_movil.ui.theme.Taxflow_MovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Taxflow_MovilTheme {
                TaxflowApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaxflowAppPreview() {
    Taxflow_MovilTheme {
        TaxflowApp()
    }
}