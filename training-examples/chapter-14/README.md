# Chapitre 14 — Architecture hexagonale et tests

## Objectif pédagogique
Montrer que "port" et "mock automatique" sont deux notions différentes : une frontière
architecturale n'est pas nécessairement une frontière observable.

## Contexte fonctionnel
Le projet définit cinq ports : `TrajetRepository`, `TrajetSeatRepository`, `TrainRepository`,
`TrainMovementRepository`, `TicketRepository`, `PaymentGateway`, `EmailSender`.

## Comportement étudié
> Un achat réussi.

## Mauvais exemple
Mocker systématiquement chaque port rencontré, au seul motif que c'est une interface (voir
[`chapter-11/bad-example-3-mocks-everything.java`](../chapter-11/bad-example-3-mocks-everything.java)).

## Pourquoi il est fragile / incorrect
`TrajetSeatRepository` est une frontière **architecturale** : elle sépare le domaine de la
persistance. Ce n'est pas pour autant une frontière **observable** : le Customer ne voit jamais
"un appel à `saveAll`", il voit "son siège est réservé". Le vrai in-memory
(`InMemoryTrajetSeatRepository`) suffit à observer ce résultat sans mock.

## Bon exemple
`PurchaseTicketApplicationServiceTest` (extrait réel) : les repositories restent de vrais objets
in-memory ; seul `EmailSender` est mocké, car l'envoi d'un email est directement observable **à
l'extérieur du système** — c'est une frontière à la fois architecturale et observable.

```text
                  frontière architecturale   frontière observable
TrajetRepository          oui                       non
TrajetSeatRepository      oui                       non
PaymentGateway            oui                       oui (le débit est un effet externe réel)
EmailSender               oui                       oui (l'email reçu est un effet externe réel)
```

## Pourquoi il est meilleur
Le choix du double de test suit la nature de la frontière, pas sa simple existence en tant
qu'interface.

## Refactoring proposé
Aucun : ce chapitre relit une distinction déjà appliquée dans les tests réels du projet.

## Résultat attendu après refactoring
Sans objet.

## Messages clés
- Port ≠ mock automatique.
- Une frontière architecturale (permet de remplacer l'implémentation) et une frontière observable
  (visible depuis l'extérieur du système) sont deux notions indépendantes.
- `PaymentGateway` et `EmailSender` cumulent les deux ; les repositories, dans ce projet, n'ont que
  la première.
