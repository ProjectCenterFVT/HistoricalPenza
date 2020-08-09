package com.projectcenterfvt.historicalpenza.landmarks_list

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.utils.inflate
import kotlinx.android.synthetic.main.item_landmark.view.*


class LandmarksAdapter(private var data: List<LandmarkAdapterItem>,
                       val onItemClickListener : (LandmarkAdapterItem) -> Unit)
    : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {

    fun setData(list: List<LandmarkAdapterItem>) {
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

            if (item.distance != -1L) {
                distance.text = item.getPrettyDistance()
            }

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
