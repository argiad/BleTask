package com.crtmg.bletime

import java.util.HashMap
import kotlin.math.pow

class BleHelper {
    companion object {
        const val UUID_1800 = "00001800-0000-1000-8000-00805f9b34fb"
        const val UUID_1801 = "00001801-0000-1000-8000-00805f9b34fb"
        const val UUID_180A = "0000180a-0000-1000-8000-00805f9b34fb"
        const val UUID_180F = "0000180f-0000-1000-8000-00805f9b34fb"
        const val UUID_SERVICE = "07a2715c-3b28-4f7f-8824-ba18255b2fd7"


        const val CHAR_CMD = "07A2715D-3B28-4F7F-8824-BA18255B2FD7"
        const val CHAR_INFO = "07A2715F-3B28-4F7F-8824-BA18255B2FD7"

        const val CMD_DFU = "01"
        const val CMD_GET_MAC = "1608"
        const val CMD_GET_SOS = "1607"
        const val CMD_ROUTE = "0F%s%s01AABBCCDDEEFF"
        const val CMD_START_DEMO = "61%s4E030000000000000000000000"
        const val CMD_RED_LIGHT = "61%s4E0401012c0000000000000000"
        const val CMD_GREEN_LIGHT = "61%s4E0402012c0000000000000000"
        const val CMD_MESSAGE = "3901FFFFFFFFF8%s%s%s"

        const val CMD_REQUEST_FOR_DATA = "573FFFFFFFFFF0%s4eFEFFFFFFFFFF"
        const val CMD_CLEAR_DATA_WB = "381ffffffffff8%s5202FFFFFFFFFF"

        const val LED_GREEN = 1
        const val LED_RED = 2

        const val MT_34 = 34

        const val pathOld = "1FFFFFFFFFF8"
        const val pathNew = "0FFFFFFFFFF0"

        const val NAME_WB = "WB"
        const val NAME_MESHB = "MESHB"
        const val NAME_MESHN = "MESHN"
        const val NAME_MES4N = "MES4N"
        val ACCEPTED_DEVICES_NAME = arrayOf(NAME_WB, NAME_MESHN)


        /*
         BLE Scan record parsing
        */
        fun parseRecord(scanRecord: ByteArray): Map<EBLE, ByteArray> {
            val ret = HashMap<EBLE, ByteArray>()
            var index = 0
            while (index < scanRecord.size) {
                val length = scanRecord[index++].toInt()
                //Zero value indicates that we are done with the record now
                if (length == 0) break

                val type = scanRecord[index].toInt()
                //if the type is zero, then we are pass the significant section of the data,
                // and we are thud done
                if (type == 0) break

                scanRecord.copyOfRange(index + 1, index + length).let {
                    ret[EBLE.fromInt(type)] = it //HexUtil.formatHexString(it)
                }

                index += length
            }

            return ret
        }

        fun getServiceUUID(record: Map<EBLE, String>): String {
            var ret = ""
            // for example: 0105FACB00B01000800000805F9B34FB --> 010510ee-0000-1000-8000-00805f9b34fb
            if (record.containsKey(EBLE.EBLE_128BitUUIDCom)) {
                val tmpString = record[EBLE.EBLE_128BitUUIDCom].toString()
                ret = tmpString.substring(0, 8) + "-" + tmpString.substring(
                    8,
                    12
                ) + "-" + tmpString.substring(
                    12,
                    16
                ) + "-" + tmpString.substring(16, 20) + "-" + tmpString.substring(
                    20,
                    tmpString.length
                )
                //010510EE --> 010510ee-0000-1000-8000-00805f9b34fb
            } else if (record.containsKey(EBLE.EBLE_32BitUUIDCom)) {
                ret = record[EBLE.EBLE_32BitUUIDCom].toString() + "-0000-1000-8000-00805f9b34fb"
            }
            return ret
        }
    }


    /*
      BLE Scan record type IDs
      data from:
      https://www.bluetooth.org/en-us/specification/assigned-numbers/generic-access-profile
    */
    enum class EBLE {

