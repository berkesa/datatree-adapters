# datatree-adapters
Various text and binary adapters to DataTree API.

## Text formats
JSON adapters:

- Boon JSON API
- BSON (MongoDB API)
- DSLJson
- FastJson
- Flexjson
- Genson
- Google Gson
- Jackson JSON API
- Jodd Json
- Apache Johnzon
- JsonIO
- NanoJson
- JSON.simple
- Json-smart
- SOJO
- JsonUtil
- Amazon Ion JSON API
- Built-in JSON API

TOML adapters:

- Toml4J TOML API
- JToml (me.grison.jtoml package)
- JToml (io.ous.jtoml package)

TSV adapters:

- OpenCSV CSV API

CSV adapters:

- OpenCSV CSV API

XML adapters:

- Jackson XML API
- Built-in XML API

YAML adapters:

- SnakeYAML API
- Jackson YAML API

Java Properties adapters:

- Jackson Properties API 
- Built-in Properties API

## Binary formats
MessagePack adapters:

- Jackson MsgPack API
- MsgPack.org API

BSON adapters:

- Jackson BSON API

CBOR adapters:

- Jackson CBOR API

SMILE adapters:

- Jackson SMILE API

ION adapters:

- Amazon ION binary API

Java Object Serialization:

- Built-in API

## Usage

```java
Tree tree = new Tree();

tree.put("address.city", "Washington D.C.");
tree.put("address.zip", 20000);

String json = tree.toString("json");
String xml = tree.toString("xml");
String toml = tree.toString("toml");
String csv = tree.toString("csv");
String tsv = tree.toString("tsv");
String properties = tree.toString("properties");
String yaml = tree.toString("yaml");

Tree yamlCopy = new Tree(yaml, "yaml");

byte[] ion = tree.toBinary("ion");
byte[] cbor = tree.toBinary("cbor");
byte[] smile = tree.toBinary("smile");
byte[] java = tree.toBinary("java");
byte[] bson = tree.toBinary("bson");

Tree bsonCopy = new Tree(bson, "bson");

String city = bsonCopy.get("address.city", "defaultValue");
int zip = bsonCopy.get("address.zip", 1000);
```
