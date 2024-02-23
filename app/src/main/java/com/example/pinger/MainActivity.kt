package com.example.pinger

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.pinger.ui.theme.PingerTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            MyApp(auth)
        }
    }
}

data class Perfil(val descrip: String?, val edad: Int?, val foto: String?, val nombre: String?, val numrand: Int?)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TinderUI() {
    val items = listOf("Home", "Chats")
    var selectedItem by remember { mutableStateOf("Home") }
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B)),
        startY = 0f
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Pinger", textAlign = TextAlign.Center) },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Filled.Notifications, contentDescription = null)
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "App desarrollada por Emils y Sergio v-2.7.3 23-feb-2024", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                items.forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text(item) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                "Home" -> HomeScreen()
                "Chats" -> ChatsScreen()
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {
    var perfilActual by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }

    var backgroundColor by remember { mutableStateOf(Color.White) }
    var rotation by remember { mutableStateOf(0f) }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val perfiles = remember { mutableStateOf<List<Perfil>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            perfiles.value = getPerfilFromFirebase()
        }
    }
    if (perfiles.value.isNotEmpty() && perfilActual in perfiles.value.indices) {
        val perfil = perfiles.value[perfilActual]
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("¡Es un MATCH!") },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(backgroundColor)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetX > 200) {
                                backgroundColor = Color.Green

                                val randomNum = Random.nextInt(0, 10)
                                if (perfiles.value[perfilActual].numrand == randomNum) {
                                    showDialog = true
                                } else {
                                    perfilActual = (perfilActual + 1) % perfiles.value.size
                                }
                            }
                            else if (offsetX < -200) {
                                backgroundColor = Color.Red
                                perfilActual = (perfilActual + 1) % perfiles.value.size
                            }
                            offsetX = 0f
                            rotation = 0f
                        },
                        onDrag = { change, dragAmount ->
                            offsetX += dragAmount.x
                            rotation = (offsetX / 1000f) * 45
                            change.consumeAllChanges()
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            backgroundColor = Color.White
                            Toast
                                .makeText(context, "¡Has dado un Super Like!", Toast.LENGTH_SHORT)
                                .show()
                            showDialog = true
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .rotate(rotation)
            ) {
                Image(
                    painter = rememberImagePainter(data = perfil.foto),
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
                        text = "${perfil.nombre}, ${perfil.edad}",
                        style = TextStyle(fontSize = 24.sp, color = Color.White),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = perfil.descrip  ?: "",
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
                IconButton(onClick = {
                    backgroundColor = Color.Red
                    perfilActual = (perfilActual + 1) % perfiles.value.size
                }) {Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Dislike", tint = Color.Red , modifier = Modifier.size(36.dp))
                }
                }
                IconButton(onClick = {
                    backgroundColor = Color.White
                    Toast.makeText(context, "¡Has dado un Super Like!", Toast.LENGTH_SHORT).show()
                    showDialog = true
                }) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Super Like", tint = Color.Blue , modifier = Modifier.size(36.dp))
                    }
                }
                IconButton(onClick = {
                    backgroundColor = Color.Green
                    val randomNum = Random.nextInt(0, 10)
                    if (perfiles.value[perfilActual].numrand == randomNum) {
                        showDialog = true
                    }else {
                        perfilActual = (perfilActual + 1) % perfiles.value.size
                    }}) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Like", tint = Color.Green, modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChatsScreen() {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A1C71), Color(0xFFD76D77), Color(0xFFFFAF7B)),
        startY = 0f
    )

    val coroutineScope = rememberCoroutineScope()
    val messages = remember { mutableStateOf<List<Message>>(emptyList()) }
    val showDialog = remember { mutableStateOf(false) }
    val newChatName = remember { mutableStateOf("") }
    val newChatMessage = remember { mutableStateOf("") }
    val newChatPhoto = remember { mutableStateOf("") }
    val searchQuery = remember { mutableStateOf("") }
    val deleteMode = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            messages.value = getMessagesFromFirebase()
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Añadir chat") },
            text = {
                Column {
                    TextField(
                        value = newChatName.value,
                        onValueChange = { newChatName.value = it },
                        label = { Text("Nombre") }
                    )
                    TextField(
                        value = newChatMessage.value,
                        onValueChange = { newChatMessage.value = it },
                        label = { Text("Mensaje") }
                    )
                    TextField(
                        value = newChatPhoto.value,
                        onValueChange = { newChatPhoto.value = it },
                        label = { Text("Foto (URL)") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        addChatToFirebase(newChatName.value, newChatMessage.value, newChatPhoto.value)
                        messages.value = getMessagesFromFirebase()
                    }
                    showDialog.value = false
                }) {
                    Text("Guardar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient)
                .padding(16.dp)
        ) {
            TextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = { Text("Buscar por nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "MENSAJES",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn {
                items(messages.value.filter { it.nombre.contains(searchQuery.value, ignoreCase = true) }) { message ->
                    ChatRow(message = message, onDelete = { msg ->
                        if (deleteMode.value) {
                            coroutineScope.launch {
                                deleteChatFromFirebase(msg)
                                messages.value = getMessagesFromFirebase()
                                deleteMode.value = false
                            }
                        }
                    })
                }
            }
            Row {
                Button(onClick = { showDialog.value = true }) {
                    Text("Añadir chat")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { deleteMode.value = !deleteMode.value }) {
                    Text(if (deleteMode.value) "Cancelar" else "Borrar")
                }
            }
        }
    }
}

@Composable
fun ChatRow(message: Message, onDelete: (Message) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .clickable { onDelete(message) }
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            if (message.foto.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(data = message.foto),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = "${message.nombre} - ${message.texto}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

suspend fun addChatToFirebase(name: String, message: String, photo: String) {
    val db = FirebaseFirestore.getInstance()
    val newChat = hashMapOf(
        "nombre" to name,
        "texto" to message,
        "foto" to photo
    )
    db.collection("chat").add(newChat).await()
}

suspend fun deleteChatFromFirebase(message: Message) {
    val db = FirebaseFirestore.getInstance()
    val chatRef = db.collection("chat").whereEqualTo("nombre", message.nombre).whereEqualTo("texto", message.texto).get().await()
    chatRef.documents.firstOrNull()?.reference?.delete()?.await()
}

suspend fun getMessagesFromFirebase(): List<Message> {
    val messages = mutableListOf<Message>()

    val db = FirebaseFirestore.getInstance()
    val result = db.collection("chat").get().await()

    for (document in result) {
        val nombre = document.getString("nombre") ?: ""
        val texto = document.getString("texto") ?: ""
        val foto = document.getString("foto") ?: ""
        val message = Message(nombre, texto, foto)
        messages.add(message)
    }

    return messages
}

data class Message(
    val nombre: String,
    val texto: String,
    val foto: String
)
suspend fun getPerfilFromFirebase(): List<Perfil> {
    val perfiles = mutableListOf<Perfil>()

    val db = FirebaseFirestore.getInstance()
    val result = db.collection("pinger").get().await()

    for (document in result) {
        val descrip = document.getString("descrip") ?: ""
        val edad = document.getLong("edad")?.toInt() ?: 0
        val numrand = document.getLong("numrand")?.toInt() ?: 0
        val nombre = document.getString("nombre") ?: ""
        val foto = document.getString("foto") ?: ""
        val perfil = Perfil(descrip, edad , foto, nombre, numrand)
        perfiles.add(perfil)
    }

    return perfiles
}


@SuppressLint("ComposableDestinationInComposeScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(auth: FirebaseAuth) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showMessage = remember { mutableStateOf<String?>(null) }
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") {
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
                            onClick = {
                                if (email.value.isBlank() || password.value.isBlank()) {
                                    showMessage.value =
                                        "Por favor, ingresa tu nombre de usuario y contraseña."
                                    return@Button
                                }
                                signIn(auth, email.value, password.value) { message ->
                                    showMessage.value = message
                                    if (message == "Inicio de sesión exitoso") {
                                        navController.navigate("tinderUI")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors()
                        ) {
                            Text("Iniciar sesión")
                        }
                        TextButton(
                            onClick = {
                                if (email.value.isBlank() || password.value.isBlank()) {
                                    showMessage.value =
                                        "Por favor, ingresa tu nombre de usuario y contraseña."
                                    return@TextButton
                                }
                                register(auth, email.value, password.value) { message ->
                                    showMessage.value = message
                                }
                            },
                        ) {
                            Text("Registrarse")
                        }

                        showMessage.value?.let {
                            AlertDialog(
                                onDismissRequest = { showMessage.value = null },
                                title = { Text("Mensaje") },
                                text = { Text(it) },
                                confirmButton = {
                                    Button(onClick = { showMessage.value = null }) {
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }
                }

            }
        }
        composable("tinderUI") {
            TinderUI()
        }
    }
}

private fun signIn(auth: FirebaseAuth, email: String, password: String, callback: (String) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback("Inicio de sesión exitoso")
            } else {
                val errorMessage = when(task.exception?.message) {
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> "No hay ningún registro de usuario correspondiente a este nombre de usuario. El usuario puede haber sido eliminado."
                    "The password is invalid or the user does not have a password." -> "La contraseña es inválida o el usuario no tiene una contraseña."
                    else -> "Error al iniciar sesión: ${task.exception?.message}"
                }
                callback(errorMessage)
            }
        }
}

private fun register(auth: FirebaseAuth, email: String, password: String, callback: (String) -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback("Registro exitoso")
            } else {
                val errorMessage = when(task.exception?.message) {
                    "The email address is already in use by another account." -> "La dirección de correo electrónico ya está en uso por otra cuenta."
                    "The email address is badly formatted." -> "La dirección de correo electrónico tiene un formato incorrecto."
                    "The given password is invalid. [ Password should be at least 6 characters ]" -> "La contraseña proporcionada es inválida. La contraseña debe tener al menos 6 caracteres."
                    else -> "Error al registrarse: ${task.exception?.message}"
                }
                callback(errorMessage)
            }
        }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PingerTheme {
        TinderUI()
    }
}