package com.jbloit.androidosctestapp

import com.illposed.osc.OSCMessage

/**
 * Created by julien on 6/8/18.
 */
interface OscSocketListener {
    fun receivedMessage(task: OscSocket, message: OSCMessage)
}