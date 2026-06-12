# Developer Guide: GrimReich Systems

Przewodnik techniczny dotyczący utrzymania i rozbudowy systemów gry.

## 1. System Dialogowy (`DialogueManager`)
Dialogi są sercem gry. Każdy węzeł to `DialogueNode`.
- **Wyzwalanie**: `startNodeId` jest przekazywany w `Intent` do `DialogueActivity`.
- **Glitche**: System automatycznie wymazuje 25% słów, gdy `globalStability < 30`.
- **Jak dodać dialog?**: Edytuj `DialogueManager.seedBasicDialogues()` i zarejestruj nowy węzeł z unikalnym ID.

## 2. Visual Effects (`GlitchOverlayView`)
Widok ten jest nakładany na `HubActivity` i `CityActivity`.
- **Parametry**: Natężenie efektu zależy od `(70 - world.globalStability)`.
- **Logika**: Wykorzystuje `canvas.translate()` do generowania drżenia obrazu (jitter) oraz post-delayed invalidation dla animacji.

## 3. Katalog Regionalny (`CityCatalogue`)
Centralne źródło prawdy o świecie.
- **Synchronizacja**: Każde miasto musi mieć przypisany `backgroundDrawable` zgodny z plikami w `drawable-nodpi`.
- **Ceny**: `priceModifier` wpływa na koszty w `TradeActivity`.

## 4. Testowanie i Quality Assurance
Projekt wykorzystuje JUnit 5.
- **Coverage**: Główne testy logiki znajdują się w `com.grimreich.systems.AuditLogicTest`.
- **Krytyczne**: Zawsze sprawdzaj `colorSurface` w `styles.xml` po dodaniu nowych komponentów Material – ich brak powoduje natychmiastowe crashe na emulatorze.

## 5. Konwencja Assetów
- `bg_region_[nazwa]`: 1920x1080 PNG (Nodpi).
- `port_[rola]`: Portrety NPC 512x512 PNG.
- `ui_frame_gold`: 9-patch lub PNG wysokiej gęstości dla ramek interfejsu.

---
*Kod jest snem programisty. Ty jesteś jego debuggerem.*
