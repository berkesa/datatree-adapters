# TODO — Modernize `datatree-adapters` to 2.0.0

> **You are the per-project Claude Code instance for `datatree-adapters`.** Self-contained file.
> Goal: Gradle/Java 8 → **Maven + JDK 21**, upgrade/drop the ~30 format libraries, fix breaking
> changes, tests green on **JUnit 5**, legacy files removed, version **2.0.0**. This project provides
> the swappable JSON/XML/YAML/TOML/CSV/binary adapters for `datatree-core`. Adapters are named
> `<Format><Library>` and live in `src/main/java/io/datatree/dom/adapters/`.

## Coordinates & facts
- Maven: `com.github.berkesa:datatree-adapters`, `jar`, license **Apache-2.0**.
- `name`: *DataTree Adapters* · `inceptionYear`: 2017
- `url`: https://berkesa.github.io/datatree-adapters/ · `scm`: https://github.com/berkesa/datatree-adapters.git
- developer: `berkesa` / Andras Berkes / andras.berkes@programmer.net
- **Version → `2.0.0`**.

## Inter-project dependency (PIN to 2.0.0)
- `com.github.berkesa:datatree-core:2.0.0` (was 1.1.3 impl / 1.1.2 POM — now unified). Build
  `datatree` first.

## KEEP & upgrade (confirm newest at execution; unify all Jackson via `jackson-bom`)
| Library | Current | Target | Note |
|---|---|---|---|
| `org.slf4j:slf4j-api` | 1.7.30 | **2.0.18** | |
| Jackson core/databind + dataformat yaml/xml/properties/smile/cbor | mixed 2.12.3 / 2.16.0 | **one 2.x via `jackson-bom`** (e.g. 2.19.x) | ⚠ today they're inconsistent — fix |
| `com.google.code.gson:gson` | 2.10.1 | **2.11+** | |
| `org.mongodb:bson` | 4.11.1 | **5.x** | |
| `com.cedarsoftware:json-io` | 4.19.1 | **latest 4.x** | ⚠ package may be `com.cedarsoftware.io` now |
| `net.minidev:json-smart` | 2.5.0 | **2.5.x** | |
| `com.grack:nanojson` | 1.7 | **latest** | |
| `com.dslplatform:dsl-json` | 1.9.8 | **latest** | |
| `org.yaml:snakeyaml` | 1.28 | **2.x** | ⚠ SafeConstructor is default in 2.x — adjust loader if needed |
| `com.opencsv:opencsv` | 5.4 | **5.9/5.10** | |
| `xstream` → `com.thoughtworks.xstream:xstream` | 1.2.2 | **1.4.21** | ⚠ groupId change + huge jump (CVEs between) |
| `com.moandjiezana.toml:toml4j` | 0.7.2 | keep (or flag if unmaintained) | |
| `io.ous:jtoml` | 2.0.0 | **keep/upgrade** | (the maintained jtoml) |
| `de.undercouch:bson4jackson` | 2.12.0 | **2.15.x** | |
| `ion-java` → `com.amazon.ion:ion-java` | software.amazon 1.5.1 | **com.amazon 1.11.x** | ⚠ groupId change |
| `com.esotericsoftware:kryo` | 5.1.1 | **5.6.x** | |
| `org.jodd:jodd-json` | 6.0.3 | **latest** (flag if dead) | |
| Johnzon: `javax.json:javax.json-api` + `org.apache.johnzon:johnzon-mapper` | 1.1.4 / 1.2.12 | **`jakarta.json:jakarta.json-api:2.x` + `johnzon-mapper:2.x`** | ⚠ jakarta |
| `com.owlike:genson` | 1.6 | keep (flag: semi-dead) | |
| msgpack | `org.msgpack:msgpack:0.6.12` + `jackson-dataformat-msgpack:0.8.24` | **drop old `msgpack`; use `org.msgpack:msgpack-core:0.9.x` + `jackson-dataformat-msgpack:0.9.x`** | |

