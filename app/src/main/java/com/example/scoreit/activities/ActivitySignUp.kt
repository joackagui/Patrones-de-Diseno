package com.example.scoreit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.components.User
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class ActivitySignUp : AppCompatActivity() {

    // Binding para la actividad de registro de usuario
    private lateinit var binding: ActivitySignUpBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_USER_SU: String = "ID_USER" // Clave para pasar el ID del usuario entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar el botón de registro
        signUpButton()
    }

    // Función para configurar el botón de registro
    private fun signUpButton() {
        binding.createAccountButton.setOnClickListener {
            val password = binding.passwordSignUp.text.toString()
            val confirmPassword = binding.passwordSafetySignUp.text.toString()
            // Verificar si todos los campos están completos
            if (binding.usernameSignUp.text.toString().isEmpty() || binding.emailSignUp.text.toString().isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()
            ) {
                errorMessage(1) // Mostrar mensaje de error si algún campo está vacío
            } else if (password != confirmPassword) {
                errorMessage(3) // Mostrar mensaje de error si las contraseñas no coinciden
            } else if (password.length < 8) {
                errorMessage(2) // Mostrar mensaje de error si la contraseña es demasiado corta
            } else if (!Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+").matches(password)) {
                errorMessage(4)
            } else {
                // Asegurarse de que ningún otro usuario esté marcado como último usuario
                noOneIsLastUser()
                // Guardar el nuevo usuario
                saveUser()
            }
        }
    }

    // Función para guardar el nuevo usuario en la base de datos
    private fun saveUser() {
        lifecycleScope.launch {
            val email = binding.emailSignUp.text.toString()
            val password = binding.passwordSignUp.text.toString()
            val name = binding.usernameSignUp.text.toString()
            val idUser = intent.getStringExtra(ID_USER_SU)
            if (idUser != null) {
                // Crear un nuevo objeto User con los datos proporcionados
                val newUser =
                    User(
                        email = email,
                        name = name,
                        password = password,
                        lastUser = true, // Marcar este usuario como el último que inició sesión
                        id = idUser.toInt()
                    )

                // Actualizar el usuario en la base de datos
                dbAccess.userDao().update(newUser)
                // Mostrar mensaje de éxito
                successfulMessage()
                // Navegar al menú principal
                changeToActivityMainMenu(idUser)
            }
        }
    }

    // Función para asegurarse de que ningún otro usuario esté marcado como último usuario
    private fun noOneIsLastUser() {
        lifecycleScope.launch {
            val listOfUsers = dbAccess.userDao().getEveryUser().toMutableList()
            for (user in listOfUsers) {
                if (user.lastUser) {
                    // Desmarcar el último usuario anterior
                    user.lastUser = false
                    dbAccess.userDao().update(user)
                }
            }
        }
    }

    // Función para mostrar mensajes de error
    private fun errorMessage(number: Int) {
        when (number) {
            1 -> { Toast.makeText(this, "Must fill every field", Toast.LENGTH_LONG).show() }

            2 -> { Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_LONG).show() }

            3 -> { Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show() }

            4 -> { Toast.makeText(this, "Password needs Uppercase, Lowercase and Number", Toast.LENGTH_LONG).show() }}

    }

    // Función para mostrar un mensaje de éxito
    private fun successfulMessage() {
        Toast.makeText(this, "User created successfully", Toast.LENGTH_LONG).show()
    }

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.Companion.ID_USER_MM, idUser)

        // Limpiar la pila de actividades
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Iniciar la actividad del menú principal
        startActivity(activityMainMenu)
        finish() // Finalizar la actividad actual
    }
}