package ipt.p09_coffee

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class OrderPage : Activity() {
    private var serverDestination: String? = null
    private var lvOrderDetails: ListView? = null
    private var orderAdapter: ArrayAdapter<OrderPackage>? = null
    private val orderPackList: MutableList<OrderPackage> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverDestination = getString(R.string.server_destination)
        setContentView(R.layout.orderpage)
        buildViews()
    }

    private fun buildViews() {
        lvOrderDetails = findViewById(R.id.listOrderDetails)
        invokeLoadOrder("$serverDestination/order")

        orderAdapter = ArrayAdapter(this@OrderPage, android.R.layout.simple_list_item_1, orderPackList)
        lvOrderDetails!!.adapter = orderAdapter
        lvOrderDetails!!.setOnItemClickListener(lvOrderListener)

    }

    private val lvOrderListener = { parent: AdapterView<*>, view: View, position: Int, id: Long ->
        val intent = Intent()
        intent.setClass(this@OrderPage, QRCode::class.java)

        val bundle = Bundle()
        bundle.putLong("orderId", orderPackList[position].orderId)
        intent.putExtras(bundle)

        startActivity(intent)
    }

    private fun invokeLoadOrder(url: String) {
        val authToken = PreferenceHelper.getAuthToken(this)
        val temp = "Bearer ${authToken.accessToken}"

        val client = AsyncHttpClient()
        client.addHeader("authorization", temp)

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val data = String(responseBody)

                try {
                    val myOrderContent = JSONArray(data)
                    for (idx in 0 until myOrderContent.length()) {
                        val orderList = mutableListOf<OrderData>()
                        val orderItem = myOrderContent.getJSONObject(idx)
                        val orderId = orderItem.getLong("order_id")
                        val orderDate = orderItem.getString("order_date")
                        val orderContent = orderItem.getJSONArray("order_contents")
                        for (idy in 0 until orderContent.length()) {
                            val orderExpand = orderContent.getJSONObject(idy)
                            val menuId = orderExpand.getLong("menu_id")
                            val menuName = orderExpand.getString("menu_name")
                            val tasteLevel = orderExpand.getString("taste_level")
                            val waterLevel = orderExpand.getString("water_level")
                            val grindLevel = orderExpand.getString("grind_size")
                            val foamLevel = orderExpand.getString("foam_level")
                            val menuType = orderExpand.getString("menu_type")
                            val orderCups = orderExpand.getInt("counts")

                            val order = OrderData(MenuData(menuName, waterLevel, foamLevel,
                                    grindLevel, tasteLevel, menuId, menuType), orderCups)

                            orderList.add(order)
                        }
                        orderPackList.add(OrderPackage(orderId, orderDate, orderList))
                    }

                } catch (errMsg: Exception) {
                    Toast.makeText(applicationContext, errMsg.message, Toast.LENGTH_LONG).show()
                }

                orderAdapter = ArrayAdapter(this@OrderPage, android.R.layout.simple_list_item_1, orderPackList)
                orderAdapter!!.notifyDataSetChanged()
                lvOrderDetails!!.adapter = orderAdapter

                Toast.makeText(applicationContext, "訂單資料下載成功", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (statusCode == 401) {
                    Toast.makeText(applicationContext, getString(R.string.token_expired), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "訂單資料下載失敗", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

}

