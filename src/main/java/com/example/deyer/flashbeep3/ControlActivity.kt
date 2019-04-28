package com.example.deyer.flashbeep3

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*

class ControlActivity: AppCompatActivity(){

    companion object {
        var m_myUUID: UUID = UUID.fromString("7bafada4-4445-420a-82bc-e7923a6a5754")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevices(this).execute()

        control_led_on.setOnClickListener { sendCommand("a") }
        control_led_off.setOnClickListener { sendCommand("b") }
        control_led_disconnect.setOnClickListener { disonnect() }

        beep.setOnClickListener {
            //sendCommand(cameraManager.setTorchMode(cameraId, false)
        }

    }

    private fun sendCommand(input: String){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())

            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disonnect(){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevices(c: Context) : AsyncTask<Void, Void, String>(){


        private var connectSuccess: Boolean = true
        private var context: Context


        init{
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg params: Void?): String? {
            try{
                if(m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }

            }catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("Data", "couldn't connect")
            }else{
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }

}