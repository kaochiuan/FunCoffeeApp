package ipt.p09_coffee

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.*

import org.json.JSONArray
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.StringEntity
import cz.msebera.android.httpclient.message.BasicHeader
import cz.msebera.android.httpclient.protocol.HTTP
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class MakeOrder : Activity() {

    var m_accessToken: String? = null
    var m_refreshToken: String? = null
    var numPicker: NumberPicker? = null
    var selectMenu: Spinner? = null
    var mSelectedOrder: Int = -1
    private var btnMakeOrderAdd: Button? = null
    private var btnMakeOrderDelete: Button? = null
    private var btnMakeOrderSubmit: Button? = null
    private var lvOrderContent: ListView? = null
    private var etOrderMessage: EditText? = null
    private var serverDestination: String? = null
    private var menuAdapter: ArrayAdapter<MenuData>? = null
    private var orderAdapter: ArrayAdapter<OrderData>? = null
    private val menuList: MutableList<MenuData> = mutableListOf()
    private val orderList: MutableList<OrderData> = mutableListOf()
    private var mMenuData: MenuData = MenuData("Test", WaterLevels.STANDARD.value,
            FoamLevels.STANDARD.value, GrindLevels.MEDIUM.value, TasteLevels.STANDARD.value, 0, MenuTypes.CUSTOMIZED.value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_order)

        serverDestination = getString(R.string.server_destination)
        buildViews()
    }

    private fun buildViews() {
        val bundle = this.intent.extras
        m_accessToken = bundle!!.getString("accessToken")
        m_refreshToken = bundle.getString("refreshToken")

        numPicker = findViewById(R.id.num_menuItemCount)
        numPicker!!.maxValue = 50
        numPicker!!.minValue = 0
        numPicker!!.value = 1

        lvOrderContent = findViewById(R.id.listOrderContent)
        selectMenu = findViewById(R.id.spi_menuItem)
        invokeLoadUserMenu("$serverDestination/menu")
        selectMenu!!.onItemSelectedListener = spnMenuListener
        //numPicker!!.setOnValueChangedListener(numPickerListener)
        btnMakeOrderAdd = findViewById(R.id.btnMakeOrderAdd)
        btnMakeOrderDelete = findViewById(R.id.btnMakeOrderDelete)
        btnMakeOrderSubmit = findViewById(R.id.btnMakeOrderSubmit)
        etOrderMessage = findViewById(R.id.textOrderMessage)
        btnMakeOrderAdd!!.setOnClickListener(btnAddListener)
        btnMakeOrderDelete!!.setOnClickListener(btnDeleteListener)
        btnMakeOrderSubmit!!.setOnClickListener(btnSubmitListener)

        orderAdapter = ArrayAdapter(this@MakeOrder, android.R.layout.simple_list_item_1, orderList)
        lvOrderContent!!.adapter = orderAdapter
        lvOrderContent!!.setOnItemClickListener(lvOrderListener)
    }

    private val lvOrderListener = { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            mSelectedOrder = position
            val orderItem = orderList[position].menuItem
            Toast.makeText(this@MakeOrder, "Select for deletion", Toast.LENGTH_SHORT).show()
    }

    //取得使用者選擇的值
    private val numPickerListener = { p0: NumberPicker?, p1: Int, p2: Int ->
        val text = "Changed from $p1 to $p2"
        Toast.makeText(this@MakeOrder, text, Toast.LENGTH_SHORT).show()
    }

    private val btnAddListener = View.OnClickListener {
        val counts = numPicker!!.value
        val order = OrderData(menuItem = mMenuData, counts = counts)
        val exists = orderList.takeWhile { e -> e.menuItem == mMenuData }
        if (exists.count() > 0) {
            val itemIndex = orderList.indexOf(exists[0])
            orderList[itemIndex] = order
        } else {
            orderList.add(order)
        }
        orderAdapter = ArrayAdapter(this@MakeOrder, android.R.layout.simple_list_item_1, orderList)
        orderAdapter!!.notifyDataSetChanged()
        lvOrderContent!!.adapter = orderAdapter
    }

    private val btnDeleteListener = View.OnClickListener {
        if (orderList.count() > 0 && mSelectedOrder >= 0) {
            orderList.removeAt(mSelectedOrder)
            orderAdapter = ArrayAdapter(this@MakeOrder, android.R.layout.simple_list_item_1, orderList)
            orderAdapter!!.notifyDataSetChanged()
            lvOrderContent!!.adapter = orderAdapter
            mSelectedOrder = -1
        }
    }

    private val btnSubmitListener = View.OnClickListener {
        invokeMakeOrder("$serverDestination/order")
    }

    private fun invokeMakeOrder(url: String) {
        val temp = "Bearer " + m_accessToken!!

        val message = etOrderMessage!!.text
        val client = AsyncHttpClient()
        client.addHeader("authorization", temp)
        val paramRoot = JSONObject()
        val params = JSONArray()
        for(orderItem in orderList) {
            val jsonObj = JSONObject()
            jsonObj.put("menu_id", orderItem.menuItem.menuId)
            jsonObj.put("counts", orderItem.counts)
            params.put(jsonObj)
        }


        paramRoot.put("order", params)
        paramRoot.put("message", message)

        var entity: StringEntity? = null
        try {
            entity = StringEntity(paramRoot.toString())
            entity.contentType = BasicHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        client.post(applicationContext, url, entity, ContentType.APPLICATION_JSON.mimeType, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                try {
                    val m_JOrder_Tmp = JSONObject(String(responseBody))

                    if (m_JOrder_Tmp.getString("message") == "successful") {
                        val msg = "訂單資料傳送成功"

                        Toast.makeText(this@MakeOrder, msg, Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Toast.makeText(applicationContext, "訂單接收失敗", Toast.LENGTH_LONG).show()
            }
        })
    }

    private val spnMenuListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            mMenuData = menuList[position]
            val text = "selected item: ${mMenuData.menuName}"
            Toast.makeText(this@MakeOrder, text, Toast.LENGTH_SHORT).show()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {

        }
    }

    fun invokeLoadUserMenu(url: String) {
        val temp = "Bearer " + m_accessToken!!

        val client = AsyncHttpClient()
        client.addHeader("authorization", temp)
        client.addHeader("Content-Type", "application/json")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val data = String(responseBody)

                // Decoding downloaded menu data

                try {
                    menuList.clear()
                    val mJMenu = JSONArray(data)
                    val userMenuCount = mJMenu.length()
                    if (userMenuCount > 0) {
                        for (i in 0 until userMenuCount) {
                            val mJMenuTmp = mJMenu.getJSONObject(i)
                            val menuId = mJMenuTmp.getLong("menu_id")
                            val menuName = mJMenuTmp.getString("name")
                            val menuWaterLevel = mJMenuTmp.getString("water_level")
                            val menuFoamLevel = mJMenuTmp.getString("foam_level")
                            val menuTasteLevel = mJMenuTmp.getString("taste_level")
                            val menuGrindSize = mJMenuTmp.getString("grind_size")
                            val menuType = mJMenuTmp.getString("menu_type")

                            val menuItem = MenuData(menuName, menuWaterLevel, menuFoamLevel,
                                    menuGrindSize, menuTasteLevel, menuId, menuType)
                            menuList.add(menuItem)
                        }
                    }

                    menuAdapter = ArrayAdapter(this@MakeOrder, android.R.layout.simple_spinner_item, menuList)
                    menuAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    menuAdapter!!.notifyDataSetChanged()
                    selectMenu!!.adapter = menuAdapter
                } catch (err_Mag: Exception) {
                    Toast.makeText(applicationContext, err_Mag.message, Toast.LENGTH_LONG).show()
                }

                Toast.makeText(applicationContext, "菜單資料下載成功", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Toast.makeText(applicationContext, "菜單資料下載失敗", Toast.LENGTH_LONG).show()
            }
        })

    }


}