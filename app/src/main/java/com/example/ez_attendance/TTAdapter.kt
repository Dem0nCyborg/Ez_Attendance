package com.example.ez_attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TTAdapter(private val timetable: List<timetable>) : RecyclerView.Adapter<TTAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val day: TextView = itemView.findViewById(R.id.day)
        val lec1: TextView = itemView.findViewById(R.id.lec1)
        val lec2: TextView = itemView.findViewById(R.id.lec2)
        val lec3: TextView = itemView.findViewById(R.id.lec3)
        val lec4: TextView = itemView.findViewById(R.id.lec4)
        val lec5: TextView = itemView.findViewById(R.id.lec5)
        val lec6: TextView = itemView.findViewById(R.id.lec6)
        val lec7: TextView = itemView.findViewById(R.id.lec7)
        val lec8: TextView = itemView.findViewById(R.id.lec8)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TTAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.time_table_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TTAdapter.MyViewHolder, position: Int) {
        val currentItem = timetable[position]
        holder.day.text = currentItem.day
        holder.lec1.text = currentItem.lec1
        holder.lec2.text = currentItem.lec2
        holder.lec3.text = currentItem.lec3
        holder.lec4.text = currentItem.lec4
        holder.lec5.text = currentItem.lec5
        holder.lec6.text = currentItem.lec6
        holder.lec7.text = currentItem.lec7
        holder.lec8.text = currentItem.lec8
    }

    override fun getItemCount(): Int {
        return timetable.size
    }


}