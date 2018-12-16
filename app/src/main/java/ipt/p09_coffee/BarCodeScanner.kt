package ipt.p09_coffee

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.google.zxing.Result
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.layout_popupwindow.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
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
    private var popupWindow: PopupWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner)
        popupWindow = PopupWindow(this)
        mScannerView = ZXingScannerView(this)
        serverDestination = getString(R.string.server_destination)
        bt_scanner = findViewById(R.id.Btn_id_scanner)
        bt_scanner!!.setOnClickListener(bt_ScannerListener)
    }

    private val bt_ScannerListener = View.OnClickListener {
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

        val actContext = this
        val serialNumber = result?.text
        val url = "$serverDestination/serial_number?serial_number=$serialNumber"
        val client = AsyncHttpClient()
        client.addHeader("authorization", temp)
        client.addHeader("Content-Type", "application/json")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val data = String(responseBody)
                try {
                    val mJMessage = JSONObject(data)
                    val message = mJMessage.getString("customized_message")
                    if (message.startsWith("http://", ignoreCase = true) or
                            message.startsWith("https://", ignoreCase = true)) {
                        openNewTabWindow(urls = message, context = actContext)
                    } else {
                        showPopupWindow(message = message)
                    }
                } catch (err_Mag: Exception) {
                    showPopupWindow(message = err_Mag.message!!)
                    // Toast.makeText(applicationContext, err_Mag.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                when (statusCode) {
                    401 -> showPopupWindow(message = getString(R.string.token_expired))
                    404 -> showPopupWindow(message = "Bar code not found, invalid operation")
                    else -> showPopupWindow(message = "取得訊息失敗")
                }
            }
        })
        //Camera will stop after scanning result, so we need to resume the
        //preview in order scan more codes
        mScannerView.resumeCameraPreview(this)
    }

    fun openNewTabWindow(urls: String, context: Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    private fun showPopupWindow(message: String) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow, null)


        //get txt view from "layout" which will be added into popup window
        //before it you tried to find view in activity container
        val txt = popupView.findViewById(R.id.textViewPrivateMessage) as TextView
        txt.text = message

        // set click listener for ok button
        popupView.layout_popupwindow_confirmButton.setOnClickListener { view ->
            popupWindow!!.dismiss()
        }

        popupWindow!!.contentView = popupView
        popupWindow!!.width = ViewGroup.LayoutParams.MATCH_PARENT
        popupWindow!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // disappear popupWindow if touch outside of it
        popupWindow!!.isOutsideTouchable = true

        // show popWindow
        popupWindow!!.showAsDropDown(bt_scanner, 0, 800)
    }

}