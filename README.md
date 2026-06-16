# DataTree Adapter Pack

Text and binary format adapters for the [DataTree API](https://berkesa.github.io/datatree/).

DataTree is an extensible Java library for reading, manipulating, and writing hierarchical data
structures in many formats. It is **not** yet another JSON parser — it is a top-level API layer
that drives existing implementations through a single `Tree` document type. JSON is the default
format, but DataTree also supports XML, YAML, TOML, and many more, and lets you replace the
underlying implementation (with a smaller, smarter, or faster one) **without changing any of
your code**. On top of that, the `Tree` API gives you a complete toolset to manipulate documents
(put, get, remove, insert, sort, find, stream, …).

This **adapter pack** sits on top of [`datatree-core`](https://github.com/berkesa/datatree) and
adds the ~30 format/implementation adapters listed below. Each one registers itself automatically
as soon as it is on the classpath.

![architecture](https://raw.githubusercontent.com/berkesa/datatree/master/docs/architecture.png)

## Documentation

[![Documentation](https://raw.githubusercontent.com/berkesa/datatree/master/docs/docs-button.png)](https://berkesa.github.io/datatree/introduction.html)

## Download

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.0.0</version>
</dependency>
```

This pulls in `datatree-core` plus the third-party libraries behind every adapter (Jackson,
Gson, SnakeYAML, XStream, …) as transitive dependencies, so all the formats below work out of
the box. If you only need a few formats, exclude the libraries you don't use to keep your
dependency tree small.

## Supported formats and implementations

DataTree keeps the **format** (how the data looks: JSON, XML, YAML, ...) separate from the
**implementation** (the third-party library that actually reads and writes it). Each adapter
is a small class named `<Format><Library>` — for example `JsonJackson` is the JSON format
backed by Jackson, and `XmlXStream` is the XML format backed by XStream. Because the format
and the implementation are decoupled, you can switch the underlying library without changing
a line of your application code.

All adapters listed below ship in the pack and are registered automatically. When more than
one implementation is available for the same format, DataTree uses the one with the highest
built-in priority (marked _default_ in the tables); you can override that choice at any time
(see [Choosing an implementation](#choosing-an-implementation)).

### Reading and writing

**Text** formats are parsed from and serialized to `String`:

```java
// Parse — JSON is the default format
Tree node = new Tree(jsonString);

// Parse an explicit format
Tree config = new Tree(yamlString, "yaml");

// Serialize
String json = node.toString();        // default (JSON)
String yaml = node.toString("yaml");   // a specific format
```

**Binary** formats are read from and written to `byte[]`:

```java
Tree node   = new Tree(inputBytes, "cbor");
byte[] bytes = node.toBytes("cbor");
```

### Text formats

#### JSON — `"json"`

| Adapter | Implementation |
|---|---|
| `JsonJackson` _(default)_ | FasterXML Jackson |
| `JsonJodd` | Jodd Json |
| `JsonSmart` | JSON-Smart |
| `JsonGenson` | Genson |
| `JsonGson` | Google Gson |
| `JsonDSL` | DSL-JSON |
| `JsonJohnzon` | Apache Johnzon |
| `JsonIon` | Amazon Ion |
| `JsonBson` | MongoDB BSON library |
| `JsonNano` | nanojson |
| `JsonJsonIO` | json-io |

#### XML — `"xml"`

| Adapter | Implementation |
|---|---|
| `XmlXStream` _(default)_ | XStream |
| `XmlBuiltin` | built-in, no extra dependency |
| `XmlJackson` | Jackson XML |

#### YAML — `"yaml"`

| Adapter | Implementation |
|---|---|
| `YamlJackson` _(default)_ | Jackson (SnakeYAML backend) |
| `YamlSnakeYaml` | SnakeYAML |

#### TOML — `"toml"`

| Adapter | Implementation |
|---|---|
| `TomlJtoml2` _(default)_ | JToml v2 |
| `TomlToml4j` | toml4j |

#### CSV and TSV

| Adapter | Format | Implementation |
|---|---|---|
| `CsvOpenCSV` | `"csv"` | OpenCSV (comma-separated) |
| `TsvOpenCSV` | `"tsv"` | OpenCSV (tab-separated) |

#### Java Properties — `"properties"`

| Adapter | Implementation |
|---|---|
| `PropertiesBuiltin` _(default)_ | built-in, no extra dependency |
| `PropertiesJackson` | Jackson Properties |

### Binary formats

| Format | Adapter | Implementation |
|---|---|---|
| `"cbor"` | `CborJackson` | CBOR (Jackson) |
| `"smile"` | `SmileJackson` | Smile (Jackson) |
| `"msgpack"` | `MsgPackJackson` | MessagePack (Jackson) |
| `"bson"` | `BsonJackson` | BSON (bson4jackson) |
| `"ion"` | `IonIon` | Amazon Ion |
| `"kryo"` | `KryoKryo` | Kryo object-graph serialization |

### Choosing an implementation

When several libraries for the same format are present, you can decide which one DataTree
uses in three ways, from the most local to the most global:

**1. Per call**, by adapter class name — overrides everything else for that one call:

```java
Tree node   = new Tree(jsonString, "JsonGson");
String json = node.toString("JsonGson");
```

**2. Globally, in code**, via the reader/writer registries:

```java
TreeReaderRegistry.setReader("json", new JsonGson());
TreeWriterRegistry.setWriter("json", new JsonGson());
```

**3. Globally, with system properties** — no code change, set at startup:

```
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonGson
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonGson
```

Replace `json` with any format name (`xml`, `yaml`, `toml`, `cbor`, ...) and `JsonGson` with
any adapter from the tables above.

## Requirements

Java 21 or newer.

## License

DataTree is licensed under the Apache License, Version 2.0 — you can use it in your commercial
products for free.
