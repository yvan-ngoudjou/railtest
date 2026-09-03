# Chapitre 11 — Pourquoi les tests deviennent fragiles

## Objectif pédagogique
Fournir plusieurs démonstrations fortes et réalistes de tests fragiles, puis montrer un
refactoring réel (déjà appliqué au projet, commits `07`, `08`, `09`) qui conserve le comportement
externe mais casse ces mauvais tests, alors que le bon test correspondant reste vert.

## Contexte fonctionnel
Trois vices classiques, chacun illustré avec du code réaliste — un développeur expérimenté
pourrait tout à fait écrire ces tests sans y voir de problème sur le moment.

---

## Démonstration 1 — vérification excessive des interactions

### Comportement étudié
Le calcul du prix d'une commande premium avec siège choisi.

### Mauvais exemple
[`bad-example-1-interaction-verification.java`](bad-example-1-interaction-verification.java) —
vérifie que `PricingPolicy.transportPriceFor(...)` et `.seatFeeFor(...)` sont appelées, en plus de
vérifier le résultat final.

### Pourquoi il est fragile
Il connaît une collaboration interne introduite par le commit `07`. Avant ce commit, ces méthodes
n'existaient pas : ce test n'aurait même pas pu être écrit contre le code du commit `06`. Un
Domain Model n'a pourtant pas changé de comportement observable entre les deux commits.

### Ce qu'il connaît de trop
L'existence de `PricingPolicy` et le découpage exact de son API.

### Quel refactoring le cassera
Fusionner `PricingPolicy` dans `Order` (retour arrière du commit `07`), ou simplement renommer ses
méthodes.

### Pourquoi cette casse est un faux positif
Le Customer paie toujours `95 €` : rien d'observable n'a changé.

### Comment améliorer le test
Ne vérifier que `order.evaluatePrice()`, comme le fait le test de référence
`OrderPricingTest#seat_selection_fee_is_not_discounted_for_premium_customers`.

---

## Démonstration 2 — assertion sur l'état interne via une méthode exposée uniquement pour le test

### Comportement étudié
Le nombre de sièges automatiquement libérés après un paiement échoué.

### Mauvais exemple
[`bad-example-2-getter-for-test.java`](bad-example-2-getter-for-test.java) — nécessiterait un
`TrajetSeat.getVersion()` exposé uniquement pour vérifier que `version` a été incrémentée deux fois
(réservé puis libéré), au lieu de vérifier le `status` réellement observable.

### Pourquoi il est fragile
`version` n'a aucun sens métier observable : c'est un simple compteur technique préparé pour un
futur optimistic locking (voir `TrajetSeat`, §9 du cahier des charges). Le figer dans un test le
transforme en pseudo-API publique alors qu'il ne devrait jamais être lu par personne d'autre que
la couche de persistance.

### Ce qu'il connaît de trop
Le détail que `reserve()` et `release()` incrémentent un compteur interne, et de combien.

### Quel refactoring le cassera
Changer la stratégie de `version` (par exemple, ne plus l'incrémenter sur `release()`, ou utiliser
un timestamp à la place d'un compteur).

### Pourquoi cette casse est un faux positif
Le comportement observable — un siège libéré redevient `AVAILABLE` — n'a pas changé.

### Comment améliorer le test
Vérifier uniquement `seat.status() == TrajetSeatStatus.AVAILABLE`, comme le fait
`TrajetSeatTest#releasing_a_reserved_seat_makes_it_available_again`.

---

## Démonstration 3 — forêt de mocks reproduisant l'algorithme interne du SUT

### Comportement étudié
L'attribution automatique d'un siège à chaque passager sans siège choisi.

### Mauvais exemple
[`bad-example-3-mocks-everything.java`](bad-example-3-mocks-everything.java) — mocke tous les
collaborateurs de `PurchaseTicketApplicationService` et vérifie l'ordre exact des appels
(`findAvailableSeats` avant `find`, nombre exact d'appels par passager).

### Pourquoi il est fragile
Il connaît l'algorithme exact d'attribution de sièges tel qu'il existait **avant** le commit `08`.

### Ce qu'il connaît de trop
Le fait que le service pré-calcule un lot de sièges via un seul appel à `findAvailableSeats`,
plutôt que d'en demander un par passager au fil de l'eau.

### Quel refactoring le cassera
Le commit `08` lui-même : remplacement de l'`Iterator` pré-calculé par une boucle incrémentale
appelant `findAvailableSeats` une fois par passager automatique.

### Pourquoi cette casse est un faux positif
`PurchaseTicketApplicationServiceTest` (le test de référence réel) reste vert après le commit `08` :
chaque passager obtient toujours un siège disponible distinct, et le nombre de billets émis est
inchangé.

### Comment améliorer le test
Vérifier l'outcome ("chaque passager obtient un siège disponible distinct, N billets sont émis"),
pas la mécanique d'appel — exactement ce que fait
`PurchaseTicketApplicationServiceTest#successful_purchase_issues_one_ticket_per_passenger`.

---

## Messages clés
- Vérifier une interaction interne, un état technique interne, ou reproduire l'algorithme du SUT
  produisent tous le même symptôme : un test rouge pour un comportement observable inchangé.
- Ce n'est pas la présence de mocks qui rend un test fragile, c'est **ce qu'on vérifie avec eux**
  et **ce que le test connaît** du SUT.
