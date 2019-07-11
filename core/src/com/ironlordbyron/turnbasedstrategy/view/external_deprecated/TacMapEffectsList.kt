package com.ironlordbyron.turnbasedstrategy.view.external_deprecated

object TacMapEffectsList {
    val gameEffects = ArrayList<AbstractGameEffect>()

    fun add(effect: AbstractGameEffect) {

        gameEffects.add(effect)
    }

    fun update(){
        val i = gameEffects.iterator()
        while (i.hasNext()) {
            val e = i.next()
            e.update()
            if (e.isDone) {
                i.remove()
            }
        }
    }
}