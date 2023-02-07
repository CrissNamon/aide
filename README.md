## About

[![Build](https://github.com/CrissNamon/aide/actions/workflows/maven.yml/badge.svg)](https://github.com/CrissNamon/aide/actions/workflows/maven.yml)
[![Releases](https://img.shields.io/github/v/release/crissnamon/aide?include_prereleases)](https://github.com/CrissNamon/aide/releases)
[![Maven](https://maven-badges.herokuapp.com/maven-central/tech.hiddenproject/aide/badge.svg)](https://central.sonatype.com/artifact/tech.hiddenproject/aide/1.2)

Aide is a set of useful utils for fast reflection, extended optionals and conditionals. It can help you with development
of some service or your own framework.

## Content

#### Reflection

Aide reflection contains utils for reflection such as fast method invocation with lambda wrapping, annotation processing
and other useful methods.

Reflective method calls with Aide are simple:

```java
// Get LambdaWrapperHolder isntance with default LambdaWrapper interface loaded
LambdaWrapperHolder lambdaWrapperHolder = LambdaWrapperHolder.DEFAULT;
// Find static method
Method staticMethod = ReflectionUtil.getMethod(TestClass.class, "staticMethod", String.class);
// Wrap static method
// LambdaWrapper - default wrapper interface from Aide
// Void - caller class. In case of static method caller is not needed, so Void
// Integer - return type
MethodHolder<LambdaWrapper, Void, Integer> staticHolder = lambdaWrapperHolder.wrapSafe(staticMethod);
// Invoke static method without caller
int staticResult = staticHolder.invokeStatic("Hello");
```

#### Optional

Aide optional contains extended optional classes for String, Boolean types, IfTrue and When conditionals, Object utils.

Extended optionals provides new methods for some types:

```java
BooleanOptional.of(Modifier.isPublic(executable.getModifiers()))
    .ifFalseThrow(() -> ReflectionException.format("Wrapping is supported for PUBLIC methods only!"));
```

With conditionals you can make your code more functional. Thats how Aide reflection uses them:

```java
AbstractSignature signature = IfTrueConditional.create()
    .ifTrue(exact).then(() -> ExactMethodSignature.from(method))
    .ifTrue(!exact).then(() -> MethodSignature.from(method))
    .orElseThrow(() -> ReflectionException.format("%s undefined!", exact));
```

Or WhenConditional:

```java
WhenConditional.create()
    .when(someObj, Objects::nonNull).then(MyClass::nonNull)
    .when(someObj, Objects::isNull).then(MyClass::isNull)
    .orDoNothing();
```

SwitchConditional too:
```java
Status status = Status.BAD_REQUEST;
String message = SwitchConditional.<Status, String>on(status)
  .caseOn(Status.BAD_REQUEST::equals).thenGet("Error: Bad request")
  .caseOn(Status.INTERNAL_ERROR::equals).thenGet("Error: Internal error")
  .orElse("");
    
assert message.equals("Error: Bad request");
    
SwitchConditional.on(status)
  // false = no break;, so all branches below will be executed
  .caseOn(Status.BAD_REQUEST::equals, false).thenDo(this::action)
  .caseOn(Status.INTERNAL_ERROR::equals).thenDo(this::action)
  .orElseDo(() -> System.out.println("No action"));
```

## Use

Artifact ids:

- `tech.hiddenproject:aide-all` - all components
- `tech.hiddenproject:aide-optional` - optionals and conditionals
- `tech.hiddenproject:aide-reflection` - reflection utils

### Maven

```xml

<dependency>
  <groupId>tech.hiddenproject</groupId>
  <artifactId>aide-all</artifactId>
  <version>1.3</version>
</dependency>
```

### Gradle

```groovy
implementation 'tech:hiddenproject:aide-all:1.2'
```

## Resources

___

* Learn more at Aide [Wiki](https://github.com/CrissNamon/aide/wiki)
* Look at some examples
  in [example](https://github.com/CrissNamon/aide/tree/main/aide-all/src/main/java/tech/hiddenproject/aide/example)
  package

## Dependencies and source

___

Aide has no dependencies and use only Java 8.

## Repository info

___

* The main branch contains stable release
* Development branch contains WIP code
* Aide is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0)

## Authors

___

* [Danila Rassokhin](https://gihub.com/crissnamon) [![Twitter](https://img.shields.io/twitter/follow/kpekepsalt_en?style=social)](https://twitter.com/kpekepsalt_en)
