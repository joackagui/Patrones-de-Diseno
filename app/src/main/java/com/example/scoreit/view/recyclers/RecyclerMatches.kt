package com.example.scoreit.view.recyclers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.controller.activities.ActivityRefereeButtons
import com.example.scoreit.controller.activities.ActivityRefereeButtons.Companion.ID_MATCH_RB
import com.example.scoreit.model.components.Match
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.databinding.FrameMatchBinding
import kotlinx.coroutines.launch

class RecyclerMatches(private val dbAccess: AppDataBase, private val lifecycleScope: LifecycleCoroutineScope) :
    RecyclerView.Adapter<RecyclerMatches.MatchViewHolder>() {

    private val dataList = mutableListOf<Match>()
    private var context: Context? = null
    private var totalMatchDays: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MatchViewHolder {
        context = parent.context

        return MatchViewHolder(
            FrameMatchBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(dataList[position], totalMatchDays)
    }

    override fun getItemCount(): Int = dataList.size

    inner class MatchViewHolder(private val binding: FrameMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, totalMatchDays: Int) {
            lifecycleScope.launch {
                val firstTeam = dbAccess.teamDao().getTeamById(match.idFirstTeam.toString())
                val secondTeam = dbAccess.teamDao().getTeamById(match.idSecondTeam.toString())

                binding.firstTeamName.text = firstTeam.name
                binding.secondTeamName.text = secondTeam.name

                if (!match.playable) {
                    val endedMatchText = "End Match"
                    binding.playableMatch.text = endedMatchText
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
                        val matchDayText = "Match Day ${match.matchDay} of $totalMatchDays"
                        binding.matchSection.text = matchDayText
                    }
                }

                if (match.firstTeamRounds != null && match.secondTeamRounds != null) {
                    val firstTeamRoundsText = "(${match.firstTeamRounds})"
                    val secondTeamRoundsText = "(${match.secondTeamRounds})"
                    binding.firstTeamPoints.text = firstTeamRoundsText
                    binding.secondTeamPoints.text = secondTeamRoundsText
                    val roundsText = "Rounds:"
                    binding.inGamePoints.text = roundsText
                } else {
                    binding.firstTeamPoints.text = match.firstTeamPoints.toString()
                    binding.secondTeamPoints.text = match.secondTeamPoints.toString()
                }

                val isTwoLegged = dataList.count {
                    (it.idFirstTeam == match.idFirstTeam && it.idSecondTeam == match.idSecondTeam) ||
                            (it.idFirstTeam == match.idSecondTeam && it.idSecondTeam == match.idFirstTeam)
                } == 2

                binding.matchDayNumber.text = if (isTwoLegged) {
                    if (match.firstMatch) "1 of 2" else "2 of 2"
                } else {
                    "1 of 1"
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
    }

    fun addDataToList(list: MutableList<Match>) {
        dataList.clear()
        val sortedList = list.sortedBy { it.matchDay }
        dataList.addAll(sortedList)

        totalMatchDays = sortedList.maxOfOrNull { it.matchDay ?: 0 } ?: 0
    }
}
