package com.example.examen1
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



// Definición de la entidad User
@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "usuario") val usuario: String,
    @ColumnInfo(name = "nombre") val nombre: String
)

// Definición de la entidad Item
@Entity(tableName = "item")
data class Item(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "descripcion") val descripcion: String
)

// Data Access Object (DAO) para la tabla User
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE usuario = :usuario AND nombre = :nombre")
    fun getUser(usuario: String, nombre: String): User?
    @Insert
    fun insert(users: List<User>)
}

// Data Access Object (DAO) para la tabla Item
@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>
    @Insert
    fun insert(item: List<Item>)
}

// Database
@Database(entities = [User::class, Item::class], version = 14)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
}

//Clase principal donde llenamos datos de antemano a la base de datos
class MainActivity : ComponentActivity() {
    private lateinit var userDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear la instancia de la base de datos
        userDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user-db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        // Insertar usuarios predefinidos
        insertPredefinedUsers()

        setContent {
            MyApp(userDatabase)
        }
    }

    private fun insertPredefinedUsers() {
        val userDao = userDatabase.userDao()
        val users = listOf(
            User(1, "usuario1", "Nombre1"),
            User(2, "usuario2", "Nombre2"),
            User(3, "usuario3", "Nombre3")
        )
        val itemDao = userDatabase.itemDao()
        val item = listOf(
            Item(1, "la llorona ", "dasd"),
            Item(2, "usuadadsario2", "dasd"),
            Item(3, "das", "Nomadadbre3")
        )
        // Ejecutar la inserción de usuarios en un hilo en segundo plano utilizando corutinas
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.insert(users)
            itemDao.insert(item)
        }
    }
}

//Navegacion entre pantallas
@Composable
fun MyApp(userDatabase: AppDatabase) {
    var usuario by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        // Segunda pantalla después de iniciar sesión
        GreetingScreen(userDatabase)
    } else {
        // Primera pantalla de inicio de sesión
        LoginScreen(userDatabase, usuario, nombre) { isLoggedIn = true }
    }
}

//funcion de la primera pantalla
@Composable
fun LoginScreen(
    userDatabase: AppDatabase,
    usuario: String,
    nombre: String,
    onLoggedIn: () -> Unit
) {
    var userUsuario by remember { mutableStateOf(usuario) }
    var userNombre by remember { mutableStateOf(nombre) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userUsuario,
            onValueChange = { userUsuario = it },
            label = { Text("Usuario") }
        )

        TextField(
            value = userNombre,
            onValueChange = { userNombre = it },
            label = { Text("Nombre") }
        )

        Button(
            onClick = {
                val user = userDatabase.userDao().getUser(userUsuario, userNombre)
                if (user != null) {
                    onLoggedIn()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Iniciar sesión")
        }
    }
}

//Funcion que se mostrar en la seunda pantalla
@Composable
fun GreetingScreen(userDatabase: AppDatabase) {
    val itemDao = userDatabase.itemDao()
    val allItems: List<Item> = itemDao.getAllItems()
    val items = remember { mutableStateListOf<Item>() }
    Column() {
        Text(text = "sadsa")
// Haz lo que necesites con los datos, por ejemplo, mostrarlos en el registro
        allItems.forEach { item ->
            Text("hola ---${item.id} ${item.titulo} ${item.descripcion}", modifier = Modifier.padding(top = 8.dp))
        }
    }
}