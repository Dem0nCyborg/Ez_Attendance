package com.example.ez_attendance

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ez_attendance.databinding.ActivityBinder
import com.example.ez_attendance.professor.updateTimetable
import com.example.ez_attendance.students.attendance
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

public class MainActivity : AppCompatActivity, CompoundButton.OnCheckedChangeListener, NfcAdapter.ReaderCallback {

    companion object {
        private val TAG = MainActivity::class.java.getSimpleName()
    }

    private var binder: ActivityBinder? = null

    //private val viewModel : MainViewModel by lazy { ViewModelProvider(this@MainActivity).get(MainViewModel::class.java) }
    private val viewModel: MainViewModel by viewModels<MainViewModel>()

    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binder = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binder?.setViewModel(viewModel)
        binder?.setLifecycleOwner(this@MainActivity)
        super.onCreate(savedInstanceState)

        binder?.bottomNavigation?.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_1 -> {
                    //Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show()
                    false
                }

                R.id.item_2 -> {
                    startActivity(Intent(this, updateTimetable::class.java))
                    //Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show()
                    false
                }

                else -> false
            }

        }


        binder?.toggleButton?.setOnCheckedChangeListener(this@MainActivity)
        Coroutines.main(this@MainActivity) { scope ->
            scope.launch(block = {
                binder?.getViewModel()?.observeNFCStatus()?.collectLatest(action = { status ->
                    Log.d(TAG, "observeNFCStatus $status")
                    if (status == NFCStatus.NoOperation) NFCManager.disableReaderMode(
                        this@MainActivity,
                        this@MainActivity
                    )
                    else if (status == NFCStatus.Tap) NFCManager.enableReaderMode(
                        this@MainActivity,
                        this@MainActivity,
                        this@MainActivity,
                        viewModel.getNFCFlags(),
                        viewModel.getExtras()
                    )
                })
            })
            scope.launch(block = {
                binder?.getViewModel()?.observeToast()?.collectLatest(action = { message ->
                    Log.d(TAG, "observeToast $message")
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                })
            })
            scope.launch(block = {
                binder?.getViewModel()?.observeTag()?.collectLatest(action = { tag ->
                    Log.d(TAG, "observeTag $tag")
                    binder?.textViewExplanation?.setText(tag)
                })
            })
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == binder?.toggleButton)
            binder?.getViewModel()?.onCheckNFC(isChecked)
            viewModel.onCheckNFC(isChecked)
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag?.let {
            val ndef = Ndef.get(it)
            ndef?.let {
                try {
                    it.connect()
                    val ndefMessage = it.ndefMessage
                    if (ndefMessage != null && ndefMessage.records.isNotEmpty()) {
                        // Assuming the first record contains the NDEF text record
                        val textRecord = ndefMessage.records[0]

                        // Decode the payload and language code
                        val payload = textRecord.payload
                        val languageCodeLength =
                            (payload[0].toInt() and 0x3F) + 1 // Extract the language code length
                        val languageCode =
                            String(payload.copyOfRange(1, languageCodeLength), Charsets.UTF_8)

                        //This is storing the value on the nfc tag
                        val textPayload = String(
                            payload.copyOfRange(languageCodeLength, payload.size),
                            Charsets.UTF_8
                        )

                        val split = textPayload.split("|")
                        GlobalData.rollno = split[0]
                        GlobalData.name = split[1]

                        if (GlobalData.subject=="SPCC-System Programming and Compiler Construction"){
                            val url="https://script.google.com/macros/s/AKfycbyVYcJvv2nEKDdsdSWuXffH43316mCjdPwW6sb6dASY1sNYmtH9fgXzO8VhS73mJ9P2/exec"
                            putData(GlobalData.rollno,GlobalData.name,url)
                        }else if (GlobalData.subject=="AI-Artificial Intelligence"){
                            val url = "https://script.google.com/macros/s/AKfycbw0Dzpp76WvIMnowIdd-UxG-I_BSz8W8od_teNDr4PHlZkg1oG2NVWGH9SByls1t5NxhA/exec"
                            putData(GlobalData.rollno,GlobalData.name,url)
                        }else if (GlobalData.subject=="CSS-Cryptography and System Security") {
                            val url = "https://script.google.com/macros/s/AKfycbw3Va9DxOhuvyZNykrdY3LmoX2kQ-2iwXg74l0havczaMax83ECSeN_eGPbGqIFqJB7Bw/exec"
                            putData(GlobalData.rollno,GlobalData.name,url)
                        }



                        // Update UI with the extracted text payload
                        runOnUiThread {
                            binder?.textViewExplanation?.text = textPayload
                        }
                    } else {
                        Log.e(TAG, "No NDEF records found on the tag.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading NFC tag: ${e.message}")
                } finally {
                    // Close the NDEF connection
                    it.close()
                }
            }
        }
    }

    private fun putData(rollno:String,name:String,url:String){

        val stringRequest=object :StringRequest(Request.Method.POST,url,
            Response.Listener{
                             Toast.makeText(this@MainActivity,it.toString(),Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()

            }){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String,String>()
                params["rollno"]=rollno
                params["name"]=name
                return params
            }
        }
        val queue = Volley.newRequestQueue(this@MainActivity)
        queue.add(stringRequest)
    }




}