package com.tomergoldst.gepp.model

import com.google.gson.annotations.SerializedName

/** A Plus Code encoded location reference.  */
data class PlusCode(

    /** The global Plus Code identifier.  */
    @SerializedName("compound_code")
    val globalCode: String? = null,

    /** The compound Plus Code identifier. May be null for locations in remote areas.  */
    @SerializedName("global_code")
    val compoundCode: String? = null) {

    override fun toString(): String {
        val sb = StringBuilder("[PlusCode: ")
        sb.append(globalCode)
        if (compoundCode != null) {
            sb.append(", compoundCode=").append(compoundCode)
        }
        sb.append("]")
        return sb.toString()
    }
}