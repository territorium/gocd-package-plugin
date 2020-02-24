# GoCD task plugin for packaging

This is merely a skeleton plugin that plugin developers can fork to get quickly 
started with writing task plugins for GoCD. It works out of the box, but you should change 
it to do something besides executing `curl`.
 
All the documentation is hosted at https://plugin-api.gocd.io/current/tasks.



## Getting started

The packaging plugin is used to create package structures for the Qt Installer. The configuration allow different operations:

### Initialize

The initialize operation creates the local package structure for the packaging. The task requires a single configuration parameter:

- The *Module Path* defines the location, from where the structure should be copied and initialized.

### Package

The package is the principal process to create the structure of a package. The process copies the installable content to the *data* folder. Different options can be used.

- The *Module Name* defines the name of the module to package.
- The *Source Pattern* defines a path pattern from where the content should be copied. The pattern allows to use named regular expression, that can be used as parameters for the processing of the package; e.g.: *smartIO-iOS-(?&lt;VERSION&gt;[0-9.\-]+).ipa* , the ?&lt;VERSION&gt; defines the name of parameter.
- The *Target Pattern* defines a path pattern to define the target in the *data* folder. The pattern allows to use parameters as placeholders to replace parts of the path with the parameters catched from the source pattern or the environment; e.g.: *smartio-$VERSION.ipa*



### Repository

The repository process creates the structure for the remote repository. All files contained in the repository should be upload to a remote repository, from where an online installer can consume the information. The process doesn't use any option.

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
