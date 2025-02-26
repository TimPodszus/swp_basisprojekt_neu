# SWP Basissystem

Der folgende Code sollte als Basis für das Software Projekt verwendet werden.

## Maven

Das Projekt kann mit Maven gebaut werden.

Dazu auf der Hauptebene zunächst

```bash
# Windows
mvnw.cmd clean install
```

```bash
# Linux
./mvnw clean install
```

aufrufen.

Um den Client zu bauen:

```bash
# Windows
cd client
mvnw.cmd clean package
```

```bash
# Linux
cd client
./mvnw clean package
```

der Client ist dann unter `target` zu finden und kann dann z.B. wie folgt aufgerufen werden:

```bash
cd target
java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Analoges gilt für den Server.

Weitere Hinweise finden sich im Wiki https://confluence.swl.informatik.uni-oldenburg.de/display/SWP/Vorlesungsvideos

## Troubleshooting

Wenn IntelliJ Probleme mit dem Finden von Klassen hat, kann es helfen, die Ordner
`client/target/generated-sources/openapi/src/main/java` und `server/target/generated-sources/openapi/src/main/java` als
Generated Sources Root zu markieren.
Dazu mit Rechtsklick auf den Ordner → Mark Directory as → Generated Sources Root.
