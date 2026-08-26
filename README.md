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
adds ~30 format/implementation adapters for the formats listed below.

![architecture](https://raw.githubusercontent.com/berkesa/datatree/master/docs/architecture.png)

## Documentation

[![Documentation](https://raw.githubusercontent.com/berkesa/datatree/master/docs/docs-button.png)](https://berkesa.github.io/datatree/introduction.html)

## Download

```xml
<dependency>
    <groupId>com.github.berkesa</groupId>
    <artifactId>datatree-adapters</artifactId>
    <version>2.1.0</version>
</dependency>
```

This pulls in `datatree-core` plus the third-party libraries behind every adapter (Jackson,
Gson, SnakeYAML, XStream, …) as transitive dependencies, so all the formats below work out of
the box. If you only need a few formats, exclude the libraries you don't use to keep your
dependency tree small.

## Supported formats

**Text:** JSON, XML, YAML, TOML, CSV, TSV, Properties
**Binary:** CBOR, Smile, MessagePack, BSON, Ion, Kryo

~30 swappable adapters in total, each registering itself automatically as soon as it is
on the classpath. See the [documentation](https://berkesa.github.io/datatree/introduction.html)
for the full adapter list and how to choose between implementations.

## Requirements

Java 11 or newer.

## License

DataTree is licensed under the Apache License, Version 2.0 — you can use it in your commercial
products for free.
