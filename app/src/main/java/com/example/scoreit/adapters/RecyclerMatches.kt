package com.example.scoreit.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.ActivityRefereeButtons
import com.example.scoreit.ActivityRefereeButtons.Companion.ID_MATCH_RB
import com.example.scoreit.components.Match
import com.example.scoreit.databinding.FrameMatchBinding
import com.example.scoreit.database.Converters

class RecyclerMatches :
    RecyclerView.Adapter<RecyclerMatches.MatchViewHolder>() {

    private val dataList = mutableListOf<Match>()
    private var context: Context? = null
    private var totalMatchDays: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerMatches.MatchViewHolder {
        context = parent.context

        return MatchViewHolder(
            FrameMatchBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerMatches.MatchViewHolder, position: Int) {
        holder.binding(dataList[position], totalMatchDays)
    }

    override fun getItemCount(): Int = dataList.size

    inner class MatchViewHolder(private val binding: FrameMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(match: Match, totalMatchDays: Int) {

            val firstTeam = Converters().toTeam(match.firstTeamJson)
            val secondTeam = Converters().toTeam(match.secondTeamJson)
            val firstTeamName = firstTeam.name
            val secondTeamName = secondTeam.name

            binding.firstTeamName.text = firstTeamName
            binding.secondTeamName.text = secondTeamName

            if (!match.playable) {
                binding.playableMatch.text = "Ended Match"
            }

            if (!match.firstOfKind) {
                binding.matchSection.visibility = View.GONE
                binding.middleSpace.visibility = View.GONE
            } else {
                if (match.stage != null) {
                    val stages = mapOf(
                        1 to "Final",
                        2 to "Semi-finals",
                        4 to "Quarter-finals",
                        8 to "Round of 16",
                        16 to "Round of 32"
                    )
                    binding.matchSection.text = stages[match.stage]
                } else if (match.matchDay != null) {
                    binding.matchSection.text =
                        "Match Day ${match.matchDay} of $totalMatchDays"
                }
            }

            if (match.firstTeamRounds != null && match.secondTeamRounds != null) {
                binding.firstTeamPoints.text = "(${match.firstTeamRounds})"
                binding.secondTeamPoints.text = "(${match.secondTeamRounds})"
                binding.inGamePoints.text = "Rounds:"
            } else {
                binding.firstTeamPoints.text = match.firstTeamPoints.toString()
                binding.secondTeamPoints.text = match.secondTeamPoints.toString()
            }

            binding.currentMatchButton.setOnClickListener {
                if (match.playable) {
                    val activityRefereeButtons = Intent(context, ActivityRefereeButtons::class.java)
                    activityRefereeButtons.putExtra(ID_MATCH_RB, match.id.toString())

                    context?.startActivity(activityRefereeButtons)
                }
            }
        }
    }

    fun addDataToList(list: MutableList<Match>) {
        dataList.clear()
        val sortedList = list.sortedBy { it.matchDay }
        dataList.addAll(sortedList)

        totalMatchDays = sortedList.maxOfOrNull { it.matchDay ?: 0 } ?: 0
    }
}
