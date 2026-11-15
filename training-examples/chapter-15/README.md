# Chapitre 15 — TDD après avoir appris à écrire de bons tests

## Objectif pédagogique
Illustrer RED / GREEN / REFACTOR sur une petite règle métier, et rappeler qu'écrire le test avant
le code ne garantit ni sa pertinence ni sa résistance au refactoring : seuls les principes des
chapitres précédents (comportement observable, nommage, niveau d'abstraction) le garantissent.

**Important** : la règle utilisée ici ("réduction senior") est un kata isolé, à but pédagogique
uniquement. Elle n'est **pas** intégrée à `src/main` : les règles métier du projet (voir le
README principal) ne prévoient pas de réduction senior, et l'ajouter au domaine réel enrichirait le système au-delà de sa
spécification. Le code de ce chapitre reste donc entièrement dans `training-examples/`.

## Contexte fonctionnel
Nouvelle règle fictive, pour l'exercice : *"Un passager de 65 ans ou plus bénéficie de la même
réduction de 10 % qu'un client premium sur le prix du transport (mais pas sur le supplément de
siège)."*

## Comportement étudié
`Order.evaluatePrice()` pour un passager senior, client non premium, siège automatique.

## RED
[`step-1-red.java`](step-1-red.java) — le test est écrit en premier, contre le code du projet tel
qu'il existe (commit `09`). Il échoue : rien dans `PricingPolicy` ne connaît l'âge du passager.

```text
expected: <90.00 EUR> but was: <100.00 EUR>
```

## GREEN
[`step-2-green.java`](step-2-green.java) — la implémentation minimale pour faire passer le test :
un test d'âge ajouté directement dans le calcul, sans se soucier encore de l'élégance.

## REFACTOR
[`step-3-refactor.java`](step-3-refactor.java) — une fois vert, le code est nettoyé : extraction
d'une notion explicite "éligible à la réduction transport" plutôt que deux conditions dupliquées
(`customer.isPremium() || passenger.isSenior()`), en gardant le test RED/GREEN vert sans le
modifier.

## Résultat attendu après refactoring
Le test écrit en RED reste vert après le REFACTOR : c'est lui qui protège la règle, pas
l'implémentation particulière choisie.

## Inside-out vs outside-in
Cet exercice est mené *inside-out* : on part de la règle de calcul (`PricingPolicy`), déjà connue
et déjà testée pour les autres cas. Une approche *outside-in* aurait démarré par un test de
`PurchaseTicketApplicationService` vérifiant le prix total payé, avant de descendre vers
`PricingPolicy` seulement quand le besoin s'en fait sentir.

## Messages clés
- RED doit être écrit en respectant les mêmes principes que n'importe quel bon test (comportement
  observable, nom explicite) — sinon TDD ne fait que garantir un mauvais test tôt plutôt que tard.
- GREEN peut être délibérément imparfait : son seul rôle est de prouver que le test peut passer.
- REFACTOR améliore le design sans jamais changer le test qui protège le comportement.
- Écrire le test avant le code ne garantit ni sa pertinence, ni sa résistance au refactoring.
