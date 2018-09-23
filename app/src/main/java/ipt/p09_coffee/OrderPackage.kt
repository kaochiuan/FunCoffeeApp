package ipt.p09_coffee

import android.os.Parcel
import android.os.Parcelable

class OrderPackage(var orderId: Long,
                   var orderCreateDate: String,
                   var orderItems: MutableList<OrderData>): Parcelable
{
    var counts:Int = orderItems.sumBy {e -> e.counts}

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            mutableListOf<OrderData>().apply{ parcel.readTypedList(this, OrderData.CREATOR)}) {
        counts = parcel.readInt()
    }

    /**
    for displaying by spinner text, you should override toString()
     */
    override fun toString(): String {
        return "$orderCreateDate (Cups: $counts)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(orderId)
        parcel.writeString(orderCreateDate)
        parcel.writeInt(counts)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OrderPackage> {
        override fun createFromParcel(parcel: Parcel): OrderPackage {
            return OrderPackage(parcel)
        }

        override fun newArray(size: Int): Array<OrderPackage?> {
            return arrayOfNulls(size)
        }
    }
}