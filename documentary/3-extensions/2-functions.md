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

%EXAMPLE: example/functions.gss, css%

Running **`java -jar closure-stylesheets.jar --pretty-print
functions-example.gss`** will print:

<shell noconsole language="css">
java -jar closure-stylesheets.jar --pretty-print example/functions.gss
</shell>

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