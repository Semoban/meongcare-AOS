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
| 10 | `home` (dog selector·주간 달력·요약 카드 → `HomeScreen` 단일 화면, 주간 스와이프는 `detectHorizontalDragGestures`, 어댑터 3종·클릭 리스너 인터페이스 삭제) | 1 | ✅ 완료 |
| 11 | `toolbar` 공유 Fragment → `ToolbarCalendarWeek` 공용 컴포저블 전환 (주간 스트립 `CalendarWeekRow`는 home과 공유, 날짜 선택·주 이동 로직은 `ToolbarViewModel`로 이동, 호스트 4개 화면 `FragmentContainerView` 제거) | 1 | ✅ 완료 |
| 12 | 다이얼로그·바텀시트·스낵바(`CustomSnackBar`) 공용 컴포넌트화 — `designsystem/component/` 신설(GlideImage·CircleGlideImage / ConfirmDialog·DeleteDialogOverlay / 폼 필드·GenderChip·NeuterCheckbox), 사진 선택 바텀시트 6종 → 공용 `PhotoSelectBottomSheetFragment` 1종 + 리스너 `PhotoMenuListener`로 통일 | - | ✅ 완료 |
| 13 | 정리: 미사용 XML 레이아웃 10개·drawable 90개·미도달 Fragment 2개(PhotoEditor/RecordShare) 삭제, `dataBinding` 플래그 제거(뷰 계층은 `viewBinding`만 사용) | - | ✅ 완료 |

## 빌드 설정 (완료)

- `app/build.gradle`: `compose = true`, compiler extension **1.5.10** (Kotlin 1.9.22 호환), BOM **2024.05.00**
- 의존성: ui, ui-tooling-preview, foundation, material3, runtime-livedata, lifecycle-viewmodel-compose, activity-compose
- Kotlin/AGP 업그레이드 시 [compiler ↔ Kotlin 호환표](https://developer.android.com/jetpack/androidx/releases/compose-kotlin) 확인 필요. Kotlin 2.0 이상으로 올리면 `org.jetbrains.kotlin.plugin.compose` 플러그인 방식으로 교체한다.

## 후속 과제 (로드맵 완료 이후)

0~13단계 완료(2026-07-09) 시점 기준 남은 작업. 우선순위 순.

### 1. 구형 패턴 정리 (소규모·안전, 먼저 진행)

- [ ] `symptom`의 static Retrofit 싱글톤(`SymptomRetrofitInstance.getInstance()`) → Hilt 주입 `RetrofitClient`로 교체 (`supplement`는 8단계에서 완료). `SymptomRepository`가 인터페이스 없이 직접 생성되는 구조도 함께 interface + impl + Module 3파일 구조로 정리
- [ ] `GlobalApplication.applicationContext()` 직접 호출 잔재 제거 — `SymptomViewModel`, `MedicalRecordViewModel` 2곳. CLAUDE.md 신규 패턴(Hilt 주입 `UserPreferences` + `asLiveData()`)으로 교체
- [ ] 구형 토큰 패턴(`fetchAccessToken()` + collect) 정리 — excreta 5개·feed 계열·weight Fragment. `accessTokenPreferencesLiveData` 관찰 방식으로 통일

### 2. 상태 관리 현대화

- [ ] LiveData → StateFlow 전환 — 화면 전환이 끝났으므로 feature 단위로 점진 진행. `observeAsState()` → `collectAsStateWithLifecycle()` 교체 (`lifecycle-runtime-compose` 의존성 추가 필요)

### 3. Navigation Compose 도입 (대규모, 마지막)

- [ ] Fragment 껍데기 제거 + Navigation Compose 전환. 이때 함께 처리:
  - `CustomSnackBar` → Compose `SnackbarHost` 전환 (Fragment 호스트 구조에서는 View 기반 유지가 결정 사항)
  - 날짜 계열 바텀시트 통합 — `CalendarBottomSheetFragment` / `CalendarBottomSheetDialogFragment` / `MedicalRecordDateBottomSheetDialogFragment` 3종 + `BirthdayBottomSheetFragment`, supplement 주기·시간 바텀시트 (동작 차이가 있어 12단계에서 미통합)
  - `MainActivity`의 바텀 내비게이션·FAB Compose 전환

### 4. 빌드 인프라

- [ ] Kotlin 2.x 업그레이드 — `org.jetbrains.kotlin.plugin.compose` 플러그인 방식으로 교체하고 compiler extension 버전 제거 (위 "빌드 설정" 참고)

### 참고: 관찰된 정리 후보 (선택)

- feature별 TopBar 컴포저블(`InfoTopBar`/`ExcretaTopBar`/`SupplementTopBar` 등) 중복 — designsystem 통합 후보
- `medicalRecord/MedicalRecordDeleteDialog`는 공용 `DeleteDialogOverlay`와 별개 구현으로 잔존 — 디자인 확인 후 통합 검토
- 미사용 문자열·색상 리소스 정리 (13단계는 레이아웃/drawable만 수행)
