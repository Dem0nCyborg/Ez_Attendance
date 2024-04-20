package com.example.ez_attendance

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
import com.example.ez_attendance.databinding.ActivityBinder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        binder?.toggleButton?.setOnCheckedChangeListener(this@MainActivity)
        Coroutines.main(this@MainActivity, { scope ->
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
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == binder?.toggleButton)
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
                        val textPayload = String(
                            payload.copyOfRange(languageCodeLength, payload.size),
                            Charsets.UTF_8
                        )

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


}