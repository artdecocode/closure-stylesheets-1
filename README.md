# Closure Stylesheets

![Java CI with Maven](https://github.com/artdecocode/closure-stylesheets-java/workflows/Java%20CI%20with%20Maven/badge.svg)

Closure Stylesheets is an extension to CSS that adds
**[variables](#variables)**, **[functions](#functions)**,
**[conditionals](#conditionals)**, and **[mixins](#mixins)** to standard
CSS. The tool also supports **[minification](#minification)**,
**[linting](#linting)**, **[RTL flipping](#rtl-flipping)**, and CSS class
**[renaming](#renaming)**.

## Table Of Contents

- [Table Of Contents](#table-of-contents)
- [Fork Diversion](#fork-diversion)
- [Get Closure Stylesheets!](#get-closure-stylesheets)
- [CSS Extensions](#css-extensions)
  * [Variables](#variables)
  * [Functions](#functions)
  * [Mixins](#mixins)
  * [Conditionals](#conditionals)
- [Auto Expansion](#auto-expansion)
  * [Output Prefixes](#output-prefixes)
  * [Specific Prefixes](#specific-prefixes)
  * [Keyframes](#keyframes)
    * [`@gen-webkit-keyframes`](#gen-webkit-keyframes)
- [Minification](#minification)
- [Linting](#linting)
  * [`--allowed-non-standard-function`, `--allow-unrecognized-functions`](#--allowed-non-standard-function---allow-unrecognized-functions)
    * [`--allow-duplicate-declarations`](#--allow-duplicate-declarations)
  * [`--allow-unrecognized-properties`, `--allowed-unrecognized-property`](#--allow-unrecognized-properties---allowed-unrecognized-property)
- [RTL Flipping](#rtl-flipping)
- [Renaming](#renaming)
- [Root Selector](#root-selector)

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/0.svg?sanitize=true">
</a></p>

## Fork Diversion

These are the fixes and improvements to the [original version](https://github.com/google/closure-stylesheets/compare/master...artdecocode:master):

- Switch on and document auto-expander with [--expand-browser-prefix](https://github.com/google/closure-stylesheets/commit/ec28d2ab6bea4f8d5788b23c91ff285c6652cf54) and ([do not apply expansions if the rule already exists](https://github.com/google/closure-stylesheets/commit/8a49266fb85ab9bb39b6a9ffdf50ca3b84704ef4). Expand [`clip-path`](https://github.com/google/closure-stylesheets/commit/a305325ab3abc1ed4b92b91e1d5dd1037e130946).
- [Auto-expand](../../compare/v1.10.0...v1.11.0) [prefixes](../../compare/v1.11.0...v1.11.1) into a separate output CSS file with a _JSON_ map of properties to check for CSS support.
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
- [Update](https://github.com/artdecocode/closure-stylesheets-java/commit/219d1cdc9d509df925583f081d8241d6a66edcca) to [`ph-javacc-maven-plugin@4`](https://github.com/phax/ph-javacc-maven-plugin) from 2.8. This one uses the latest version of parser generator (_ParserGeneratorCC_ instead of _JavaCC_).
- Fix [recursive calc](https://github.com/artdecocode/closure-stylesheets-java/commit/df8210feb1fdc1a487a79786610fb4365bcaaf59), e.g., `calc(calc(-1 * 3em) + 1px)`.
- Allow [multiple refiners](https://github.com/artdecocode/closure-stylesheets-java/commit/960ff6fb4bf8107b953a0afe9d2fbaede1966d34), e.g., `a:not(.A.B)`.

Checkout <kbd>📙 [WIKI](../../wiki)</kbd> for some development information.

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/1.svg?sanitize=true">
</a></p>

## Get Closure Stylesheets!

Closure Stylesheets is available as a Java jar named `closure-stylesheets.jar`.
You can either [download] a precompiled jar or [build it from source].

Using Closure Stylesheets requires Java. To make sure that Java is installed
correctly, try running the following command to print the list of command-line
options for Closure Stylesheets:

```
java -jar closure-stylesheets.jar --help
```

[download]: https://github.com/google/closure-stylesheets/releases
[build it from source]: https://github.com/google/closure-stylesheets/wiki/Building-From-Source.md

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/2.svg?sanitize=true">
</a></p>

## CSS Extensions

Internally at Google, Closure Stylesheets are frequently referred to as "Google
Stylesheets" or "GSS", so you will see references to GSS in the
[source code](https://github.com/google/closure-stylesheets). Some
developers prefer to be explicit about which files use the Closure Stylesheets
extensions to CSS by using a **`.gss`** file extension.

### Variables

Variables can be defined in Closure Stylesheets using **`@def`** followed by a
variable name and then a value. Variables can also be defined in terms of other
variables. Consider the following file, **`variable-example.gss`**:

```css
@def BG_COLOR              rgb(235, 239, 249);

@def DIALOG_BORDER_COLOR   rgb(107, 144, 218);
@def DIALOG_BG_COLOR       BG_COLOR;

body {
  background-color: BG_COLOR;
}

.dialog {
  background-color: DIALOG_BG_COLOR;
  border: 1px solid DIALOG_BORDER_COLOR;
}
```

Running **`java -jar closure-stylesheets.jar --pretty-print
variable-example.gss`** will print:

```css
body {
  background-color: #ebeff9;
}
.dialog {
  background-color: #ebeff9;
  border: 1px solid #6b90da;
}
```

### Functions

Closure Stylesheets provides support for several arithmetic functions:

  * `add()`
  * `sub()`
  * `mult()`
  * `divide()`
  * `min()`
  * `max()`

Each of these functions can take a variable number arguments. Arguments may be
purely numeric or CSS sizes with units (though `mult()` and `divide()` only
allow the first argument to have a unit). When units such as `px` are specified
as part of an argument, all arguments to the function must have the same
unit. That is, you may do `add(3px, 5px)` or `add(3ex, 5ex)`, but you cannot do
`add(3px, 5ex)`. Here is an example of when it might be helpful to use `add()`:

```css
@def LEFT_HAND_NAV_WIDTH    180px;
@def LEFT_HAND_NAV_PADDING  3px;

.left_hand_nav {
  position: absolute;
  width: LEFT_HAND_NAV_WIDTH;
  padding: LEFT_HAND_NAV_PADDING;
}

.content {
  position: absolute;
  margin-left: add(LEFT_HAND_NAV_PADDING,  /* padding left */
                   LEFT_HAND_NAV_WIDTH,
                   LEFT_HAND_NAV_PADDING); /* padding right */

}
```

Running **`java -jar closure-stylesheets.jar --pretty-print
functions-example.gss`** will print:

```css
.left_hand_nav {
  position: absolute;
  width: 180px;
  padding: 3px;
}
.content {
  position: absolute;
  margin-left: 186px;
}
```

Although these functions are not as full-featured as
[CSS3 calc()](http://www.w3.org/TR/css3-values/#calc) because they do not allow
you to mix units as `calc()` does, they can still help produce more maintainable
stylesheets.


There are also built-in functions that deal with colors. For now, you need to
[see the code](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/compiler/gssfunctions/GssFunctions.java)
for details, but here are the functions and the arguments that they take:

  * `blendColorsHsb(startColor, endColor)` blends using HSB values
  * `blendColorsRgb(startColor, endColor)` blends using RGB values
  * `makeMutedColor(backgroundColor, foregroundColor [, saturationLoss])`
  * `addHsbToCssColor(baseColor, hueToAdd, saturationToAdd, brightnessToAdd)`
  * `makeContrastingColor(color, similarityIndex)`
  * `adjustBrightness(color, brightness)`
  * `saturateColor(color, saturationToAdd)` increase saturation in HSL color space
  * `desaturateColor(color, saturationToRemove)` decrease saturation in HSL color space
  * `greyscale(color)` full desaturation of a color in HSL color space
  * `lighten(color, lightnessToAdd)` increase the lightness in HSL color space
  * `darken(color, lightnessToRemove)` decrease the lightness in HSL color space
  * `spin(color, hueAngle)` increase or decrease hue of the color, like rotating in a color wheel

There is also a `selectFrom()` function that behaves like the ternary operator:

```css
/* Implies MYDEF = FOO ? BAR : BAZ; */
@def MYDEF selectFrom(FOO, BAR, BAZ);
```

This could be used with `@def FOO true;` to have the effect of `@def MYDEF =
BAR`.

It is also possible to define your own functions in Java by implementing
[GssFunctionMapProvider](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/GssFunctionMapProvider.java)
and passing the fully-qualified class name to Closure Stylesheets via the
**`--gss-function-map-provider`** flag. If you choose to do this, you will
likely want to compose
[DefaultGssFunctionMapProvider](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/compiler/gssfunctions/DefaultGssFunctionMapProvider.java)
so that your
[GssFunctionMapProvider](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/GssFunctionMapProvider.java)
provides your custom functions in addition to the built-in arithmetic functions.

### Mixins

Mixins make it possible to reuse a list of parameterized declarations. A mixin
definition (**`@defmixin`**) can be seen as a function with arguments that
contains a list of declarations. At the place where a mixin is used
(**`@mixin`**), the values for the arguments are defined and the declarations
are inserted. A mixin can be used in any place where declarations are allowed.

The names of the arguments in the **`@defmixin`** declaration must be all
uppercase.

Global constants defined with **`@def`** can be used in combination with
mixins. They can be used both within the definition of a mixin, or as an
argument when using a mixin.

For example, consider defining a mixin in **`mixin-simple-example.gss`** that
could be used to create a shorthand for declaring the dimensions of an element:

```css
@defmixin size(WIDTH, HEIGHT) {
  width: WIDTH;
  height: HEIGHT;
}

.logo {
  @mixin size(150px, 55px);
  background-image: url('http://www.google.com/images/logo_sm.gif');
}
```

Running **`java -jar closure-stylesheets.jar --pretty-print
mixin-simple-example.gss`** prints:

```css
.logo {
  width: 150px;
  height: 55px;
  background-image: url('http://www.google.com/images/logo_sm.gif');
}
```

Mixins are even more compelling when you consider using them to abstract away
cross-browser behavior for styles such as gradients:

```css
@defmixin gradient(POS, HSL1, HSL2, HSL3, COLOR, FALLBACK_COLOR) {
  background-color: FALLBACK_COLOR; /* fallback color if gradients are not supported */
  background-image: -webkit-linear-gradient(POS, hsl(HSL1, HSL2, HSL3), COLOR);               /* Chrome 10+,Safari 5.1+ */
  /* @alternate */ background-image: -moz-linear-gradient(POS, hsl(HSL1, HSL2, HSL3), COLOR); /* FF3.6+ */
  /* @alternate */ background-image: -ms-linear-gradient(POS, hsl(HSL1, HSL2, HSL3), COLOR);  /* IE10 */
  /* @alternate */ background-image: -o-linear-gradient(POS, hsl(HSL1, HSL2, HSL3), COLOR);   /* Opera 11.10+ */
}

.header {
  @mixin gradient(top, 0%, 50%, 70%, #cc0000, #f07575);
}
```

The above is compiled to:

```css
.header {
  background-color: #f07575;
  background-image: -webkit-linear-gradient(top,hsl(0%,50%,70%) ,#cc0000);
  background-image: -moz-linear-gradient(top,hsl(0%,50%,70%) ,#cc0000);
  background-image: -ms-linear-gradient(top,hsl(0%,50%,70%) ,#cc0000);
  background-image: -o-linear-gradient(top,hsl(0%,50%,70%) ,#cc0000);
}
```

See the section on [linting](#linting) for more details on the
**`@alternate`** annotation.

### Conditionals

Variables can be defined using conditionals with **`@if`**, **`@elseif`**, and
**`@else`**. The following is a real-world example adapted from the
[Closure Library](https://github.com/google/closure-library/blob/master/closure/goog/css/common.css),
which defines a cross-browser CSS class to apply the style **`display:
inline-block`**. The Closure Library example uses browser hacks to define
`.goog-inline-block`, but it can be done explicitly in Closure Stylesheets by
using conditionals as shown in **`conditionals.gss`**:

```css
@if (BROWSER_IE) {
  @if (BROWSER_IE6) {
    @def GOOG_INLINE_BLOCK_DISPLAY  inline;
  } @elseif (BROWSER_IE7) {
    @def GOOG_INLINE_BLOCK_DISPLAY  inline;
  } @else {
    @def GOOG_INLINE_BLOCK_DISPLAY  inline-block;
  }
} @elseif (BROWSER_FF2) {
  @def GOOG_INLINE_BLOCK_DISPLAY    -moz-inline-box;
} @else {
  @def GOOG_INLINE_BLOCK_DISPLAY    inline-block;
}

.goog-inline-block {
  position: relative;
  display: GOOG_INLINE_BLOCK_DISPLAY;
}
```

Values for the conditionals can be set via a **`--define`** flag. By default,
all conditional variables are assumed to be false, so running **`java -jar
closure-stylesheets.jar --pretty-print conditionals.gss`** will print:

```css
.goog-inline-block {
  position: relative;
  display: inline-block;
}
```

whereas **`java -jar closure-stylesheets.jar --define BROWSER_FF2 --pretty-print
conditionals.gss`** will print:

```css
.goog-inline-block {
  position: relative;
  display: -moz-inline-box;
}
```

It is also possible to specify the `--define` flag multiple times, so **`java
-jar closure-stylesheets.jar --define BROWSER_IE --define BROWSER_IE6
--pretty-print conditionals.gss`** will print:

```css
.goog-inline-block {
  position: relative;
  display: inline;
}
```

Admittedly, to get the benefit of serving the CSS specific to a particular user
agent, one must generate a separate stylesheet for each user agent and then
serve it appropriately.

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/3.svg?sanitize=true">
</a></p>



## Auto Expansion

There is an auto-expansion pass that adds vendor-specific directives, however it was switched off in the _Google's version_. The reason remains unknown, however it might be because it slow down the compiler.

```css
.row {
  margin: 15px;
  display: flex;
  flex-flow: row wrap;
  height: calc(100vh - 10rem);
  background: linear-gradient(5deg, red, tan);
}
.col {
  flex-flow: row-reverse nowrap;
  display: inline-flex;
  width: calc(10px);
}
```

This fork allows to switch it on with the `--expand-browser-prefix` flag:

```console
closure-stylesheets:~$ java -jar closure-stylesheets.jar --expand-browser-prefix \
> --pretty-print example/prefix.css
```

```css
.row {
  margin: 15px;
  display: flex;
  display: -webkit-box;
  display: -moz-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  flex-flow: row wrap;
  -ms-flex-flow: row wrap;
  -webkit-flex-flow: row wrap;
  height: calc(100vh - 10rem);
  height: -webkit-calc(100vh - 10rem);
  height: -moz-calc(100vh - 10rem);
  background: linear-gradient(5deg,red,tan);
  background: -webkit-linear-gradient(5deg,red,tan);
  background: -moz-linear-gradient(5deg,red,tan);
  background: -ms-linear-gradient(5deg,red,tan);
  background: -o-linear-gradient(5deg,red,tan);
}
.col {
  flex-flow: row-reverse nowrap;
  -ms-flex-flow: row-reverse nowrap;
  -webkit-flex-flow: row-reverse nowrap;
  display: inline-flex;
  display: -webkit-inline-box;
  display: -webkit-inline-flex;
  display: -ms-inline-flexbox;
  width: calc(10px);
  width: -webkit-calc(10px);
  width: -moz-calc(10px);
}
```

There is no control over which rules to expand by supplied desired browser coverage right now. All known rules will be expanded which can increase the size of the stylesheets by a lot.

### Output Prefixes

To generate a separate CSS file with expanded prefixes, the `--output-browser-prefix` arg must be set to the desired location of the file. In this case, an additional JSON file with the map of properties and values that were expanded will be written to the same location but under the `.json` name. It then can be used in conjunction with the `CSS.supports` function to test whether the prefixes file is required.

```console
closure-stylesheets:~$ java -jar closure-stylesheets.jar --expand-browser-prefix \
> --output-browser-prefix example/prefixes.css --pretty-print example/prefix.css
```

```css
.row {
  margin: 15px;
  display: flex;
  flex-flow: row wrap;
  height: calc(100vh - 10rem);
  background: linear-gradient(5deg,red,tan);
}
.col {
  flex-flow: row-reverse nowrap;
  display: inline-flex;
  width: calc(10px);
}
```

As you can see, the input does not contain the expanded properties, however 2 new files were generated:

<table>
<tr><th>example/prefixes.css</th><th>example/prefixes.css.json</th></tr>
<tr><td>

```css
.row {
  display: -webkit-box;
  display: -moz-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  -ms-flex-flow: row wrap;
  -webkit-flex-flow: row wrap;
  height: -webkit-calc(100vh - 10rem);
  height: -moz-calc(100vh - 10rem);
  background: -webkit-linear-gradient(5deg,red,tan);
  background: -moz-linear-gradient(5deg,red,tan);
  background: -ms-linear-gradient(5deg,red,tan);
  background: -o-linear-gradient(5deg,red,tan);
}
.col {
  -ms-flex-flow: row-reverse nowrap;
  -webkit-flex-flow: row-reverse nowrap;
  display: -webkit-inline-box;
  display: -webkit-inline-flex;
  display: -ms-inline-flexbox;
  width: -webkit-calc(10px);
  width: -moz-calc(10px);
}
```
</td>
<td>

```json
{
  "background": [
    "linear-gradient(5deg,red,tan)"
  ],
  "display": [
    "flex",
    "inline-flex"
  ],
  "width": [
    "calc(10px)"
  ],
  "flex-flow": [
    "row wrap"
  ]
}
```
</td></tr>
</table>

There are 3 general cases for the map:

1. A property regardless of the value, such as `flex-flow` that will be expanded into `-[]-flex-flow`. The shortest value will be added to the map (_`row wrap`_), because here we expand the property name.
1. A value of a property, such as `display: flex` or `display: inline-flex`. Each value will be added to the map to be tested against its property name.
1. A value which is a function, like `calc` or `linear-gradient`. The shorted value will be selected (e.g., between `calc(10px)` and `calc(100vh - 10rem)`, the first one will be selected) and its property name used (such as `width` in the example).

The map will only be created for unprefixed properties, because only they are expanded, i.e. `-ms-flex` is not expanded and will not be tested against because it's already in the CSS.

Now the prefixes map can be used on a page to figure out if to download the fallbacks:

```js
/* eslint-env browser */
const entries = Object.entries(prefixes)
let supportsAll = true
if ('CSS' in window && 'supports' in CSS) {
  let nonSupported = []
  for (let i=0; i<entries.length; i++) {
    const [keys, values] = entries[i]
    // eg the key is either "flex", or "flex|-ms-flex"
    // when multiple given, ensure at least one is supported
    const k = keys.split('|')
    let anyKeySupported = false
    for (let j=0; j<k.length; j++) {
      const key = k[j]
      // values is an array against property like [
      //  "flex|-webkit-flex"
      //  "inline-flex|-ms-inline-flexbox",
      // ] or just single value ["auto"]
      const sup = values.map((value) => {
        const v = value.split('|')
        let anyValueSupported = false
        for (let n=0; n<v.length; n++) {
          const val = n[j]
          const s = CSS.supports(key, val)
          if (s) {
            anyValueSupported = true
            break
          } else {
            nonSupported.push(`${key}: ${val}`)
          }
        }
        return anyValueSupported
      })
      const allValuesSupported = sup.filter(Boolean).length
        == values.length
      if (allValuesSupported) {
        anyKeySupported = true
        break
      }
    }
    if (!anyKeySupported) supportsAll = false
  }
  if (nonSupported.length) {
    console.log('Browser does not support CSS properties: %s',
      nonSupported.join('\n '))
  }
} else {
  supportsAll = false
}
if (!supportsAll) {
  const link = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = 'prefixes.css'
  document.head.appendChild(link)
}
```

And a noscript version should be added to the head of the document:

```html
<html>
  <head>
    <!-- the support detection script -->
    <noscript>
      <link href="prefixes.css" rel="stylesheet">
    </noscript>
  </head>
</html>
```


<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/4.svg?sanitize=true">
</a></p>

### Specific Prefixes

When creating a separate output for prefixes, it is possible to have control over which rules are actually kept in the output CSS, and which are put into the external prefixes CSS. This is done with the `--prefix` flag. It can be of four kinds:

1. A generic property name, such as `hyphens`, which will keep all expanded prefixes for the `hyphens` property name in the output CSS.
1. An expanded, prefixed property name, e.g., `-ms-flex` that will keep that rule in the output.
1. A property name with a generic value that will keep all expansions of that property-value pair, such as `position:sticky`.
1. A property name with an expanded value to specify the exact expansions that should be preserved, e.g., `display:-ms-flexbox`.
1. A generic function name, e.g., `calc`.
1. (_todo_) An expanded function name, e.g., `-webkit-calc`.

The map will be generated accordingly with the account of rules already present in the output CSS. Consider the following style:

```css
.row {
  margin: 15px;
  display: flex;
  display: -ms-inline-flexbox;
  display: inline-flex;
  flex: auto;
  hyphens: auto;
  position: sticky;
}
.col {
  height: calc(1rem - 10px);
}
```

```console
closure-stylesheets:~$ java -jar closure-stylesheets.jar --expand-browser-prefix \
> --output-browser-prefix example/prefix/output.css --prefixes hyphens --prefixes -ms-flex \
> --prefixes display:-ms-flexbox --prefixes position:sticky --prefixes calc --pretty-print \
> example/prefix/style.css
```

```css
.row {
  margin: 15px;
  display: flex;
  display: -ms-flexbox;
  display: -ms-inline-flexbox;
  display: inline-flex;
  flex: auto;
  -ms-flex: auto;
  hyphens: auto;
  -webkit-hyphens: auto;
  -moz-hyphens: auto;
  -ms-hyphens: auto;
  position: sticky;
  position: -webkit-sticky;
}
.col {
  height: calc(1rem - 10px);
  height: -webkit-calc(1rem - 10px);
  height: -moz-calc(1rem - 10px);
}
```

This produced the following prefixes file and output map:

<table>
<tr><th>output.css</th><th>output.css.json</th></tr>
<tr><td>

```css
.row {
  display: -webkit-box;
  display: -moz-box;
  display: -webkit-flex;
  display: -webkit-inline-box;
  display: -webkit-inline-flex;
  -webkit-box-flex: auto;
  -moz-box-flex: auto;
  -webkit-flex: auto;
}
```
</td>
<td>

```json
{
  "flex|-ms-flex": [
    "auto"
  ],
  "display": [
    "flex|-ms-flexbox",
    "inline-flex|-ms-inline-flexbox"
  ]
}
```
</td></tr>
</table>

- The `hyphens` rule was not added to the map because all of its expansions are present in the source CSS.
- The `flex|-ms-flex` were combined into a single key, so that we can check that either of those is supported (property name combination), and each of the expanded _display_ props such as _flex_ and _inline-flex_ were also combined to form `flex|-ms-flexbox` and `-ms-inline-flexbox|inline-flex` (property value combination).
- The `position:sticky` was not added to the map because it was globally prefixed. Using the script we've given above, it's possible to optimise the prefixes CSS tree loading process.
- The `calc` function was not added because it was specified in `--prefixes` flag.

For example, neither _Safari_ nor _Edge_ support `hyphens` without the `-webkit-` prefix, however _Firefox_ has supported it for a number of years already. So we can pass `--prefixes -webkit-hyphens:auto -ms-hyphens:auto` to include those in the main stylesheet, while placing the rest of the rules in the separate prefixes CSS tree that will be downloaded on demand.

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/5.svg?sanitize=true">
</a></p>

### Keyframes

Additionally, keyframe rules can be generated for webkit, by using the <a name="gen-webkit-keyframes">`@gen-webkit-keyframes`</a> comment before the rule. This was part of the compiler however not documented.

<table>
<tr><th>Source</th><th>Output</th></tr>
<tr><td>

```css
/* @gen-webkit-keyframes */
@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
.RunFadeIn {
  opacity: 0;
  animation: fadeIn 0.5s;
  animation-fill-mode: forwards;
}
```
</td>
<td>

```css
@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
@-webkit-keyframes fadeIn {
  0% {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.RunFadeIn {
  opacity: 0;
  animation: fadeIn .5s;
  animation-fill-mode: forwards;
}
```
</td></tr>
</table>

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/6.svg?sanitize=true">
</a></p>

## Minification

You can concatenate and minify a list of stylesheets with the following command:

```console
$ java -jar closure-stylesheets.jar input1.css input2.css input3.css
```

This will print the minified output to standard out. You can also specify a file
to write the output to using the **`--output-file`** option:

```console
$ java -jar closure-stylesheets.jar --output-file output.css input1.css input2.css input3.css
```

Of course, the **`>`** operator also works just as well:

```console
$ java -jar closure-stylesheets.jar input1.css input2.css input3.css > output.css
```

If you would like to create a vendor-specific stylesheet, you can use the
**`--vendor`** flag. Current recognized vendors are: **`WEBKIT`**,
**`MOZILLA`**, **`OPERA`**, **`MICROSOFT`**, and **`KONQUEROR`**. When this flag
is present, all vendor-specific properties for other vendors will be removed.

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/7.svg?sanitize=true">
</a></p>

## Linting

_Closure Stylesheets_ performs some static checks on your CSS. For example, its most basic function is to ensure that your CSS parses: if there are any parse errors, _Closure Stylesheets_ will print the errors to standard error and return with an exit code of 1.

### `--allowed-non-standard-function`, `--allow-unrecognized-functions`

It will also error out when there are unrecognized function names or duplicate
style declarations. For example, if you ran Closure Stylesheets on
**`linting.gss`**:

```css
.logo {
  width: 150px;
  height: 55px;
  background-image: urel('http://www.google.com/images/logo_sm.gif');
  border-color: #DCDCDC;
  border-color: rgba(0, 0, 0, 0.1);
}
```

Then you would get the following output:

```sh
Detected multiple identical, non-alternate declarations in the same ruleset. If this is intentional please use the /* @alternate */ annotation. border-color:[rgba(0,0,0,.1)] in example/linting.gss at line 1 column 1:
.logo {
^

Unknown function "urel" in example/linting.gss at line 4 column 21:
  background-image: urel('http://www.google.com/images/logo_sm.gif');
                    ^

2 error(s), 0 warning(s)
```

In this particular case, the function `urel()` should have been `url()`, though if you are using a function that is not on the whitelist (see
[CssFunctionNode](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/compiler/ast/CssFunctionNode.java)
for the list of recognized functions, which is admittedly incomplete), then you
can specify **`--allowed-non-standard-function`** to identify additional
functions that should be whitelisted:

```
java -jar closure-stylesheets.jar --allowed-non-standard-function urel example/linting.gss
```

The `--allowed-non-standard-function` flag may be specified multiple times.

It is also possible to disable the check for unknown functions altogether using
the **`--allow-unrecognized-functions`** flag.

Further, in this example, the multiple declarations of `border-color` are
intentional. They are arranged so that user agents that recognize `rgba()` will
use the second declaration whereas those that do not will fall back on the first
declaration. In order to suppress this error, use the `/* @alternate */`
annotation that the error message suggests as follows:

```css
.logo {
  width: 150px;
  height: 55px;
  background-image: url('http://www.google.com/images/logo_sm.gif');
  border-color: #DCDCDC;
  /* @alternate */ border-color: rgba(0, 0, 0, 0.1);
}
```

This signals that the re-declaration is intentional, which silences the
error. It is also common to use this technique with multiple `background`
declarations that use `-webkit-linear-gradient`, `-moz-linear-gradient`, etc. In
general, using [conditionals](#Conditionals.md) to select the appropriate
declaration based on user agent is preferred; however, that requires the
additional overhead of doing user agent detection and serving the appropriate
stylesheet, so using the `@alternate` annotation is a simpler solution.

On the other hand, the check for duplicates can be disabled altogether with <a name="--allow-duplicate-declarations">`--allow-duplicate-declarations`</a> flag. This can be useful when compiling some vendor CSS, for example, Bootstrap, which will on purpose write multiple declarations within the same block:

```css
abbr[title],
abbr[data-original-title] {
  text-decoration: underline;
  -webkit-text-decoration: underline dotted;
  text-decoration: underline dotted; /* duplicate */
}
button:focus {
  outline: 1px dotted;
  outline: 5px auto -webkit-focus-ring-color; /* duplicate */
}
```

### `--allow-unrecognized-properties`, `--allowed-unrecognized-property`

By default, Closure Stylesheets validates the names of CSS properties used in a
stylesheet. We have attempted to capture all legal properties in the
[hardcoded list of recognized properties](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/compiler/ast/Property.java)
that is bundled with Closure Stylesheets. However, you can allow properties that
aren't in the list with the **`--allowed-unrecognized-property`** flag. Consider
the file **`bleeding-edge.gss`**:

```css
.amplifier {
  /* A hypothetical CSS property recognized by the latest version of WebKit. */
  -webkit-amp-volume: 11;
}
```

Then running the compiler would yield the following error:

```sh
-webkit-amp-volume is an unrecognized property in example/bleeding-edge.gss at line 3 column 3:
  -webkit-amp-volume: 11;
  ^

1 error(s), 0 warning(s)
```

You can whitelist `-webkit-amp-volume` with the
**`--allowed-unrecognized-property`** flag as follows:

```
java -jar closure-stylesheets.jar \
    --allowed-unrecognized-property -webkit-amp-volume example/bleeding-edge.gss
```

Like `--allowed-non-standard-function`, `--allowed-unrecognized-property` may be
specified multiple times, once for each property to whitelist. We discourage
using the blanket `--allow-unrecognized-properties` because it lets through
everything, including simple spelling mistakes.

Note that some recognized properties will emit warnings. These warnings will not
be silenced with the `--allowed-unrecognized-property` flag.

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/8.svg?sanitize=true">
</a></p>

## RTL Flipping

Closure Stylesheets has support for generating left-to-right (LTR) as well as
right-to-left (RTL) stylesheets. By default, LTR is the assumed directionality
for both the input and output, though those settings can be overridden by
**`--input-orientation`** and **`--output-orientation`**, respectively.

For example, consider the following stylesheet, **`rtl-example.gss`**, which is
designed for an LTR page:

```css
.logo {
  margin-left: 10px;
}

.shortcut_accelerator {
  /* Keyboard shortcuts are untranslated; always left-to-right. */
  /* @noflip */ direction: ltr;
  border-right: 2px solid #ccc;
  padding: 0 2px 0 4px;
}
```

Generating the equivalent stylesheet to use on an RTL version of the page can be
achieved by running **`java -jar closure-stylesheets.jar --pretty-print
--output-orientation RTL rtl.gss`**, which prints:

```css
.logo {
  margin-right: 10px;
}
.shortcut_accelerator {
  direction: ltr;
  border-left: 2px solid #ccc;
  padding: 0 4px 0 2px;
}
```

Note how the following properties were changed:
  * **`margin-left`** became **`margin-right`**
  * **`border-right`** became **`border-left`**
  * The right and left values of **`padding`** were flipped.

However, the **`direction`** property was unchanged because of the special
**`@noflip`** annotation. The annotation may also appear on the line before the
property instead of alongside it:

```css
  /* @noflip */
  direction: ltr;
```

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/9.svg?sanitize=true">
</a></p>

## Renaming

Closure Stylesheets makes it possible to rename CSS class names in the generated
stylesheet, which helps reduce the size of the CSS that is sent down to your
users. Of course, this is not particularly useful unless the class names are
renamed consistently in the HTML and JavaScript files that use the
CSS. Fortunately, you can use the
[Closure Compiler](https://developers.google.com/closure/compiler/) to update the class
names in your JavaScript and
[Closure Templates](https://developers.google.com/closure/templates/) to update the
class names in your HTML.

To get the benefits of CSS renaming in Closure, instead of referencing a CSS
class name as a string literal, you must use that string literal as an argument
to `goog.getCssName()`:

```javascript
// Do the following instead of goog.dom.getElementByClass('dialog-content'):
var element = goog.dom.getElementByClass(goog.getCssName('dialog-content'));
```

Similarly, in a Closure Template, you must wrap references to CSS classes with
the
[css command](https://developers.google.com/closure/templates/docs/commands#css):

```html
{namespace example}

/**
 * @param title
 */
{template .dialog}
<div class=\"{css('dialog-content')}\">
  <div class=\"{css('dialog-title')}\">{$title}</title>
  {call .content data=\"all\" /}
</div>
{/template}
```

When you generate the JavaScript for the template, be sure to use the
`--cssHandlingScheme GOOG` option with `SoyToJsSrcCompiler`. This ensures that
the generated JavaScript code will also use `goog.getCssName()`. For example, if
the above were named **`dialog.soy`**, then the following command would be used
to create **`dialog.soy.js`**:

```
java -jar SoyToJsSrcCompiler.jar \\
    --shouldProvideRequireSoyNamespaces \\
    --codeStyle concat \\
    --cssHandlingScheme GOOG \\
    --outputPathFormat '{INPUT_FILE_NAME_NO_EXT}.soy.js' \\
    dialog.soy
```

The contents of the generated **`dialog.soy.js`** file are:

```javascript
// This file was automatically generated from dialog.soy.
// Please don't edit this file by hand.

goog.provide('example');

goog.require('soy');
goog.require('example');


example.dialog = function(opt_data) {
  return '<div class=\"' + goog.getCssName('dialog-content') + '\"><div class=\"' +
      goog.getCssName('dialog-title') + '\">' + soy.$$escapeHtml(opt_data.title) +
      '</title>' + example.content(opt_data) + '</div>';
};
```

Note the uses of `goog.getCssName()` in the generated JavaScript file.

Now that all references to CSS class names are wrapped in `goog.getCssName()`,
it is possible to leverage renaming. By default, `goog.getCssName()` simply
returns the argument that was passed to it, so no renaming is done unless a
_renaming map_ has been set.

When running Closure Library code without processing it with the Closure
Compiler, it is possible to set a renaming map by defining a global variable
named `CLOSURE_CSS_NAME_MAPPING` in JavaScript code that is loaded before the
Closure Library's `base.js` file. For example, if you defined your CSS in a file
named **`dialog.gss`**:

```css
.dialog-content {
  padding: 10px;
}

.dialog-title {
  font-weight: bold;
}
```

Then you would run the following command to generate a stylesheet
(**`dialog.css`**) with renamed classes, as well as the mapping data as a
JavaScript file (**`renaming_map.js`**):

```
java -jar closure-stylesheets.jar \\
    --pretty-print \\
    --output-file dialog.css \\
    --output-renaming-map-format CLOSURE_UNCOMPILED \\
    --rename CLOSURE \\
    --output-renaming-map renaming_map.js \\
    dialog.gss
```

The generated **`dialog.css`** would be as follows:

```css
.a-b {
  padding: 10px;
}
.a-c {
  font-weight: bold;
}
```

while the generated **`renaming_map.js`** would be:

```javascript
CLOSURE_CSS_NAME_MAPPING = {
  \"dialog\": \"a\",
  \"content\": \"b\",
  \"title\": \"c\"
};
```

An HTML file that uses the renaming map must be sure to include both the
generated stylesheet with renamed class names as well as the renaming map:

```html
<!doctype html>
<html>
<head>
  <link rel=\"stylesheet\" href=\"dialog.css\" type=\"text/css\">
</head>
<body>

  <script src=\"renaming_map.js\"></script>
  <script src=\"path/to/base.js\"></script>
  <script>
    goog.require('example');
  </script>
  <script>
    // Your application logic that uses example.dialog() and other code.
  </script>

</body>
</html>
```

This ensures that when **`goog.getCssName('dialog-content')`** is called, it
returns **`'a-b'`**. In this way, the abbreviated name is used in place of the
original name throughout the code.

An astute reader will note that so far, we have reduced only the size of the
stylesheet, but not the JavaScript. To reduce the size of the JavaScript code,
we must use the [Closure Compiler](https://developers.google.com/closure/compiler/) in
either
[SIMPLE or ADVANCED](https://developers.google.com/closure/compiler/docs/compilation_levels)
mode with the **`--process_closure_primitives`** flag enabled (it is enabled by
default). When enabled, if it finds a call to **`goog.setCssNameMapping()`** in
any of its inputs, it will use the argument to `goog.setCssNameMapping()` as the
basis of a renaming map that is applied at compile time. To create the
appropriate renaming map with Closure Stylesheets, use **`CLOSURE_COMPILED`** as
the argument to **`--output-renaming-map-format`**:

```
java -jar closure-stylesheets.jar \\
    --pretty-print \\
    --output-file dialog.css \\
    --output-renaming-map-format CLOSURE_COMPILED \\
    --rename CLOSURE \\
    --output-renaming-map renaming_map.js \\
    dialog.gss
```

This yields the following content for **`renaming_map.js`**:

```javascript
goog.setCssNameMapping({
  \"dialog\": \"a\",
  \"content\": \"b\",
  \"title\": \"c\"
});
```

Now **`renaming_map.js`** is a suitable input for the Closure Compiler. Recall
our original snippet of JavaScript code:

```javascript
var element = goog.dom.getElementByClass(goog.getCssName('dialog-content'));
```

If passed to the Closure Compiler in SIMPLE mode along with
**`renaming_map.js`**, it will be transformed to the following after
compilation:

```javascript
var element = goog.dom.getElementByClass(\"a-b\");
```

This achieves the goal of reducing both CSS and JS file sizes without changing
the behavior of the application.

Admittedly, using CSS renaming is a fairly advanced option that requires a
well-organized build system to ensure that the appropriate CSS and JS assets are
produced for both development and production. See MoreOnCssRenaming for more
details on this topic.

**Note:** it is also possible to exclude certain class names from being renamed
by using the **`--excluded_classes_from_renaming`** flag. This may be necessary
if some of your HTML is generated by a process that does not take CSS renaming
into account. For example, if you are using a Python Django server and are using
its template system, then any CSS classes used in those templates will not be
renamed (unless you introduce a process to do so). In order to ensure that the
JS and CSS that use the HTML reference CSS classes consistently, each CSS class
in the Django template should be passed as an argument to Closure Stylesheets
with the **`--excluded_classes_from_renaming`** flag when generating the CSS.

References to CSS class names that are excluded from renaming should _never_ be
wrapped in `goog.getCssName()`, or else they run the risk of being partially
renamed.

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/10.svg?sanitize=true">
</a></p>

## Root Selector

When generating CSS that must work only inside of specific element, the `--root-selector` option may be passed to prepend all selectors with a global selector.

```css
.EXAMPLE {
  display: inline;
}
span {
  text-align: right;
}
.test {
  color: green;
}
```

In case when the root selector is already part of the selector, it will just be skipped. Only ID and Class root selectors are supported right now.

```console
closure-stylesheets:~$ java -jar closure-stylesheets.jar --root-selector .EXAMPLE --rename \
> CLOSURE --pretty-print example/root-selector.css
```

```css
.EXAMPLE {
  display: inline;
}
.EXAMPLE span {
  text-align: right;
}
.EXAMPLE .a {
  color: green;
}
```

<p align="center"><a href="#table-of-contents">
  <img src="/.documentary/section-breaks/-1.svg?sanitize=true">
</a></p>