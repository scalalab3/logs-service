# logs-service

[![Gitter](https://badges.gitter.im/scalalab3/logs-service.svg)](https://gitter.im/scalalab3/logs-service?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


# Base modules structure

* common
* core
* parser
* storage
* ui
* analytics


## How to switch modules in development

If you want to work on specific module (eg. storage), you have to run `project` command:

```
> project core
 [info] Set current project to core (in build file:logs-service/)
```

After that all commands (test, compile, etc) will be ran only for that module.