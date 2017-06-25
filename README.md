# DataTree Adapter Pack
Various text and binary adapters to DataTree API.

DataTree is an extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats. DataTree is NOT an another JSON parser. It's a top-level API layer that uses existing JSON implementations.
Even though the JSON format is the default, DataTree supports other formats, such as XML, YAML, TOML, etc.
DataTree enables you to replace the underlaying implementation (to a smaller, smarter, faster version)
during the software development without any code modifications.
In addition, the DataTree API provides you with a logical set of tools
to manipulate (put, get, remove, insert, sort, find, stream, etc.) the content of the hierarchical documents.

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

## Required dependencies of JSON adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Boon JSON API | JsonBoon | [group: 'io.fastjson', name: 'boon', version: '0.34'](https://mvnrepository.com/artifact/io.fastjson/boon) |
| BSON (MongoDB) | JsonBson | [group: 'org.mongodb', name: 'bson', version: '3.4.2'](https://mvnrepository.com/artifact/org.mongodb/bson) |
| DSLJson | JsonDSL | [group: 'com.dslplatform', name: 'dsl-json', version: '1.4.0'](https://mvnrepository.com/artifact/com.dslplatform/dsl-json) |
| FastJson | JsonFast | [group: 'com.alibaba', name: 'fastjson', version: '1.2.31'](https://mvnrepository.com/artifact/com.alibaba/fastjson) |
| Flexjson | JsonFlex | [group: 'net.sf.flexjson', name: 'flexjson', version: '3.3'](https://mvnrepository.com/artifact/net.sf.flexjson/flexjson) |
| Genson | JsonGenson | [group: 'com.owlike', name: 'genson', version: '1.4'](https://mvnrepository.com/artifact/com.owlike/genson) |
| Google Gson | JsonGson | [group: 'com.google.code.gson', name: 'gson', version: '2.8.0'](https://mvnrepository.com/artifact/com.google.code.gson/gson) |
| Jackson JSON API | JsonJackson | [group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) |
| Jodd Json | JsonJodd  | [group: 'org.jodd', name: 'jodd-json', version: '3.8.5'](https://mvnrepository.com/artifact/org.jodd/jodd-json) |
| Apache Johnzon | JsonJohnzon | [group: 'org.apache.johnzon', name: 'johnzon-normalMapper', version: '1.1.0'](https://mvnrepository.com/artifact/org.apache.johnzon/johnzon-normalMapper) |
| JsonIO | JsonJsonIO | [group: 'com.cedarsoftware', name: 'json-io', version: '4.9.12'](https://mvnrepository.com/artifact/com.cedarsoftware/json-io) |
| NanoJson | JsonNano | [group: 'com.grack', name: 'nanojson', version: '1.2'](https://mvnrepository.com/artifact/com.grack/nanojson) |
| JSON.simple | JsonSimple | [group: 'com.googlecode.json-simple', name: 'json-simple', version: '1.1.1'](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple) |
| Json-smart | JsonSmart | [group: 'net.minidev', name: 'json-smart', version: '2.3'](https://mvnrepository.com/artifact/net.minidev/json-smart) |
| SOJO | JsonSojo | [group: 'net.sf.sojo', name: 'sojo', version: '1.0.8'](https://mvnrepository.com/artifact/net.sf.sojo/sojo) |
| JsonUtil | JsonUtil | [group: 'org.kopitubruk.util', name: 'JSONUtil', version: '1.10.4'](https://mvnrepository.com/artifact/org.kopitubruk.util/JSONUtil) |
| Amazon Ion | JsonIon  | [group: 'software.amazon.ion', name: 'ion-java', version: '1.0.2'](https://mvnrepository.com/artifact/software.amazon.ion/ion-java) |
| Built-in JSON API | JsonBuiltin   | - |
