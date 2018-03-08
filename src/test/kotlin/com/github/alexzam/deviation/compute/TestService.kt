package com.github.alexzam.deviation.compute

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Math.*

class TestService {
    @Test
    fun testMCConversion() {
        val boat = BoatFeatures(PI / 6, 0.1, PI / 300)
        val service = DeviationService()

        val compass = PI / 9
        println("Compass is ${Math.toDegrees(compass)} ($compass)")

        val magnetic = service.compassCourseToMagnetic(boat, compass)
        println("To magnetic: ${Math.toDegrees(magnetic)} ($magnetic)")

        val backCompass = service.magneticCourseToCompass(boat, magnetic)
        println("Back to compass: ${Math.toDegrees(backCompass)} ($backCompass)")

        assertEquals(compass, backCompass)
    }

    @Test
    fun stepTest() {
        val phi = PI / 6
        val r = 0.1
        val b = PI / 300
        val boat = BoatFeatures(phi, r, b)

        val service = DeviationService()

        val c = PI / 9
        show("c", c)

        val k = service.magneticCourseToCompass(boat, c)
        show("k", k)

        val d = service.calcDeviation(boat, c)
        show("d", d)

        show(1, c, k + d)

        show(2, c, k + b + atan2(r * sin(phi + c), 1 + r * cos(phi + c)))

        val kb = k + b
        show(3, r * sin(phi + c) / (1 + r * cos(phi + c)), tan(c - kb))
    }

    private fun show(str: String, angle: Double) {
        println("$str: ${Math.toDegrees(angle)} ($angle)")
    }

    private fun show(num: Int, val1: Double, val2: Double) {
        val d = val1 - val2
        println("$num:\t$val1\t$val2\t$d")
    }
}