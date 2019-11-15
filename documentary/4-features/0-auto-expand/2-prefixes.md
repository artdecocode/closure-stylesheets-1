### Specific Prefixes

When creating a separate output for prefixes, it is possible to have control over which rules are actually kept in the output CSS, and which are put into the external prefixes CSS. This is done with the `--prefix` flag. It can be of four kinds:

1. A generic property name, such as `hyphens`, which will keep all expanded prefixes for the `hyphens` property name in the output CSS.
1. An expanded, prefixed property name, e.g., `-ms-flex` that will keep that rule in the output.
1. A property name with a generic value that will keep all expansions of that property-value pair, such as `position:sticky`.
1. A property name with an expanded value to specify the exact expansions that should be preserved, e.g., `display:-ms-flexbox`.

The map will be generated accordingly with the account of rules already present in the output CSS. Consider the following style:

%EXAMPLE: example/prefix/style.css%

<java jar="closure-stylesheets.jar" lang="css" console="closure-stylesheets">
  --expand-browser-prefix --output-browser-prefix example/prefix/output.css --prefixes hyphens --prefixes -ms-flex --prefixes display:-ms-flexbox --prefixes position:sticky --pretty-print example/prefix/style.css
</java>

This produced the following prefixes file and output map:

<table>
<tr><th>output.css</th><th>output.css.json</th></tr>
<!-- block-start -->
<tr><td>

%EXAMPLE: example/prefix/output.css%
</td>
<td>

%EXAMPLE: example/prefix/output.css.json%
</td></tr>
</table>

The `hyphens` rule was not added to the map because all of its expansions are present in the source CSS. The `flex|-ms-flex` were combined into a single key, so that we can check that either of those is supported (property name combination), and each of the expanded _display_ props such as _flex_ and _inline-flex_ were also combined to form `flex|-ms-flexbox` and `-ms-inline-flexbox|inline-flex` (property value combination). The `position:sticky` was not added to the map because it was globally prefixed. Using the script we've given above, it's possible to optimise the prefixes CSS tree loading process.

For example, neither _Safari_ nor _Edge_ does not support `hyphens` without the `-webkit-` prefix, however _Firefox_ has supported it for a number of years already. So we can pass `--prefixes -webkit-hyphens:auto -ms-hyphens:auto` to include those in the main stylesheet, while placing the rest of the rules in the separate prefixes CSS tree that will be downloaded on demand.

%~%