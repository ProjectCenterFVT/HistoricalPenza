package com.projectcenterfvt.historicalpenza.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.utils.inflate
import kotlinx.android.synthetic.main.item_landmark.view.*


class LandmarksAdapter(private var data: List<Landmark>,
                       val onItemClickListener : (Landmark) -> Unit)
    : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {

    fun setData(list: List<Landmark>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_landmark)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        with (holder.itemView) {
            title.text = item.title
            distance.text = "35 км"
            setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
