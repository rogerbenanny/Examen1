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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



// Definición de la entidad User
@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "password") val password: String,
)

// Definición de la entidad Item
@Entity(tableName = "item",
    foreignKeys = [ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["id"]
    )])
data class Item(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "archive_name") val archive: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "userId") val userId: Int
)

// Data Access Object (DAO) para la tabla User
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    fun getUser(username: String, password: String): User?
    @Insert
    fun insert(users: User)
    @Insert
    fun insertUsers(users: User)
}

// Data Access Object (DAO) para la tabla Item
@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>
    @Query("SELECT * FROM item WHERE userId = :userId")
    fun getItemsByUser(userId: Int): List<Item>
    @Insert
    fun insert(item: Item)
    @Insert
    fun insertItems(item: List<Item>)
    @Update
    fun updateItem(item: Item)

    @Delete
    fun deleteItem(item: Item)
    
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

        // Insertar usernames predefinidos
        insertPredefinedUsers()

        setContent {
            val userObject = User(0, "","","")
            val user by remember { mutableStateOf(userObject) }
            val itemObj = Item(0, "", "", "", 0)
            val item by remember { mutableStateOf(itemObj) }
            MyApp(userDatabase, user, item)
        }
    }

    private fun insertPredefinedUsers() {
        val userDao = userDatabase.userDao()
        /*val users = listOf(
            User(1,"username", "Admin","qwerty"),
            //User(2, "username2", "name2","qwerty"),
            //User(3, "username3", "name3","qwerty")
        )*/
        val user = User(1,"username", "Admin","qwerty")
        val itemDao = userDatabase.itemDao()
        val items = listOf(
            Item(1, "img1.jpg","la llorona ", "dasdasdjkasaskjdhas",1),
            Item(2, "img2.jpg","usuadadsario2", "aqwertyuioopqweqw",1),
            Item(3, "img3.jpg","das", "zxbnvcnbzzvxnbcv",1)
        )
        // Ejecutar la inserción de usernames en un hilo en segundo plano utilizando corutinas
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.insert(user)
            itemDao.insertItems(items)
        }
    }
}

//Navegacion entre pantallas
@Composable
fun MyApp(userDatabase: AppDatabase, user: User, item: Item) {
    var isLoggedIn by remember { mutableStateOf(false) }
    val user by remember { mutableStateOf(user) }
    if (isLoggedIn) {
        // Segunda pantalla después de iniciar sesión
        GreetingScreen(userDatabase, user, item)
    } else {
        // Primera pantalla de inicio de sesión
        LoginScreen(userDatabase, user){ isLoggedIn = true}
    }
}

//funcion de la primera pantalla
@Composable
fun LoginScreen(
    userDatabase: AppDatabase,
    user:User,
    onLoggedIn: () -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var password by remember { mutableStateOf(user.password) }
    var user by remember { mutableStateOf(user) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("username") }
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            onClick = {
                val userget = userDatabase.userDao().getUser(username, password)
                if (userget != null) {
                    user = userget

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
fun GreetingScreen(userDatabase: AppDatabase, user:User, item: Item) {
    val itemDao = userDatabase.itemDao()
    val allItemsByUser: List<Item> = itemDao.getItemsByUser(user.id)
    val item = remember { mutableStateOf(item) }
    val allItemsUser = remember { mutableStateListOf<Item>() }
    Column() {
        Text(text = "Items")
// Haz lo que necesites con los datos, por ejemplo, mostrarlos en el registro
        allItemsByUser.forEach{ item -> allItemsUser.add(item)}
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ){
            /*items(allItemsUser){
                itemg ->
                CardItem(
                    userDatabase = userDatabase,
                    funItem = { item = it },
                    item = itemg
                )
            }*/
        }
    }
}
