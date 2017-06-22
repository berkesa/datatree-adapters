# DataTree Adapters
Various text and binary adapters to DataTree API.

 ![architecture](https://github.com/berkesa/datatree/blob/master/architecture.png)

## Usage

```javascript
Tree document = new Tree();
document.put("address.city", "Phoenix");
String json = document.toString();

Result:

{
  "address": {
    "city": "Phoenix"
  }
}

Other formats:

String yaml = tree.toString("yaml");
String xml  = tree.toString("xml");
String toml = tree.toString("toml");
String prop = tree.toString("properties");
String csv  = tree.toString("csv");
String tsv  = tree.toString("tsv");
```

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
