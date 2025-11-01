package com.zeafen.petwalker.data.helpers

import com.zeafen.petwalker.domain.models.TileLoc
import com.zeafen.petwalker.domain.models.api.users.APILocation
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

const val EARTHRAD = 6_371
const val a = 6_378_137

fun APILocation.calculateDistance(other: APILocation): Double {
    val p1Lo = this.longitude.toRadians()
    val p1La = this.latitude.toRadians()
    val p2Lo = other.longitude.toRadians()
    val p2La = other.latitude.toRadians()
    return EARTHRAD.toDouble() * acos(
        sin(p1Lo) * sin(p2Lo) +
                cos(p1Lo) * cos(p2Lo) * cos(p1La - p2La)
    );
}

fun APILocation.toTileLoc(zoomLvl: Int): TileLoc {
    val lat = this.latitude.toRadians()
    val lon = this.longitude.toRadians()

    val worldX = lon / (2 * PI)
    val worldY = ln(tan(PI / 4 + lat / 2)) / (2 * PI)

    val mapWidth = 2.0.pow(8 + zoomLvl)
    return TileLoc(
        zoomLvl,
        (worldX + 0.5) * mapWidth,
        (0.5 - worldY) * mapWidth
    )
}

fun getDegreesCoordinatesFromMercator(
    x: Double,
    y: Double,
    mapWidth: Double,
): APILocation {
    val worldY = -(y / mapWidth - 0.5)
    val latRad = 2 * (atan(E.pow(worldY * 2 * PI)) - PI / 4)
    val lat = latRad * 180 / PI

    val lon = (x / mapWidth) * 360 - 180

    return APILocation(
        lat, lon
    )
}


fun Double.toRadians(): Double = this / 180.0 * PI