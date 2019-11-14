## Auto Expansion

There is an auto-expansion pass that adds vendor-specific directives, however it was switched off in the _Google's version_. The reason remains unknown, however it might be because it slow down the compiler.

%EXAMPLE: example/prefix.css%

This fork allows to switch it on with the `--expand-browser-prefix` flag:

<java jar="closure-stylesheets.jar" lang="css" console="closure-stylesheets">
  --expand-browser-prefix --pretty-print example/prefix.css
</java>

There is no control over which rules to expand by supplied desired browser coverage right now. All known rules will be expanded which can increase the size of the stylesheets by a lot.