## DROP (dead upstream / CVE) — remove dep **and** the adapter code
For each, delete: the `Json<Library>`/`<Format><Library>` adapter class(es), any companion
`*BsonSerializers`/`*JavaSerializers`, the matching `TreeTestWith<Library>` test, and its entry in
`TreeTestSuite`. (Search the source for the class names — don't guess.) These are intentional API
removals justified by the 2.0 major; list each removed public class in the release notes.

| Library to drop | groupId:artifactId |
|---|---|
| Boon | `io.fastjson:boon:0.34` |
| FastJSON v1 (Alibaba) | `com.alibaba:fastjson:2.0.43` *(optionally replace with `com.alibaba.fastjson2:fastjson2`)* |
| SOJO | `net.sf.sojo:sojo:1.0.13` |
| FlexJSON | `net.sf.flexjson:flexjson:3.3` |
| Jsoniter | `com.jsoniter:jsoniter:0.9.23` |
| JSONUtil | `org.kopitubruk.util:JSONUtil:1.10.4` |
| json-simple | `com.googlecode.json-simple:json-simple:1.1.1` |
| jtoml (grison) | `me.grison:jtoml:1.0.0` *(keep `io.ous:jtoml`)* |
| old msgpack | `org.msgpack:msgpack:0.6.12` |

## Steps
1. **`pom.xml`** with metadata + `release=21`. Add `<dependencyManagement>` importing `jackson-bom`
   so every `com.fasterxml.jackson.*` module shares one version. `datatree-core:2.0.0-SNAPSHOT`
   compile dep; `slf4j-api` 2.0.18; `junit-jupiter` test. Build plugins: compiler 3.14.0, surefire
   3.5.3 (replicate test excludes — see step 4), (release profile) sources/javadoc/gpg +
   central-publishing 0.9.0.
2. **Remove ECJ** → javac. Expect new javac errors (raw types/unchecked) the ECJ `-nowarn` build hid.
3. **Apply KEEP/upgrade + DROP** tables above. For jakarta Johnzon, the JSON-P API moves
   `javax.json` → `jakarta.json` — update the Johnzon adapter imports. For snakeyaml 2.x, verify
   the loader/constructor still parses the test data (SafeConstructor default change). For xstream
   and ion-java, fix the changed groupId/imports.
4. **Tests → JUnit 5**, preserving the design: `ExtendedTreeTest` is the shared abstract body; each
   `TreeTestWith<Library>` sets the reader/writer impl in `setUp()` (`@BeforeEach`) and inherits the
   assertions. Replicate the old excludes in surefire: `**/ExtendedTreeTest*`, `**/TreeTestSuite*`,
   `**/Performance*`. Remove tests for dropped libraries. `mvn test` green offline.
5. **Adapter priorities:** keep each adapter's `@Priority(n)` relative ordering intact when editing.
6. **Cleanup — delete:** `build.gradle`, `settings.gradle`, `gradlew`, `gradlew.bat`, `gradle/`,
   `.gradle/`, `.travis.yml`, `.codacy.yaml`, `.classpath`, `.project`, `.settings/`.
7. **VSCode + .gitignore** (library — no `launch.json`).
8. **Build & install:** `mvn clean install`, then `mvn clean verify`.
9. **Update `CLAUDE.md`**: Maven commands; list which adapters were removed; note the jackson-bom
   unification and the jakarta Johnzon move.

## Definition of done
- `mvn clean verify` green on JDK 21; all Jackson modules share one version via BOM.
- Dropped libraries (+ their adapters/tests/suite entries) gone; kept libraries on current releases.
- jakarta Johnzon, snakeyaml 2.x, xstream 1.4.21, com.amazon ion-java building.
- JUnit 5; legacy files gone; VSCode + .gitignore; version `2.0.0`; depends only on
  `datatree-core:2.0.0`; publishing configured.
