# Chapitre 03 — La pyramide des tests

## Objectif pédagogique
Situer les tests déjà présents dans le projet sur les trois niveaux classiques, sans construire
de nouvelle infrastructure : aucune base de données ni serveur HTTP n'est nécessaire pour illustrer
la pyramide.

## Contexte fonctionnel
Le projet contient déjà les trois niveaux, à des degrés différents de maturité.

## Comportement étudié
Le même système (achat de billets), observé à trois granularités différentes.

## Mauvais exemple
Croire qu'il faut un vrai framework web ou une vraie base de données pour "avoir" une pyramide de
tests complète. Ce n'est pas nécessaire pour apprendre le principe.

## Pourquoi il est fragile / incorrect
Confondre "type de test" et "outil utilisé". La pyramide parle de portée (combien de composants
réels sont impliqués), pas de technologie.

## Bon exemple
Trois extraits déjà existants dans le dépôt, à comparer :

```text
Unit          src/test/java/domain/OrderPricingTest.java
              -> un seul objet du domaine (Order + ses collaborateurs directs), aucune I/O.

Sociable unit src/test/java/application/PurchaseTicketApplicationServiceTest.java
              -> un Application Service + de vrais adapters in-memory, un seul mock (EmailSender).

End-to-end    src/main/java/demo/DemoApplication.java
              -> tout le système, du "create trajet" au "purchase ticket", exécuté d'un bloc.
              Ce n'est pas un test automatisé : c'est ce qu'un test e2e vérifierait s'il existait.
```

## Pourquoi il est meilleur
Chaque niveau a un rôle différent :
- le test unitaire donne un feedback en millisecondes sur une règle métier précise ;
- le test d'Application Service prouve que l'orchestration assemble correctement les règles ;
- un test end-to-end (non automatisé ici) prouverait que le système entier, câblé avec de vrais
  adapters, produit le bon résultat pour un utilisateur.

## Refactoring proposé
Aucun : ce chapitre est une lecture croisée du code existant, pas une nouvelle implémentation.

## Résultat attendu après refactoring
Sans objet.

## Messages clés
- Plus un test est bas dans la pyramide, plus il est rapide et précis sur l'erreur ; plus il est
  haut, plus il rassure sur le comportement réellement vécu par l'utilisateur.
- On n'a pas besoin d'infrastructure réelle pour enseigner la différence.
- Un `main` de démonstration (`DemoApplication`) n'est pas un test end-to-end : personne n'automatise
  ses assertions.
