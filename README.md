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

## Using XML format:

```javascript
// Parsing XML document
String xml = "< ... xml document ...>";
Tree document = new Tree(xml, "xml");

// Generating XML string from Tree
String xml = document.toString("xml");
```

## Required dependencies of XML adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson XML | XmlJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-xml', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml) |
| XMLStream | XmlXStream | [group: 'xstream', name: 'xstream', version: '1.2.2'](https://mvnrepository.com/artifact/xstream/xstream) |
| Built-in XML | XmlBuiltin | - |

## Using YAML format:

Add SnakeYAML JARs to the classpath. If DataTree detects SnakeYAML API on classpath, DataTree will use SnakeYAML API to read/write YAML documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- SNAKEYAML API -->
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.18</version>
</dependency>
```

```javascript
// Parsing YAML document
String yaml = " ... yaml document ... ";
Tree document = new Tree(yaml, "yaml");

// Generating YAML string from Tree
String yaml = document.toString("yaml");
```

## Required dependencies of YAML adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson YAML | YamlJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-yaml', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml) |
| SnakeYAML | YamlSnakeYaml | [compile group: 'org.yaml', name: 'snakeyaml', version: '1.18'](https://mvnrepository.com/artifact/org.yaml/snakeyaml) |

## Using TOML format:

Add Toml4j JARs to the classpath. If DataTree detects Toml4j API on classpath, DataTree will use Toml4j API to read/write TOML documents.

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- TOML4J API -->
<dependency>
    <groupId>com.moandjiezana.toml</groupId>
    <artifactId>toml4j</artifactId>
    <version>0.7.1</version>
</dependency>
```

```javascript
// Parsing TOML document
String toml = " ... toml document ... ";
Tree document = new Tree(toml, "toml");

// Generating TOML string from Tree
String toml = document.toString("toml");
```

## Required dependencies of TOML adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| JToml | TomlJtoml | [group: 'me.grison', name: 'jtoml', version: '1.0.0'](https://mvnrepository.com/artifact/me.grison/jtoml) |
| JToml | TomlJtoml2 | [group: 'io.ous', name: 'jtoml', version: '2.0.0'](https://mvnrepository.com/artifact/io.ous/jtoml) |
| Toml4j | TomlToml4j | [group: 'com.moandjiezana.toml', name: 'toml4j', version: '0.7.1'](https://mvnrepository.com/artifact/com.moandjiezana.toml/toml4j) |

## Using Java Property format:

```javascript
// Parsing Java Properties file
String properties = "< ... properties ...>";
Tree document = new Tree(properties, "properties");

// Generating Java Properties string from Tree
String properties = document.toString("properties");
```

## Required dependencies of Java Property adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson Properties | PropertiesJackson  | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-properties', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-properties) |
| Built-in Properties | PropertiesBuiltin | - |

## Required dependencies of CSV adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| OpenCSV | CsvOpenCSV | [group: 'net.sf.opencsv', name: 'opencsv', version: '2.3'](https://mvnrepository.com/artifact/net.sf.opencsv/opencsv) |

## Required dependencies of TSV adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| OpenCSV | TsvOpenCSV | [group: 'net.sf.opencsv', name: 'opencsv', version: '2.3'](https://mvnrepository.com/artifact/net.sf.opencsv/opencsv) |

## Using CBOR format:

CBOR is based on the wildly successful JSON data model: numbers,
strings, arrays, maps (called objects in JSON), and a few values such as
false, true, and null. One of the major practical wins of JSON is that
successful data interchange is possible without casting a schema in concrete.
This works much better in a world where both ends of a communication
relationship may be evolving at high speed.

Add DataTree Adapters and CBOR JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- CBOR API -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-cbor</artifactId>
    <version>2.9.0.pr3</version>
</dependency>
```

Reading and writing CBOR documents:

```javascript
// Parsing CBOR document
byte[] cbor = " ... bytes of the CBOR document ... ";
Tree document = new Tree(cbor, "cbor");

// Generating CBOR byte array from Tree
byte[] cbor = document.toBinary("cbor");
```

## Required dependencies of CBOR adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson CBOR | CborJackson | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-cbor', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-cbor) |

## Using BSON format:

BSON is a computer data interchange format used mainly as a data storage and
network transfer format in the MongoDB database. It is a binary form for
representing simple data structures, associative arrays
(called objects or documents in MongoDB), and various data types of specific interest to MongoDB.

Add DataTree Adapters and BSON JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- BSON API -->
<dependency>
    <groupId>de.undercouch</groupId>
    <artifactId>bson4jackson</artifactId>
    <version>2.7.0</version>
</dependency>
```

Reading and writing BSON documents:

```javascript
// Parsing BSON document
byte[] bson = " ... bytes of the BSON document ... ";
Tree document = new Tree(bson, "bson");

// Generating BSON byte array from Tree
byte[] bson = document.toBinary("bson");
```

## Required dependencies of BSON adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson BSON | BsonJackson | [group: 'de.undercouch', name: 'bson4jackson', version: '2.7.0'](https://mvnrepository.com/artifact/de.undercouch/bson4jackson) |

## Using SMILE format:

Smile is a computer data interchange format based on JSON. It can also be considered
as a binary serialization of generic JSON data model, which means that tools that operate
on JSON may be used with Smile as well, as long as proper encoder/decoder exists for tool to use. 

Add DataTree Adapters and SMILE JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- SMILE API -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-smile</artifactId>
    <version>2.9.0.pr3</version>
</dependency>
```

Reading and writing SMILE documents:

```javascript
// Parsing SMILE document
byte[] smile = " ... bytes of the SMILE document ... ";
Tree document = new Tree(smile, "smile");

// Generating SMILE byte array from Tree
byte[] smile = document.toBinary("smile");
```

## Required dependencies of SMILE adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Jackson SMILE | SmileJackson | [group: 'com.fasterxml.jackson.dataformat', name: 'jackson-dataformat-smile', version: '2.9.0.pr3'](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-smile) |

## Using ION format:

Amazon Ion is a richly-typed, self-describing, hierarchical data serialization format
offering interchangeable binary and text representations. The text format (a superset of JSON)
is easy to read and author, supporting rapid prototyping. The binary representation is efficient
to store, transmit, and skip-scan parse. The rich type system provides unambiguous semantics for
long-term preservation of business data which can survive multiple generations of software evolution.

Add DataTree Adapters and SMILE JARs to the classpath:

```xml
<!-- DATATREE API -->
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- ION API -->
<dependency>
    <groupId>software.amazon.ion</groupId>
    <artifactId>ion-java</artifactId>
    <version>1.0.2</version>
</dependency>
```

Reading and writing ION documents:

```javascript
// Parsing ION document
byte[] ion = " ... bytes of the ION document ... ";
Tree document = new Tree(ion, "ion");

// Generating ION byte array from Tree
byte[] ion = document.toBinary("ion");
```

## Required dependencies of ION adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Amazon ION | IonIon | [group: 'software.amazon.ion', name: 'ion-java', version: '1.0.2'](https://mvnrepository.com/artifact/software.amazon.ion/ion-java) |

## Using Java Object Serializator/Deserializator

Reading and writing CBOR documents:

```javascript
// Reading serialized data structure
byte[] bytes = " ... bytes of the document ... ";
Tree document = new Tree(bytes, "java");

// Serialize Java Objects into byte array
byte[] bytes = document.toBinary("java");
```

## Required dependencies of Java Object Serializator/Deserializator adapters:

| API Name            | Adapter Class | Dependency |
| ------------------- | ------------- | ---------- |
| Built-in Java Object Serializator/Deserializator | JavaBuiltin  | - |
