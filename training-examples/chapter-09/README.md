# Chapitre 09 — Quand utiliser les mocks ?

## Objectif pédagogique
Distinguer communication intra-système et communication inter-système, et introduire
`managed dependency` / `unmanaged dependency` : ni "interface => mock", ni
"repository => toujours mock et verify" ne sont des règles valables.

## Contexte fonctionnel
`Order` collabore avec `PricingPolicy` (interne au domaine). `PurchaseTicketApplicationService`
collabore avec `PaymentGateway` et `EmailSender` (systèmes externes).

## Comportement étudié
> Un achat réussi débite le client et envoie une confirmation.

## Mauvais exemple
[`bad-example.java`](bad-example.java) — vérifie l'appel `Order` → `PricingPolicy`, une
communication purement interne, invisible depuis l'extérieur du système.

## Pourquoi il est fragile / incorrect
`PricingPolicy` est un détail d'organisation interne à `Order` (introduit par le refactoring du
commit `07`). Vérifier son appel fige une décision de conception qui pourrait changer sans que le
prix payé par le Customer change. C'est une communication **intra-system**.

## Bon exemple
[`good-example.java`](good-example.java) — vérifie l'appel à `PaymentGateway.pay(...)`, une
communication **inter-system** : cet appel a un effet réel hors du système (débiter le client) et
constitue un effet observable du use case.

## Refactoring proposé
Fusionner `PricingPolicy` dans `Order` (retour en arrière du commit `07`).

## Résultat attendu après refactoring
Le mauvais exemple ne compile plus (`PricingPolicy` n'existe plus). Le bon exemple reste vert : il
n'a jamais dépendu de cette classe.

## Messages clés
- **Intra-system communication** : collaboration interne, généralement invisible depuis
  l'extérieur → à vérifier rarement, sauf si elle constitue elle-même une garantie utile.
- **Inter-system communication** : appel vers une dépendance externe (paiement, email) → vérifier
  l'interaction est souvent légitime, car c'est justement l'effet observable attendu.
- **Managed dependency** (le système contrôle son état, ex. un repository qu'on peut relire après
  écriture) se prête bien à des vérifications d'état plutôt que d'interaction.
- **Unmanaged dependency** (le système ne peut pas observer l'effet autrement, ex. un email
  vraiment envoyé) se prête bien à une vérification d'interaction, faute de mieux.
- "Interface ⇒ mock" et "repository ⇒ toujours mock et verify" restent de fausses règles : le
  contexte décide.
