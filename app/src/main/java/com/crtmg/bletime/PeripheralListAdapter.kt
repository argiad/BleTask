package com.crtmg.bletime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crtmg.bletime.databinding.ItemBinding
import java.util.*

class PeripheralListAdapter(private var dataModel: PeripheralListModel = CentralManager.model) :
    RecyclerView.Adapter<PeripheralListAdapter.MyViewHolder>(), Observer {

    init {
        dataModel.addObserver(this)
    }

    class MyViewHolder(private val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.item = CentralManager.model.getPeripheral(position)
            binding.position = position
            binding.centralManager = CentralManager
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataModel.list.list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(position)

    override fun update(o: Observable?, arg: Any?) {
        notifyDataSetChanged()
    }
}