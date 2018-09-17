package ipt.p09_coffee

import android.app.Activity
import android.os.Bundle

import org.json.JSONException
import org.json.JSONObject

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams


import java.io.UnsupportedEncodingException
import java.util.ArrayList

import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity

class UserInfo : Activity() {

    private var et_UserPhone: EditText? = null
    private var bt_UserInfoOK: Button? = null
    private var rdg_Gender: RadioGroup? = null
    private var rdb_Female: RadioButton? = null
    private var rdb_Male: RadioButton? = null
    private var spn_Y: Spinner? = null
    private var spn_M: Spinner? = null
    private var spn_D: Spinner? = null


    var m_accessToken: String? = null
    var m_refreshToken: String? = null
    var m_UserData: UserData = UserData()
    var m_fg_NewUser: Boolean? = null
    private val TBname = "User"
    private var dbHper: CompUserDBHper? = null
    private val recSet: ArrayList<String>? = null
    private var serverDestination: String? = null

    private val bt_UserInfoOKListener = View.OnClickListener {
        val params = RequestParams()
        val jsonParams = JSONObject()

        UserInfo_Update()

        try {
            jsonParams.put("gender", m_UserData.m_UserGender)
            jsonParams.put("birthday", m_UserData.m_UserBdate_Y.toString() + "-" +
                    m_UserData.m_UserBdate_M.toString() + "-" +
                    m_UserData.m_UserBdate_D.toString())
            jsonParams.put("phone", m_UserData.m_UserPhone)

            val Profile_Link = "$serverDestination/user_profile"
            invoke_UserInfoUpdate(Profile_Link, jsonParams)

        } catch (err_Msg: Exception) {
            Toast.makeText(applicationContext, "資料轉型失敗$err_Msg", Toast.LENGTH_LONG).show()

        }
        Toast.makeText(applicationContext, "使用者資料更新中", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverDestination = getString(R.string.server_destination)
        setContentView(R.layout.userinfo)
        buildViews()
    }

    public override fun onPause() {
        super.onPause()
        if (dbHper != null)
            dbHper!!.close()
        dbHper = null
    }

    public override fun onResume() {
        super.onResume()
    }

    //endregion


    private fun buildViews() {

        val bundle = this.intent.extras
        m_accessToken = bundle!!.getString("accessToken")
        m_refreshToken = bundle.getString("refreshToken")

        m_UserData = UserData("male", "0912345678", 1990, 1, 1)


        //region Initialization
        et_UserPhone = findViewById(R.id.edIdPhone) as EditText
        rdg_Gender = findViewById(R.id.rdgIdGender) as RadioGroup
        rdb_Female = findViewById(R.id.rdbId_Female) as RadioButton
        rdb_Male = findViewById(R.id.rdbId_Male) as RadioButton
        spn_Y = findViewById(R.id.spi_AgeYear) as Spinner
        spn_M = findViewById(R.id.spi_AgeMonth) as Spinner
        spn_D = findViewById(R.id.spi_AgeDay) as Spinner

        bt_UserInfoOK = findViewById(R.id.btId_userinfo_OK) as Button
        bt_UserInfoOK!!.setOnClickListener(bt_UserInfoOKListener)

        //endregion

        invoke_UserInfoGet("$serverDestination/user_profile")
    }

    fun invoke_UserInfoGet(url: String) {
        val client = AsyncHttpClient()
        val Temp = "Bearer " + m_accessToken!!
        val targetUrl = url
        client.addHeader("authorization", Temp)
        client.addHeader("Content-Type", "application/json")
        client.get(targetUrl, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                try {

                    val m_JUserInfo_Tmp = JSONObject(String(responseBody))

                    m_UserData.m_UserGender = m_JUserInfo_Tmp.getString("gender")

                    val regexPhone = Regex("[1-9]{10}")
                    if(regexPhone.matches(m_JUserInfo_Tmp.getString("phone"))) {
                        m_UserData.m_UserPhone = m_JUserInfo_Tmp.getString("phone")
                    }

                    var m_UserBdate = m_JUserInfo_Tmp.getString("birthday")
                    val regexBirthday = Regex("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}")
                    if(regexBirthday.matches(m_UserBdate)) {
                        val m_UserBdate_Detail = m_UserBdate.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                        m_UserData.m_UserBdate_Y = Integer.valueOf(m_UserBdate_Detail[0])
                        m_UserData.m_UserBdate_M = Integer.valueOf(m_UserBdate_Detail[1])
                        m_UserData.m_UserBdate_D = Integer.valueOf(m_UserBdate_Detail[2])
                    }

                    m_UserBdate = ""
                    UserInfo_Display()

                    var msg = "個人資料取得成功"

                    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Toast.makeText(applicationContext, "使用者資料取得成功", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Toast.makeText(applicationContext, "使用者資料取得失敗", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun invoke_UserInfoUpdate(url: String, jsonParams: JSONObject) {
        val client = AsyncHttpClient()
        val Temp = "Bearer " + m_accessToken!!
        client.addHeader("authorization", Temp)

        var entity: StringEntity? = null
        try {
            entity = StringEntity(jsonParams.toString())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        client.post(applicationContext, url, entity, "application/json", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                try {
                    val m_JUserInfo_Tmp = JSONObject(String(responseBody))

                    var msg = "個人資料更新成功"

                    Toast.makeText(this@UserInfo, msg, Toast.LENGTH_SHORT).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Toast.makeText(applicationContext, "使用者資料更新成功", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Toast.makeText(applicationContext, "使用者資料更新失敗:" + "new String(responseBody)", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun invoke_UserInfoCreate(url: String, jsonParams: JSONObject) {
        val client = AsyncHttpClient()
        val Temp = "Bearer " + m_accessToken!!
        client.addHeader("authorization", Temp)
        //client.addHeader("Content-Type", "application/json");
        var entity: StringEntity? = null
        try {
            entity = StringEntity(jsonParams.toString())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        client.post(applicationContext, url, entity, "application/json", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                try {
                    val m_JUserInfo_Tmp = JSONObject(String(responseBody))

                    var msg = ""
                    val RecCount = dbHper!!.RecCount()
                    if (RecCount > 0) {

                        val rowID = dbHper!!.updateRec(m_UserData).toLong()

                        if (rowID != -1L)
                            msg = "個人資料更新成功"
                        else
                            msg = "個人資料更新失敗"
                    } else {
                        val rowID = dbHper!!.insertRec(m_UserData)
                        if (rowID != -1L)
                            msg = "個人資料新增成功"
                        else
                            msg = "個人資料新增失敗"
                    }
                    Toast.makeText(this@UserInfo, msg, Toast.LENGTH_SHORT).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Toast.makeText(applicationContext, "使用者資料更新成功", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Toast.makeText(applicationContext, "使用者資料更新失敗:" + "new String(responseBody)", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun UserInfo_Update() {
        if (rdb_Male!!.isChecked)
            m_UserData.m_UserGender = "male"
        else {
            if (rdb_Female!!.isChecked)
                m_UserData.m_UserGender = "female"
            else
                m_UserData.m_UserGender = "male"
        }
        m_UserData.m_UserPhone = et_UserPhone!!.text.toString()
        m_UserData.m_UserBdate_Y = Integer.valueOf(spn_Y!!.selectedItem.toString())
        m_UserData.m_UserBdate_M = Integer.valueOf(spn_M!!.selectedItem.toString())
        m_UserData.m_UserBdate_D = Integer.valueOf(spn_D!!.selectedItem.toString())

    }

    private fun UserInfo_Display() {
        et_UserPhone!!.setText(m_UserData.m_UserPhone)

        if (m_UserData.m_UserGender == "male") {
            rdb_Male!!.isChecked = true
            rdb_Female!!.isChecked = false
        } else {
            rdb_Male!!.isChecked = false
            rdb_Female!!.isChecked = true
        }

        for (i in 0 until spn_Y!!.adapter.count) {
            if (Integer.valueOf(spn_Y!!.adapter.getItem(i).toString()) == m_UserData.m_UserBdate_Y) {
                spn_Y!!.setSelection(i)
                break
            }
        }

        for (i in 0 until spn_M!!.adapter.count) {
            if (Integer.valueOf(spn_M!!.adapter.getItem(i).toString()) == m_UserData.m_UserBdate_M) {
                spn_M!!.setSelection(i)
                break
            }
        }

        for (i in 0 until spn_D!!.adapter.count) {
            if (Integer.valueOf(spn_D!!.adapter.getItem(i).toString()) == m_UserData.m_UserBdate_D) {
                spn_D!!.setSelection(i)
                break
            }
        }

    }

    companion object {

        //region Establish Menu database
        private val DBname = "user_profile.db" //"使用者資料0626_v1.db";
        private val DBversion = 1
    }


}
