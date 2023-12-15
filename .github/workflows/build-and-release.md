# Build and Release

This pipeline is the one responsible for both building and creating a new release on Github. I felt the need to document the parameters, so in the future I don't have to dig into the pipeline code to understand what each one of them do.

It was built with the intention of being used solely on the Github Web interface with no other triggers (i.e. new commits, new tags, etc), mainly because releases are usually very infrequent and also because manual releases are way more predictable.

# Pipeline Parameters

## 1. Build Mode

There are some build mode, each one being useful in some situation, here follows a brief explanation of each one.

- ### build and release

The default build mode. It will build binaries for all OSes, then create a new release in Github.

In case the `tag` already exists, it'll be reused, which may lead to inaccurate `source-code.zip` in releases if there was a commit after the tag was created.

In case the `release` under the same `tag` already exists (release with same name), the binaries will be uploaded to this existing release. If the release already contains some of the files (matched by name and extension), they'll be overridden. Files that don't match the generated ones will not be deleted. The release note text will not be modified.

- ### build and release (force) ⚠️

A very aggressive build mode. It will build binaries for all OSes, then create a new release in Github. The difference between this and the previous is what it does with existing tags and releases.

In case the `tag` already exists, it'll be **permanently deleted** together with **all releases** using the tag, so please **BE CAREFUL**!

In case the `release` already exists (release with same name) but under a different `tag`, it won't be touched, a new `release` will be created under newly created tag. To make it clear: Github **allows** multiple releases with the same name to exist within the same project - they do not interfere with each other.

- ### build only

It will build binaries for all OSes, but not release any of them. Useful for cases where the binaries are needed but a new release is not.

## 2. Version Tag

By default, this pipeline will grab the version directly from `build.gradle.kts`, but a different version may be specified to be used instead, which will override the `build.gradle.kts` file one.

When providing a custom version tag, be aware that a new commit updating the `build.gradle.kts` version will **not** be created, and the program will *still* be built using the version in it. This custom version tag will *only* be used to create the Github tag and the release name. This option does nothing if the `Build Mode` is set to `build only`.

## 3. Release Name

The name used in the release. If not provided, the generated name will follow the pattern `v{version_tag}`. This option does nothing if the `Build Mode` is set to `build only`.
