# JSON support for Mybatis 3.x using Jackson 2.6.x

Provide support for JSON like field types in any Database.
I'm developed this handler with PostgreSql in mind, 
but it looks like it can be used with any other database even without json types.

Artifact does not include direct jackson dependencies - it is up to you to add them into your project.
Looks like you can use any Jackson version compatible by API with 2.6.x.

##How does it work
Because JDBC does not support JSON types, it transfer JSON to/from database as a string.
We searialize JSON to string when we are saving and deserialize from string when reading.
This feature means that we are really do not care if our DB can support JSON or not.

###Lazy reading
During reading from DB handler return TreeNode wrapper that actually does not parse JSON from string.
It is waiting for you to call any of its methods - only then it will read JSON into structure.
But this approach may lead to unexpected runtime exception in a case if your database will return
invalid JSON string.


## Adding to your project

For now this package is available only via https://jitpack.io/

I'm expecting that you are already have jackson dependencies in your project.
```
compile 'com.fasterxml.jackson.core:jackson-core:2.6.+'
compile 'com.fasterxml.jackson.core:jackson-databind:2.6.+'
```

### Gradle dependencies
```
repositories {
  maven {
    url "https://jitpack.io"
  }
}

dependencies {
  compile 'com.github.javaplugs:mybatis-jackson:0.1'
}
```

### Maven dependencies
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.javaplugs</groupId>
    <artifactId>mybatis-jackson</artifactId>
    <version>0.1</version>
</dependency>
```

## Configure
In result map configuration you should use ```javaType="com.fasterxml.jackson.core.TreeNode"```

You should not configure anything if you want to use TreeNode types as arguments in your mapper
functions, but keep in mind that handler only expect objects of type ArrayNode or ObjectNode.


### Mybatis config
```
<!-- mybatis-config.xml -->
<typeHandlers>
  <typeHandler handler="com.github.javaplugs.mybatis.TreeNodeTypeHandler"/>
</typeHandlers>
```

Or you can use package search

```
<!-- mybatis-config.xml -->
<typeHandlers>
  <package name="com.github.javaplugs.mybatis"/>
</typeHandlers>
```

### Mybatis via Spring
```
<bean id="SomeId" class="org.mybatis.spring.SqlSessionFactoryBean">
    <!-- your configuration -->
    <property name="typeHandlersPackage" value="com.github.javaplugs.mybatis" />
</bean>
```