## Auto Expansion

There is an auto-expansion pass that adds vendor-specific directives, however it was switched off in the _Google's version_. The reason remains unknown, however it might be because it slow down the compiler.

%EXAMPLE: example/prefix.css%

This fork allows to switch it on with the `--expand-browser-prefix` flag:

<java jar="closure-stylesheets.jar" lang="css" console="closure-stylesheets">
  --expand-browser-prefix --pretty-print example/prefix.css
</java>

There is no control over which rules to expand by supplied desired browser coverage right now. All known rules will be expanded which can increase the size of the stylesheets by a lot.

### Output Prefixes

To generate a separate CSS file with expanded prefixes, the `--output-browser-prefix` arg must be set to the desired location of the file. In this case, an additional JSON file with the map of properties and values that were expanded will be written to the same location but under the `.json` name. It then can be used in conjunction with the `CSS.supports` function to test whether the prefixes file is required.

<java jar="closure-stylesheets.jar" lang="css" console="closure-stylesheets">
  --expand-browser-prefix --output-browser-prefix example/prefixes.css --pretty-print example/prefix.css
</java>

As you can see, the input does not contain the expanded properties, however 2 new files were generated:

**example/prefixes.css**

%EXAMPLE: example/prefixes.css%

and **example/prefixes.css.json**

%EXAMPLE: example/prefixes.css.json%

There are 3 general cases for the map:

1. A property regardless of the value, such as `flex-flow` that will be expanded into `-[webkit|ms]-flex-flow`. The shortest value will be added to the map, because here we expand the property name.
1. A value of a property, such as `display: flex` or `display: inline-flex`. Each value will be tested against the key.
1. A value which is a function, like `calc` or `linear-gradient`. The shorted value will be selected (e.g., between `calc(3px)` and `calc(0.1855rem)`, the first one will be selected) and its property name used (e.g., `height`).

The map will only be created for unprefixed properties, because only they are expanded, i.e. `-ms-flex` is not expanded and will not be tested against because it's already in the CSS.

Now the prefixes map can be used on a page to figure out if to download the fallbacks:

```js
const entries = Object.entries(prefixes)
let supportsAll = true
if (CSS && 'supports' in CSS) {
  let nonSupported = []
  for (let i=0; i<entries.length; i++) {
    const [key, values] = entries[i]
    values.forEach((value) => {
      const s = CSS.supports(key, value)
      if (!s) {
        supportsAll = false
        nonSupported.push(`${key}: ${value}`)
      }
    })
  }
  if (nonSupported.length) {
    console.log('Browser does not support CSS properties: %s',
      nonSupported.join('\n '))
  }
} else {
  supportsAll = false
}
if (!supportsAll) {
  const link = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = 'prefixes.css'
  document.head.appendChild(link)
}
```

And a noscript version should be added to the head of the document:

```html
<html>
  <head>
    <!-- the support detection script -->
    <noscript>
      <link href="prefixes.css" rel="stylesheet">
    </noscript>
  </head>
</html>
```

### Keyframes

Additionally, keyframe rules can be generated for webkit, by using the [`@gen-webkit-keyframes`](t) comment before the rule. This was part of the compiler however not documented.

%EXAMPLE: example/keyframes.css%

<java jar="closure-stylesheets.jar" lang="css">
  --pretty-print example/keyframes.css
</java>

%~%