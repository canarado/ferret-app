package com.example.ferretapp.android

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ferretapp.Greeting
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // title bar, and everything else
                    Column() {
                        TitleBar(title = "Ferret App 3000")
                        Spacer(modifier = Modifier.padding(20.dp))
                        Row() {

                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ImageToUploadWithButton()
                        }
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
}

@Composable
fun OpenGalleryButton(imagePicker: ActivityResultLauncher<PickVisualMediaRequest>) {
    Button(onClick = {
        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }) {
        Text(text = "Pick From Gallery")
    }
}

@Composable
fun ImageToUploadWithButton() {
    
    var imageState by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri -> imageState = uri})
    
    Column() {
        if(imageState != null) {
            AsyncImage(model = imageState, contentDescription = "Cute Ferret Picture")
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Upload Image to API")
                }
                Button(onClick = { imageState = null }) {
                    Text(text = "Cancel")
                }
            }
            
        } else {
            OpenGalleryButton(imagePicker = imagePicker)
        }
    }
}