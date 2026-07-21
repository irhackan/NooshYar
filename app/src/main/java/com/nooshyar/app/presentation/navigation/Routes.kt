package com.nooshyar.app.presentation.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val PROFILE_SETUP = "profile_setup"
    const val MAIN = "main"
    const val WHAT_TO_DRINK = "what_to_drink"
    const val LOG_DRINK = "log_drink"
    const val LOG_DRINK_WITH_ID = "log_drink/{drinkId}"
    const val ABOUT = "about"
    const val HISTORY = "history"

    fun logDrink(drinkId: Long) = "log_drink/$drinkId"
}

object BottomNavItem {
    const val HOME = "home"
    const val LOG = "log"
    const val SUGGEST = "suggest"
    const val REPORTS = "reports"
    const val PROFILE = "profile"
}
