# logs-service

[![Gitter](https://badges.gitter.im/scalalab3/logs-service.svg)](https://gitter.im/scalalab3/logs-service?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/scalalab3/logs-service.svg?branch=master)](https://travis-ci.org/scalalab3/logs-service)

Tasks description [here](https://github.com/scalalab3/logs-service/wiki).

# Base modules structure

* common
* core
* parser
* storage
* ui
* analytics
* tests


## How to switch modules in development

If you want to work on specific module (eg. storage), you have to run `project` command:

```
> project core
 [info] Set current project to core (in build file:logs-service/)
```

After that all commands (test, compile, etc) will be ran only for that module.


## Testing

You can see some example tests in `tests/src/test/scala/com/github/scallab3/logs/common.scala`

Consult with [structure doc](https://etorreborre.github.io/specs2/guide/SPECS2-3.7.2/org.specs2.guide.Structure.html) and [matchers doc](https://etorreborre.github.io/specs2/guide/SPECS2-3.7.2/org.specs2.guide.Matchers.html) to write matchers.

To run all tests ensure that you're in `project main`.

`~test` will run watcher that execute test after each change in source code.

## Build jar and run

```
$ sbt main/assembly
$ java -jar target/scala-2.11/main-assembly-0.0.1.jar
```

Or jush run `./run.sh` in project root directory