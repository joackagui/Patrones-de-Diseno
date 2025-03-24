package com.example.scoreit.controller.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.view.recyclers.RecyclerUsers
import com.example.scoreit.model.components.User
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.model.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivitySelectUserBinding
import kotlinx.coroutines.launch

class ActivitySelectUser : AppCompatActivity() {

    // Inicialización perezosa del adaptador para la lista de usuarios
    private val recyclerUsers: RecyclerUsers by lazy { RecyclerUsers() }

    // Binding para la actividad de selección de usuario
    private lateinit var binding: ActivitySelectUserBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivitySelectUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Crear cuentas de usuario si no existen
        createAccounts()
    }

    // Función para crear cuentas de usuario por defecto si no hay ninguna
    private fun createAccounts() {
        lifecycleScope.launch {
            // Verificar si no hay usuarios en la base de datos
            if (dbAccess.userDao().getEveryUser().isEmpty()) {
                // Crear 5 usuarios por defecto
                for (i in 1..5) {
                    val nuevoUser = User(name = "Usuario $i")
                    dbAccess.userDao().insert(nuevoUser) // Insertar el nuevo usuario en la base de datos
                }
            }
            // Configurar el RecyclerView después de crear las cuentas
            setUpRecyclerView()
        }
    }

    // Función para configurar el RecyclerView con la lista de usuarios
    private fun setUpRecyclerView() {
        lifecycleScope.launch {
            // Obtener la lista de todos los usuarios de la base de datos
            val listOfUsers = dbAccess.userDao().getEveryUser().toMutableList()
            // Agregar los datos al adaptador
            recyclerUsers.addDataToList(listOfUsers)

            // Configurar el RecyclerView
            binding.recyclerUsers.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = recyclerUsers
            }
        }
    }
}