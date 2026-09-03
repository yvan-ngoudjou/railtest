# railtest

Un système de réservation et d'achat de billets de train, écrit en Java pur, qui sert de fil rouge
à une formation sur l'écriture de tests centrés sur les comportements et résistants au
refactoring.

## Objectif fonctionnel

Une compagnie ferroviaire possède plusieurs trains. Le projet implémente deux cas d'usage :

- **Créer un trajet** : un opérateur planifie un trajet (gares, horaires, prix de base, train
  affecté) ; le système vérifie que le train n'est pas déjà engagé sur une période qui chevauche.
- **Acheter des billets** : un client achète des billets pour un ou plusieurs passagers, avec
  attribution automatique ou choix explicite du siège, paiement, puis émission des billets et
  confirmation par email en cas de succès.

## Objectif pédagogique

Ce dépôt n'est pas qu'une application fonctionnelle : sa structure, ses tests et ses exemples sont
pensés pour illustrer, avec du code réel plutôt que des extraits artificiels :

- la différence entre tester ce que fait le système et tester comment il le fait ;
- pourquoi tester une méthode n'est pas nécessairement tester un comportement ;
- quand un double de test joue le rôle de Stub, et quand il joue le rôle de Mock ;
- pourquoi certains tests deviennent fragiles, et comment les reconnaître ;
- comment le Domain Model et l'Application Service se testent à des niveaux complémentaires.

L'ensemble des chapitres couverts, avec leurs exemples, est détaillé dans
[`training-examples/`](training-examples/README.md).

## Règles métier principales

- Un même train ne peut pas être affecté à deux trajets dont les périodes se chevauchent.
- Chaque siège d'un train est rendu disponible pour un trajet dès sa création (`TrajetSeat`).
- Un siège déjà réservé pour un trajet ne peut pas être réservé une seconde fois.
- Un client premium bénéficie de 10 % de réduction sur le prix du transport (pas sur le supplément
  de siège).
- Un passager de moins de 10 ans voyage gratuitement (transport et supplément de siège inclus).
- Un siège choisi explicitement coûte 5 € de supplément ; un siège attribué automatiquement ne
  coûte rien.
- Les billets ne sont créés qu'après succès du paiement ; en cas d'échec, aucun billet n'est créé,
  aucun email n'est envoyé, et les sièges réservés sont libérés.

## Architecture

Architecture simple inspirée des ports et adapters, sans framework applicatif :

```text
src/main/java/
    domain/       entités et règles métier (Java pur)
    application/  Application Services : orchestration des use cases
    ports/        interfaces (repositories, PaymentGateway, EmailSender) — aucune implémentation réelle
    demo/         implémentations in-memory minimales + DemoApplication

src/test/java/
    domain/       tests comportementaux du Domain Model
    application/  tests comportementaux des Application Services
```

Les repositories, le système de paiement et l'envoi d'email restent des ports : aucun adapter réel
(base de données, serveur HTTP, prestataire de paiement) n'est implémenté. Les implémentations
in-memory de `demo/` existent uniquement pour exécuter `DemoApplication` et servent aussi de
collaborateurs réels (non mockés) dans les tests d'Application Service.

## Absence volontaire de BDD / framework

Aucun framework de BDD (Cucumber, etc.), aucun framework applicatif (Spring, Spring Boot,
Hibernate, JPA), aucune base de données réelle et aucun serveur HTTP ne sont utilisés. Les seules
dépendances sont JUnit 5 et Mockito — ce dernier uniquement là où son usage est justifié (voir
[`training-examples/chapter-08`](training-examples/chapter-08/README.md) et
[`chapter-09`](training-examples/chapter-09/README.md)).

## Exécuter les tests

```bash
mvn test
```

## Exécuter la démonstration

```bash
mvn compile exec:java -Dexec.mainClass=demo.DemoApplication
```

Rejoue un scénario complet : création d'un trajet Paris → Lyon, achat de deux billets par un
client premium (un adulte avec siège choisi, un enfant de 8 ans avec siège automatique), pour un
total attendu de 95 €. `DemoApplication` sert à démontrer le système ; ce n'est pas un remplacement
de la suite de tests automatisés.

## Organisation des exemples pédagogiques

Le dossier [`training-examples/`](training-examples/README.md) contient, par chapitre de
formation, un `README.md` court et des extraits `bad-example.java` / `good-example.java`. Il n'est
**pas** compilé avec le projet Maven principal. Les mauvais exemples y sont volontairement
conservés et documentés comme tels ; ils ne représentent jamais une recommandation. La suite de
tests de `src/test` ne contient que des tests de référence propres.
