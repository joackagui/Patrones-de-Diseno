package com.example.scoreit.recyclers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.activities.ActivityNewTeamSettings
import com.example.scoreit.activities.ActivityNewTeamSettings.Companion.ID_TEAM_NT
import com.example.scoreit.components.Team
import com.example.scoreit.databinding.FrameTeamBinding

class RecyclerTeams : RecyclerView.Adapter<RecyclerTeams.TeamViewHolder>() {
    private val dataList = mutableListOf<Team>()
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        context = parent.context
        return TeamViewHolder(FrameTeamBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.binding(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class TeamViewHolder(private val binding: FrameTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(team: Team) {
            binding.teamNameText.text = team.name

            binding.enterTeam.setOnClickListener {
                val activityNewTeamSettings = Intent(context, ActivityNewTeamSettings::class.java)
                activityNewTeamSettings.putExtra(ID_TEAM_NT, team.id.toString())

                context?.startActivity(activityNewTeamSettings)
            }
        }
    }

    fun addDataToList(list: MutableList<Team>) {
        dataList.clear()
        dataList.addAll(list)
    }
}


