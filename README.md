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

String yaml  = tree.toString("yaml");
String xml   = tree.toString("xml");
String toml  = tree.toString("toml");
String prop  = tree.toString("properties");
String csv   = tree.toString("csv");
String tsv   = tree.toString("tsv");

byte[] bson  = tree.toBinary("bson");
byte[] ion   = tree.toBinary("ion");
byte[] cbor  = tree.toBinary("cbor");
byte[] smile = tree.toBinary("smile");
byte[] msgpk = tree.toBinary("msgpack");
```

Supported JSON APIs (readers and writers):

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
