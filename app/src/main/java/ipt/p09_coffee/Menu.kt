package ipt.p09_coffee

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*

import org.json.JSONArray
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class Menu : Activity() {
    var idCoffeeWaterLevel: Array<String>? = null
    var idCoffeeTasteLevel: Array<String>? = null
    var idCoffeeGrindLevel: Array<String>? = null
    var idCoffeeFoamLevel: Array<String>? = null
    private var bt_UserMenuOK: Button? = null
    private var bt_UserRegisterMenu: Button? = null
    private var et_UserMenu_Name: EditText? = null
    private var spnMenu: Spinner? = null
    private var spnWaterLevel: Spinner? = null
    private var spnGrindLevel: Spinner? = null
    private var spnFoamLevel: Spinner? = null
    private var spnTasteLevel: Spinner? = null

    private var mMenuData: MenuData = MenuData("Test", WaterLevels.STANDARD.value,
            FoamLevels.STANDARD.value, GrindLevels.MEDIUM.value, TasteLevels.STANDARD.value, 0, MenuTypes.CUSTOMIZED.value)
    private var menuAdapter: ArrayAdapter<MenuData>? = null
    private val menuList: MutableList<MenuData> = mutableListOf()
    private var serverDestination: String? = null

    private val bt_UserMenuOKLisetner = View.OnClickListener {
        Menu_Update()
        invoke_UploadMenu("$serverDestination/menu", mMenuData)
    }

    private val bt_UserMenuRegisterListener = View.OnClickListener {
        Menu_Update()
        invoke_RegisterMenu("$serverDestination/menu", mMenuData)
    }

    private val spnMenuListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            mMenuData.menuId = menuList[position].menuId
            val foamPos = idCoffeeFoamLevel!!.indexOf(menuList[position].foamLevel)
            val waterPos = idCoffeeWaterLevel!!.indexOf(menuList[position].waterLevel)
            val tastePos = idCoffeeTasteLevel!!.indexOf(menuList[position].tasteLevel)
            val grindPos = idCoffeeGrindLevel!!.indexOf(menuList[position].grindLevel)
            spnFoamLevel!!.setSelection(foamPos)
            spnWaterLevel!!.setSelection(waterPos)
            spnTasteLevel!!.setSelection(tastePos)
            spnGrindLevel!!.setSelection(grindPos)
            et_UserMenu_Name!!.setText(menuList[position].menuName)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {

        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        serverDestination = getString(R.string.server_destination)

        idCoffeeWaterLevel = resources.getStringArray(R.array.ID_Coffee_waterLevel)
        idCoffeeTasteLevel = resources.getStringArray(R.array.ID_Coffee_tasteLevel)
        idCoffeeFoamLevel = resources.getStringArray(R.array.ID_Coffee_foamLevel)
        idCoffeeGrindLevel = resources.getStringArray(R.array.ID_Coffee_grindLevel)
        buildViews()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
    }

    //endregion

    private fun buildViews() {

        //region Interface setting
        bt_UserMenuOK = findViewById(R.id.btId_usermenu_OK)
        bt_UserRegisterMenu = findViewById(R.id.btId_usermenu_Register)
        et_UserMenu_Name = findViewById(R.id.etId_UserMenu_Name)
        spnMenu = findViewById(R.id.spi_CoffeeMenuCloud)
        spnWaterLevel = findViewById(R.id.spi_waterLevel)
        spnGrindLevel = findViewById(R.id.spi_grindSize)
        spnFoamLevel = findViewById(R.id.spi_foamLevel)
        spnTasteLevel = findViewById(R.id.spi_tasteLevel)
        //endregion

        invoke_InitMenu("$serverDestination/menu")
        //region Event setting
        bt_UserMenuOK!!.setOnClickListener(bt_UserMenuOKLisetner)
        bt_UserRegisterMenu!!.setOnClickListener(bt_UserMenuRegisterListener)
        spnMenu!!.onItemSelectedListener = spnMenuListener
        //endregion

    }

    fun invoke_UploadMenu(url: String, menuData: MenuData) {
        if (menuData.menuId == 0L) {
            val message = "請選擇新增菜單"
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            return
        }

        val authToken = PreferenceHelper.getAuthToken(this)
        val temp = "Bearer ${authToken.accessToken}"

        val param = mutableMapOf<String, String>()
        param["name"] = menuData.menuName
        param["menu_type"] = menuData.menuType
        param["taste_level"] = menuData.tasteLevel
        param["water_level"] = menuData.waterLevel
        param["foam_level"] = menuData.foamLevel
        param["grind_size"] = menuData.grindLevel
        param["menu_id"] = menuData.menuId.toString()

        val client = AsyncHttpClient()
        client.addHeader("authorization", temp)
        val params = RequestParams(param)

        client.patch(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val message = String(responseBody)
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                invoke_InitMenu("$serverDestination/menu")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (statusCode == 401) {
                    Toast.makeText(applicationContext, getString(R.string.token_expired), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "菜單上傳失敗", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun invoke_RegisterMenu(url: String, menuData: MenuData) {
        val authToken = PreferenceHelper.getAuthToken(this)
        val temp = "Bearer ${authToken.accessToken}"

        val client = AsyncHttpClient()
        val param = mutableMapOf<String, String>()
        param["name"] = menuData.menuName
        param["menu_type"] = menuData.menuType
        param["taste_level"] = menuData.tasteLevel
        param["water_level"] = menuData.waterLevel
        param["foam_level"] = menuData.foamLevel
        param["grind_size"] = menuData.grindLevel

        client.addHeader("authorization", temp)
        val params = RequestParams(param)

        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val message = String(responseBody)
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                invoke_InitMenu("$serverDestination/menu")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (statusCode == 401) {
                    Toast.makeText(applicationContext, getString(R.string.token_expired), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "菜單上傳失敗", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    fun invoke_InitMenu(url: String) {
        val authToken = PreferenceHelper.getAuthToken(this)
        val temp = "Bearer ${authToken.accessToken}"

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

                    menuAdapter = ArrayAdapter(this@Menu, android.R.layout.simple_spinner_item, menuList)
                    menuAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    menuAdapter!!.notifyDataSetChanged()
                    spnMenu!!.adapter = menuAdapter
                } catch (err_Mag: Exception) {
                    Toast.makeText(applicationContext, err_Mag.message, Toast.LENGTH_LONG).show()
                }

                Toast.makeText(applicationContext, "菜單資料下載成功", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (statusCode == 401) {
                    Toast.makeText(applicationContext, getString(R.string.token_expired), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "菜單資料下載失敗", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun Menu_Update() {
        mMenuData.menuName = et_UserMenu_Name!!.text.toString()
        mMenuData.foamLevel = idCoffeeFoamLevel!![spnFoamLevel!!.selectedItemPosition]
        mMenuData.waterLevel = idCoffeeWaterLevel!![spnWaterLevel!!.selectedItemPosition]
        mMenuData.grindLevel = idCoffeeGrindLevel!![spnGrindLevel!!.selectedItemPosition]
        mMenuData.tasteLevel = idCoffeeTasteLevel!![spnTasteLevel!!.selectedItemPosition]
        mMenuData.menuType = "customized"
    }

}
