package ipt.p09_coffee

enum class WaterLevels(val value: String) {
    LONG("long"), STANDARD("standard"), SMALL("small")
}

enum class TasteLevels(val value: String) {
    MILD("mild"), STANDARD("standard"), STRONG("strong")
}

enum class FoamLevels(val value: String) {
    NONE("none"), STANDARD("standard"), THICK("thick")

}

enum class GrindLevels(val value: String) {
    FINE("fine"), MEDIUM("medium"), COARSE("coarse")
}

enum class GenderTypes(val value: String) {
    NONE("none"), MALE("male"), FEMALE("female")
}

enum class MenuTypes(val value: String) {
    CUSTOMIZED("customized"), GENERAL("general")
}


fun parseWaterLevels(value: String): WaterLevels {
    var result: WaterLevels = WaterLevels.STANDARD

    for (c in WaterLevels.values()) {
        if (value == c.value) {
            result = c
            break
        }
    }
    return result
}

fun parseTasteLevels(value: String): TasteLevels {
    var result: TasteLevels = TasteLevels.STANDARD

    for (c in TasteLevels.values()) {
        if (value == c.value) {
            result = c
            break
        }
    }
    return result
}

fun parseFoamLevels(value: String): FoamLevels {
    var result: FoamLevels = FoamLevels.STANDARD

    for (c in FoamLevels.values()) {
        if (value == c.value) {
            result = c
            break
        }
    }
    return result
}

fun parseSizeLevels(value: String): GrindLevels {
    var result: GrindLevels = GrindLevels.MEDIUM

    for (c in GrindLevels.values()) {
        if (value == c.value) {
            result = c
            break
        }
    }
    return result
}

fun parseGenderTypes(value: String): GenderTypes {
    var result: GenderTypes = GenderTypes.NONE

    for (c in GenderTypes.values()) {
        if (value == c.value) {
            result = c
            break
        }
    }
    return result
}

fun parseMenuTypes(value: String): MenuTypes {
    var result: MenuTypes = MenuTypes.GENERAL

    for (c in MenuTypes.values()) {
        if (value == c.value) {
            result = c
            break
        }
    }
    return result
}