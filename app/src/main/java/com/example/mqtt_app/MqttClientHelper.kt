package com.example.mqtt_app

import android.app.PendingIntent
import android.content.Context
import android.nfc.Tag
import android.os.Build
import android.os.Message
import android.util.Log
import com.example.mqtt_app.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.*
import kotlin.jvm.Throws

class MqttClientHelper(context: Context) {

    companion object {
        const val TAG = "MqttClientHelper"
    }

    var mqttAndroidClient: MqttAndroidClient
    val serverUri = MQTT_HOST
    private val clientId: String = MqttClient.generateClientId()
    fun setCallback(callback: MqttCallbackExtended) {
        mqttAndroidClient.setCallback(callback)
    }

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                Log.w(TAG, serverURI)
            }

            override fun connectionLost(cause: Throwable?) {

            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w(TAG, message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
//        connect()
    }

    fun connect() {
        val mqttConnectOptions: MqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.connectionTimeout = CONNECT_TIMEOUT   //設定連線超時的數值
        mqttConnectOptions.keepAliveInterval = CONNECT_KEEP_ALIVE_INTERVAL  //設定保持喚醒間隔
        mqttConnectOptions.isAutomaticReconnect =
            CONNECT_RECONNECT    //當client跟server連線中斷時，是否嘗試自動連線
        mqttConnectOptions.isCleanSession = CONNECT_CLEAN_SESSION    //是否要再重新連線時清空Session
//        mqttConnectOptions.userName = CLIENT_USER_NAME    //客戶端帳號
//        mqttConnectOptions.password = CLIENT_USER_PASSWORD.toCharArray()    //客戶端密碼
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true    //是否開啟緩衝配置
                    disconnectedBufferOptions.bufferSize = 100    //離線後最多緩存100個單位
                    disconnectedBufferOptions.isPersistBuffer = false   //是否要繼續留存
                    disconnectedBufferOptions.isDeleteOldestMessages = false    //是否刪除就訊息
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "Failed to connect to: $serverUri ; $exception")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(subscriptionTopic: String, qos: Int = 0) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w(TAG, "Subscribe Successfully! Topic: $subscriptionTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "Subscribe Failed! Topic: $subscriptionTopic")
                }

            })
        } catch (e: MqttException) {
            System.err.println("Exception whilst to topic $subscriptionTopic")
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(topic, message.payload, qos, false)
            Log.d(TAG, "Message published to topic: $topic: $msg")
        } catch (e: MqttException) {
            Log.d(TAG, "Error publishing to $topic: " + e.message)
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return mqttAndroidClient.isConnected
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}