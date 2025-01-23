package com.buzzware.vendingmachinetech.model

import android.os.Parcel
import android.os.Parcelable

data class Videos(
    var title: String = "",
    var categoryId: String = "",
    var description: String = "",
    var date: Long = 0,
    var duration: Long = 0,
    var userId: String = "",
    var postId: String = "",
    var thumbnailImage: String = "",
    var videoLink: String = "",
    var favorites: ArrayList<String>? = arrayListOf(),
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
    parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(categoryId)
        parcel.writeString(description)
        parcel.writeLong(date)
        parcel.writeLong(duration)
        parcel.writeString(userId)
        parcel.writeString(postId)
        parcel.writeString(thumbnailImage)
        parcel.writeString(videoLink)
        parcel.writeStringList(favorites)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Videos> {
        override fun createFromParcel(parcel: Parcel): Videos {
            return Videos(parcel)
        }

        override fun newArray(size: Int): Array<Videos?> {
            return arrayOfNulls(size)
        }
    }
}
