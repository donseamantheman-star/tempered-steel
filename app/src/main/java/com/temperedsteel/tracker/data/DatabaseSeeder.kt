package com.temperedsteel.tracker.data

object DatabaseSeeder {
    private val exercises = listOf(
        "Barbell Back Squat", "Barbell Bench Press", "Chest-Supported T-Bar Row",
        "Seated Leg Curl", "Single-Arm Cable Lateral Raise",
        "Smith Machine Hip Thrust", "Close-Grip Lat Pulldown", "Seated DB Overhead Press",
        "Standing Barbell Curl", "Machine Reverse Fly",
        "Belt Squat", "Incline Barbell Press", "Bent-Over Barbell Row",
        "Lying Leg Curl", "Seated Calf Raise",
        "Leg Press", "Cable Fly", "Overhead Cable Rope Extension",
        "EZ-Bar Preacher Curl", "Standing Calf Raise", "Bent-Over DB Reverse Fly",
        "Deadlift", "Romanian Deadlift", "Front Squat", "Hack Squat",
        "Pull-Up", "Chin-Up", "Dips", "Close-Grip Bench Press",
        "DB Bench Press", "Incline DB Press", "DB Row", "DB Shoulder Press",
        "DB Lateral Raise", "DB Bicep Curl", "Hammer Curl", "Incline DB Curl",
        "DB Skull Crusher", "DB Bulgarian Split Squat", "DB Shrug",
        "Cable Row", "Cable Lateral Raise", "Cable Face Pull",
        "Cable Triceps Pushdown", "Cable Bicep Curl",
        "Leg Extension", "Leg Press Calf Raise",
        "Lat Pulldown", "Chest Press Machine", "Shoulder Press Machine",
        "Pec Deck", "Smith Machine Squat", "Smith Machine Calf Raise"
    )

    suspend fun seed(dao: ExerciseDao) {
        if (dao.getExerciseCount() > 0) return
        dao.insertExercises(exercises.mapIndexed { i, name -> Exercise(name = name, sortOrder = i) })
    }
}
