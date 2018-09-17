package ipt.p09_coffee

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

import org.json.JSONArray
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import cz.msebera.android.httpclient.Header

import kotlin.CharSequence
import java.util.ArrayList

class MakeOrder : Activity() {

    var m_accessToken: String? = null
    var m_refreshToken: String? = null
    private var serverDestination: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        serverDestination = getString(R.string.server_destination)
        buildViews()
    }

    private fun buildViews() {
        val bundle = this.intent.extras
        m_accessToken = bundle!!.getString("accessToken")
        m_refreshToken = bundle.getString("refreshToken")

    }

    fun LoadMenu(view: View){
        val spn_menu = findViewById<View>(R.id.spi_menuItem) as Spinner
    }
}