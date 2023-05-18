package com.example.ferretapp.android

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File


const val API_URL_SUBMIT: String = "https://ferret-api.canarado.xyz/api/submit"
class MainActivity : ComponentActivity() {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = false
            })
        }
    }

    lateinit var shared : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shared = getSharedPreferences("FerretToken", Context.MODE_PRIVATE)

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
                            ImageToUploadWithButton(httpClient = httpClient, prefs = shared)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column() {
                                SetTokenTextAndButton(prefs = shared)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetTokenTextAndButton(prefs: SharedPreferences) {

    var tokenState by remember { mutableStateOf("") }

    val context = LocalContext.current

    TextField(value = tokenState, onValueChange = {
        tokenState = it
    })
    Row() {
        Button(onClick = {
            if(tokenState != "") {

                val edit = prefs.edit()

                edit.putString("token", tokenState)
                edit.apply()

                Toast.makeText(context, "Set API Token to $tokenState", Toast.LENGTH_SHORT).show()
                tokenState = ""
            }
        }) {
            Text(text = "Set Token")
        }
        Button(onClick = {
            val token = prefs.getString("token", "")

            Toast.makeText(context, "Current Token: $token", Toast.LENGTH_LONG).show()
        }) {
            Text(text = "See Token")
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
fun ImageToUploadWithButton(httpClient: HttpClient, prefs: SharedPreferences) {
    
    var imageState by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope();

    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri -> imageState = uri})
    
    Column() {

        val context = LocalContext.current

        if(imageState != null) {

            val localuri = imageState

            localuri ?: return

            AsyncImage(model = imageState, contentDescription = "Cute Ferret Picture")
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    // TODO

                    val cursor = context.contentResolver.query(localuri, null, null, null, null)
                    cursor!!.moveToFirst()
                    val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    val path = cursor.getString(idx)
                    cursor.close()

                    coroutineScope.launch {
                        submitFerret(httpClient = httpClient, imagePath = path, token = "${prefs.getString("token", "")}")
                    }

                    Toast.makeText(context, "Submitted ferret picture!", Toast.LENGTH_SHORT).show()

                    imageState = null
                }) {
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
@Throws(Exception::class)
suspend fun submitFerret(httpClient: HttpClient, imagePath: String, token: String): HttpResponse {

    val file = File(imagePath)

    val res = httpClient.submitFormWithBinaryData(
        url = "$API_URL_SUBMIT",
        formData = formData {
            append("description", "Ferret Picture")
            append("file", file.readBytes(), Headers.build {
                append(HttpHeaders.ContentType, "image/${file.extension}")
                append(HttpHeaders.ContentDisposition, "filename=\"ferret.${file.extension}\"")
            })
            append("token", token)
        }
    )

    return res
}