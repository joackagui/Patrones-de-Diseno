package com.example.scoreit.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.components.Team
import com.example.scoreit.databinding.ScoreBoardRowBinding

class RecyclerScoreBoardRows : RecyclerView.Adapter<RecyclerScoreBoardRows.ScoreBoardViewHolder>() {
    private val dataList = mutableListOf<Team>()
    private var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerScoreBoardRows.ScoreBoardViewHolder {
        context = parent.context

        return ScoreBoardViewHolder(
            ScoreBoardRowBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerScoreBoardRows.ScoreBoardViewHolder,
        position: Int
    ) {
        holder.binding(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class ScoreBoardViewHolder(private val binding: ScoreBoardRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(team: Team) {
            binding.teamName.text = team.name
            val newInGamePoints = (team.pointsWon - team.pointsLost).toString()
            val newFinalPoints = team.finalPoints.toString()
            val newMatchesPlayed = team.matchesPlayed.toString()
            val newMatchesWon = team.matchesWon.toString()
            val newMatchesLost = team.matchesLost.toString()

            binding.teamMatchesPlayed.text = newMatchesPlayed
            binding.teamMatchesWon.text = newMatchesWon
            binding.teamMatchesLost.text = newMatchesLost
            binding.teamFinalPoints.text = newFinalPoints
            binding.teamInGamePoints.text = newInGamePoints
        }
    }

    private fun sortTeams(listOfTeams: MutableList<Team>): List<Team> {
        return listOfTeams.sortedWith(
            compareByDescending<Team> { it.finalPoints }
                .thenByDescending { (it.pointsWon - it.pointsLost) }
                .thenBy { it.name }
        )
    }

    fun addDataToList(list: MutableList<Team>) {
        dataList.clear()
        val sortedList = sortTeams(list)
        dataList.addAll(sortedList)
    }
}
