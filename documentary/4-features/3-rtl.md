### RTL Flipping

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
  border-right:\t2px solid #ccc;
  padding: 0 2px 0 4px;
}
```

Generating the equivalent stylesheet to use on an RTL version of the page can be
achieved by running **`java -jar closure-stylesheets.jar --pretty-print
--output-orientation RTL rtl-example.gss`**, which prints:

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