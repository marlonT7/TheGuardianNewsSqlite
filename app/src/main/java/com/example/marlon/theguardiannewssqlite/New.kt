package com.example.marlon.theguardiannewssqlite

import android.os.Parcel
import android.os.Parcelable

const val FALSE = "false"
const val TRUE = "true"

data class New(
        val id: String,
        var sectionName: String,
        var headline: String,
        var url: String,
        var thumbnail: String,
        var bodyText: String,
        var seeLater: String = FALSE
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    fun changeSeeLater(){
        if (seeLater== FALSE){
            seeLater= TRUE
        } else{
            seeLater= FALSE
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(sectionName)
        parcel.writeString(headline)
        parcel.writeString(url)
        parcel.writeString(thumbnail)
        parcel.writeString(bodyText)
        parcel.writeString(seeLater)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<New> {
        override fun createFromParcel(parcel: Parcel): New {
            return New(parcel)
        }

        override fun newArray(size: Int): Array<New?> {
            return arrayOfNulls(size)
        }
    }

}