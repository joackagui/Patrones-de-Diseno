package com.example.scoreit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.componentes.Team
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
            binding.teamIngamePoints.text = team.ingamePoints
            binding.teamFinalPoints.text = team.finalPoints
            binding.teamMatchesPlayed.text = team.matchesPlayed
            binding.teamMatchesWon.text = team.matchesWon
            binding.teamMatchesLost.text = team.matchesLost
        }
    }

    fun addDataToList(list: MutableList<Team>) {
        dataList.clear()
        dataList.addAll(list)
    }
}
