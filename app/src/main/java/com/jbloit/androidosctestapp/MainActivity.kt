package com.jbloit.androidosctestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.illposed.osc.*
import java.net.InetAddress
import kotlin.concurrent.thread
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCListener
import com.illposed.osc.OSCPort
import com.illposed.osc.OSCPortIn




/*
* This test app periodically sends an OSC message to a remote host.
* This is a Kotlin adaptation from:
* https://courses.ideate.cmu.edu/16-223/f2014/tutorial-android-osc-communication/
*
* Don't forget the INTERNET permission in the Manifest file.
* */


class MainActivity : AppCompatActivity() {

    private val TAG = "OSC_ACTIVITY"
    
    private val remotePort = 12345
    private val localPort = 54321

    private lateinit var oscPortOut: OSCPortOut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val utils = Utils(this)
        val broadCastAddress = utils.broadcastAddress

        thread(true, false, name="oscThread"){

            try {
                oscPortOut = OSCPortOut(InetAddress.getByName(broadCastAddress.hostAddress), remotePort)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }

            var receiver = OSCPortIn(localPort)
            val listener = OSCListener { time, message ->
                println("Message received! ${message.arguments[0]}")

            }
            receiver.addListener("/sayhello", listener)
            receiver.startListening()

            while(true) {
                Thread.sleep(1000)
                if (oscPortOut != null){

                    var message = OSCMessage()
                    message.address = "/android"
                    message.addArgument("hi")
                    message.addArgument(12345)
                    message.addArgument(1.2345.toFloat())

                    try {
                        oscPortOut.send(message)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
        }
    }
}
