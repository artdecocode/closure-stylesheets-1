### Keyframes

Additionally, keyframe rules can be generated for webkit, by using the [`@gen-webkit-keyframes`](t) comment before the rule. This was part of the compiler however not documented.

<table>
<tr><th>Source</th><th>Output</th></tr>
<!-- block-start -->
<tr><td>

%EXAMPLE: example/keyframes.css%
</td>
<td>

<java jar="closure-stylesheets.jar" lang="css">
  --pretty-print example/keyframes.css
</java>
</td></tr>
</table>

%~%