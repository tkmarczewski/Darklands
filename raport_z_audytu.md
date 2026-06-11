# Raport z Audytu Projektu: Grimreich

## 1. Status Budowania (Build)
*   **Wynik:** SUKCES
*   **Główny artefakt:** `app-debug.apk`
*   **Uwagi:** Proces budowania jest stabilny. Projekt poprawnie wykorzystuje system Gradle oraz narzędzia do analizy kodu (Jacoco, Detekt).

## 2. Testy Jednostkowe i Pokrycie
*   **Wynik testów:** 21/21 zaliczonych (100% sukcesu).
*   **Pokrycie kodu (Coverage):** **~9.3%**
*   **Analiza:** 
    *   Testy skupiają się wyłącznie na logice matematycznej (system reputacji, ekonomia, generatory statystyk).
    *   Całkowity brak testów jednostkowych dla widoków (Activities, ViewModels) oraz logiki NPC i Questów.
    *   Większość klas w paczce `com.grimreich.grimreich.v1` ma 0% pokrycia.

## 3. Wykryte Błędy Krytyczne (Crashes)

### Błąd 1: Crash przy otwieraniu okien (Brak atrybutu motywu)
*   **Lokalizacja:** `styles.xml` / `UiUtils.showNarrativePopup`
*   **Typ:** `java.lang.IllegalArgumentException`
*   **Opis:** Motyw `Theme.GrimReich` nie posiada zdefiniowanego atrybutu `colorSurface`. Ponieważ aplikacja używa komponentów Material, każda próba wyświetlenia okna dialogowego (Karczma, wybór regionu na Mapie) kończy się natychmiastowym crashem.
*   **Skutek:** Gracz nie może podróżować ani korzystać z interakcji w mieście.

### Błąd 2: NPE w HubActivity
*   **Lokalizacja:** `HubActivity.kt:55`
*   **Typ:** `java.lang.NullPointerException`
*   **Opis:** Próba ustawienia `setOnClickListener` na widoku `tvDevMenuTrigger`, który w niektórych układach landscape nie jest poprawnie odnajdywany lub jest nullem.

## 4. Audyt Działania (Emulator)
*   **Menu Główne:** Działa poprawnie.
*   **Kreator Postaci:** Funkcjonalny, ale walidacja (wymagane 3 specjalizacje) jest rygorystyczna i mało czytelna dla użytkownika. Przycisk "AUTO" ułatwia przejście dalej.
*   **System Dialogów:** Działa (sprawdzone na Heinrichu w Grimhold). Wybory gracza poprawnie modyfikują stan (np. utrata HP przy ofierze krwi).
*   **Mapa Świata:** Wyświetla się poprawnie, ale interakcja z punktami na mapie powoduje crash (patrz Błąd 1).
*   **Ekran Miasta:** Przyciski Karczmy i Sklepu nie działają przez błąd dialogów. Przycisk wyjścia z miasta działa.

## 5. Rekomendacje
1.  **Poprawka Motywu (Priorytet):** Dodać `<item name="colorSurface">?attr/colorBackground</item>` do głównego stylu w `styles.xml`.
2.  **Bezpieczny dostęp do widoków:** Wprowadzić View Binding lub null-safe calls (`?.`) w `HubActivity`, aby zapobiec NPE.
3.  **Zwiększenie Pokrycia:** Dodać testy dla systemów NPC oraz walki (obecnie 0% pokrycia).
4.  **Usprawnienie Nawigacji:** Naprawić przejścia między Mapą a Hubem, aby zmiany lokalizacji były odświeżane bez konieczności restartu Activity.
