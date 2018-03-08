package com.github.alexzam.deviation.compute

@Suppress("unused")
class DeviationService {

    @Suppress("MemberVisibilityCanPrivate")
    fun calcDeviation(boat: BoatFeatures, course: Double): Double {
        val devC = boat.phi + course
        val sinDevC = Math.sin(devC)
        val cosDevC = Math.cos(devC)
        val xNorth = boat.r * cosDevC + 1
        val yNorth = boat.r * sinDevC

        return Math.atan2(yNorth, xNorth) + boat.b
    }

    fun norm(a: Double): Double {
        var ret = a
        while (ret < 0) ret += 2 * Math.PI
        while (ret > 2 * Math.PI) ret -= 2 * Math.PI
        return ret
    }

    fun magneticCourseToCompass(boat: BoatFeatures, course: Double): Double = course - calcDeviation(boat, course)

    fun compassCourseToMagnetic(boat: BoatFeatures, course: Double): Double {
        val s = Math.sin(boat.phi + course + boat.b)
        return Math.asin(boat.r * s) + course + boat.b
    }

    fun learnDeviation(dataSet: DataSet, iterations: Int = 100, alpha: Double = 0.1, lambda: Double = 0.1,
                       costListener: (Double) -> Unit = {}): BoatFeatures {
        val boat = BoatFeatures(0.0, 0.1, 0.0)

        for (i in 1..iterations) {
            var J = 0.0
            val changeBoat = BoatFeatures(0.0, 0.0, 0.0)

            dataSet.forEach { element ->
                val dev = calcDeviation(boat, element.course)
                val error = dev - element.deviation

                J += error * error

                val devC = boat.phi - element.course
                val sinDevC = Math.sin(devC)
                val cosDevC = Math.cos(devC)

                // Common part of derivatives
                val z = boat.r * boat.r + 2 * boat.r * cosDevC + 1
                changeBoat.apply {
                    r += error * sinDevC / z
                    phi += error * boat.r * (boat.r + cosDevC) / z
                    b += error
                }
            }

            val m = dataSet.size
            J = J / (2 * m) + lambda * boat.b * boat.b
            costListener(J)

            boat.apply {
                r -= alpha * changeBoat.r / m
                phi -= alpha * 1000 * changeBoat.phi / m  // 1000 stands for "learn phi faster please"
                b -= alpha * (changeBoat.b / m + 2 * lambda * b)
            }
        }

        return boat
    }
}

/**
 * @param phi Deviation vector angle from DP
 * @param r Deviation vector module. Earth magnetic field strength is taken as 1.
 * @param b Some constant bias for deviation. Not sure if it has any physical sense except systematic measurement error.
 */
data class BoatFeatures(var phi: Double, var r: Double, var b: Double)

data class DataSetElement(val course: Double, val deviation: Double)

typealias DataSet = List<DataSetElement>