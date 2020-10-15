package com.crtmg.bletime

data class PeripheralList(var list: MutableList<Peripheral> = mutableListOf()) {

    fun addPeripheral(peripheral: Peripheral) {
        list.add(peripheral)
    }
}