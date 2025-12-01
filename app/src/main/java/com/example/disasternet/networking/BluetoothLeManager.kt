package com.example.disasternet.networking

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.disasternet.data.DisasterMessage
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@SuppressLint("MissingPermission")
object BluetoothLeManager {

    private const val TAG = "DisasterNet_BLE"
    private val SERVICE_UUID = ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private const val MAX_PAYLOAD_SIZE = 20 // Max data payload per packet, leaving 2 bytes for header

    private var advertiser: BluetoothLeAdvertiser? = null
    private var scanner: BluetoothLeScanner? = null
    private val handler = Handler(Looper.getMainLooper())
    private var myDeviceName = "User-${(1000..9999).random()}"
    private var messageIdCounter: Byte = 0

    private val broadcastQueue: Queue<ByteArray> = ConcurrentLinkedQueue()
    private var isBroadcasting = false

    private val _messages = MutableLiveData<MutableList<DisasterMessage>>(mutableListOf())
    val messages: LiveData<MutableList<DisasterMessage>> = _messages
    private val discoveredDevices = ConcurrentHashMap<String, String>()
    private val _devices = MutableLiveData<List<Pair<String, String>>>(emptyList())
    val devices: LiveData<List<Pair<String, String>>> = _devices

    private val seenMessageIds = Collections.newSetFromMap(ConcurrentHashMap<Byte, Boolean>())
    private val messageReassemblyBuffer = ConcurrentHashMap<Byte, MutableMap<Byte, ByteArray>>()

    fun initialize(bluetoothAdapter: BluetoothAdapter) {
        this.advertiser = bluetoothAdapter.bluetoothLeAdvertiser
        this.scanner = bluetoothAdapter.bluetoothLeScanner
        myDeviceName = bluetoothAdapter.name?.ifEmpty { myDeviceName } ?: myDeviceName
    }

    // --- BROADCASTING LOGIC ---
    fun broadcastMessage(message: String) {
        val messageId = getNextMessageId()
        val formattedMessage = "MSG::$myDeviceName::$message"

        // Broadcast the first time
        chunkAndQueue(messageId, formattedMessage)

        // **THE FIX: Schedule the second, redundant broadcast after 1 second**
        handler.postDelayed({
            Log.d(TAG, "Re-broadcasting messageId: $messageId")
            chunkAndQueue(messageId, formattedMessage)
        }, 1000) // 1000 milliseconds = 1 second

        val sentMessage = DisasterMessage(messageId.toString(), myDeviceName, message, System.currentTimeMillis(), true)
        addMessageToLiveData(sentMessage)
    }

    fun broadcastSos(lat: Double, lon: Double) {
        val messageId = getNextMessageId()
        val content = "${String.format(Locale.US, "%.6f", lat)},${String.format(Locale.US, "%.6f", lon)}"
        val formattedMessage = "SOS::$myDeviceName::$content"

        // Broadcast the first time
        chunkAndQueue(messageId, formattedMessage)

        // **THE FIX: Schedule the second, redundant broadcast after 1 second**
        handler.postDelayed({
            Log.d(TAG, "Re-broadcasting SOS messageId: $messageId")
            chunkAndQueue(messageId, formattedMessage)
        }, 1000)

        val sentSosMessage = DisasterMessage(messageId.toString(), "$myDeviceName (SOS)", "🚨 EMERGENCY! Location:\n$content", System.currentTimeMillis(), true)
        addMessageToLiveData(sentSosMessage)
    }

    private fun getNextMessageId(): Byte {
        if (messageIdCounter == Byte.MAX_VALUE) {
            messageIdCounter = 0
        }
        return messageIdCounter++
    }

