package com.jbloit.androidosctestapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.illposed.osc.OSCListener
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCPortIn
import com.illposed.osc.OSCPortOut
import java.net.InetAddress

/**
 * Created by julien on 6/8/18.
 */
class OscSocket (context: Context, private val listener: OscSocketListener): Runnable  {

    private val TAG = "OSC_SOCKET"
    private val mContext: Context
    private val remotePort = 12345
    private val localPort = 54321
    private lateinit var oscPortOut: OSCPortOut

    val broadCastAddress: InetAddress

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WHAT_RECEIVEDMESSAGE -> {
                    val messageAsOSCmessage: OSCMessage = msg.obj as OSCMessage
                    listener.receivedMessage(this@OscSocket, messageAsOSCmessage)
                }
            }
        }
    }

    init{
        mContext = context
        val utils = Utils(mContext)
        broadCastAddress = utils.broadcastAddress
    }


    override fun run(){
        try {
            oscPortOut = OSCPortOut(InetAddress.getByName(broadCastAddress.hostAddress), remotePort)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        var receiver = OSCPortIn(localPort)
        val listener = OSCListener { time, message ->
//            println("Message received! ${message.arguments[0]}")
            reportMessage(message)
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

    fun reportMessage(oscMessage: OSCMessage){
        val message = Message()
        message.what = WHAT_RECEIVEDMESSAGE
        message.obj = oscMessage
        handler.sendMessage(message)
    }

    companion object {
        private val WHAT_RECEIVEDMESSAGE = 0
    }

}