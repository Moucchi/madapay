# Jeu de données initial de MadaPay

Les trois stacks chargent **le même jeu de données** au démarrage (fichier `seed.json`).
Les tests de conformité s'appuient dessus : ne modifiez ni les numéros, ni les codes PIN, ni les soldes.

| Clé | Titulaire | Téléphone | Code PIN | Rôle | Solde (Ar) | Usage dans les tests |
|---|---|---|---|---|---:|---|
| rasoa | Rasoa Ravelo | 0321111111 | 1111 | CLIENT | 500 000 | Bénéficiaires, transferts courants |
| andry | Andry Rakoto | 0332222222 | 2222 | CLIENT | 300 000 | Contrôle des droits (compte d'un autre client) |
| hery | Hery Rabe | 0343333333 | 3333 | CLIENT | 10 000 000 | Idempotence, concurrence, historique |
| miora | Miora Ranaivo | 0346666666 | 6666 | CLIENT | 5 000 | Solde insuffisant |
| plafond | Compte plafond test | 0347777777 | 7777 | CLIENT | 10 000 000 | Plafond journalier (compte dédié) |
| agent | Agent Analakely | 0384444444 | 4444 | AGENT | 0 | Droits de l'agent |
| admin | Administrateur | 0385555555 | 5555 | ADMIN | 0 | Droits de l'administrateur |

Chaque personne possède **un seul compte**. Les identifiants (UUID) sont générés au chargement :
ne les écrivez pas en dur, retrouvez-les avec `GET /comptes?telephone=...`.

Le plafond journalier est de **2 000 000 Ar** pour tous les comptes.

## Repartir d'une base neuve

Certaines règles consomment des ressources durables (plafond journalier, historique, soldes).
Avant une exécution complète de la suite de conformité, redémarrez votre API : elle recharge `seed.json` en mémoire au démarrage.
