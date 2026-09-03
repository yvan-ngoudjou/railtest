# Chapitre 06 — Nommer correctement les tests

## Objectif pédagogique
Montrer que le nom d'un test doit raconter *scénario + comportement attendu*, pas reprendre le nom
de la méthode appelée.

## Contexte fonctionnel
Trois comportements déjà présents dans le projet : le calcul du prix, la réservation d'un siège.

## Comportement étudié
Les mêmes trois comportements, nommés de deux façons différentes.

## Mauvais exemple
Voir [`bad-example.java`](bad-example.java).

## Pourquoi il est fragile / incorrect
- `testEvaluatePrice()` et `shouldEvaluatePrice()` ne disent rien du scénario testé : quel client,
  quel passager, quel siège ? Il faut lire le corps du test pour le savoir.
- Si un jour trois tests différents portent sur `evaluatePrice()` (premium, enfant, siège choisi),
  ces noms entrent en collision ou deviennent `testEvaluatePrice2()`, `testEvaluatePrice3()`...
- `shouldCallReserve()` décrit un appel de méthode, pas un comportement métier.

## Bon exemple
Voir [`good-example.java`](good-example.java).

## Pourquoi il est meilleur
Chaque nom se lit comme une phrase : *"quand telle situation, alors tel résultat"*. Un rapport de
tests qui échoue devient lisible sans ouvrir le code.

## Refactoring proposé
Aucun changement de comportement ici : uniquement un renommage.

## Résultat attendu après refactoring
Les deux versions passent ou échouent exactement pareil ; seule la lisibilité du rapport change.

## Messages clés
- `scénario + comportement attendu`, pas `test + nom de la méthode`.
- Un bon nom de test sert de documentation vivante quand on lit la liste des tests d'une classe.
