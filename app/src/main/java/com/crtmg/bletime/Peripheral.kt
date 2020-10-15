package com.crtmg.bletime

import com.clj.fastble.data.BleDevice

data class Peripheral(
    var name: String,
    var address: String,
    var bleDevice: BleDevice? = null
) {


    fun isTimeServiceAvailable(): Boolean {
        bleDevice?.scanRecord?.let {
            return BleHelper.parseRecord(it).keys.any { it == BleHelper.EBLE.EBLE_16BitUUIDCom }
        }
        return false
    }

}

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }