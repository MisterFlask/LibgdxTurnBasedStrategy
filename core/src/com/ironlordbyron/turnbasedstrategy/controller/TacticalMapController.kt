package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileLocation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Aaron on 3/24/2018.
 */
@Singleton
class TacticalMapController @Inject constructor(val gameBoardOperator: GameBoardOperator){

    fun playerClickedOnTile(location: TileLocation){
        
    }
}