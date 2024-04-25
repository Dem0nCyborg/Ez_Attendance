import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ez_attendance.R

class ReadNFC : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var writingTagFilters: IntentFilter
    private lateinit var mytag: Tag
    private lateinit var txtread: TextView
    private lateinit var btnread: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_read_nfc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtread = findViewById(R.id.read)
        btnread = findViewById(R.id.readNFC)
        btnread.setOnClickListener {
            txtread.text = "Please tap the NFC tag"
        }



        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )

        readFromIntent(intent)
    }

    private fun readFromIntent(intent: Intent?) {
        Log.e("Tag red", "Tag red")
        Toast.makeText(this, "NFC Tag Detected", Toast.LENGTH_SHORT).show()
        val action = intent?.action.toString()
        if (action == NfcAdapter.ACTION_TAG_DISCOVERED || action == NfcAdapter.ACTION_TECH_DISCOVERED || action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
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

                            Log.e("Tag red", "textPayload: $textPayload")

                            // Update UI with the extracted text payload
                            runOnUiThread {
                                txtread.text = textPayload
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

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, arrayOf(writingTagFilters), null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        readFromIntent(intent)
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }
}