        EBLE_ZERO, // Zero element
        EBLE_FLAGS, //«Flags»	Bluetooth Core Specification:
        EBLE_16BitUUIDInc, //«Incomplete List of 16-bit Service Class UUIDs»	Bluetooth Core Specification:
        EBLE_16BitUUIDCom, //«Complete List of 16-bit Service Class UUIDs»	Bluetooth Core Specification:
        EBLE_32BitUUIDInc,//«Incomplete List of 32-bit Service Class UUIDs»	Bluetooth Core Specification:
        EBLE_32BitUUIDCom,//«Complete List of 32-bit Service Class UUIDs»	Bluetooth Core Specification:
        EBLE_128BitUUIDInc,//«Incomplete List of 128-bit Service Class UUIDs»	Bluetooth Core Specification:
        EBLE_128BitUUIDCom,//«Complete List of 128-bit Service Class UUIDs»	Bluetooth Core Specification:
        EBLE_SHORTNAME,//«Shortened Local Name»	Bluetooth Core Specification:
        EBLE_LOCALNAME,//«Complete Local Name»	Bluetooth Core Specification:
        EBLE_TXPOWERLEVEL,//«Tx Power Level»	Bluetooth Core Specification:
        EBLE_DEVICECLASS,//«Class of Device»	Bluetooth Core Specification:
        EBLE_SIMPLEPAIRHASH,//«Simple Pairing Hash C»	Bluetooth Core Specification:​«Simple Pairing Hash C-192»	​Core Specification Supplement, Part A, section 1.6
        EBLE_SIMPLEPAIRRAND,//«Simple Pairing Randomizer R»	Bluetooth Core Specification:​«Simple Pairing Randomizer R-192»	​Core Specification Supplement, Part A, section 1.6
        EBLE_DEVICEID,//«Device ID»	Device ID Profile v1.3 or later,«Security Manager TK Value»	Bluetooth Core Specification:
        EBLE_SECURITYMANAGER,//«Security Manager Out of Band Flags»	Bluetooth Core Specification:
        EBLE_SLAVEINTERVALRA,//«Slave Connection Interval Range»	Bluetooth Core Specification:
        EBLE_16BitSSUUID,//«List of 16-bit Service Solicitation UUIDs»	Bluetooth Core Specification:
        EBLE_128BitSSUUID, //«List of 128-bit Service Solicitation UUIDs»	Bluetooth Core Specification:
        EBLE_SERVICEDATA,//«Service Data»	Bluetooth Core Specification:​«Service Data - 16-bit UUID»	​Core Specification Supplement, Part A, section 1.11
        EBLE_PTADDRESS,//«Public Target Address»	Bluetooth Core Specification:
        EBLE_RTADDRESS,//«Random Target Address»	Bluetooth Core Specification:
        EBLE_APPEARANCE,//«Appearance»	Bluetooth Core Specification:
        EBLE_DEVADDRESS,//«​LE Bluetooth Device Address»	​Core Specification Supplement, Part A, section 1.16
        EBLE_LEROLE,//«​LE Role»	​Core Specification Supplement, Part A, section 1.17
        EBLE_PAIRINGHASH,//«​Simple Pairing Hash C-256»	​Core Specification Supplement, Part A, section 1.6
        EBLE_PAIRINGRAND,//«​Simple Pairing Randomizer R-256»	​Core Specification Supplement, Part A, section 1.6
        EBLE_32BitSSUUID,//​«List of 32-bit Service Solicitation UUIDs»	​Core Specification Supplement, Part A, section 1.10
        EBLE_32BitSERDATA,//​«Service Data - 32-bit UUID»	​Core Specification Supplement, Part A, section 1.11
        EBLE_128BitSERDATA,//​«Service Data - 128-bit UUID»	​Core Specification Supplement, Part A, section 1.11
        EBLE_SECCONCONF,//​«​LE Secure Connections Confirmation Value»	​Core Specification Supplement Part A, Section 1.6
        EBLE_SECCONRAND,//​​«​LE Secure Connections Random Value»	​Core Specification Supplement Part A, Section 1.6​
        EBLE_3DINFDATA, //​​«3D Information Data»	​3D Synchronization Profile, v1.0 or later
        EBLE_MANDATA; //«Manufacturer Specific Data»	Bluetooth Core Specification:

        companion object {
            private val map = EBLE.values()
            fun fromInt(type: Int) = if (type > 0) map[type] else EBLE_MANDATA

            fun getDistance(rssi: Int, txPower: Int) = run {
                /*
                                        * RSSI = TxPower - 10 * n * lg(d)
                                        * n = 2 (in free space)
                                        *
                                        * d = 10 ^ ((TxPower - RSSI) / (10 * n))
                                       */

                10.0.pow((txPower.toDouble() - rssi) / (10 * 2))
            }
        }
    }
}