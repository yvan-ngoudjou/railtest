# Chapitre 07 — Identifier les comportements à tester

## Objectif pédagogique
Chapitre central : apprendre à descendre d'un objectif utilisateur vers un outcome observable,
sans s'arrêter aux détails d'implémentation rencontrés en chemin.

## Contexte fonctionnel
```text
Objectif utilisateur :
Acheter deux billets pour un adulte premium et un enfant.

Sous-objectif métier :
Calculer correctement le prix.

Outcome observable :
95 €  (100 - 10% premium = 90, + 5 de siège choisi pour l'adulte, l'enfant est gratuit)

Détails d'implémentation rencontrés en chemin :
PricingPolicy, transportPriceFor(), seatFeeFor(), Order.priceFor() par passager...
```

## Comportement étudié
`Order.evaluatePrice()` pour la commande décrite ci-dessus.

## Mauvais exemple
[`bad-example.java`](bad-example.java) — le test "vérifie" le comportement en rejouant lui-même
l'algorithme de calcul, détail par détail.

## Pourquoi il est fragile / incorrect
- Il connaît l'existence de `PricingPolicy` et de ses deux méthodes : un détail d'implémentation
  qui n'intéresse pas le Customer.
- Il duplique la logique de `Order.priceFor()` dans le test lui-même : si l'algorithme change de
  forme (même sans changer le résultat), le test doit être réécrit en miroir.
- Un test qui réimplémente l'algorithme du SUT ne prouve plus rien : il compare l'algorithme à
  lui-même.

## Bon exemple
[`good-example.java`](good-example.java) — le test vérifie directement l'outcome : `95 €`.

## Pourquoi il est meilleur
Il ignore volontairement `PricingPolicy`, `transportPriceFor`, `seatFeeFor` : ces noms
n'apparaissent nulle part. Il ne connaît que l'objectif utilisateur et l'outcome observable.

## Refactoring proposé
Réorganiser complètement le calcul interne (fusionner `PricingPolicy` dans `Order`, ou au contraire
le découper davantage).

## Résultat attendu après refactoring
Le bon exemple reste vert dans tous les cas : `95 €` ne dépend pas de la façon dont on l'obtient.

## Messages clés
- Un test ne devrait généralement pas connaître les détails d'implémentation traversés pour
  produire l'outcome.
- "Identifier un comportement" = trouver l'outcome observable le plus proche de l'objectif
  utilisateur, pas le premier point d'arrêt pratique dans le code.
