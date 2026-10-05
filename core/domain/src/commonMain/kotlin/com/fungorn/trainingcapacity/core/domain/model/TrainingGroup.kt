package com.fungorn.trainingcapacity.core.domain.model

enum class TrainingGroup(val displayName: String, val isLegacy: Boolean = false) {
    CHEST("Chest"),
    BACK("Back"),
    FRONT_DELTS("Front delts"),
    SIDE_DELTS("Side delts"),
    REAR_DELTS("Rear delts"),
    TRAPS("Traps"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    FOREARMS("Forearms"),
    ABS("Abs"),
    QUADS("Quads"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    CALVES("Calves"),
    DELTS("Delts", isLegacy = true),
    LEGS("Legs", isLegacy = true);

    companion object {
        val selectable: List<TrainingGroup> get() = entries.filterNot(TrainingGroup::isLegacy)
    }
}
