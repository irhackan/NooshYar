package com.nooshyar.app.data.local

import com.nooshyar.app.data.local.entity.DrinkEntity

object DrinkSeedData {

    fun defaultDrinks(): List<DrinkEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            drink("آب", "Water", "آب", 250, 0, 0f, 0, "COLD", false, "💧", "آب ساده برای آب‌رسانی", now),
            drink("آب لیمو", "Lemon Water", "آب طعم‌دار", 300, 0, 2f, 10, "COLD", false, "🍋", "آب با لیمو تازه", now),
            drink("چای سیاه", "Black Tea", "چای سیاه", 200, 47, 0f, 2, "HOT", true, "🍵", "چای سیاه ایرانی", now, night = false),
            drink("چای سبز", "Green Tea", "چای سبز", 200, 28, 0f, 2, "HOT", true, "🍃", "چای سبز ملایم", now),
            drink("دمنوش بابونه", "Chamomile", "دمنوش", 200, 0, 0f, 0, "HOT", false, "🌼", "آرامش‌بخش برای شب", now, morning = false, night = true),
            drink("دمنوش نعناع", "Mint Tea", "دمنوش", 200, 0, 0f, 0, "HOT", false, "🌿", "نعناع تازه", now),
            drink("قهوه فرانسه", "French Coffee", "قهوه", 200, 95, 0f, 5, "HOT", true, "☕", "قهوه دمی", now, night = false),
            drink("اسپرسو", "Espresso", "اسپرسو", 30, 63, 0f, 3, "HOT", true, "☕", "اسپرسo تک", now, night = false),
            drink("کاپوچینو", "Cappuccino", "کاپوچینو", 180, 75, 6f, 80, "HOT", true, "☕", "قهوه با شیر", now, night = false),
            drink("لاته", "Latte", "لاته", 250, 75, 10f, 120, "HOT", true, "☕", "لاته ملایم", now, night = false),
            drink("قهوه بدون کافئین", "Decaf Coffee", "قهوه بدون کافئین", 200, 5, 0f, 5, "HOT", false, "☕", "طعم قهوه بدون کافئین", now),
            drink("شیر", "Milk", "شیر", 250, 0, 12f, 150, "COLD", false, "🥛", "شیر ساده", now),
            drink("دوغ", "Doogh", "دوغ", 300, 0, 3f, 60, "COLD", false, "🥛", "دوغ سنتی", now),
            drink("آب‌میوه پرتقال", "Orange Juice", "آب‌میوه", 250, 0, 22f, 110, "COLD", false, "🍊", "ویتامین C", now),
            drink("آب‌میوه سیب", "Apple Juice", "آب‌میوه", 250, 0, 24f, 115, "COLD", false, "🍎", "طعم طبیعی سیب", now),
            drink("نوشابه", "Soda", "نوشابه", 330, 0, 35f, 140, "COLD", false, "🥤", "نوشابه گازدار", now, morning = false),
            drink("نوشیدنی انرژی‌زا", "Energy Drink", "نوشیدنی انرژی‌زا", 250, 80, 27f, 110, "COLD", true, "⚡", "انرژی سریع", now, night = false),
            drink("نوشیدنی ورزشی", "Sports Drink", "نوشیدنی ورزشی", 500, 0, 14f, 80, "COLD", false, "🏃", "الکترولیت", now),
            drink("اسموتی میوه", "Fruit Smoothie", "اسموتی", 350, 0, 30f, 180, "COLD", false, "🥤", "میوه مخلوط", now),
            drink("شکلات داغ", "Hot Chocolate", "شیر", 250, 5, 24f, 190, "HOT", false, "🍫", "گرم و انرژی‌بخش", now),
            drink("چای سفید", "White Tea", "چای سفید", 200, 15, 0f, 2, "HOT", true, "🍵", "ملایم‌تر از چای سیاه", now),
            drink("دمنوش گزنه", "Nettle Tea", "دمنوش", 200, 0, 0f, 0, "HOT", false, "🌿", "گزنه خشک", now),
            drink("دمنوش زنجبیل", "Ginger Tea", "دمنوش", 200, 0, 2f, 5, "HOT", false, "🫚", "گرم‌کننده", now),
            drink("آب نارگیل", "Coconut Water", "آب", 330, 0, 6f, 45, "COLD", false, "🥥", "طبیعی و مفید", now),
            drink("ماست‌آب", "Yogurt Drink", "دوغ", 300, 0, 8f, 90, "COLD", false, "🥛", "خنک و مقوی", now),
            drink("چای ماسالا", "Masala Chai", "چای سیاه", 200, 50, 8f, 60, "HOT", true, "🍵", "ادویه‌ای", now, night = false),
            drink("آیس کافه", "Iced Coffee", "قهوه", 300, 90, 10f, 100, "COLD", true, "🧊", "قهوه سرد", now, night = false),
            drink("آیس لاته", "Iced Latte", "لاته", 350, 75, 12f, 130, "COLD", true, "🧊", "لاته سرد", now, night = false),
            drink("دمنوش گل گاوزبان", "Borage Tea", "دمنوش", 200, 0, 0f, 0, "HOT", false, "🌸", "آرام‌بخش", now, night = true),
            drink("آب گازدار", "Sparkling Water", "آب", 330, 0, 0f, 0, "COLD", false, "💧", "آب گازدار بدون قند", now),
            drink("شربت سکنجبین", "Sekanjebin", "آب طعم‌دار", 250, 0, 30f, 120, "COLD", false, "🍯", "سنتی و خنک", now),
            drink("چای ترش", "Sour Tea", "دمنوش", 200, 0, 1f, 5, "HOT", false, "🌺", "چای ترش شمال", now),
            drink("قهوه ترک", "Turkish Coffee", "قهوه", 60, 65, 0f, 5, "HOT", true, "☕", "قهوه غلیظ", now, night = false),
            drink("ماکتیل میوه", "Fruit Mocktail", "آب‌میوه", 300, 0, 18f, 90, "COLD", false, "🍹", "بدون الکل", now),
            drink("شیر کاکائو", "Chocolate Milk", "شیر", 250, 2, 24f, 190, "COLD", false, "🍫", "میان‌وعده شیرین", now),
            drink("دمنوش آویشن", "Thyme Tea", "دمنوش", 200, 0, 0f, 0, "HOT", false, "🌿", "آویشن کوهی", now),
            drink("آب هویج", "Carrot Juice", "آب‌میوه", 250, 0, 10f, 80, "COLD", false, "🥕", "سرشار از ویتامین", now),
            drink("چای دارچین", "Cinnamon Tea", "دمنوش", 200, 0, 2f, 5, "HOT", false, "🫖", "گرم و معطر", now),
            drink("قهوه سرد دم", "Cold Brew", "قهوه", 300, 100, 0f, 5, "COLD", true, "🧊", "کافئین ملایم‌تر", now, night = false)
        )
    }

    private fun drink(
        nameFa: String, nameEn: String, category: String,
        volume: Int, caffeine: Int, sugar: Float, calories: Int,
        temp: String, caffeinated: Boolean, icon: String, desc: String,
        now: Long,
        morning: Boolean = true, night: Boolean = false
    ) = DrinkEntity(
        nameFa = nameFa, nameEn = nameEn, category = category,
        defaultVolume = volume, caffeinePerServing = caffeine,
        sugarPerServing = sugar, caloriesPerServing = calories,
        temperatureType = temp, isCaffeinated = caffeinated,
        icon = icon, description = desc,
        suitableMorning = morning, suitableNoon = true,
        suitableEvening = !night, suitableNight = night,
        isCustom = false, createdAt = now
    )
}
