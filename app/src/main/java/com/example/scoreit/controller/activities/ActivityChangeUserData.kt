package com.example.scoreit.controller.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.model.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityChangeUserDataBinding
import kotlinx.coroutines.launch

class ActivityChangeUserData : AppCompatActivity() {

    // Binding para la actividad de cambio de datos de usuario
    private lateinit var binding: ActivityChangeUserDataBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_USER_CUD: String = "USER_ID" // Clave para pasar el ID del usuario entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityChangeUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Establecer los datos del usuario en la UI
        setData()

        // Configurar el botón para cambiar de usuario
        changeUserButton()

        // Configurar el botón para eliminar los datos del usuario
        deleteButton()

        // Configurar el botón para guardar los cambios en los datos del usuario
        saveButton()
    }

    // Función para configurar el botón de cambio de usuario
    private fun changeUserButton() {
        binding.changeUserButton.setOnClickListener {
            changeToActivitySelectUser() // Navegar a la actividad de selección de usuario
        }
    }

    // Función para configurar el botón de eliminación de datos del usuario
    private fun deleteButton() {
        binding.deleteButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_CUD) // Obtener el ID del usuario
            if (idUser != null) {
                lifecycleScope.launch {
                    // Obtener el usuario de la base de datos
                    val user = dbAccess.userDao().getUserById(idUser)
                    // Restablecer los datos del usuario a valores predeterminados
                    user.name = "User ${user.id}"
                    user.email = null
                    user.password = null
                    user.logo = null
                    // Actualizar el usuario en la base de datos
                    dbAccess.userDao().update(user)
                    // Eliminar todos los datos asociados al usuario
                    deleteEverythingOfUser(idUser)
                    // Navegar a la actividad de selección de usuario
                    changeToActivitySelectUser()
                }
            }
        }
    }

    // Función para configurar el botón de guardar cambios
    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_CUD) // Obtener el ID del usuario
            if (idUser != null) {
                lifecycleScope.launch {
                    // Obtener el usuario de la base de datos
                    val user = dbAccess.userDao().getUserById(idUser)
                    // Actualizar los datos del usuario con los valores de la UI
                    user.name = binding.userName.text.toString()
                    user.email = binding.userEmail.text.toString()
                    user.password = binding.userPassword.text.toString()
                    // Actualizar el usuario en la base de datos
                    dbAccess.userDao().update(user)
                    // Navegar al menú principal
                    changeToActivityMainMenu(idUser)
                }
            }
        }
    }

    // Función para establecer los datos del usuario en la UI
    private fun setData() {
        val idUser = intent.getStringExtra(ID_USER_CUD) // Obtener el ID del usuario
        if (idUser != null) {
            lifecycleScope.launch {
                // Obtener el usuario de la base de datos
                val user = dbAccess.userDao().getUserById(idUser)
                // Establecer los valores en los campos de texto de la UI
                binding.userName.setText(user.name)
                binding.userEmail.setText(user.email)
                binding.userPassword.setText(user.password)
            }
        }
    }

    // Función para eliminar todos los datos asociados a un usuario
    private fun deleteEverythingOfUser(idUser: String) {
        lifecycleScope.launch {
            // Eliminar todas las copas asociadas al usuario
            dbAccess.cupDao().deleteCupsByIdUser(idUser)
        }
    }

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        // Pasar el ID del usuario a la actividad del menú principal
        activityMainMenu.putExtra(ActivityMainMenu.ID_USER_MM, idUser)
        // Establecer flags para limpiar la pila de actividades
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Iniciar la actividad del menú principal
        startActivity(activityMainMenu)
        finish() // Finalizar la actividad actual
    }

    // Función para navegar a la actividad de selección de usuario
    private fun changeToActivitySelectUser() {
        val activitySelectUser = Intent(this, ActivitySelectUser::class.java)
        // Establecer flags para limpiar la pila de actividades
        activitySelectUser.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Iniciar la actividad de selección de usuario
        startActivity(activitySelectUser)
        finish() // Finalizar la actividad actual
    }
}