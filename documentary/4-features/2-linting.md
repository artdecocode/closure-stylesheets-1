### Linting

_Closure Stylesheets_ performs some static checks on your CSS. For example, its most basic function is to ensure that your CSS parses: if there are any parse errors, _Closure Stylesheets_ will print the errors to standard error and return with an exit code of 1.

#### `--allowed-non-standard-function`, `--allow-unrecognized-functions`

It will also error out when there are unrecognized function names or duplicate
style declarations. For example, if you ran Closure Stylesheets on
**`linting.gss`**:

%EXAMPLE: example/linting.gss, css%

Then you would get the following output:

<java jar="closure-stylesheets.jar" stderr>
  --pretty-print example/linting.gss
</java>

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

On the other hand, the check for duplicates can be disabled altogether with [`--allow-duplicate-declarations`](####) flag. This can be useful when compiling some vendor CSS, for example, Bootstrap, which will on purpose write multiple declarations within the same block:

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

#### `--allow-unrecognized-properties`, `--allowed-unrecognized-property`

By default, Closure Stylesheets validates the names of CSS properties used in a
stylesheet. We have attempted to capture all legal properties in the
[hardcoded list of recognized properties](https://github.com/google/closure-stylesheets/blob/master/src/com/google/common/css/compiler/ast/Property.java)
that is bundled with Closure Stylesheets. However, you can allow properties that
aren't in the list with the **`--allowed-unrecognized-property`** flag. Consider
the file **`bleeding-edge.gss`**:

%EXAMPLE: example/bleeding-edge.gss, css%

Then running the compiler would yield the following error:

<java jar="closure-stylesheets.jar" stderr>
  example/bleeding-edge.gss
</java>

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