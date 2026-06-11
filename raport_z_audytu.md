# Raport z Audytu Projektu: Grimreich

## 1. Status Budowania i Testów
*   **Budowanie:** SUKCES (Gradle 9.2.1, Kotlin 2.2.10).
*   **Testy Jednostkowe:** 21/21 zaliczonych. Skupione na kalkulatorach statystyk.
*   **Pokrycie (Jacoco):** **~9.3%**. Bardzo niskie. Kluczowe systemy (`com.grimreich.systems`) nie są testowane automatycznie.

## 3. Analiza Statyczna i Jakość Kodu

### Android Lint (590 ostrzeżeń)
*   **Problemy z Zasobami:** Ogromna liczba nieużywanych layoutów i grafik (drawables), co niepotrzebnie zwiększa rozmiar aplikacji.
*   **Dostępność (Accessibility):** Prawie wszystkie elementy `ImageView` nie posiadają `contentDescription`.
*   **Internacjonalizacja:** Większość tekstów jest zakodowana na sztywno (hardcoded) w plikach XML i kodzie Kotlin, co uniemożliwia łatwe tłumaczenie.
*   **Wydajność UI:** Wykryto problemy z "overdraw" (wielokrotne rysowanie tła) oraz mało wydajne układy (zagnieżdżone `layout_weight`).
*   **Splash Screen:** Używana jest przestarzała metoda `SplashActivity`, co na Androidzie 12+ powoduje wyświetlanie dwóch ekranów powitalnych.

### Detekt (Problemy Architektoniczne)
*   **Magic Numbers:** Powszechne użycie "magicznych liczb" w logice gry (balans walki, szanse na loot, ekonomia). Utrudnia to balansowanie gry i wprowadzanie zmian.
*   **Złożoność metod:** Główne funkcje logiki (np. `resolveRound`, `resolvePlayerAction`) są zbyt długie i skomplikowane (wysoka złożoność cyklomatyczna).
*   **Złe praktyki:** Wykryto połykanie wyjątków (Swallowed Exceptions) oraz łapanie zbyt ogólnych błędów (`Exception`), co może ukrywać realne bugi.
*   **Konstruktor `Career`:** Posiada aż 12 parametrów, co narusza zasady czystego kodu.

## 4. Wykryte Błędy Krytyczne (Crashes)

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

## 6. Rekomendacje
1.  **Poprawka Motywu (KRYTYCZNE):** Dodać `colorSurface` do `Theme.GrimReich`, aby umożliwić nawigację po mapie i mieście.
2.  **Refaktoryzacja Logiki:** Wydzielić magiczne liczby do stałych (Constants) w celu łatwiejszego balansu gry.
3.  **Czyszczenie Zasobów:** Usunąć nieużywane layouty i grafiki wskazane przez Lint.
4.  **Poprawa Obsługi Błędów:** Zastąpić ogólne bloki `catch (e: Exception)` konkretnymi wyjątkami i logowaniem błędów.
5.  **Usprawnienie Kreatora:** Poprawić feedback w Kreatorze Postaci (obecnie trudno zgadnąć, że trzeba wybrać dokładnie 3 umiejętności).
