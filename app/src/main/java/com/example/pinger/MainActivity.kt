package com.example.pinger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
