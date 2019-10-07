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
using conditionals as shown in **`conditionals-example.gss`**:

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
closure-stylesheets.jar --pretty-print conditionals-example.gss`** will print:

```css
.goog-inline-block {
  position: relative;
  display: inline-block;
}
```

whereas **`java -jar closure-stylesheets.jar --define BROWSER_FF2 --pretty-print
conditionals-example.gss`** will print:

```css
.goog-inline-block {
  position: relative;
  display: -moz-inline-box;
}
```

It is also possible to specify the `--define` flag multiple times, so **`java
-jar closure-stylesheets.jar --define BROWSER_IE --define BROWSER_IE6
--pretty-print conditionals-example.gss`** will print:

```css
.goog-inline-block {
  position: relative;
  display: inline;
}
```

Admittedly, to get the benefit of serving the CSS specific to a particular user
agent, one must generate a separate stylesheet for each user agent and then
serve it appropriately.