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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun genCode(view: View) {
        val ivCode = findViewById<View>(R.id.ivCode) as ImageView
        val etContent = findViewById<View>(R.id.etContent) as EditText
        val encoder = BarcodeEncoder()
        try {
            val bit = encoder.encodeBitmap(etContent.text.toString(), BarcodeFormat.QR_CODE,
                    600, 600)
            ivCode.setImageBitmap(bit)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }

}
