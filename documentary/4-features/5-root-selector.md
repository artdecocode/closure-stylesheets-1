### Root Selector

When generating CSS that must work only inside of specific element, the `--root-selector` option may be passed to prepend all selectors with a global selector.

%EXAMPLE: example/root-selector.css%

In case when the root selector is already part of the selector, it will just be skipped.

<java jar="closure-stylesheets.jar" lang="css" console="closure-stylesheets">
  --root-selector .EXAMPLE --rename CLOSURE --pretty-print example/root-selector.css
</java>