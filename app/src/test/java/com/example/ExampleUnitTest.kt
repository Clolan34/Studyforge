package com.example

import com.example.data.SampleDataSeed
import com.example.data.StudyForgeRepository
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*

class ExampleUnitTest {

    @Test
    fun physicsKinematics_uniformAcceleration_isAccurate() {
        val u = 10.0 // m/s
        val a = 2.5  // m/s^2
        val t = 4.0  // s
        val v = u + a * t
        val s = u * t + 0.5 * a * t * t
        assertEquals(20.0, v, 0.001)
        assertEquals(60.0, s, 0.001)

        val vSquared = u * u + 2 * a * s
        assertEquals(v * v, vSquared, 0.001)
    }

    @Test
    fun vectorDecomposition_andPythagoras_isAccurate() {
        val magnitude = 100.0
        val angleDeg = 60.0
        val rad = Math.toRadians(angleDeg)
        val vx = magnitude * cos(rad)
        val vy = magnitude * sin(rad)

        assertEquals(50.0, vx, 0.01)
        assertEquals(86.6025, vy, 0.01)

        val reconstructedMag = sqrt(vx * vx + vy * vy)
        assertEquals(magnitude, reconstructedMag, 0.001)
    }

    @Test
    fun studyForgeRepository_initialCourses_existAndStructured() {
        val repo = StudyForgeRepository()
        val courses = repo.courses.value

        assertEquals(2, courses.size)
        val physics = courses.find { it.code == "CSPC 132" }
        assertNotNull(physics)
        assertEquals(12, physics?.totalModules)

        val prog = courses.find { it.code == "CSSD 101" }
        assertNotNull(prog)
        assertEquals(12, prog?.totalModules)

        val physicsModules = repo.getModulesForCourse("course_physics")
        assertEquals(12, physicsModules.size)

        val progModules = repo.getModulesForCourse("course_programming")
        assertEquals(12, progModules.size)

        assertTrue(repo.questions.value.isNotEmpty())
        assertTrue(repo.flashcards.value.isNotEmpty())
        assertTrue(repo.documents.value.isNotEmpty())
    }

    @Test
    fun scientificCalculator_orderOfOperations_isAccurate() {
        // 2 + 3 * 4 == 14
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("2 + 3 * 4")
        assertTrue(result.isSuccess)
        assertEquals(14.0, result.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_trigonometryDegree_isAccurate() {
        // sin(30) in DEG == 0.5
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("sin(30)", com.example.calculator.AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals(0.5, result.getOrThrow(), 1e-6)
    }

    @Test
    fun scientificCalculator_trigonometryRadian_isAccurate() {
        // cos(pi) in RAD == -1.0
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("cos(pi)", com.example.calculator.AngleMode.RAD)
        assertTrue(result.isSuccess)
        assertEquals(-1.0, result.getOrThrow(), 1e-6)
    }

    @Test
    fun scientificCalculator_squareRoot_isAccurate() {
        // sqrt(144) == 12
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("sqrt(144)")
        assertTrue(result.isSuccess)
        assertEquals(12.0, result.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_logarithmBase10_isAccurate() {
        // log(100) == 2
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("log(100)")
        assertTrue(result.isSuccess)
        assertEquals(2.0, result.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_naturalLogarithm_isAccurate() {
        // ln(e) == 1
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("ln(e)")
        assertTrue(result.isSuccess)
        assertEquals(1.0, result.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_factorial_isAccurate() {
        // 5! == 120
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("5!")
        assertTrue(result.isSuccess)
        assertEquals(120.0, result.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_powersAndExponents_isAccurate() {
        // 2^3 == 8
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("2^3")
        assertTrue(result.isSuccess)
        assertEquals(8.0, result.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_combinationsAndPermutations_isAccurate() {
        // 5C2 == 10 and 5P2 == 20
        val combResult = com.example.calculator.ScientificCalculatorEngine.evaluate("5C2")
        assertTrue(combResult.isSuccess)
        assertEquals(10.0, combResult.getOrThrow(), 1e-9)

        val permResult = com.example.calculator.ScientificCalculatorEngine.evaluate("5P2")
        assertTrue(permResult.isSuccess)
        assertEquals(20.0, permResult.getOrThrow(), 1e-9)
    }

    @Test
    fun scientificCalculator_divisionByZero_returnsFailure() {
        // 10 / 0 returns error without crash
        val result = com.example.calculator.ScientificCalculatorEngine.evaluate("10 / 0")
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue(ex?.message?.contains("zero", ignoreCase = true) == true)
    }

    @Test
    fun scientificCalculator_ansAndFormatting_isAccurate() {
        val ansResult = com.example.calculator.ScientificCalculatorEngine.evaluate("ANS * 2 + 5", lastAnswer = 12.0)
        assertTrue(ansResult.isSuccess)
        assertEquals(29.0, ansResult.getOrThrow(), 1e-9)
        assertEquals("29", com.example.calculator.ScientificCalculatorEngine.formatResult(ansResult.getOrThrow()))
    }
}
