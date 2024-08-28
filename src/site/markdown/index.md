<div class="note"></div>
Documentation for Legacy Version is located in [TAF User Documentation](https://taf.seli.wh.rnd.internal.ericsson.com/userdocs/Latest/tools/cli-command-helper.html) <br/>

<div class="note"></div>
All examples below are located in [cli-tool repository](https://gerrit.ericsson.se/#/admin/projects/OSS/com.ericsson.de/cli-tool), `cli-tool/src/itest/java/com/ericsson/de/tools/examples/`.

# Javadocs located here

* [JavaDocs](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs)

# What is Cli Tool?

Cli Tool is a new convenience tool to replace current `CliCommandHelper`.
 
The purpose of this tool is:

* Solve performance issues of Cli Command Helper
* Simplify API usage

# Cli Tool Structure

New Cli Tool consists of the three "sub tools":

<img src="images/cli-tool-2-structure.png" />

* Local Cli Tool - allows to execute commands on the current host (localhost)
* Cli Tool Shell - allows to execute commands on the remote host via SSH
* Simple Executor - remote command execution via ssh (Stateless)

For more information on the additional `CliTool` and `CliTooShell` functionality please see API documentation links below:

* [CliTool](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/CliTool.html)
* [CliToolShell](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/CliToolShell.html)

# Features

Local Cli Tool features:

* Local command execution

Cli Tool Shell features:

* Stateful
* Full support of Shell features

Simple Executor features:

* Stateless (Does not store session specific state)
* Executing standalone commands
* More performant than Cli Tool Shell

# Getting started

## Get CLI Tool

You can get CLI Tool by adding maven dependency

```xml
<dependency>
    <groupId>com.ericsson.de</groupId>
    <artifactId>cli-tool</artifactId>
    <version>${version}</version>
</dependency>
```

or you can use CLI Tool TAF adaptor where [TAF specific features](#Using CLI Tool With TAF Host Object) are supported

```xml
<dependency>
    <groupId>com.ericsson.de</groupId>
    <artifactId>cli-tool-adaptor</artifactId>
    <version>${version}</version>
</dependency>
```

How to use Cli Tool API you can see below.

## Instantiation

Instantiation steps

<img src="images/cli-tool-instantiation.png" />

**NOTE:** Instances of Cli Tool are provided by builders.

There are three builders:

* [LocalCliToolBuilder](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/LocalCliToolBuilder.html) - build instance of Local Cli Tool
* [SshShellBuilder](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/SshShellBuilder.html) - build instance of Cli Tool Shell
* [SimpleExecutorBuilder](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/SimpleExecutorBuilder.html) - build instance of  Simple Executor

To simplify instantiation you can use [CliTools](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/CliTools.html) factory class which returns specified builder

### Local Cli Tool

<!-- MACRO{snippet|id=LOCAL_CLI_TOOL_INSTANTIATION|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/LocalCliToolExamples.java} -->

### Cli Tool Shell

<!-- MACRO{snippet|id=CLI_TOOL_SHELL_INSTANTIATION_USING_HOSTNAME|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

### Simple Executor

<!-- MACRO{snippet|id=SIMPLE_EXECUTOR_INSTANTIATION_USING_HOSTNAME|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/SimpleExecutorExamples.java} -->

## Executing commands

### Local Cli Tool:

<!-- MACRO{snippet|id=LOCAL_CLI_TOOL_COMMAND_EXECUTION|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/LocalCliToolExamples.java} -->

### Simple Executor

<!-- MACRO{snippet|id=SIMPLE_EXECUTOR_COMMAND_EXECUTION|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/SimpleExecutorExamples.java} -->

### Cli Tool Shell

#### Simple commands

<!-- MACRO{snippet|id=CLI_TOOL_SHELL_COMMAND_EXECUTION|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

#### Interactive commands

Interactive commands should be used when user input required. When specifying a WaitCondition you can also specify a timeout value.

<!-- MACRO{snippet|id=CLI_TOOL_SHELL_INTERACTIVE_COMMAND_EXECUTION|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

#### Buffered shell

Buffered shell should be used when buffered output is required:

<!-- MACRO{snippet|id=CLI_TOOL_SHELL_BUFFERED_SHELL|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->


## Getting result

Each command execution method returns command result. There are two result types:

* `CliCommandResult` 
* `CliIntermediateResult`

In the table below you can see the correspondence between methods and result types:

Method              | Result Type
--------------------|------------
`CliTool.execute()` | `CliCommandResult`
`CliToolShell.execute()` | `CliCommandResult`
`CliToolShell.writeLine()` | `CliIntermediateResult`

### CliCommandResult

`CliCommandResult` object provides:

* output
* exit code
* execution time

<!-- MACRO{snippet|id=SIMPLE_EXECUTOR_COMMAND_RESULT|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/SimpleExecutorExamples.java} -->
<!-- Adding a comment line for E2C FEM testing -->
### CliIntermediateResult

`CliIntermediateResult` object provides only output:

<!-- MACRO{snippet|id=CLI_INTERMEDIATE_RESULT|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

### Closing connection

You can close a connection by calling method `close()`

<!-- MACRO{snippet|id=CLOSING_CONNECTION|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/SimpleExecutorExamples.java} -->

**IMPORTANT:** Don't forget to close connection, otherwise resource leaks will occur.

## Complete Examples

### Executing simple command using Simple Executor

For the example we will incorporate the steps mentioned above into a one sequential flow. We will create a shell instance, execute a command, return the ouputs and then close the shell session using the `CliTool.close()`

<!-- MACRO{snippet|id=SIMPLE_EXECUTOR_COMPLETE_EXAMPLE|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/SimpleExecutorExamples.java} -->

### Executing a script with user input required

The following example will shows a simple bash script (`test.sh`), which prompts the user for three inputs: Name, Age and Date of Birth.

<!-- MACRO{snippet|file=cli-tool/src/itest/resources/scripts/cli-tool-complete-example.sh} -->

You can execute the script above providing user input in the following way:

<!-- MACRO{snippet|id=SHELL_COMPLETE_EXAMPLE|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

For more information on the additional `CliToolShell` functionality please see the API documentation link below:

[CliToolShell](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/CliToolShell.html)

# Hops and Tunnels

<img src="images/tunnel-hop.png" />

## CliHostHopper

[CliHostHopper](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/CliHostHopper.html) is a convenience class which contains a single overloaded method `hop`. This method allows the user to establish SSH connection onto a host:

<!-- MACRO{snippet|id=HOST_HOPPER_ONE_LEVEL|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

The previous code snippet is equivalent to executing the following shell command:

```bash
ssh user@hostname
```

It is possible to make multiple 'hops':

<!-- MACRO{snippet|id=HOST_HOPPER_MULTIPLE_HOP|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/CliToolShellExamples.java} -->

**IMPORTANT:** Tunneling is **NOT** supported in `CliToolShell`

For more information on the additional details of `CliHostHopper` see API documentation link below:

[CliHostHopper](https://taf.seli.wh.rnd.internal.ericsson.com/cli-tool/apidocs/com/ericsson/de/tools/cli/CliHostHopper.html)

## Tunneling

Tunneling is available only for the Simple Executor.

Example below demonstrates how to establish connection via tunnel:

<!-- MACRO{snippet|id=TUNNELING_EXAMPLE|file=cli-tool/src/itest/java/com/ericsson/de/tools/cli/examples/SimpleExecutorExamples.java} -->

**IMPORTANT:** `withTunnelHost()` can be used only **ONCE**, otherwise value will be reassigned

**IMPORTANT:** 'Hops' are **NOT** supported in Simple Executor

**IMPORTANT:** Only **ONE** level of tunneling is supported

#Using CLI Tool With TAF Host Object

It is possible to use CLI Tool with [Host](https://taf.seli.wh.rnd.internal.ericsson.com/userdocs/Latest/handlers_and_executors/data_handler.html) object using `cli-tool-adaptor`.

`cli-tool-adaptor` takes as input [Host](https://taf.seli.wh.rnd.internal.ericsson.com/userdocs/Latest/handlers_and_executors/data_handler.html).

In the table below you can see the correspondence between `cli-tool` and `cli-tool-adaptor`

CLI Tool class              | Adaptor class
----------------------------|------------
`CliTools` | `TafCliTools`
`SimpleExecutorBuilder` | `TafSimpleExecutorBuilder`
`SshShellBuilder` | `TafSshShellBuilder`
`CliToolShell` | `TafCliToolShell`
`CliHostHopper` | `TafCliHostHopper`

# Additional sources

* Cli Tool [presentation](https://confluence-nam.lmera.ericsson.se/display/TAF/TAF+CLI+Tool+2+workshop)
* Cli Tool [KOANS](https://gerrit.ericsson.se/#/admin/projects/OSS/com.ericsson.cifwk/ERICtaf_koans)

You can find Cli Tools koans under the following path:

```xpath
ERICtaf_koans\cli-tool-workshop
```
