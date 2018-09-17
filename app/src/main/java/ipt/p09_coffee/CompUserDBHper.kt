package ipt.p09_coffee

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

/**
 * Created by north on 2016/6/10.
 */
class CompUserDBHper(context: Context, DBName: String, TBName: String, factory: SQLiteDatabase.CursorFactory, DBversion: Int) : SQLiteOpenHelper(context, DBName, null, 1) {

    val recSet: ArrayList<String>
        get() {
            val db = readableDatabase
            val sql = "SELECT * FROM $m_TBName"
            val recSet = db.rawQuery(sql, null)
            val recAry = ArrayList<String>()
            val UserList = ArrayList<String>()
            val columnCount = recSet.columnCount
            while (recSet.moveToNext()) {
                var fldSet = ""
                for (i in 0 until columnCount)
                    fldSet += recSet.getString(i) + "#"
                recAry.add(fldSet)

                UserList.add(recSet.getString(0))
            }
            recSet.close()
            db.close()
            return recAry
        }

    //ManuList.add(recSet.getString(0));
    val userList: ArrayList<UserData>
        get() {
            val db = readableDatabase
            val sql = "SELECT * FROM $m_TBName"
            val recSet = db.rawQuery(sql, null)
            val UserList = ArrayList<UserData>()
            val columnCount = recSet.columnCount


            if (columnCount > 0) {
                while (recSet.moveToNext()) {
                    val DataBuf = UserData("male", "0900123456", 1980, 1, 1)
                    DataBuf.m_UserGender = recSet.getString(0)
                    DataBuf.m_UserPhone = recSet.getString(1)
                    DataBuf.m_UserBdate_Y = Integer.valueOf(recSet.getString(2))
                    DataBuf.m_UserBdate_M = Integer.valueOf(recSet.getString(3))
                    DataBuf.m_UserBdate_D = Integer.valueOf(recSet.getString(4))
                    UserList.add(DataBuf)
                }
            }
            recSet.close()
            db.close()
            return UserList
        }

    init {
        m_DBName = DBName
        m_TBName = TBName

        m_crUsersql = "CREATE TABLE " + m_TBName + "(" +
                "UserName VARCHAR(50) NOT NULL," + "UserGender INTEGER ," + "UserPhone VAECHAR(50)," +
                "UserBDateYear INTEGER," + "UserBDateMonth INTEGER," + "UserBDateDay INTEGER," +
                "UserAddress VARCHAR(500)," + "UserID VARCHAR(10)," + "ProfileID VARCHAR(10)," + "PRIMARY KEY (UserName));"
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(m_crUsersql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $m_TBName")
        onCreate(db)
    }

    fun insertRec(m_UserData: UserData): Long {
        val rec = ContentValues()
        rec.put("UserGender", m_UserData.m_UserGender)
        rec.put("UserPhone", m_UserData.m_UserPhone)
        rec.put("UserBDateYear", m_UserData.m_UserBdate_Y)
        rec.put("UserBDateMonth", m_UserData.m_UserBdate_M)
        rec.put("UserBDateDay", m_UserData.m_UserBdate_D)

        val db = writableDatabase
        val rowID = db.insert(m_TBName, null, rec)

        db.close()
        return rowID
    }

    fun updateRec(m_UserData: UserData): Int {

        val db = writableDatabase
        val sql = "SELECT * FROM $m_TBName"
        val recSet = db.rawQuery(sql, null)

        if (recSet.count != 0) {

            val rec = ContentValues()
            rec.put("UserGender", m_UserData.m_UserGender)
            rec.put("UserPhone", m_UserData.m_UserPhone)
            rec.put("UserBDateYear", m_UserData.m_UserBdate_Y)
            rec.put("UserBDateMonth", m_UserData.m_UserBdate_M)
            rec.put("UserBDateDay", m_UserData.m_UserBdate_D)

            val rowAffected = db.update(m_TBName, rec,"", null)
            db.close()
            return rowAffected
        } else {
            db.close()
            return -1
        }
    }

    fun replacetRec(m_UserData: UserData): Long {
        val rec = ContentValues()
        rec.put("UserGender", m_UserData.m_UserGender)
        rec.put("UserPhone", m_UserData.m_UserPhone)
        rec.put("UserBDateYear", m_UserData.m_UserBdate_Y)
        rec.put("UserBDateMonth", m_UserData.m_UserBdate_M)
        rec.put("UserBDateDay", m_UserData.m_UserBdate_D)

        val db = writableDatabase
        val rowID = db.replace(m_TBName, null, rec)

        db.close()
        return rowID
    }

    fun deleteRec(ManuName: String): Long {
        val db = writableDatabase
        val sql = "SELECT * FROM $m_TBName"
        val recSet = db.rawQuery(sql, null)
        val whereClause = "UserName='$ManuName'"
        if (recSet.count != 0) {
            val rowID = db.delete(m_TBName, whereClause, null).toLong()
            db.close()
            return rowID
        } else {
            db.close()
            return -1
        }

    }

    fun RecCount(): Int {
        val db = writableDatabase
        val sql = "SELECT * FROM $m_TBName"
        val recSet = db.rawQuery(sql, null)
        val recSet_Count = recSet.count
        recSet.close()
        return recSet_Count
    }

    companion object {
        private var m_DBName: String = ""
        private var m_TBName = "User"
        private var m_crUsersql = ""
    }
}