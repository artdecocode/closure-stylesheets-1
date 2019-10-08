### Conditionals

Variables can be defined using conditionals with **`@if`**, **`@elseif`**, and
**`@else`**. The following is a real-world example adapted from the
[Closure Library](https://github.com/google/closure-library/blob/master/closure/goog/css/common.css),
which defines a cross-browser CSS class to apply the style **`display:
inline-block`**. The Closure Library example uses browser hacks to define
`.goog-inline-block`, but it can be done explicitly in Closure Stylesheets by
using conditionals as shown in **`conditionals.gss`**:

%EXAMPLE: example/conditionals.gss, css%

Values for the conditionals can be set via a **`--define`** flag. By default,
all conditional variables are assumed to be false, so running **`java -jar
closure-stylesheets.jar --pretty-print conditionals.gss`** will print:

<shell language="css">
java -jar closure-stylesheets.jar --pretty-print example/conditionals.gss
</shell>

whereas **`java -jar closure-stylesheets.jar --define BROWSER_FF2 --pretty-print
conditionals.gss`** will print:

<shell language="css">
java -jar closure-stylesheets.jar --define BROWSER_FF2 --pretty-print example/conditionals.gss
</shell>

It is also possible to specify the `--define` flag multiple times, so **`java
-jar closure-stylesheets.jar --define BROWSER_IE --define BROWSER_IE6
--pretty-print conditionals.gss`** will print:

<shell language="css">
java -jar closure-stylesheets.jar --define BROWSER_IE --define BROWSER_IE6 --pretty-print example/conditionals.gss
</shell>

Admittedly, to get the benefit of serving the CSS specific to a particular user
agent, one must generate a separate stylesheet for each user agent and then
serve it appropriately.

%~%