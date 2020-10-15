package com.crtmg.bletime


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.crtmg.bletime.BleApp.Companion.CHANNEL_ID
import java.nio.ByteBuffer
import java.time.DayOfWeek
import java.util.*


object CentralManager {

    private val Time_Service_UUID: UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
    private val Time_Characteristic_UUID: UUID =
        UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb")
    private val SCAN_PERIOD = 5000L
    private val WAIT_PERIOD = 4000L

    val model = PeripheralListModel()
    var bleManager: BleManager? = null


    fun initBle(application: Application) {
        val scanRuleConfig = BleScanRuleConfig.Builder()
            .setScanTimeOut(SCAN_PERIOD)
            .setAutoConnect(true)
            .build()
        bleManager = BleManager.getInstance()
        bleManager?.init(application)
        bleManager?.initScanRule(scanRuleConfig)
        bleManager?.enableBluetooth()
    }

    fun getConnected(): List<Peripheral> {
        bleManager?.let { bleManager ->
            return model.list.list.filter { bleManager.isConnected(it.bleDevice) }
        }
        return listOf()
    }

    fun startScan() {

        bleManager?.let { bleManager ->
            bleManager.disconnectAllDevice()
            model.clear()
            bleManager.scan(object : BleScanCallback() {
                override fun onScanStarted(success: Boolean) {
                    Log.e("oSS", "onScanStarted with $success")
                }

                override fun onScanning(bleDevice: BleDevice?) {
                    model.addPeripheral(
                        Peripheral(
                            bleDevice?.name ?: "UnNamed",
                            bleDevice?.mac ?: "",
                            bleDevice
                        )
                    )
                }

                override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                    Log.e("oSF", " result count = ${scanResultList?.count()}")
                }
            })
        }
    }

    fun itemClicked(position: Int) {
        val peripheral = model.getPeripheral(position)
        if (peripheral.isTimeServiceAvailable()) {
            bleManager?.let { bleManager ->

                if (bleManager.isConnected(peripheral.bleDevice)) {
                    readTime(peripheral)
                    return
                }

                bleManager.connect(peripheral.bleDevice, object : BleGattCallback() {
                    override fun onStartConnect() {
                    }

                    override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                    }

                    override fun onConnectSuccess(
                        bleDevice: BleDevice?,
                        gatt: BluetoothGatt?,
                        status: Int
                    ) { // e407 0a 0f 00 29 38 04 e202
                        readTime(peripheral)
                    }

                    override fun onDisConnected(
                        isActiveDisConnected: Boolean,
                        device: BleDevice?,
                        gatt: BluetoothGatt?,
                        status: Int
                    ) {
                    }

                })


            }
        }
        Log.e("itemClicked", " >>>> $position <<<<<")
    }


    fun readTime(peripheral: Peripheral) {
        bleManager?.read(
            peripheral.bleDevice,
            Time_Service_UUID.toString(),
            Time_Characteristic_UUID.toString(),
            object : BleReadCallback() {
                override fun onReadSuccess(data: ByteArray?) {
                    data?.let { data ->
                        val year = ByteBuffer.wrap(
                            data.copyOfRange(0, 2).reversedArray()
                                .addLeft(0.toByte(), 2)
                        ).int
                        val month = data[2].toInt()
                        val day = data[3].toInt()
                        val hours = data[4].toInt()
                        val minutes = data[5].toInt()
                        val seconds = data[6].toInt()
                        val dayOfWeek = data[7].toInt()
                        Log.e(
                            "oRC",
                            "${data.toHexString()} $year $month $day $hours:$minutes:$seconds ${
                                DayOfWeek.of(dayOfWeek)
                            }"
                        )
                        runAsNotification(
                            bleManager!!.context, "$year $month $day $hours:$minutes:$seconds ${
                                DayOfWeek.of(dayOfWeek)
                            }"
                        )
                    }

                }

                override fun onReadFailure(exception: BleException?) {
                    Log.e("oRF", "${exception?.description} ${exception?.code}")
                }

            })
    }


    fun runAsNotification(context: Context, text: String) {
        val notificationBuilder = NotificationCompat.Builder(context, "BLETimer")

        notificationBuilder.setContentTitle("Current time is")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setChannelId(CHANNEL_ID)
            .setOngoing(true)

        val notification = notificationBuilder.build()


        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(CHANNEL_ID, "Timer", importance)
            mNotificationManager.createNotificationChannel(mChannel)

        }

        mNotificationManager.notify(1, notification)
    }

}

fun ByteArray.addLeft(element: Byte, count: Int): ByteArray {
    val arrayList = ArrayList<Byte>()
    for (i in 0 until count) {
        arrayList.add(element)
    }
    arrayList.addAll(this.toTypedArray())
    return arrayList.toByteArray()
}