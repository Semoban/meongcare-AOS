# Jetpack Compose 마이그레이션 로드맵

세모반 앱의 XML(View) 기반 UI를 Jetpack Compose로 점진적으로 전환하기 위한 계획 문서.

## 전략

**Fragment 단위 점진 전환.** Navigation 그래프, `MainActivity`의 바텀 내비게이션·FAB, 공유 `ToolbarFragment`는 그대로 두고, 각 Fragment의 뷰 계층만 `ComposeView`로 교체한다. 앱 전체가 전환된 후에 Navigation Compose 도입을 검토한다.

### 화면 전환 패턴 (SymptomFragment 참고)

1. 레이아웃 XML을 `FragmentContainerView`(툴바 등 공유 Fragment) + `ComposeView`만 남기고 비운다.
2. 화면 UI는 `<Feature>Screen.kt`의 stateless 컴포저블로 작성한다.
   - 상태(LiveData)는 Fragment에서 `observeAsState()`로 구독해 파라미터로 내려준다.
   - 클릭·내비게이션은 람다 콜백으로 올려 Fragment에서 처리한다 (`findNavController()` 유지).
3. `ComposeView`에는 반드시 `ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed`를 설정한다.
4. 루트에 `SemobanTheme`을 감싼다.
5. `@Preview` 컴포저블(데이터 있음/없음 상태)을 함께 작성한다.
6. URL/Uri 이미지는 Coil 미도입 상태이므로 `AndroidView` + Glide 인터롭(`ExcretaComponents.kt`의 `GlideImage`)을 사용한다.
7. 화면 전용 재사용 컴포넌트는 `<feature>/view/<Feature>Components.kt`에 `internal`로 모아 두고, 공용화는 12단계에서 일괄 처리한다.
8. 카드형 컨테이너는 `Card` 대신 `Surface(color, shape, shadowElevation)`를 사용한다. 그림자 없는 카드(elevation 0)는 `shadowElevation`을 생략한다.

### 디자인 시스템 (`designsystem/theme/`)

| 파일 | 내용 | 원본 리소스 |
|---|---|---|
| `Color.kt` | `Main1~4`, `Sub1~8`, `Gray1~5`, `Black`, `White` 등 | `res/values/colors.xml` |
| `Type.kt` | `Pretendard` FontFamily + `SemobanTypography`(title1SemiBold ~ body3Light) | `res/values/typography.xml` |
| `Theme.kt` | `SemobanTheme` — Material3 lightColorScheme 매핑 | `res/values/themes.xml` |

새 화면에서 색·타이포는 반드시 이 파일들을 사용한다. XML 리소스를 `colorResource()`로 직접 참조하지 않는다.

## 진행 순서

복잡도가 낮고 구형 패턴 정리 효과가 큰 feature부터 진행한다.

| 단계 | 대상 | 화면 수 | 상태 |
|---|---|---|---|
| 0 | Compose 빌드 설정 + 디자인 시스템 | - | ✅ 완료 |
| 1 | `symptom` — 메인 리스트 | 1 | ✅ 완료 |
| 2 | `symptom` — 나머지 (Add/Edit/Info/ListEdit/Select/SelectCustom/BottomSheet) | 7 | ✅ 완료 |
| 3 | `excreta` (메인/Add/Edit/Info/RecordEdit — Add·Edit는 `ExcretaAddEditScreen` 공유) | 5 | ✅ 완료 |
| 4 | `notice` (탭 2개 → `NoticeScreen` 단일 화면, TabRow+HorizontalPager) | 1 | ✅ 완료 |
| 5 | `weight` (MPAndroidChart는 `AndroidView` 인터롭 유지, 편집 다이얼로그는 Compose `Dialog`) | 1 | ✅ 완료 |
| 6 | `feed` (메인/Add/Edit/Info/OldFeed/SearchFeed — Add·Edit는 `FeedAddEditScreen` 공유, DateRangeCalendarView는 `AndroidView` 인터롭) | 6 | ✅ 완료 |
| 7 | `medicalRecord` (메인/Add/Info/InfoEdit/ListEdit — Add·InfoEdit는 `MedicalRecordFormScreen` 공유, 캘린더·TimePicker는 `AndroidView` 인터롭, 날짜·사진 바텀시트는 기존 Fragment 유지) | 5 | ✅ 완료 |
| 8 | `supplement` (메인/Add/Info/RoutineEdit — 주기·시간·사진 바텀시트는 기존 Fragment 유지, static Retrofit → Hilt `SupplementRetrofitClient` 전환, 미사용 SearchFragment 삭제) | 4 | ✅ 완료 |
| 9 | `info` / `login` / `onboarding` (Profile/Setting/PetInfo/PetEdit/Login/OnBoarding 페이저/Complete/DogAdd/DogVarietySearch — 사진·생일 바텀시트는 기존 Fragment 유지, 온보딩 ViewPager2 → `HorizontalPager`, PetEdit·DogAdd 폼 필드는 `InfoComponents.kt` 공유) | 10 | ✅ 완료 |
| 10 | `home` (dog selector, 날짜 스트립, 요약 카드) | ~3 | |
| 11 | `toolbar` 공유 Fragment → 공용 컴포저블 전환 | 1 | |
| 12 | 다이얼로그·바텀시트·스낵바(`CustomSnackBar`) 공용 컴포넌트화 | - | |
| 13 | 정리: 미사용 XML 레이아웃/drawable 삭제, dataBinding 플래그 제거 검토 | - | |

## 빌드 설정 (완료)

- `app/build.gradle`: `compose = true`, compiler extension **1.5.10** (Kotlin 1.9.22 호환), BOM **2024.05.00**
- 의존성: ui, ui-tooling-preview, foundation, material3, runtime-livedata, lifecycle-viewmodel-compose, activity-compose
- Kotlin/AGP 업그레이드 시 [compiler ↔ Kotlin 호환표](https://developer.android.com/jetpack/androidx/releases/compose-kotlin) 확인 필요. Kotlin 2.0 이상으로 올리면 `org.jetbrains.kotlin.plugin.compose` 플러그인 방식으로 교체한다.

## 마이그레이션 시 함께 정리할 것

- 구형 ViewModel 패턴(`GlobalApplication.applicationContext()` 직접 호출)은 화면 전환 시 CLAUDE.md의 신규 패턴(Hilt 주입 `UserPreferences` + `asLiveData()`)으로 교체
- `symptom`의 static Retrofit 싱글톤 → Hilt 주입 `RetrofitClient`로 교체 (`supplement`는 8단계에서 완료)
- RecyclerView Adapter는 컴포저블 `LazyColumn`으로 대체되므로 삭제
- LiveData는 화면 전환이 끝난 feature부터 StateFlow 전환 검토
