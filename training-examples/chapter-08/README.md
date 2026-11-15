# Chapitre 08 — Mock vs Stub

## Objectif pédagogique
Montrer que "mock" et "stub" sont des **rôles**, alors que `Mockito.mock()` n'est qu'un **mécanisme
technique** : le même objet Mockito peut jouer l'un ou l'autre rôle selon comment on l'utilise.

## Contexte fonctionnel
`PurchaseTicketApplicationService.purchase(...)` utilise `TrajetSeatRepository.countAvailableSeats(...)`
pour décider si l'achat est possible, et `EmailSender` pour confirmer un achat réussi.

## Comportement étudié
> Un achat demandant plus de billets que de sièges disponibles est refusé.
> Un achat réussi envoie une confirmation.

## Mauvais exemple
[`bad-example.java`](bad-example.java) — un double Mockito de `TrajetSeatRepository` est utilisé
pour fournir la donnée (`countAvailableSeats`), **et** son appel est vérifié avec `verify(...)`.

## Pourquoi il est fragile / incorrect
`countAvailableSeats(...)` n'est pas un effet observable du système vers l'extérieur : c'est une
simple lecture qui permet au service de décider. Vérifier qu'elle a été appelée "une fois" fige un
détail d'implémentation (le service pourrait légitimement l'appeler différemment après un
refactoring, sans que le comportement observable change).

## Bon exemple
[`good-example.java`](good-example.java) — `TrajetSeatRepository` reste un Stub (on configure sa
réponse, on ne vérifie jamais son appel) ; `EmailSender` est un Mock légitime, car l'envoi de la
confirmation est lui-même l'effet observable attendu par le use case.

## Refactoring proposé
Changer la façon dont `PurchaseTicketApplicationService` interroge la disponibilité (par exemple,
appeler `countAvailableSeats` deux fois au lieu d'une pour affiner un message d'erreur).

## Résultat attendu après refactoring
Le mauvais exemple casse (`verify(repository, times(1))...` échoue) pour un comportement observable
identique. Le bon exemple reste vert : il n'a jamais rien vérifié sur cet appel.

## Messages clés
- `Mock` / `Stub` = rôles conceptuels. `Mockito.mock()` = mécanisme technique, utilisable pour l'un
  ou l'autre rôle.
- Un Stub fournit une donnée pour permettre une décision ; on ne vérifie pas son appel.
- Un Mock vérifie qu'une interaction a eu lieu, seulement quand cette interaction *est* le
  comportement observable attendu.
