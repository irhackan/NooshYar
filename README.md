# NooshYar (نوش‌یار)

دستیار هوشمند انتخاب و مدیریت نوشیدنی — نسخه MVP اندروید

**طراح و برنامه‌نویس:** کاظم دهناد  
**پست الکترونیکی:** irhackan@gmail.com

## قابلیت‌های نسخه ۱.۱ (فاز ۲)

- گزارش ماهانه و سالانه
- تقویم مصرف رنگی
- یادگیری رفتاری از پذیرش/رد پیشنهادها
- پشتیبان‌گیری و بازیابی JSON

## قابلیت‌های نسخه ۱.۰ (MVP)

- صفحه Splash و Onboarding فارسی
- ساخت پروفایل کاربر
- بانک ۴۰ نوشیدنی
- ثبت سریع نوشیدنی
- محاسبه کافئین روزانه و کافئین فعال (تخمین)
- صفحه اصلی با پیشنهاد هوشمند
- «الان چه بنوشم؟» با موتور قواعد
- گزارش روزانه و هفتگی
- تاریخچه مصرف با فیلتر
- اعلان‌های پایه (آب و کافئین)
- حالت روشن/تاریک/سیستم
- صفحه درباره برنامه

## فناوری‌ها

- Kotlin
- Jetpack Compose + Material 3
- MVVM + Clean Architecture
- Room Database
- DataStore
- Hilt
- WorkManager
- Navigation Compose

## پیش‌نیازها

- Android Studio Hedgehog (2023.1.1) یا جدیدتر
- JDK 17
- Android SDK 34

## اجرا در Android Studio

1. پروژه را باز کنید: `File → Open → NooshYarApp`
2. Gradle Sync را اجرا کنید
3. یک Emulator یا دستگاه Android 8.0+ (API 26) وصل کنید
4. Run ▶ را بزنید

## ساخت APK

### Debug (محلی)

```bash
./gradlew assembleDebug
```

خروجی: `app/build/outputs/apk/debug/app-debug.apk`

### Release

```bash
./gradlew assembleRelease
```

خروجی: `app/build/outputs/apk/release/app-release-unsigned.apk`

## GitHub Actions

با push به `main`، workflow خودکار APK می‌سازد. فایل APK در بخش **Actions → Artifacts** قابل دانلود است.

## ساختار پروژه

```
app/src/main/java/com/nooshyar/app/
├── core/           # UI theme, components, utils
├── data/           # Room, repositories, mappers
├── domain/         # Models, engines
├── presentation/   # Screens, ViewModels, navigation
├── notification/   # WorkManager notifications
└── di/             # Hilt modules
```

## تست

1. اولین اجرا: Splash → Onboarding → پروفایل → خانه
2. ثبت یک لیوان آب و یک قهوه
3. بررسی کافئین در گزارش روزانه
4. «الان چه بنوشم؟» را امتحان کنید
5. حالت تاریک را از پروفایل فعال کنید

## هشدار سلامت

نوش‌یار ابزار ثبت عادت‌هاست و جایگزین نظر پزشک نیست. مقادیر کافئین تخمینی هستند.

## مجوز

© 2026 کاظم دهناد — All rights reserved.
