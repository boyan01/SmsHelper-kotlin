package tech.summerly.smshelper.model

import android.os.Parcel
import android.os.Parcelable

/**
 * data class for SmsMessage
 *
 * we only care about [number] and [content]
 *
 */
data class SmsMessage(
        val number: String,
        val content: String
) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(number)
        writeString(content)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SmsMessage> = object : Parcelable.Creator<SmsMessage> {
            override fun createFromParcel(source: Parcel): SmsMessage = SmsMessage(source)
            override fun newArray(size: Int): Array<SmsMessage?> = arrayOfNulls(size)
        }
    }
}