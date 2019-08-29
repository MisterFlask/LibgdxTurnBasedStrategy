package com.ironlordbyron.turnbasedstrategy.common.equipment

enum class EquipmentClass(val superclass: EquipmentSuperclass) {
    // weapon types (primary)
    MELEE_WEAPON_LARGE(EquipmentSuperclass.PRIMARY_WEAPON),
    LONGSWORD(EquipmentSuperclass.PRIMARY_WEAPON),
    SPELLBOOK(EquipmentSuperclass.PRIMARY_WEAPON),
    HOOK(EquipmentSuperclass.PRIMARY_WEAPON),
    GLAIVE(EquipmentSuperclass.PRIMARY_WEAPON),
    SHOTGUN(EquipmentSuperclass.PRIMARY_WEAPON),

    //WEAPON TYPES (secondary)
    PISTOL(EquipmentSuperclass.SECONDARY_WEAPON),
    DAGGER(EquipmentSuperclass.SECONDARY_WEAPON),

    // utilityOrVest
    UTILITY(EquipmentSuperclass.UTILITY),
    VEST(EquipmentSuperclass.VEST)
}

enum class EquipmentSuperclass{
    PRIMARY_WEAPON,
    SECONDARY_WEAPON,
    UTILITY,
    VEST
}

