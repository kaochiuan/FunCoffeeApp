package ipt.p09_coffee

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRCode : AppCompatActivity() {
    private var serverDestination: String? = null
    private var mOrderId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverDestination = getString(R.string.server_destination)
        buildViews()
        setContentView(R.layout.activity_qrcode)
    }

    private fun buildViews() {
        val bundle = this.intent.extras
        val orderId = bundle.getLong("orderId")
        mOrderId = orderId
    }

    fun genCode(view: View) {
        val ivCode = findViewById<View>(R.id.ivCode) as ImageView
        val etContent = "$serverDestination/order/$mOrderId"
        val encoder = BarcodeEncoder()
        try {
            val bit = encoder.encodeBitmap(etContent, BarcodeFormat.QR_CODE,
                    600, 600)
            ivCode.setImageBitmap(bit)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }

}
