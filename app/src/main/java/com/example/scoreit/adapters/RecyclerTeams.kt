package com.example.scoreit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.componentes.Team
import com.example.scoreit.databinding.FrameTeamBinding

class RecyclerTeams : RecyclerView.Adapter<RecyclerTeams.TeamViewHolder>() {
    private val listaDatos = mutableListOf<Team>()
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        context = parent.context
        return TeamViewHolder(FrameTeamBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.binding(listaDatos[position])
    }

    override fun getItemCount(): Int = listaDatos.size

    inner class TeamViewHolder(private val binding: FrameTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(team: Team) {
            binding.teamNameText.text = team.name

            binding.enterTeamButton.setOnClickListener {
                //TODO: Ir al team
//                val activityEntrarAlEquipo = Intent(context, ActivityGogo::class.java)
//                activityEntrarAlEquipo.putExtra(EQUIPO_ID, team.id.toString())
//                context?.startActivity(activityEntrarAlEquipo)
            }
        }
    }

    fun addDataToList(list: MutableList<Team>) {
        listaDatos.clear()
        listaDatos.addAll(list)
    }
}


