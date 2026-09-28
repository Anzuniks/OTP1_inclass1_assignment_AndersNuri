# Temperature Converter (JavaFX + MariaDB)

JavaFX-lämpötilamuunnin, joka tallentaa jokaisen muunnoksen tietokantaan.

## Tietokanta (2 toisiinsa liittyvää taulua)

```
temperature_unit (id PK, code, name)          -- C, F, K
temp_record      (id PK, input_value, from_unit_id FK, result_value, to_unit_id FK, created_at)
```

Sovellus luo taulut ja yksiköt automaattisesti käynnistyessään (`DBConnection.initSchema()`).
Yhteysasetukset tulevat ympäristömuuttujista `DB_URL`, `DB_USER`, `DB_PASSWORD`.

## Testit ja kattavuus

```bash
mvn clean test
# JaCoCo-raportti: target/site/jacoco/index.html
```

Testit käyttävät H2-muistitietokantaa, joten ne toimivat ilman MariaDB:tä (myös Jenkinsissä).

## Ajo paikallisesti

```bash
docker compose up -d db     # pelkkä tietokanta
mvn javafx:run
```

## Ajo Docker-imagena

### Linux (esim. Kali) – oma X-palvelin
```bash
xhost +local:docker
docker compose up --build
```

Docker Hubista haettu image, tietokanta compose:lla:
```bash
docker compose up -d db
docker run --rm --network host \
  -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix \
  -e DB_URL=jdbc:mariadb://localhost:3306/tempdb \
  anzuniks/temperature-converter:latest
```

### Windows – XMing
1. Käynnistä XLaunch: *Multiple windows* → *Start no client* → rastita **No Access Control**.
2. Aja:
```powershell
docker compose up -d db
docker run --rm -e DISPLAY=host.docker.internal:0.0 `
  -e DB_URL=jdbc:mariadb://host.docker.internal:3306/tempdb `
  anzuniks/temperature-converter:latest
```

## Jenkins

Pipeline: Checkout → `mvn clean verify` (testit + JUnit- ja JaCoCo-raportit) → Docker build → push Docker Hubiin.
Tarvitsee Docker Hub -tunnuksen Jenkinsiin id:llä `Docker_Hub` (Username with password, salasanaksi Docker Hubin access token).
