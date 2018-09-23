package ipt.p09_coffee

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.graphics.Bitmap
import android.widget.EditText
import android.widget.ImageView

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

import kotlinx.android.synthetic.main.activity_qrcode.*

class QRCode : AppCompatActivity() {
    private var serverDestination: String? = null
    private var mOrderId: Long = 0L
    var tasteLevel: String? = null
    var waterLevel: String? = null
    var grindLevel: String? = null
    var foamLevel: String? = null
    var cups: Int = 0
    private var vCode: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverDestination = getString(R.string.server_destination)
        buildViews()
        setContentView(R.layout.activity_qrcode)
//        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }

    private fun buildViews() {
        val bundle = this.intent.extras
//        val orderPackage = bundle.getParcelable<OrderPackage>("orderItem")
        val orderId = bundle.getLong("orderId")
        mOrderId = orderId
    }

    fun genCode(view: View) {
//        vCode = findViewById(R.id.ivCode)
        val ivCode = findViewById<View>(R.id.ivCode) as ImageView
//        val etContent = "taste=$tasteLevel|water=$waterLevel|foam=$foamLevel|grind=$grindLevel|cups=$cups"
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
