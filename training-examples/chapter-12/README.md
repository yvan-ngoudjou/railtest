# Chapitre 12 — Tests et conception logicielle

## Objectif pédagogique
Utiliser un test difficile à écrire comme signal d'un problème de design, puis améliorer le design
pour de bonnes raisons — jamais uniquement pour "faciliter les mocks".

## Contexte fonctionnel
Le test de la démonstration 3 du chapitre 11
([`bad-example-3-mocks-everything.java`](../chapter-11/bad-example-3-mocks-everything.java)) est
difficile à écrire et à maintenir : cinq mocks, un ordre d'appel précis à respecter.

## Comportement étudié
> Un achat réussi assemble correctement disponibilité, tarification, réservation, paiement,
> émission des billets et confirmation.

## Mauvais exemple
Le même fichier que le chapitre 11 : une forêt de cinq mocks pour un seul test.

## Pourquoi il est fragile / incorrect
Le signal de design ici n'est pas "il y a des mocks", mais :
```text
Arrange gigantesque      : 5 mocks + configuration de leurs réponses avant de pouvoir agir.
Forêt de mocks           : TrajetRepository, TrajetSeatRepository, PaymentGateway,
                            TicketRepository, EmailSender mockés simultanément.
Application Service      : le test doit connaître l'ordre exact de ses appels internes.
```
Un test difficile à construire pointe souvent une classe qui a trop de responsabilités ou trop de
collaborateurs directs à connaître pour son appelant.

## Bon exemple
[`good-example.java`](good-example.java) — même comportement, mais construit avec les vrais
adapters in-memory (`InMemoryTrajetRepository`, `InMemoryTrajetSeatRepository`,
`InMemoryTicketRepository`) déjà fournis par le projet, et un seul mock (`EmailSender`). C'est très
exactement `PurchaseTicketApplicationServiceTest`.

## Pourquoi il est meilleur
Le nombre de collaborateurs de `PurchaseTicketApplicationService` n'a pas changé — le test n'a pas
besoin d'un mock par port pour rester correct. Utiliser de vrais objets simples là où c'est possible
réduit l'Arrange sans changer le design de production.

## Refactoring proposé
Ne pas refactorer `PurchaseTicketApplicationService` juste pour "faciliter les mocks" : il a
exactement les collaborateurs que l'orchestration demande. Le vrai
levier ici est le choix du test double, pas le découpage du service.

## Résultat attendu après refactoring
Le bon exemple reste aussi complet fonctionnellement, avec un Arrange nettement plus court et sans
connaissance de l'ordre d'appel interne.

## Messages clés
- Un test difficile à écrire est un signal à prendre au sérieux — mais le bon remède n'est pas
  toujours de changer le design de production : parfois, c'est le choix du test double qui doit
  changer.
- Ne jamais refactorer uniquement pour rendre les mocks plus faciles à poser : l'amélioration doit
  avoir un sens de conception indépendant.
