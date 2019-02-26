package com.projectcenterfvt.historicalpenza.data

interface Mapper<E, D> {

    fun mapFromDomain(type: E): D

    fun mapToDomain(type: D): E

}