    private fun chunkAndQueue(messageId: Byte, fullMessage: String) {
        val dataBytes = fullMessage.toByteArray(Charset.defaultCharset())
        val chunks = dataBytes.asSequence().chunked(MAX_PAYLOAD_SIZE).map { it.toByteArray() }.toList()
        val totalChunks = chunks.size.toByte()

        chunks.forEachIndexed { index, chunkData ->
            val chunkNum = index.toByte()
            val chunkInfo: Byte = (chunkNum.toInt() shl 4 or totalChunks.toInt()).toByte()
            val packet = byteArrayOf(messageId, chunkInfo) + chunkData
            broadcastQueue.add(packet)
        }
        if (!isBroadcasting) {
            processBroadcastQueue()
        }
    }

    private fun processBroadcastQueue() {
        if (broadcastQueue.isEmpty()) {
            isBroadcasting = false
            advertiser?.stopAdvertising(advertiseCallback)
            return
        }
        isBroadcasting = true
        val packet = broadcastQueue.poll()

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false).build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(SERVICE_UUID)
            .addServiceData(SERVICE_UUID, packet).build()

        advertiser?.stopAdvertising(advertiseCallback)
        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.d(TAG, "SUCCESS: Packet broadcasted.")
            handler.postDelayed({ processBroadcastQueue() }, 300)
        }
        override fun onStartFailure(errorCode: Int) {
            Log.e(TAG, "FAILURE: Advertising failed: $errorCode")
            isBroadcasting = false
        }
    }

    // --- SCANNING LOGIC ---
    fun startScanning() {
        if (scanner == null) { return }
        val scanFilter = ScanFilter.Builder().setServiceUuid(SERVICE_UUID).build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        scanner?.startScan(listOf(scanFilter), settings, scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                val serviceData = it.scanRecord?.getServiceData(SERVICE_UUID)
                serviceData?.let { data -> handlePacket(data) }
            }
        }
    }

    private fun handlePacket(packet: ByteArray) {
        try {
            if (packet.size < 2) return
            val messageId = packet[0]
            val chunkInfo = packet[1]
            val chunkNum = (chunkInfo.toInt() shr 4 and 0x0F).toByte()
            val totalChunks = (chunkInfo.toInt() and 0x0F).toByte()
            val payload = packet.copyOfRange(2, packet.size)

            val buffer = messageReassemblyBuffer.getOrPut(messageId) { mutableMapOf() }
            buffer[chunkNum] = payload

            if (buffer.size == totalChunks.toInt()) {
                val fullMessageBytes = (0 until totalChunks).map { buffer[it.toByte()] }.fold(byteArrayOf()) { acc, bytes -> acc + (bytes ?: byteArrayOf()) }
                processReassembledMessage(messageId, String(fullMessageBytes, Charset.defaultCharset()))
                messageReassemblyBuffer.remove(messageId)
            }
        } catch (e: Exception) {
            // Malformed packet
        }
    }

    private fun processReassembledMessage(messageId: Byte, messageStr: String) {
        if (seenMessageIds.contains(messageId)) {
            Log.d(TAG, "Ignoring duplicate message with ID: $messageId")
            return
        }
        seenMessageIds.add(messageId)
        val parts = messageStr.split("::")
        if (parts.size < 3) return
        val type = parts[0]; val senderName = parts[1]; val content = parts[2]
        if (senderName == myDeviceName) return
        val message = when (type) {
            "MSG" -> DisasterMessage(messageId.toString(), senderName, content, System.currentTimeMillis(), false)
            "SOS" -> DisasterMessage(messageId.toString(), "$senderName (SOS)", "🚨 EMERGENCY! Location:\n$content", System.currentTimeMillis(), false)
            else -> null
        }
        message?.let { addMessageToLiveData(it) }
    }

    private fun addMessageToLiveData(message: DisasterMessage) {
        val currentList = _messages.value ?: mutableListOf()
        currentList.add(message)
        _messages.postValue(currentList)
    }

    fun stop() {
        scanner?.stopScan(scanCallback)
        advertiser?.stopAdvertising(advertiseCallback)
    }
}