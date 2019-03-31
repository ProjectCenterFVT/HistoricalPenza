package com.projectcenterfvt.historicalpenza.map

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.parcel.Parcelize

@Parcelize
class Suggestion(val id: Long, val title: String) : SearchSuggestion {

    override fun getBody(): String {
        return title
    }

}