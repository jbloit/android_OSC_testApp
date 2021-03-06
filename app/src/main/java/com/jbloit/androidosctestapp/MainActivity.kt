package com.jbloit.androidosctestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import com.illposed.osc.*
import java.net.InetAddress
import kotlin.concurrent.thread
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCListener
import com.illposed.osc.OSCPort
import com.illposed.osc.OSCPortIn
import kotlinx.android.synthetic.main.activity_main.*


/*
* This test app periodically sends an OSC message to a remote host.
* This is a Kotlin adaptation from:
* https://courses.ideate.cmu.edu/16-223/f2014/tutorial-android-osc-communication/
*
* Don't forget the INTERNET permission in the Manifest file.
* */


class MainActivity : AppCompatActivity(), OscSocketListener {

    private val TAG = "OSC_ACTIVITY"
    private lateinit var oscPortOut: OSCPortOut


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val task = OscSocket(this, this)

        button_thread.setOnClickListener(){

            val message = Message()
            message.what = OscSocket.WHAT_SENDMESSAGE
            task.handler.sendMessage(message)
        }

        val thread1 = Thread(task)
        thread1.start()
    }

    override fun receivedMessage(task: OscSocket, message: OSCMessage) {
        Log.d(TAG, "received OSC message")
        for (arg in message.arguments){
            Log.d(TAG, "with arg ${arg}")
        }
    }
}
