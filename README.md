# A Java implementation of the HBM device discovery protocol.

[![Build Status](https://travis-ci.org/HBM/java-scan.svg?branch=master)](https://travis-ci.org/HBM/java-scan)
[![Coverity](https://scan.coverity.com/projects/5097/badge.svg)](https://scan.coverity.com/projects/5097)
[![Coverage Status](https://coveralls.io/repos/HBM/java-scan/badge.svg?branch=master)](https://coveralls.io/r/HBM/java-scan?branch=master)
[ ![Download](https://api.bintray.com/packages/hbm/java/devscan/images/download.svg) ](https://bintray.com/hbm/java/devscan/_latestVersion)

## About
This library implements the HBM device discovery protocol in  Java. The
library is 100 % pure Java and fully compatible with Android.

Both scan and configuration are implemented.

## License

Copyright (c) 2014 Hottinger Baldwin Messtechnik GmbH. See the
[LICENSE](LICENSE) file for license rights and limitations (MIT).

## Build

The preferred method to build the library is gradle. Just run
```bash
gradle build
```
and you will find the generated devscan.jar file in the folder
/tmp/devscan/devscan/libs.

The gradle script has now the ability to deploy jar files and 
pom files to [bintray](https://bintray.com/) and
[Maven Central](http://search.maven.org/). Run
```bash
gradle uploadArchives -Prelease -P<repository> -Pgpg_id=<gpg-id> -Pgpg_secring=<path/to/secring.gpg> -Pgpg_passphrase=<gpg-passphrase> -PrepositoryUsername=<name> -PrepositoryPassword=<passwd
```
with either "mavencentral" or "bintray" for `<repository>`.

In addition, a maven build is also supported. Please run
```bash
mvn package
```
for the build.

## Documentation

The generated javadoc documentation can be found
[here](http://hbm.github.io/java-scan/javadoc/).
