package ipt.p09_coffee

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.zxing.Result
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.json.JSONArray
import org.json.JSONObject


class BarCodeScanner : AppCompatActivity(), ZXingScannerView.ResultHandler {
    /*
    * Scanner View that will create the layout for scanning a barcode.
    * If you want a custom layout above the scanner layout, then implement
    * the scanning code in a fragment and use the fragment inside the activity,
    * add callbacks to obtain result from the fragment
    * */
    private lateinit var mScannerView: ZXingScannerView
    private var serverDestination: String? = null
    private var bt_scanner: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner)

        mScannerView = ZXingScannerView(this)
        serverDestination = getString(R.string.server_destination)
        bt_scanner = findViewById(R.id.Btn_id_scanner)
        bt_scanner!!.setOnClickListener(bt_ScannerListener)
    }

    private val bt_ScannerListener = View.OnClickListener {
        //mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    /*
    * It is required to start and stop camera in lifecycle methods
    * (onResume and onPause)
    */
    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }


    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    /*
    * Barcode scanning result is displayed here.
    * (For demo purposes only toast is shown here)
    * For understanding what more can be done with the result,
    * visit the GitHub README(https://github.com/dm77/barcodescanner)
    * */
    override fun handleResult(result: Result?) {
        val authToken = PreferenceHelper.getAuthToken(this)
        val temp = "Bearer ${authToken.accessToken}"

        val act_context = this
        var isUrl = false
        val serial_number = result?.text
        val url = "$serverDestination/serial_number?serial_number=$serial_number"
        val client = AsyncHttpClient()
        client.addHeader("authorization", temp)
        client.addHeader("Content-Type", "application/json")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val data = String(responseBody)
                try {
                    val mJMessage = JSONObject(data)
                    val message = mJMessage.getString("customized_message")
                    if (message.startsWith("http://",ignoreCase = true) or
                            message.startsWith("https://",ignoreCase = true)){
                        isUrl = true
                        openNewTabWindow(urls = message, context = act_context)
                    }
                } catch (err_Mag: Exception) {
                    Toast.makeText(applicationContext, err_Mag.message, Toast.LENGTH_LONG).show()
                    isUrl = false
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (statusCode == 401) {
                    Toast.makeText(applicationContext, getString(R.string.token_expired), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "取得訊息失敗", Toast.LENGTH_LONG).show()
                }
            }
        })

        Toast.makeText(this, url, Toast.LENGTH_SHORT).show()
        if(!isUrl) {
            openNewTabWindow(urls = url, context = this)
        }
        //Camera will stop after scanning result, so we need to resume the
        //preview in order scan more codes
        //mScannerView.resumeCameraPreview(this)
    }

    fun openNewTabWindow(urls: String, context : Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
}