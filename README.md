# DataTree Adapter Pack
Various text and binary adapters to [DataTree API](https://github.com/berkesa/datatree).

DataTree is an extensible Java Library for reading, manipulating and writing hierarchical data structures from/to various formats. DataTree is NOT an another JSON parser. It's a top-level API layer that uses existing JSON implementations.
Even though the JSON format is the default, DataTree supports other formats, such as XML, YAML, TOML, etc.
DataTree enables you to replace the underlaying implementation (to a smaller, smarter, faster version)
during the software development without any code modifications.
In addition, the DataTree API provides you with a logical set of tools
to manipulate (put, get, remove, insert, sort, find, stream, etc.) the content of the hierarchical documents.

![architecture](https://github.com/berkesa/datatree/blob/master/docs/images/architecture.png)

## Using various JSON implementations

The following sample demonstrates, how to replace the built-in JSON API to Jackson's JSON API. The only thing you have to do is add Jackson JARs to the classpath. If DataTree detects Jackson API on classpath, DataTree will use Jackson's Object Mapper to read/write JSON documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- JACKSON JSON API -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.0.pr3</version>
</dependency>
```

```javascript
// Parsing JSON document using Jackson API
String json = "{ ... json document ...}";
Tree document = new Tree(json);

// Generating JSON string from Tree using Jackson API
String json = document.toString();
```

That is all. The table below shows the dependencies of the supported JSON implementations. If you add FastJson dependency to classpath instead of Jackson, DataTree will use FastJson, and so on.

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
| Jackson JSON | JsonJackson | [group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) |
| Jodd Json | JsonJodd  | [group: 'org.jodd', name: 'jodd-json', version: '3.8.5'](https://mvnrepository.com/artifact/org.jodd/jodd-json) |
| Apache Johnzon | JsonJohnzon | [group: 'org.apache.johnzon', name: 'johnzon-normalMapper', version: '1.1.0'](https://mvnrepository.com/artifact/org.apache.johnzon/johnzon-normalMapper) |
| JsonIO | JsonJsonIO | [group: 'com.cedarsoftware', name: 'json-io', version: '4.9.12'](https://mvnrepository.com/artifact/com.cedarsoftware/json-io) |
| NanoJson | JsonNano | [group: 'com.grack', name: 'nanojson', version: '1.2'](https://mvnrepository.com/artifact/com.grack/nanojson) |
| JSON.simple | JsonSimple | [group: 'com.googlecode.json-simple', name: 'json-simple', version: '1.1.1'](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple) |
| Json-smart | JsonSmart | [group: 'net.minidev', name: 'json-smart', version: '2.3'](https://mvnrepository.com/artifact/net.minidev/json-smart) |
| SOJO | JsonSojo | [group: 'net.sf.sojo', name: 'sojo', version: '1.0.8'](https://mvnrepository.com/artifact/net.sf.sojo/sojo) |
| JsonUtil | JsonUtil | [group: 'org.kopitubruk.util', name: 'JSONUtil', version: '1.10.4'](https://mvnrepository.com/artifact/org.kopitubruk.util/JSONUtil) |
| Amazon Ion | JsonIon  | [group: 'software.amazon.ion', name: 'ion-java', version: '1.0.2'](https://mvnrepository.com/artifact/software.amazon.ion/ion-java) |
| Built-in parser | JsonBuiltin | - |

## Using different JSON implementations for reading and writing

If DataTree detects more JSON implementations on classpath, DataTree will use the fastest implementation. To force DataTree to use the proper APIs, use the `datatree.json.reader` and `datatree.json.writer` System Properties to specify the appropriate Adapter Class (see above in the table) for reading and writing:

```javascript
-Ddatatree.json.reader=io.datatree.dom.adapters.JsonBoon
-Ddatatree.json.writer=io.datatree.dom.adapters.JsonJackson
```

Add Boon and Jackson to your pom.xml:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- JACKSON JSON API -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.0.pr3</version>
</dependency>

<!-- BOON JSON API -->
<dependency>
    <groupId>io.fastjson</groupId>
    <artifactId>boon</artifactId>
    <version>0.34</version>
</dependency>
```

After that, DataTree will use Boon API for parsing, and Jackson for generating JSON strings.

```javascript
// Parsing JSON document using Boon API
String json = "{ ... json document ...}";
Tree document = new Tree(json);

// Generating JSON string from Tree using Jackson API
String json = document.toString();
```

## Required dependencies of XML adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson XML | XmlJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-xml', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml) |
| XMLStream | XmlXStream | [group: 'xstream', name: 'xstream', version: '1.2.2'](https://mvnrepository.com/artifact/xstream/xstream) |
| Built-in XML | XmlBuiltin | - |
