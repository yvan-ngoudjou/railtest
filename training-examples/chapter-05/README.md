# Chapitre 05 — Anatomie d'un test

## Objectif pédagogique
Identifier clairement, sur un test simple du Domain Model : SUT, fixture, Arrange, Act, Assert,
outcome observable.

## Contexte fonctionnel
Un `Passenger` de moins de 10 ans voyage gratuitement.

## Comportement étudié
`Order.evaluatePrice()` pour un passager de 9 ans.

## Mauvais exemple
Sans objet pour ce chapitre : l'objectif est d'annoter un bon test, pas d'en critiquer un mauvais.

## Pourquoi il est fragile / incorrect
Sans objet.

## Bon exemple
Voir [`good-example.java`](good-example.java), annoté ligne par ligne.

## Pourquoi il est meilleur
Chaque partie du test a un rôle unique et identifiable ; rien n'est mélangé.

## Refactoring proposé
Aucun : ce chapitre sert de référence de lecture, pas de démonstration de refactoring.

## Résultat attendu après refactoring
Sans objet.

## Messages clés
- **SUT** (System Under Test) : l'objet dont on teste le comportement — ici `Order`.
- **Fixture** : les objets nécessaires pour construire une situation réaliste — ici `Trajet`,
  `Customer`, `Passenger`, `SeatSelection`.
- **Arrange** : mettre en place la situation.
- **Act** : une seule action qui déclenche le comportement — ici `order.evaluatePrice()`.
- **Assert** : vérifier l'outcome observable — ici le montant retourné.
- Les mocks n'ont pas leur place dans ce genre de test : aucune frontière externe n'est impliquée.
