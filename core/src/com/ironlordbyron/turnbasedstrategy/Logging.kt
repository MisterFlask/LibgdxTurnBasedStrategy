package com.ironlordbyron.turnbasedstrategy

public class Logging{
    public companion object {
        public fun DebugCombatLogic(msg: String){
            println("COMBAT LOGIC | " + msg)
        }

        public fun DebugGeneral(msg: String){
            println("GENERAL LOGIC | " + msg)
        }

        public fun DebugAnimation(msg: String){
            println("ANIMATION LOGIC | "  + msg)
        }

        public fun DebugPathfinding(msg: String){
            println("PATHFINDING: " + msg)
        }

        public fun Warning(msg: String, exception: Exception){
            println(msg + " : " + exception.message)
            exception.printStackTrace()
        }
    }
}