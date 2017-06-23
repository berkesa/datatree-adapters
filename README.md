# DataTree Adapters
Various text and binary adapters to DataTree API.

 ![architecture](https://github.com/berkesa/datatree/blob/master/docs/images/architecture.png)

## Download

Add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>
```

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

String yaml  = document.toString("yaml");
String xml   = document.toString("xml");
String toml  = document.toString("toml");
String prop  = document.toString("properties");
String csv   = document.toString("csv");
String tsv   = document.toString("tsv");

byte[] bson  = document.toBinary("bson");
byte[] ion   = document.toBinary("ion");
byte[] cbor  = document.toBinary("cbor");
byte[] smile = document.toBinary("smile");
byte[] msgpk = document.toBinary("msgpack");
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
