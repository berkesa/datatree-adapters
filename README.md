## DataTree Adapter Pack

Text and binary adapters for [DataTree Tools API](https://berkesa.github.io/datatree/).

DataTree is an extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats. DataTree is NOT an another JSON parser. It's a top-level API layer that uses existing JSON implementations.
Even though the JSON format is the default, DataTree supports other formats, such as XML, YAML, TOML, etc.
DataTree enables you to replace the underlaying implementation (to a smaller, smarter, faster version)
during the software development without any code modifications.
In addition, the DataTree API provides you with a logical set of tools
to manipulate (put, get, remove, insert, sort, find, stream, etc.) the content of the hierarchical documents.

![architecture](https://raw.githubusercontent.com/berkesa/datatree/master/docs/architecture.png)

## Documentation

[![Documentation](https://raw.githubusercontent.com/berkesa/datatree/master/docs/docs-button.png)](https://berkesa.github.io/datatree/introduction.html)

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

## License:

DataTree is licensed under the Apache License V2, you can use it in your commercial products for free.
