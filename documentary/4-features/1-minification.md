### Minification

You can concatenate and minify a list of stylesheets with the following command:

```console
$ java -jar closure-stylesheets.jar input1.css input2.css input3.css
```

This will print the minified output to standard out. You can also specify a file
to write the output to using the **`--output-file`** option:

```console
$ java -jar closure-stylesheets.jar --output-file output.css input1.css input2.css input3.css
```

Of course, the **`>`** operator also works just as well:

```console
$ java -jar closure-stylesheets.jar input1.css input2.css input3.css > output.css
```

If you would like to create a vendor-specific stylesheet, you can use the
**`--vendor`** flag. Current recognized vendors are: **`WEBKIT`**,
**`MOZILLA`**, **`OPERA`**, **`MICROSOFT`**, and **`KONQUEROR`**. When this flag
is present, all vendor-specific properties for other vendors will be removed.