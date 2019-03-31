package com.projectcenterfvt.historicalpenza.map

import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.Mapper

open class SuggestionMapper : Mapper<Landmark, Suggestion> {

    override fun mapFromDomain(type: Landmark): Suggestion {
        with (type) {
            return Suggestion(
                    id = id,
                    title = title
            )
        }
    }

    override fun mapToDomain(type: Suggestion): Landmark {
        throw NotImplementedError()
    }

}