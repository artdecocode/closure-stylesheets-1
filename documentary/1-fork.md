## Fork Diversion

These are the fixes and improvements to the [original version](https://github.com/google/closure-stylesheets/compare/master...artdecocode:master):

- Switch on and document auto-expander with [--expand-browser-prefix](https://github.com/google/closure-stylesheets/commit/ec28d2ab6bea4f8d5788b23c91ff285c6652cf54) and ([do not apply expansions if the rule already exists](https://github.com/google/closure-stylesheets/commit/8a49266fb85ab9bb39b6a9ffdf50ca3b84704ef4). Expand [`clip-path`](https://github.com/google/closure-stylesheets/commit/a305325ab3abc1ed4b92b91e1d5dd1037e130946).
- [Auto-expand](https://github.com/artdecocode/closure-stylesheets-java/compare/v1.10.1...v1.11.0) prefixes into a separate output CSS file with a _JSON_ map of properties to check for CSS support.
- Proper [`@supports`](https://github.com/google/closure-stylesheets/commit/b530d2d0aae57d164bac2a6ae8da97344919ffcd) parsing and compilation.
- Print more info about errors to the console.
- Target _Java 1.8_.
- Merge in [`--allow-duplicate-declarations`](https://github.com/google/closure-stylesheets/commit/802338e1de297a62f32241f33f8fc9f4e60e530b) feature, but also [properly preserve](https://github.com/google/closure-stylesheets/commit/6fcc58ba6eba08defe979996ac56e4e1d001aca3) duplicate declarations rather than keeping the last one.
- Fix [important comment preservation](https://github.com/google/closure-stylesheets/commit/0b7294d68b2c154479ee1d10f6264d0cd08ccbea) for pseudo-selectors, e.g., for _Bootstrap_ CSS.
- Eliminate 0s from [function arguments](https://github.com/google/closure-stylesheets/commit/dbefdc7641dfb18ba09d3194f29420ece2c54119), e.g., `rgba(10,10,10,.5)`.
- Allow to [skip html escaping](https://github.com/google/closure-stylesheets/commit/11f7ff5ee0c7ac02f4e15f7d8f629486738f42e3) of strings in values with `--skip-html-escaping`.
- Upgrade to [latest dependencies](https://github.com/google/closure-stylesheets/commit/58f0394180fa829f7812611b6f70cc8d0c025b10) and refactor tests for _Truth 1_.
- Remove unused dependencies, vendor `com.google.debugging` only with relevant code, and cut the JAR filesize in half (9MB -> 4MB).
- Support `--source_map_include_content` option.
- Implement [root selector](https://github.com/google/closure-stylesheets/commit/ef3f026d5fb09e7ec36835df096543deeb083da6) for global prefixing.
- Add the [_SIMPLE_ substitution map](https://github.com/google/closure-stylesheets/commit/73816e12afc63b84d27ab08c1b116d52aa5e8234) that doesn't split class names by `-`.
- Merge [input-renaming-map](https://github.com/artdecocode/closure-stylesheets-java/pull/1) fix.

%~%