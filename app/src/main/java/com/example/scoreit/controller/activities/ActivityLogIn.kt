package com.example.scoreit.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.model.components.User
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.model.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityLogInBinding
import kotlinx.coroutines.launch

class ActivityLogIn : AppCompatActivity() {

    // Binding para la actividad de inicio de sesión
    private lateinit var binding: ActivityLogInBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_USER_LI: String = "ID_USER" // Clave para pasar el ID del usuario entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar el botón de inicio de sesión
        loginButton()
    }

    // Función para configurar el botón de inicio de sesión
    private fun loginButton() {
        lifecycleScope.launch {
            binding.logInButton.setOnClickListener {
                // Verificar si los campos de correo electrónico y contraseña están vacíos
                if (binding.emailLogIn.text.toString() == "" || binding.passwordLogIn.text.toString() == "") {
                    errorMessage(1) // Mostrar mensaje de error si los campos están vacíos
                } else if (binding.passwordLogIn.text.toString().length < 8) {
                    errorMessage(2) // Mostrar mensaje de error si la contraseña es demasiado corta
                } else {
                    logInVerification() // Verificar las credenciales del usuario
                }
            }
        }
    }

    // Función para verificar las credenciales del usuario
    private fun logInVerification() {
        val idUser = intent.getStringExtra(ID_USER_LI)
        if (idUser != null) {
            lifecycleScope.launch {
                // Obtener el usuario de la base de datos
                val user = dbAccess.userDao().getUserById(idUser)
                val email = binding.emailLogIn.text.toString()
                val password = binding.passwordLogIn.text.toString()

                // Verificar si el correo electrónico y la contraseña coinciden
                if (user.email != email || user.password != password) {
                    errorMessage(4) // Mostrar mensaje de error si las credenciales son incorrectas
                } else {
                    successfulMessage(user) // Mostrar mensaje de bienvenida
                    lastUserUpdate() // Actualizar el último usuario que inició sesión
                    changeToActivityMainMenu(idUser) // Navegar al menú principal
                }
            }
        }
    }

    // Función para mostrar un mensaje de bienvenida
    private fun successfulMessage(user: User) {
        Toast.makeText(this, "Bienvenido ${user.name}", Toast.LENGTH_LONG).show()
    }

    // Función para actualizar el último usuario que inició sesión
    private fun lastUserUpdate() {
        lifecycleScope.launch {
            // Obtener todos los usuarios de la base de datos
            val listOfUsers = dbAccess.userDao().getEveryUser().toMutableList()
            for (user in listOfUsers) {
                if (user.lastUser) {
                    // Desmarcar el último usuario anterior
                    user.lastUser = false
                    dbAccess.userDao().update(user)
                }
            }
            val idUser = intent.getStringExtra(ID_USER_LI)
            if (idUser != null) {
                // Marcar el nuevo último usuario
                val user = dbAccess.userDao().getUserById(idUser)
                user.lastUser = true
                dbAccess.userDao().update(user)
            }
        }
    }

    // Función para mostrar mensajes de error
    private fun errorMessage(number: Int) {
        when (number) {
            1 -> { Toast.makeText(this, "Must fill every field", Toast.LENGTH_LONG).show() }

            2 -> { Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_LONG).show() }

            3 -> { Toast.makeText(this, "This user doesn't exist", Toast.LENGTH_LONG).show() }

            4 -> { Toast.makeText(this, "Email or password incorrect", Toast.LENGTH_LONG).show() }
        }
    }

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this@ActivityLogIn, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.ID_USER_MM, idUser)

        // Limpiar la pila de actividades
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Iniciar la actividad del menú principal
        startActivity(activityMainMenu)
        finish() // Finalizar la actividad actual
    }
}