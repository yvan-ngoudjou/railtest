# Chapitre 02 — Qu'est-ce qu'un test ?

## Objectif pédagogique
Opposer "tester une méthode" et "tester un comportement". Montrer qu'une classe n'est pas
automatiquement une unité de test, et introduire la distinction unit / integration / end-to-end
sans infrastructure réelle.

## Contexte fonctionnel
Le prix d'un billet dépend de deux étapes internes : le prix du transport (`PricingPolicy.transportPriceFor`)
et le supplément de siège (`PricingPolicy.seatFeeFor`). Ces deux méthodes existent parce qu'un
refactoring (commit `07`) les a extraites de `Order`.

## Comportement étudié
Le prix total d'une commande, pour un passager adulte ayant choisi son siège.

## Mauvais exemple
[`bad-example.java`](bad-example.java) — le test appelle directement `PricingPolicy.seatFeeFor(...)`,
comme si tester chaque méthode séparément suffisait à prouver que le comportement global est correct.

## Pourquoi il est fragile / incorrect
- Il connaît un détail d'implémentation (`PricingPolicy` existe, elle a une méthode `seatFeeFor`).
- Il ne prouve rien sur `Order.evaluatePrice()`, qui est le comportement réellement utile pour le
  Customer.
- Si `PricingPolicy` disparaît demain (fusionnée dans `Order`, comme avant le commit `07`), ce test
  casse — alors que le prix payé par le client n'a pas changé d'un centime.

## Bon exemple
[`good-example.java`](good-example.java) — extrait réel de `OrderPricingTest`.

## Pourquoi il est meilleur
Il observe `Order.evaluatePrice()`, la seule chose que le Customer perçoit réellement. Il survit à
n'importe quelle réorganisation interne du calcul.

## Refactoring proposé
Fusionner `PricingPolicy` dans `Order` (retour en arrière du commit `07`).

## Résultat attendu après refactoring
- Le bon exemple reste vert : `evaluatePrice()` retourne toujours `105 €`.
- Le mauvais exemple ne compile même plus : `PricingPolicy` n'existe plus.

## Messages clés
- Une méthode publique ou package-private n'est pas automatiquement une frontière à tester isolément.
- Un test unitaire peut tout à fait exercer plusieurs classes collaborant ensemble (ici `Order` et
  `PricingPolicy`), tant qu'il reste rapide et déterministe.
- unit test : ce chapitre (aucune I/O, en mémoire) ; integration test : par exemple vérifier qu'un
  vrai `TrajetSeatRepository` persiste correctement (hors périmètre ici, pas de base de données) ;
  end-to-end test : lancer `DemoApplication` et vérifier la sortie complète du scénario.
