# MadaPay — ETU001979 — RAFIDINJANAHARY Emmanuel
## Stack
Stack — Java 25 (Eclipse Temurin), Spring Boot 4.2.0, Gradle 9.9.2 et stockage en mémoire.
## Démarrage

```sh
docker build -t madapay_emmanuel_rafidinjanahary:1979 .
docker run --rm --name madapay_etu_1979 -p 127.0.0.1:8080:8080 madapay_emmanuel_rafidinjanahary:1979
```

### Clé de signature JWT (`JWT_SECRET`)

MadaPay utilise `JWT_SECRET` pour signer et vérifier les jetons JWT (HS256), valables une heure. Par défaut, le conteneur génère une clé aléatoire à chaque démarrage. Les jetons émis avant un redémarrage deviennent alors invalides.

Pour conserver la même clé entre les démarrages, définissez `JWT_SECRET` dans votre environnement, puis ajoutez `-e JWT_SECRET` à la commande `docker run`, avant le nom de l’image. Choisissez une valeur aléatoire d’au moins 32 octets en UTF-8 et gardez-la hors du dépôt Git.
