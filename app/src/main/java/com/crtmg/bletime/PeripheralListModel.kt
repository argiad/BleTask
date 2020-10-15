package com.crtmg.bletime

import androidx.databinding.ObservableInt
import java.util.*

class PeripheralListModel(
    var list: PeripheralList = PeripheralList(),
    val totalItems: ObservableInt = ObservableInt()
) : Observable() {

    fun getPeripheral(position: Int) = list.list[position]

    fun clear() {
        list.list.clear()
        totalItems.set(0)
        setChanged()
        notifyObservers()
    }

    fun addPeripheral(peripheral: Peripheral) {
        if (!list.list.any { it.address == peripheral.address }) {
            list.addPeripheral(peripheral)
            totalItems.set(list.list.count())
            setChanged()
            notifyObservers()
        }
    }

}