package com.example.scoreit.view.recyclers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.controller.activities.ActivityLogIn
import com.example.scoreit.controller.activities.ActivityLogIn.Companion.ID_USER_LI
import com.example.scoreit.controller.activities.ActivityMainMenu
import com.example.scoreit.controller.activities.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.controller.activities.ActivitySignUp
import com.example.scoreit.controller.activities.ActivitySignUp.Companion.ID_USER_SU
import com.example.scoreit.model.components.User
import com.example.scoreit.databinding.FrameUserBinding

class RecyclerUsers :
    RecyclerView.Adapter<RecyclerUsers.UserViewHolder>() {

    private val dataList = mutableListOf<User>()
    private var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        context = parent.context

        return UserViewHolder(FrameUserBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class UserViewHolder(private val binding: FrameUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(user: User) {
            binding.userName.text = user.name
            //binding.userLogo.setImageResource(user.logo.toInt())

            binding.currentUserButton.setOnClickListener {
                if(user.email == null && user.password == null){
                    val activitySignUp = Intent(context, ActivitySignUp::class.java)
                    activitySignUp.putExtra(ID_USER_SU, user.id.toString())

                    context?.startActivity(activitySignUp)
                } else {
                    if(user.lastUser){
                        val activityMainMenu = Intent(context, ActivityMainMenu::class.java)
                        activityMainMenu.putExtra(ID_USER_MM, user.id.toString())
                        successfulMessage(user)

                        context?.startActivity(activityMainMenu)
                    } else {
                        val activityLogIn = Intent(context, ActivityLogIn::class.java)
                        activityLogIn.putExtra(ID_USER_LI, user.id.toString())

                        context?.startActivity(activityLogIn)
                    }
                }
            }
        }
    }

    private fun successfulMessage(user: User) {
        Toast.makeText(context, "Welcome ${user.name}", Toast.LENGTH_LONG).show()
    }

    fun addDataToList(list: MutableList<User>) {
        dataList.clear()
        dataList.addAll(list)
    }
}
