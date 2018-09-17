package ipt.p09_coffee

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainPage : Activity() {

    private var bt_OrderCoffee: Button? = null
    private var bt_CheckPO: Button? = null
    private var bt_EditRecipt: Button? = null
    private var bt_EditUserInfo: Button? = null

    private var m_accessToken: String? = null
    private var m_refreshToken: String? = null

    private val bt_OrderCoffeeListener = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@MainPage, MakeOrder::class.java)

        val bundle = Bundle()
        bundle.putString("accessToken", m_accessToken)
        bundle.putString("refreshToken", m_refreshToken)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    private val bt_CheckPOListener = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@MainPage, QRCode::class.java)

        val bundle = Bundle()
        bundle.putString("accessToken", m_accessToken)
        bundle.putString("refreshToken", m_refreshToken)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    private val bt_EditReciptListener = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@MainPage, Menu::class.java)

        val bundle = Bundle()
        bundle.putString("accessToken", m_accessToken)
        bundle.putString("refreshToken", m_refreshToken)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    private val bt_EditUserInfoListener = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@MainPage, UserInfo::class.java)

        val bundle = Bundle()
        bundle.putString("accessToken", m_accessToken)
        bundle.putString("refreshToken", m_refreshToken)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)

        buildViews()
    }

    private fun buildViews() {
        bt_OrderCoffee = findViewById(R.id.Btid_OrderCoffee) as Button
        bt_CheckPO = findViewById(R.id.Btid_CheckPO) as Button
        bt_EditRecipt = findViewById(R.id.Btid_EditRecipt) as Button
        bt_EditUserInfo = findViewById(R.id.Btid_EditUserInfo) as Button

        bt_OrderCoffee!!.setOnClickListener(bt_OrderCoffeeListener)
        bt_CheckPO!!.setOnClickListener(bt_CheckPOListener)
        bt_EditRecipt!!.setOnClickListener(bt_EditReciptListener)
        bt_EditUserInfo!!.setOnClickListener(bt_EditUserInfoListener)

        val bundle = this.intent.extras
        m_accessToken = bundle!!.getString("accessToken")
        m_refreshToken = bundle.getString("refreshToken")
    }

}
