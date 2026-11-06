# Raport Audytu Kodu: Ochrona Stanu Poległych Bohaterów, Ekwipunku i Podróży (Commit efdc242)

## Wykaz Załatanych Podatności

### 1. Zabezpieczenie Modlitwy u Świętych (`SaintsViewModel.kt`)
- **Problem:** Wywołanie `pray()` wybierało pierwszego bohatera z listy (`party.firstOrNull()`), co w przypadku gdy lider był martwy (`isDead == true`), wywoływało modlitwę i odnawiało poczytalność (`sanity`) oraz wiarę na ciele poległego bohatera.
- **Rozwiązanie:** W `SaintsViewModel.kt` zmodyfikowano wybór na `party.firstOrNull { !it.isDead }`, gwarantując, że modlitwa aktywuje się wyłącznie dla żywych członków drużyny.

### 2. Ochrona przed Podróżą Poległej Drużyny (`TravelSystem.kt`)
- **Problem:** W `TravelSystem.travelTo()` brakowało weryfikacji żywych członków drużyny. W efekcie drużyna składająca się wyłącznie z martwych postaci mogła przemierzać mapę świata i ponosić koszty podróży.
- **Rozwiązanie:** Dodano warunek `if (s.party.none { !it.isDead })`, wycofujący podróż z komunikatem *"Brak żywych bohaterów do podjęcia podróży."*.

### 3. Zabezpieczenie Zbrojowni przed Ekwipowaniem Poległych (`InventorySystem.kt`)
- **Problem:** W `InventorySystem.equip()` brakowało weryfikacji stanu życiowego bohatera, co pozwalało na zakładać pancerze i broń na martwe postacie.
- **Rozwiązanie:** Dodano weryfikację `if (hero.isDead)`, odrzucającą zakładanie ekwipunku na poległych bohaterów.

---

## Wyniki Suity Testowej
* **Wszystkie testy jednostkowe i integracyjne:** 149 passed, 0 skipped, 0 failed.
* **Git Master:** Commit `efdc242` opublikowany pomyślnie.
* **Status:** Mechaniki podróży, ekwipunku i kultu świętych zostały w pełni zweryfikowane i umocnione.
