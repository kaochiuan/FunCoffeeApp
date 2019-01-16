package ipt.p09_coffee

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by north on 2016/5/15.
 */
class MenuData(var menuName: String
               , var waterLevel: String  // small, standard, long
               , var foamLevel: String // none, standard, thick
               , var grindLevel: String     // fine, medium, coarse
               , var tasteLevel: String      // mild, standard, strong
               , var menuId: Long        // ID on web server
               , var menuType: String
               , var coffeeOption: String  // coffee_A, coffee_B
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString())

    /**
    for displaying by spinner text, you should override toString()
     */
    override fun toString(): String {
        return menuName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(menuName)
        parcel.writeString(waterLevel)
        parcel.writeString(foamLevel)
        parcel.writeString(grindLevel)
        parcel.writeString(tasteLevel)
        parcel.writeLong(menuId)
        parcel.writeString(menuType)
        parcel.writeString(coffeeOption)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MenuData> {
        override fun createFromParcel(parcel: Parcel): MenuData {
            return MenuData(parcel)
        }

        override fun newArray(size: Int): Array<MenuData?> {
            return arrayOfNulls(size)
        }
    }
}
