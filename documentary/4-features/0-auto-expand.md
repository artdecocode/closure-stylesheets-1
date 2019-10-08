### Auto Expansion

There is an auto-expansion pass that adds vendor-specific directives, however it was switched off in the _Google's version_. The reason remains unknown, however it might be because it lead to unexpected changes in code.

%EXAMPLE: example/prefix.css%

This fork allows to switch it on with the `--expand-browser-prefix` flag:

<shell language="css">
java -jar closure-stylesheets.jar --expand-browser-prefix --pretty-print example/prefix.css
</shell>

There is no control over which rules to expand by supplied desired browser coverage right now. All known rules will be expanded which can increase the size of the stylesheets by a lot.

Additionally, keyframe rules can be generated for webkit, by using the [`@gen-webkit-keyframes`](t) comment before the rule. This was part of the compiler however not documented.

%EXAMPLE: example/keyframes.css%

<shell noconsole language="css">
java -jar closure-stylesheets.jar --pretty-print example/keyframes.css
</shell>

%~%