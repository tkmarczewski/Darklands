# Raport Audytu Błędów (Bug Audit) - Projekt GrimReich
**Data:** 2026-07-13
**Status:** WYKRYTO BŁĘDY KRYTYCZNE

## 1. Błędy Krytyczne (Critical)
- **Brakujący Typ Przeciwnika**: Zadanie q_blood_5_hard odwołuje się do PAST_SHADE_ELITE, który nie istnieje w enumie EnemyType. Próba rozpoczęcia tej walki zakończy się błędem/crashem.
- **Nieosiągalna Treść Meta**: MetaObservationSystem odblokowuje łańcuch meta (q_meta_1) dopiero po ukończeniu **30 zadań**. Obecnie w plikach JSON znajduje się jedynie **13 unikalnych zadań**. Zakończenie "Margines Sesji" jest fizycznie niemożliwe do osiągnięcia bez dev menu.
- **Brak definicji zadań q_meta_2 do q_meta_7**: Kod EndingSystem sprawdza q_meta_7, ale w zasobach gry (JSON) nie ma definicji zadań powyżej q_meta_1.

## 2. Błędy Logiczne (Major)
- **Pominięcie Tury/Rundy**: W CombatSystem.kt, jeśli przeciwnik ma najwyższą inicjatywę (index 0), licznik rund (c.round++) oraz przeliczenie inicjatywy zostają pominięte w danej rundzie.
- **Niespójność Śmierci**: Mechaniki zewnętrzne (np. CollapseEngine) mogą zredukować HP bohatera do 0, ale flaga isDead nie zostanie ustawiona, co tworzy "bohaterów-duchów".
- **Niedziałające Callbacks**: Pole onCombatEnd w CombatSystem nigdy nie jest wywoływane. System walki nie powiadamia poprawnie reszty gry o swoim zakończeniu przez ten mechanizm.

## 3. Sugerowane Naprawy:
1.  Dodać PAST_SHADE_ELITE do EnemyType i Bestiary.
2.  Obniżyć próg odblokowania łańcucha Meta w MetaObservationSystem (np. do 10 zadań) lub dodać brakujące zadania.
3.  Zaktualizować CombatSystem, aby inkrementacja rundy odbywała się niezależnie od tego, czyja jest tura na indeksie 0.
4.  Wprowadzić sprawdzanie śmierci w metodzie Hero.normalize().
