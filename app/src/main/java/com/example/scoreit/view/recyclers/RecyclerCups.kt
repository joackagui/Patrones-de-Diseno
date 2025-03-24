package com.example.scoreit.view.recyclers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreit.controller.activities.ActivityCupInsight
import com.example.scoreit.controller.activities.ActivityCupInsight.Companion.ID_CUP_CI
import com.example.scoreit.model.components.Cup
import com.example.scoreit.databinding.FrameCupBinding

class RecyclerCups :
    RecyclerView.Adapter<RecyclerCups.CupViewHolder>() {

    private val dataList = mutableListOf<Cup>()
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CupViewHolder {
        context = parent.context
        return CupViewHolder(FrameCupBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: CupViewHolder, position: Int) {
        holder.binding(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class CupViewHolder(val binding: FrameCupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(cup: Cup) {

            binding.name.text = cup.name
            binding.startDate.text = cup.startDate

            binding.currentCup.setOnClickListener {
                val activityCupInsight = Intent(context, ActivityCupInsight::class.java)
                activityCupInsight.putExtra(ID_CUP_CI, cup.id.toString())

                context?.startActivity(activityCupInsight)
            }
        }
    }

    fun addDataToList(list: MutableList<Cup>) {
        dataList.clear()
        dataList.addAll(list.reversed())
    }
}