# Build for Browser

This pipeline is the one responsible for building the extension for the specified browser. The result will be an artifact containing the compressed extension files for the specified browser.

# Pipeline Parameters

## 1. Target browser

The browser to build the extension for.

## 2. Development build

Whether the build is for development purposes or not. If set to `true`, the extension will be built with the `IS_DEVELOPMENT` flag, which will cause the generated JavaScript and CSS files to not be minified, which leads to much faster build times, but is not ideal for a final release (for obvious reasons).
