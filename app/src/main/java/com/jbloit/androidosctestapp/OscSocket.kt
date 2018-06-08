package com.jbloit.androidosctestapp

import android.content.Context
import android.util.Log
import com.illposed.osc.OSCListener
import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCPortIn
import com.illposed.osc.OSCPortOut
import java.net.InetAddress

/**
 * Created by julien on 6/8/18.
 */
class OscSocket (context: Context): Runnable  {

    private val TAG = "OSC_SOCKET"
    private val mContext: Context
    private val remotePort = 12345
    private val localPort = 54321
    private lateinit var oscPortOut: OSCPortOut

    val broadCastAddress: InetAddress

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