# GoCD task plugin for packaging

This is merely a skeleton plugin that plugin developers can fork to get quickly 
started with writing task plugins for GoCD. It works out of the box, but you should change 
it to do something besides executing `curl`.
 
All the documentation is hosted at https://plugin-api.gocd.io/current/tasks.



## Getting started

The packaging plugin is used to create package structures for the Qt Installer. The configuration allow different operations:

- *Package*: Creates the package structure consumed by the installer
- *Repository*: Creates an online repository
- *Installer (Online/Offline)*: Creates the installer, optionally package-ing for an offline installer.


The plugin uses different environment variables, but you need to define only 1:

- *RELEASE*: Defines the release version, optionally with the version name (e.g. 20.04-dev).

Internally they will be mapped to:

- *RELEASE*: Defines the release version for the global installer (e.g. 20.04).
- *MODULE*: Defines the package name for the namespace (e.g. 2004dev), this identifies the package globally. The name must be without spaces and special characters
- *PATTERN*: Defines the version pattern MAJOR.MINOR.PACTH-BUILDNUMBER, e.g. 00.00.0, defines the major & minor with 2 digits and a patch number or 0.00.0-0 defines a major, minor and build number, where the minor has always 2 digits. The if a version name is defined the pattern is 0.00-0, otherwise the pattern 0.00.0 is used
- *PACKAGE*: Defines package name for the title in the root package.


### Package

The package is the principal process to create the structure of a package. The process copies the install-able content to the *data* folder. Different options can be used.

- *Package Path*: Defines the directory from where the package meta informations are loaded. (e.g. packages)
- *Module Name*: Defines the module name to prepare for packaging. The module name can hold contain parameters, that are replaced by environment variables, e.g. tol.$MODULE.app.web
- *Data Source Pattern*: Defines the directory or file used to copy in the data folder of the package. If the pattern declares an archive (.zip, .tar, .war, .tar.gz), the archive will be un-packed. Optionally it is possible to define a path inside the archive, if only a subset should be packaged, e.g. *download/smartIO-Web.zip!smartio*. The pattern can define named regular expression, which are provided to the environment, e.g. *smartIO-Web-(?<VERSION>[0-9.\-]+).zip* will provide the version number as *VERSION* in the environment.
- *Data Target Pattern*: Defines the relative target directory in the data folder. You can use environment variables to create the directory or file, e.g. *webapps/client/smartio-$VERSION*.

### Repository

The repository process creates the structure for the remote repository. All files contained in the repository should be upload to a remote repository, from where an online installer can consume the information. The process doesn't use any option.

- *Data Source Pattern Name*: Optionally defines a comma separated list of the modules to used for the repository. e.g. *tol.$MODULE.server_win64,tol.$MODULE.webapp,tol.$MODULE.app.web,tol.$MODULE.app.android,tol.$MODULE.app.ios*. If the option is omitted, all packges are provided.


### Installer

The installer process creates an online/offline installer (or both).

- *Config Name*: Defines the configuration file for the installer, e.g. *config/config.xml*.
- *Data Source Pattern*: Optionally defines a comma separated list of the modules to used for the installer. e.g. *tol.$MODULE.server_win64,tol.$MODULE.webapp,tol.$MODULE.app.web,tol.$MODULE.app.android,tol.$MODULE.app.ios*. If the option is omitted, all packages are provided. 
- *Data Target Pattern*: Defines the name of the installer, e.g. *Installer*.



### Assembly

The installer process creates an archive (.zip, .tar, .tar.gz) from a list of files. All files are flatten in the archive.

- *Data Source Pattern*: A comma separated list of files, that should by archived. The Pattern allows to define a file or a folder, that should be included into the assembly. Optionally following syntax is possible: path{[bin]/*.exe}. This will catch all EXE files from the *path* and include them into the archive into the folder *bin* 
- *Data Target Pattern*: The archive name, the task will recognize the archive type from the file name.


## Building the code base

To build the jar, run `./gradlew clean test assemble`

## License

```plain
Copyright 2018 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## About the license and releasing your plugin under a different license

The skeleton code in this repository is licensed under the Apache 2.0 license. The license itself specifies the terms
under which derivative works may be distributed (the license also defines derivative works). The Apache 2.0 license is a
permissive open source license that has minimal requirements for downstream licensors/licensees to comply with.

This does not prevent your plugin from being licensed under a different license as long as you comply with the relevant
clauses of the Apache 2.0 license (especially section 4). Typically, you clone this repository and keep the existing
copyright notices. You are free to add your own license and copyright notice to any modifications.

This is not legal advice. Please contact your lawyers if needed.
