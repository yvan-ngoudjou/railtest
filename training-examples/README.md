# Exemples pédagogiques

Ce dossier n'est **pas** compilé avec le projet Maven principal (`src/main`, `src/test`). Il contient
des extraits volontairement isolés, organisés par chapitre de formation, que le formateur peut
ouvrir et projeter directement.

Chaque chapitre suit autant que possible cette structure :

```text
chapter-XX/
    README.md          objectif, comportement étudié, mauvais/bon exemple, messages clés
    bad-example.java    un test ou un code plausible mais problématique
    good-example.java   la version qui corrige le problème
```

**Important** : les fichiers `bad-example.java` de ce dossier montrent des tests ou du code
volontairement fragiles ou incorrects, à des fins d'enseignement uniquement. Ils ne représentent
jamais une recommandation. La suite de tests de référence du projet (`src/test`) ne contient que
des bons exemples.

## Sommaire

| Chapitre | Sujet |
|---|---|
| [chapter-01](chapter-01) | Pourquoi tester ? |
| [chapter-02](chapter-02) | Qu'est-ce qu'un test ? |
| [chapter-03](chapter-03) | La pyramide des tests |
| [chapter-04](chapter-04) | Les grandes écoles du testing (Classical vs London) |
| [chapter-05](chapter-05) | Anatomie d'un test |
| [chapter-06](chapter-06) | Nommer correctement les tests |
| [chapter-07](chapter-07) | Identifier les comportements à tester |
| [chapter-08](chapter-08) | Mock vs Stub |
| [chapter-09](chapter-09) | Quand utiliser les mocks ? |
| [chapter-10](chapter-10) | Reconnaître un bon test |
| [chapter-11](chapter-11) | Pourquoi les tests deviennent fragiles |
| [chapter-12](chapter-12) | Tests et conception logicielle |
| [chapter-13](chapter-13) | Domain Model vs Application Service |
| [chapter-14](chapter-14) | Architecture hexagonale et tests |
| [chapter-15](chapter-15) | TDD après avoir appris à écrire de bons tests |
