package com.ironlordbyron.turnbasedstrategy.tacmapunits.util

import java.lang.IllegalArgumentException

fun Any?.mustBeNull(){
    if (this != null){
        throw IllegalArgumentException("This element should be null")
    }
}