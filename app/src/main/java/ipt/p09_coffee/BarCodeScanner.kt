package ipt.p09_coffee

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

import kotlinx.android.synthetic.main.activity_bar_code_scanner.*

class BarCodeScanner : AppCompatActivity(), ZXingScannerView.ResultHandler {
    /*
    * Scanner View that will create the layout for scanning a barcode.
    * If you want a custom layout above the scanner layout, then implement
    * the scanning code in a fragment and use the fragment inside the activity,
    * add callbacks to obtain result from the fragment
    * */
    private lateinit var mScannerView: ZXingScannerView

    private var bt_scanner: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner)

        bt_scanner = findViewById(R.id.Btn_id_scanner)
        bt_scanner!!.setOnClickListener(bt_ScannerListener)
    }

    private val bt_ScannerListener = View.OnClickListener {
        mScannerView = ZXingScannerView(this)
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
        Toast.makeText(this, result?.text, Toast.LENGTH_SHORT).show()

        //Camera will stop after scanning result, so we need to resume the
        //preview in order scan more codes
        mScannerView.resumeCameraPreview(this)
    }
}