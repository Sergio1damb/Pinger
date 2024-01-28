package com.example.pinger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinger.ui.theme.PingerTheme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            MyApp(auth)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TinderUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(text = "Pinger", textAlign = TextAlign.Center) },
            actions = {
                IconButton(onClick = {  }) {
                    Icon(Icons.Filled.Notifications, contentDescription = null)
                }
                IconButton(onClick = {  }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null)
                }
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ramona la del pueblo, 77",
                    style = TextStyle(fontSize = 24.sp, color = Color.White),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Si la 'todo' como lema de vida y juerguista 24/7! " +
                            "Me encanta la playa, un buen viaje y un buen festón " +
                            "con reggaetón y tecno. Bueno, si me invitas a una " +
                            "burger ya me has ganado 'pa' siempre 🍔✨",
                    style = TextStyle(fontSize = 16.sp, color = Color.White),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {  }) {
                Icon(Icons.Filled.Clear, contentDescription = "Dislike", tint = Color.Red)
            }
            IconButton(onClick = {  }) {
                Icon(Icons.Filled.Star, contentDescription = "Super Like", tint = Color.Blue)
            }
            IconButton(onClick = {  }) {
                Icon(Icons.Filled.Favorite, contentDescription = "Like", tint = Color.Green)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(auth: FirebaseAuth) {
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }

    PingerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier.padding(16.dp)
                )
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Nombre de usuario") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { signIn(auth, email.value, password.value) },
                    colors = ButtonDefaults.buttonColors(
                    )
                ) {
                    Text("Iniciar sesión")
                }
                TextButton(
                    onClick = { register(auth, email.value, password.value) },
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}

private fun signIn(auth: FirebaseAuth, email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
            } else {
            }
        }
}

private fun register(auth: FirebaseAuth, email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
            } else {
            }
        }
}
