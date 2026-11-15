# Chapitre 13 — Domain Model vs Application Service

## Objectif pédagogique
Montrer que les tests du Domain Model et ceux de l'Application Service sont complémentaires, pas
redondants.

## Contexte fonctionnel
Le use case complet "acheter des billets" repose sur des règles locales (Domain Model) assemblées
par une orchestration (Application Service).

## Comportement étudié
> Un achat réussi réserve des sièges, émet des billets et envoie une confirmation, au bon prix.

## Mauvais exemple
Croire qu'il suffit de tester `PurchaseTicketApplicationService` pour couvrir toutes les règles de
prix et de réservation, ou à l'inverse croire que tester `Order` et `TrajetSeat` isolément suffit à
prouver que l'orchestration entière fonctionne.

## Pourquoi il est fragile / incorrect
Aucun des deux niveaux seul ne donne le même feedback :
- les tests de `Order`/`TrajetSeat`/`TrainMovement` ne disent rien sur l'ordre des étapes du use
  case, ni sur ce qui se passe en cas d'échec de paiement ;
- un test de `PurchaseTicketApplicationService` ne devrait pas re-décliner tous les cas de
  tarification (premium, enfant, siège choisi) : ce serait dupliquer `OrderPricingTest`.

## Bon exemple
```text
Domain Model (sous-objectifs métier, décisions locales) :
  domain.OrderPricingTest              -> premium -10%, enfant gratuit, siège +5€
  domain.TrajetSeatTest                -> reserve()/release(), invariant de double réservation
  domain.TrainMovementTest             -> overlaps(), anyOverlaps()

Application Service (objectif global, orchestration significative) :
  application.PurchaseTicketApplicationServiceTest
      -> paiement réussi => sièges réservés + tickets créés + confirmation envoyée
      -> paiement échoué => sièges libérés, aucun ticket, aucune confirmation
      -> pas assez de sièges => achat refusé
  application.CreateTrajetApplicationServiceTest
      -> chevauchement détecté => trajet refusé
      -> pas de chevauchement => sièges initialisés pour le nouveau trajet
```

## Pourquoi il est meilleur
Chaque niveau protège une chose différente : le Domain Model protège les règles elles-mêmes ;
l'Application Service protège que ces règles sont assemblées dans le bon ordre, avec la bonne
gestion des cas d'échec.

## Refactoring proposé
Aucun : ce chapitre est une lecture croisée des tests déjà écrits (`src/test/java/domain` et
`src/test/java/application`).

## Résultat attendu après refactoring
Sans objet.

## Messages clés
- Le Domain Model vérifie les sous-objectifs métier locaux.
- L'Application Service vérifie l'objectif global et l'orchestration significative.
- Les deux niveaux ne sont pas redondants tant qu'aucun des deux ne réplique le détail de l'autre.
