package com.buzzware.vendingmachinetech.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Videos(
    var title: String = "",
    var categoryId: String = "",
    var description: String = "",
    var publishDate: Long = 0,
    var duration: Long = 0,
    var userId: String = "",
    var postId: String = "",
    var thumbnailImage: String = "",
    var videoLink: String = "",
    var isFavorite: Boolean = false
): Parcelable {

    // Constructor that creates the object from a Parcel
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
        parcel.readByte() != 0.toByte()
    )

    // Write object data to Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(categoryId)
        parcel.writeString(description)
        parcel.writeLong(publishDate)
        parcel.writeLong(duration)
        parcel.writeString(userId)
        parcel.writeString(postId)
        parcel.writeString(thumbnailImage)
        parcel.writeString(videoLink)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    // Describe the contents, typically 0 or 1 for file descriptors
    override fun describeContents(): Int = 0

    // Companion object to create instances of Videos from Parcel
    companion object CREATOR : Parcelable.Creator<Videos> {
        override fun createFromParcel(parcel: Parcel): Videos {
            return Videos(parcel)
        }

        override fun newArray(size: Int): Array<Videos?> {
            return arrayOfNulls(size)
        }
    }
}
