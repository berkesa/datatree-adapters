# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

DataTree Adapter Pack: a set of text and binary format adapters for the [DataTree Tools API](https://berkesa.github.io/datatree/). DataTree is **not** a JSON parser — it's a top-level API layer (`datatree-core`, a runtime dependency) that lets applications read/manipulate/write hierarchical data while swapping the underlying implementation without code changes. This repo provides those swappable implementations: ~11 JSON libraries plus XML, YAML, TOML, CSV, Properties, and binary formats (Smile, CBOR, BSON, Ion, MessagePack, Kryo).

Java 21 (`maven.compiler.release=21`). Maven build, version **2.0.0**, published to Maven Central as `com.github.berkesa:datatree-adapters`. Depends on `com.github.berkesa:datatree-core:2.0.0`.

## Build & Test

```bash
mvn clean verify                       # compile + test + jar
mvn test                               # run unit tests
mvn -Dtest=TreeTestWithJackson test    # single test class
mvn clean install                      # install 2.0.0-SNAPSHOT to local ~/.m2
mvn -Prelease clean deploy             # sources + javadoc + GPG + Central Portal publish
```

Surefire **includes** the concrete `TreeTestWith*` classes (their names don't match the default `*Test` pattern, so they're listed explicitly) and **excludes** `ExtendedTreeTest` (abstract base), `TreeTestSuite` (a JUnit 5 `@Suite`), and all `Performance*` micro-benchmarks (see `pom.xml`). So `mvn test` effectively runs the concrete `TreeTestWith*` classes plus `DataFormatTest`.

## Architecture

### Adapter naming and structure

Each adapter targets **one format + one third-party library**, named `<Format><Library>` — e.g. `JsonJackson`, `JsonGson`, `XmlXStream`, `TomlToml4j`, `SmileJackson`, `MsgPackJackson`. All live in `src/main/java/io/datatree/dom/adapters/`.

Adapters extend abstract bases from `datatree-core` (`AbstractAdapter`, `AbstractTextAdapter`, `AbstractBinaryAdapter`) or local shared bases in this repo (`AbstractJacksonAdapter` → `AbstractJacksonTextAdapter` / `AbstractJacksonBinaryAdapter`, used by all Jackson-backed formats). The core contract an adapter implements is `getFormat()`, `parse(...)` (read), and `toString(...)` / `toBinary(...)` (write).

### Auto-selection via `@Priority`

Every adapter carries `@Priority(n)` (from `io.datatree.dom.Priority`). When multiple adapters for the same format are on the classpath, `datatree-core`'s registries pick the highest priority automatically. Built-in/dependency-free adapters (e.g. `XmlBuiltin` at 20) use low values; richer libraries (e.g. `JsonJackson` at 180) use high values. **When adding or editing an adapter, set its priority relative to the others for that format.**

Selection can be overridden three ways (documented in each adapter's Javadoc header):
- System properties: `-Ddatatree.json.reader=io.datatree.dom.adapters.JsonJackson` (and `.writer`)
- Programmatically: `TreeReaderRegistry.setReader("json", impl)` / `TreeWriterRegistry.setWriter("json", impl)`
- Per-call by class name: `new Tree(input, "JsonJackson")` / `node.toString("JsonJackson")`

### Optional BSON/MongoDB type support (reflection)

Adapters call `tryToAddSerializers("io.datatree.dom.adapters.<Name>BsonSerializers", ...)` in their constructor. This loads a companion `*BsonSerializers` class **by reflection** so that serialization of MongoDB/BSON types (`ObjectId`, `BsonInt64`, `Decimal128`, etc.) works when the `bson` library is present, but the adapter still loads fine when it is absent. Each adapter that supports BSON types has a matching `*BsonSerializers` (and some a `*JavaSerializers`) class. When adding such a class, register it via the reflective hook — don't add a hard import in the adapter.

### Tests

`ExtendedTreeTest` is the shared, format-agnostic test body (parse/serialize round-trips across many data types, including BSON types), now on **JUnit 5 (Jupiter)**. Each `TreeTestWith<Library>` subclass just overrides `setUp()` to set the specific reader/writer impl (the base wires it via a `@BeforeEach`) and inherits all assertions — so one test body validates every adapter identically. `Performance*` classes are micro-benchmarks, not correctness tests, and are excluded from the test run.

## Adding a new adapter

1. Create `src/main/java/io/datatree/dom/adapters/<Format><Library>.java` extending the right abstract base; implement `getFormat()`, `parse`, and `toString`/`toBinary`.
2. Annotate with `@Priority(n)` appropriate to that format.
3. Add the library to `<dependencies>` in `pom.xml` (grouped by format with a comment header, matching the existing layout; `com.fasterxml.jackson.*` modules need no `<version>` — they inherit `jackson-bom`).
4. (Optional) Add a `<Format><Library>BsonSerializers` companion and wire it via `tryToAddSerializers(...)`.
5. Add a `TreeTestWith<Library>` extending `ExtendedTreeTest` that overrides `setUp()` to set the impl, and register it in `TreeTestSuite` (and in the Surefire `<includes>` if the class name doesn't match `TreeTestWith*`).

## Modernization notes (2.0.0)

Migrated from Gradle/Java 8 to **Maven + JDK 21**, version **2.0.0**. Key cross-cutting changes:

- All `com.fasterxml.jackson.*` modules are unified through the **`jackson-bom`** import in `<dependencyManagement>`, so they always share one version (currently 2.19.x) — Jackson dependencies are declared without an explicit `<version>`.
- Apache **Johnzon** → 2.x; its JSON-P API moved from `javax.json` to **`jakarta.json`** (`jakarta.json-api` 2.1.x). `JsonJohnzon` imports `jakarta.json.*`.
- Amazon **Ion** coordinate `software.amazon.ion:ion-java` → **`com.amazon.ion:ion-java`** (the `IonIon`/`JsonIon` imports moved to `com.amazon.ion.*`).
- **XStream** → `com.thoughtworks.xstream:xstream` 1.4.x; its type-filtering security is enabled by default, so `XmlXStream` calls `addPermission(AnyTypePermission.ANY)` (it (de)serializes the application's own trusted data).
- **SnakeYAML** → 2.x (the no-arg `Representer` constructor was removed — `ExtensibleRepresenter` now passes a `DumperOptions`).
- **bson** → 5.x; `IterableCodec` is no longer public and `DocumentCodec` no longer encodes nested maps/lists implicitly, so `JsonBson` registers `IterableCodecProvider` + `MapCodecProvider` and obtains the iterable codec via the registry.
- **json-io** → 4.56.x; package moved to `com.cedarsoftware.io`, and `JsonJsonIO` uses the `JsonIo` facade with `ReadOptionsBuilder`/`WriteOptionsBuilder` (read in `returnAsJsonObjects` mode → `Map`/`Object[]` roots only).

**Adapters removed in 2.0.0** (dead-upstream / CVE-laden libraries — an intentional API trim justified by the major release; each removed public class is listed for release notes):

| Removed adapter(s) | Dropped library |
|---|---|
| `JsonBoon` | `io.fastjson:boon` |
| `JsonFast` | `com.alibaba:fastjson` (v1) |
| `JsonSojo`, `XmlRpcSojo` | `net.sf.sojo:sojo` |
| `JsonFlex` | `net.sf.flexjson:flexjson` |
| `JsonJsoniter` | `com.jsoniter:jsoniter` |
| `JsonUtil` | `org.kopitubruk.util:JSONUtil` |
| `JsonSimple` | `com.googlecode.json-simple:json-simple` |
| `TomlJtoml` | `me.grison:jtoml` (kept the maintained `io.ous:jtoml` as `TomlJtoml2`) |
| `MsgPackOrg` | `org.msgpack:msgpack` 0.6 (MessagePack is still supported via `MsgPackJackson` on `jackson-dataformat-msgpack` 0.9) |

Their companion `*BsonSerializers` / `*JavaSerializers` classes and the matching `TreeTestWith*` / `Performance*` tests were removed as well.
