# Chapitre 04 — Les grandes écoles du testing (Classical vs London)

## Objectif pédagogique
Montrer deux façons raisonnables de tester le même comportement, sans jamais dire que l'une est
"la bonne" et l'autre "la mauvaise".

## Contexte fonctionnel
`PurchaseTicketApplicationService.purchase(...)` pour un achat simple : un adulte, siège
automatique, paiement réussi.

## Comportement étudié
> Un achat réussi réserve un siège, émet un billet et envoie une confirmation.

## Version London
[`london-style-test.java`](london-style-test.java) — chaque collaborateur immédiat du service
(`TrajetRepository`, `TrajetSeatRepository`, `PaymentGateway`, `TicketRepository`, `EmailSender`)
est mocké. Le test vérifie les interactions : quelles méthodes ont été appelées, avec quels
arguments, combien de fois.

## Version Classical
[`classical-style-test.java`](classical-style-test.java) — c'est en réalité la structure des tests
réels du projet (`PurchaseTicketApplicationServiceTest`) : de vrais objets in-memory pour les
repositories, un seul mock (`EmailSender`) là où l'interaction est elle-même le comportement
observable.

## Pourquoi il est fragile / incorrect
La version London n'est pas "mauvaise", mais elle a un coût :
- elle connaît le détail de *comment* le service orchestre ses collaborateurs, pas seulement
  *ce qu'* il produit ;
- si l'algorithme d'attribution de siège change (voir le refactoring du commit `08`, qui remplace
  un `Iterator` pré-calculé par une boucle incrémentale), les vérifications sur le nombre exact
  d'appels à `findAvailableSeats` peuvent casser sans que le comportement change pour le Customer.

## Pourquoi il est meilleur
La version Classical connaît moins de détails : elle survit au commit `08` sans modification.
Mais elle a aussi un coût : en cas d'échec, elle localise moins précisément quel collaborateur est
en cause (elle dit "le prix est faux", pas "TrajetSeatRepository.findAvailableSeats a mal répondu").

## Refactoring proposé
Rejouer le refactoring du commit `08` (algorithme d'attribution de sièges) sur les deux versions.

## Résultat attendu après refactoring
- La version Classical reste verte.
- La version London, si elle vérifie l'ordre ou le nombre d'appels à `findAvailableSeats`, peut
  devenir rouge pour un comportement observable inchangé (faux positif).

## Messages clés
- Classical et London répondent à des questions différentes : "que produit le système ?" contre
  "comment le système collabore-t-il ?".
- Isolation, localisation de l'erreur, connaissance de l'implémentation et résistance au
  refactoring évoluent différemment selon le style choisi : c'est un compromis, pas un verdict.
- Ni "London = fragile" ni "Classical = bon" ne sont des règles absolues.
