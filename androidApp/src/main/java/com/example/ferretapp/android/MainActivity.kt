package com.example.ferretapp.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ferretapp.Greeting
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia())
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if(uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // title bar, and everything else
                    Column() {
                        TitleBar(title = "Ferret App 3000")
                        Text(text = "test")
                    }
                }
            }
        }
    }
}

@Composable
fun TitleBar(title: String) {
    TopAppBar(
        elevation = 4.dp,
        title = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(text = "$title")
            }
        },
        backgroundColor = MaterialTheme.colors.primarySurface
    )
//    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
//        TopAppBar(
//            elevation = 4.dp,
//            title = {
//                Text(text = "$title")
//            },
//            backgroundColor = MaterialTheme.colors.primarySurface,
//        )
//    }
//    Row(
//        horizontalArrangment = Arrangement.Center,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        TopAppBar(
//            elevation = 4.dp,
//            title = {
//                Text(text = "$title")
//            },
//            backgroundColor = MaterialTheme.colors.primarySurface
//        )
//    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

//@Composable
//fun ImageButtons() {
//    Column() {
//        Button(onClick = { openImagePicker() }) {
//            Text(text = "Select Image")
//        }
//        Button(onClick = { openCamera() }) {
//            Text(text = "Take Picture")
//        }
//    }
//}