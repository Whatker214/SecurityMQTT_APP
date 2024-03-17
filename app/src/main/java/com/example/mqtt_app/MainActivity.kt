package com.example.mqtt_app

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.mqtt_app.databinding.ActivityMainBinding
import com.example.mqtt_app.*
import com.google.android.material.snackbar.Snackbar
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.Calendar
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.schedule
import kotlin.jvm.Throws

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setMqttCallBack()
        mqttClient.connect()

        binding.tvNumMsgs.text = "0"

        binding.tvPayloadMsg.text = UUID.randomUUID().toString()

        binding.btnLogin.setOnClickListener {
            Intent(this, Sign_in::class.java).apply {
                startActivity(this)
            }
        }


        //Subscribe
        binding.btnSub.setOnClickListener {
            var snackbarMsg: String
            val topic = binding.edSubTopic.text.toString().trim()
            snackbarMsg = "Cannot subscribe to empty topic!"
            if (topic.isNotEmpty()) {
                snackbarMsg = try {
                    mqttClient.subscribe(topic)
                    mqttClient.connect()
                    "Subscribed to topic: $topic"
                } catch (e: MqttException) {
                    "Error subscribing to topic: $topic"
                }
            }
            Snackbar.make(binding.constlayout, snackbarMsg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        //Publish
        binding.btnPub.setOnClickListener {
            var snackbarMsg: String
            val topic = binding.edTopic.text.toString().trim()
            snackbarMsg = "Cannot publish to empty topic!"
            if (topic.isNotEmpty()) {
                snackbarMsg = try {
                    mqttClient.publish(topic, binding.edPubMsg.text.toString())
                    "Published to topic: $topic"
                } catch (e: MqttException) {
                    "Error publishing to topic: $topic"
                }
            }
            Snackbar.make(binding.constlayout, snackbarMsg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        Timer("CheckMqttConnection", false).schedule(3000) {
            if (!mqttClient.isConnected()) {
                Snackbar.make(
                    binding.constlayout,
                    "Failed to connect to: $MQTT_HOST within 3 seconds",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Action", null).show()
            }
        }

    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                val snackbarMsg = "Connection to host lost:\n $MQTT_HOST"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(binding.constlayout, snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w("Debug", "Message received from host:\n $MQTT_HOST: $message")
                binding.tvNumMsgs.text = ("${binding.tvNumMsgs.text.toString().toInt() + 1}")
                val str: String =
                    "---------" + Calendar.getInstance().time + "---------\n $message\n${binding.tvPayloadMsg.text}"
                binding.tvPayloadMsg.text = str
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.w("Debug", "Message published to host:\n $MQTT_HOST")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                val snackbarMsg = "Connected to host:\n $MQTT_HOST"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(binding.constlayout, snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

        })
    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

}