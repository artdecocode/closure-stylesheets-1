## 14 November 2019

### [1.11.1](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.11.0...v1.11.1)

- [fix] Fix bugs in output browser prefix functionality:
  * handle `!important` declaration when dealing with values;
  * write the map properly;
  * remove empty media/page/keyframes blocks.

### [1.11.0](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.10.2...v1.11.0)

- [feature] Implement `--output-browser-prefix` flag to write expanded prefixes to a separate file with a JSON map.
- [feature] Add some property names to the list of recognised properties.

## 13 November 2019

### [1.10.2](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.10.1...v1.10.2)

- [fix] Don't auto-expand existing properties with values.

### [1.10.1](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.10.0...v1.10.1)

- [fix] Fix input rename maps.

### [1.10.0](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.9.0...v1.10.0)

- [feature] Enable `SIMPLE` substitution map (for `--rename` option) that works without splitting class names by `-`.

## 22 October 2019

### [1.9.0](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.8.0...v1.9.0)

- [feature] Implement the `--root-selector` option for global prefixing.

## 13 October 2019

### [1.8.0](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.7.1...v1.8.0)

- [feature] Add an option to include contents in source maps.
- [deps] Remove unused dependencies, clean up the _SourceMapGenerator_ to avoid unused `protobuf` together with `closure-compiler` dependency.

### [1.7.1](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.0.0...v1.7.1)

- [feature] Auto-expand `clip-path`. Upgrade version to _Closure's_ semantics.

## 9 October 2019

### [1.0.0](https://github.com/artdecocode/closure-stylesheets-java/compare/v0.0.0...v1.0.0)

- [feature] Publish the JAR file.
