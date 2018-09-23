package ipt.p09_coffee

import android.os.Parcel
import android.os.Parcelable

class OrderData(var menuItem: MenuData,
                var counts: Int): Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(MenuData::class.java.classLoader),
            parcel.readInt())

    /**
    for displaying by spinner text, you should override toString()
     */
    override fun toString(): String {
        return "${menuItem.menuName} - Cups: $counts"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(menuItem, flags)
        parcel.writeInt(counts)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OrderData> {
        override fun createFromParcel(parcel: Parcel): OrderData {
            return OrderData(parcel)
        }

        override fun newArray(size: Int): Array<OrderData?> {
            return arrayOfNulls(size)
        }
    }
}