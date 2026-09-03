# Chapitre 10 — Reconnaître un bon test

## Objectif pédagogique
Analyser des tests réels du projet selon quatre axes, avec leurs compromis, sans jamais conclure
qu'un test est "bon" ou "mauvais" dans l'absolu.

## Contexte fonctionnel
Trois tests réels du dépôt, de nature différente.

## Comportement étudié
Sans objet unique : ce chapitre est une grille de lecture.

## Mauvais exemple
Sans objet ici (voir chapter-11 pour des exemples fragiles concrets).

## Pourquoi il est fragile / incorrect
Sans objet.

## Bon exemple
Trois tests réels, analysés ci-dessous.

### `domain.OrderPricingTest#premium_customer_gets_10_percent_discount_on_transport`
```text
Protection contre les régressions : forte — toute erreur dans le calcul du taux la détecte.
Résistance au refactoring : forte — a survécu telle quelle aux commits 07, 08, 09.
Feedback rapide : excellent — aucune I/O, quelques millisecondes.
Maintenabilité : élevée — fixture courte, un seul Act, un nom explicite.
Compromis : ne dit rien sur l'orchestration globale d'un achat (ce n'est pas son rôle).
```

### `application.PurchaseTicketApplicationServiceTest#successful_purchase_sends_a_purchase_confirmation`
```text
Protection contre les régressions : forte sur l'effet "confirmation envoyée".
Résistance au refactoring : bonne — reste vert malgré les commits 07, 08 et 09, car il n'observe
    que l'invocation d'EmailSender, jamais son mécanisme d'orchestration interne.
Feedback rapide : bon — pas d'I/O réelle, seul Mockito ajoute un léger surcoût.
Maintenabilité : correcte — nécessite de connaître Mockito, un peu plus de setup qu'un test domaine.
Compromis : le mock d'EmailSender est nécessaire ici, car aucune autre façon d'observer l'envoi
    n'existe sans vraie infrastructure.
```

### `application.CreateTrajetApplicationServiceTest#a_train_cannot_be_assigned_to_two_trajets_with_overlapping_periods`
```text
Protection contre les régressions : forte sur l'invariant métier central du use case.
Résistance au refactoring : forte — utilise de vrais in-memory repositories, indifférent à leur
    implémentation interne.
Feedback rapide : excellent.
Maintenabilité : bonne, bien que la fixture (Train, Seat, dates) soit un peu plus longue.
Compromis : ne teste pas directement TrainMovement.overlaps() isolément (déjà couvert par
    TrainMovementTest) ; il vérifie l'orchestration complète, un niveau au-dessus.
```

## Refactoring proposé
Aucun ici : ce chapitre relit du code déjà stable.

## Résultat attendu après refactoring
Sans objet.

## Messages clés
- Les quatre axes (régression, refactoring, feedback, maintenabilité) s'évaluent ensemble, pas
  isolément.
- Un bon test a presque toujours un compromis explicite, pas une perfection sur les quatre axes.
