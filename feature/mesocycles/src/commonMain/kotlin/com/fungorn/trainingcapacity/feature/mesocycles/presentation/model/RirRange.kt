package com.fungorn.trainingcapacity.feature.mesocycles.presentation.model

enum class RirRange(val label: String, val lowerEnd: Int, val upperEnd: Int) {
    RIR_0("0", 0, 0),
    RIR_0_2("0-2", 0, 2),
    RIR_2_4("2-4", 2, 4),
    RIR_4_6("4-6", 4, 6),
    RIR_6_8("6-8", 6, 8),
    RIR_8_10("8-10", 8, 10)
}