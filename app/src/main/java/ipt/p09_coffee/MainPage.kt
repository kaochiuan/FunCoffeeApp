package ipt.p09_coffee

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainPage : Activity() {
    private var serverDestination: String? = null
    private var bt_OrderCoffee: Button? = null
    private var bt_CheckPO: Button? = null
    private var bt_EditMenu: Button? = null
    private var bt_EditUserInfo: Button? = null
    private var bt_ScanBarCode: Button? = null

    private val bt_OrderCoffeeListener = View.OnClickListener {
        val authToken = PreferenceHelper.getAuthToken(this)
        TokenManager.refreshAccessToken(this, authToken.refreshToken, serverDestination!!)
        val intent = Intent()
        intent.setClass(this@MainPage, MakeOrder::class.java)
        startActivity(intent)
    }

    private val bt_BarCodeScanListener = View.OnClickListener {
        val authToken = PreferenceHelper.getAuthToken(this)
        TokenManager.refreshAccessToken(this, authToken.refreshToken, serverDestination!!)
        val intent = Intent()
        intent.setClass(this@MainPage, BarCodeScanner::class.java)
        startActivity(intent)
    }

    private val bt_CheckPOListener = View.OnClickListener {
        val authToken = PreferenceHelper.getAuthToken(this)
        TokenManager.refreshAccessToken(this, authToken.refreshToken, serverDestination!!)
        val intent = Intent()
        intent.setClass(this@MainPage, OrderPage::class.java)
        startActivity(intent)
    }

    private val bt_EditMenuListener = View.OnClickListener {
        val authToken = PreferenceHelper.getAuthToken(this)
        TokenManager.refreshAccessToken(this, authToken.refreshToken, serverDestination!!)
        val intent = Intent()
        intent.setClass(this@MainPage, Menu::class.java)
        startActivity(intent)
    }

    private val bt_EditUserInfoListener = View.OnClickListener {
        val authToken = PreferenceHelper.getAuthToken(this)
        TokenManager.refreshAccessToken(this, authToken.refreshToken, serverDestination!!)
        val intent = Intent()
        intent.setClass(this@MainPage, UserInfo::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)
        serverDestination = getString(R.string.server_destination)
        buildViews()
    }

    private fun buildViews() {
        bt_OrderCoffee = findViewById(R.id.Btid_OrderCoffee) as Button
        bt_CheckPO = findViewById(R.id.Btid_CheckPO) as Button
        bt_EditMenu = findViewById(R.id.Btid_EditMenu) as Button
        bt_EditUserInfo = findViewById(R.id.Btid_EditUserInfo) as Button
        bt_ScanBarCode = findViewById(R.id.Btid_ScanBarCode) as Button
        bt_OrderCoffee!!.setOnClickListener(bt_OrderCoffeeListener)
        bt_CheckPO!!.setOnClickListener(bt_CheckPOListener)
        bt_EditMenu!!.setOnClickListener(bt_EditMenuListener)
        bt_EditUserInfo!!.setOnClickListener(bt_EditUserInfoListener)
        bt_ScanBarCode!!.setOnClickListener(bt_BarCodeScanListener)
    }
}
