### Variables

Variables can be defined in Closure Stylesheets using **`@def`** followed by a
variable name and then a value. Variables can also be defined in terms of other
variables. Consider the following file, **`variable-example.gss`**:

%EXAMPLE: example/variable.gss, css%

Running **`java -jar closure-stylesheets.jar --pretty-print
variable-example.gss`** will print:

<shell noconsole language="css">
java -jar closure-stylesheets.jar --pretty-print example/variable.gss
</shell>