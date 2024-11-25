# Processing Library Template
This is a template to help developers of Processing libraries to develop and release.

Please read the [documentation website](https://processing.github.io/processing-library-template/)
for more information on how to use this template.

Three important outputs are required to contribute a library to Processing, and this template provides 
help and guidance on them. They are:
1. **The library's code** - This template will build your code into a jar file with Gradle.
2. **A website for the library** - We recommend using [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/)
   to create a static website for your library. It allows you to write content for your website
   using markdown, and structure the site using a yml configuration file. We provide a skeleton
   MkDocs website as part of this template.
3. **A url that serves the release artifacts** - We have already configured Gradle tasks to create the
   release artifacts. If you host your code on Github, You can create a Github release that serves the 
   release artifacts.


References for developing libraries for Processing can be found on the following Github wiki pages:
- https://github.com/benfry/processing4/wiki/Library-Basics
- https://github.com/benfry/processing4/wiki/Library-Guidelines
- https://github.com/benfry/processing4/wiki/Library-Overview

> [!Note]
> This template is based on Gradle. If you are looking for the old Ant-based template, see [processing/processing-library-template-ant](processing/processing-library-template-ant)

## Contributors

This template was created by Claudine Chen ([@mingness](https://github.com/mingness)) as part of the 2024 New Beginnings (pr05) Grant from the 
[Processing Foundation](https://github.com/processing), to simplify the
workflows for libraries, tools, and modes, mentored by Stef Tervelde ([@Stefterv](https://github.com/stefterv)).

It is based on and inspired by a number of Processing library templates, including:
- https://github.com/processing/processing-library-template-gradle
- https://github.com/enkatsu/processing-library-template-gradle
- https://github.com/hamoid/processing-library-template/

I wish to thank the developers of these repositories, who generously provided
guidance and time. This template has been developed in collaboration with
[@enkatsu](https://github.com/enkatsu).
