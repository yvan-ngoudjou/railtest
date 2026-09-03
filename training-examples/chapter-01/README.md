# Chapitre 01 — Pourquoi tester ?

## Objectif pédagogique
Montrer qu'un test automatisé mémorise un comportement attendu et protège contre les régressions,
avant même de parler de mocks, de style ou de nommage.

## Contexte fonctionnel
Un trajet coûte 100 €. Un `Customer` premium bénéficie de 10 % de réduction sur le transport.

## Comportement étudié
`Order.evaluatePrice()` pour un client premium seul, sans siège choisi.

## Mauvais exemple
Aucun test. Le comportement n'existe que dans la tête du développeur qui l'a écrit.

## Pourquoi il est fragile / incorrect
Sans test, rien n'empêche une modification future de casser silencieusement la règle des 10 %.
La seule façon de le savoir est de la découvrir en production.

## Bon exemple
Voir [`good-example.java`](good-example.java) — c'est en réalité le test réel du projet :
`src/test/java/domain/OrderPricingTest.java::premium_customer_gets_10_percent_discount_on_transport`.

## Pourquoi il est meilleur
Le test décrit un résultat observable (`90 €`) pour une situation métier précise. Il tourne en
quelques millisecondes et donne un verdict binaire.

## Refactoring proposé
Introduire volontairement une régression : dans `PricingPolicy.transportPriceFor`, remplacer
`minusPercentage(10)` par `minusPercentage(5)`.

```java
// régression introduite volontairement
return customer.isPremium()
        ? trajet.basePrice().minusPercentage(5) // était 10
        : trajet.basePrice();
```

## Résultat attendu après refactoring
Le test `premium_customer_gets_10_percent_discount_on_transport` devient **rouge** immédiatement :

```text
expected: <90.00 EUR> but was: <95.00 EUR>
```

Remettre `10` fait revenir le test au vert. C'est le feedback immédiat que le test fournit.

## Messages clés
- Un test n'est pas là pour "passer" : il est là pour échouer le jour où le comportement change
  sans qu'on le veuille.
- Le feedback est rapide (millisecondes) et sans ambiguïté.
- Aucune notion de mock n'est nécessaire pour ce premier bénéfice.
