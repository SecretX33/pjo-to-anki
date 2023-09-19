## Preparing the environment

### Java

Running Gradle might require Java 17 installed. Please install [Java 17](https://adoptium.net/temurin/releases/?version=17) if an error arises when executing any of the provided Gradle commands.

### Environment variables

This project makes use of some environment variables in order to control some things. The variables used are the following:

- `TARGET_BROWSER`: required, valid values are `firefox`, `chrome`.
    - Change this to change which browser to build to.
- `IS_DEVELOPMENT`: optional, valid values are unset, `true`, or `false`.

## Development

### Build only extension files (for manual loading on browser)
```bash
./gradlew extension:buildExtension
```

### Little tip
Be aware that there is still some funkiness with Gradle and Kotlin/JS, but most of these issues are fixed by running Gradle `clean` command.
```bash
./gradlew clean
```

## Build

### Compile

To compile the extension to an extension package (`.zip` for Chrome, `.xpi` for Firefox), run the following command. The final result will be in `extension/build/distributions/{browserName}.{browserExtension}`.

```bash
./gradlew clean extension:packageExtension
```
