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

%EXAMPLE: example/mixin-simple.gss, css%

Running **`java -jar closure-stylesheets.jar --pretty-print
mixin-simple-example.gss`** prints:

<java jar="closure-stylesheets.jar" lang="css">
  --pretty-print example/mixin-simple.gss
</java>

Mixins are even more compelling when you consider using them to abstract away
cross-browser behavior for styles such as gradients:

%EXAMPLE: example/mixin.gss, css%

The above is compiled to:

<java jar="closure-stylesheets.jar" lang="css">
  --pretty-print example/mixin.gss
</java>

See the section on [linting](#linting) for more details on the
**`@alternate`** annotation.