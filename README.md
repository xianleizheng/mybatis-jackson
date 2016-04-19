# JSON support for Mybatis 3.x using Jackson 2.6.x

Provide support for JSON like field types in any Database.
I'm developed this handler with PostgreSql in mind, 
but it looks like it can be used with any other database even without JSON support.

Artifact does not include direct jackson dependencies - it is up to you to add them into your project.
Looks like you can use any Jackson version compatible with API version >= 2.6.0.

[![Release](https://jitpack.io/v/javaplugs/mybatis-jackson.svg)](https://jitpack.io/#javaplugs/mybatis-jackson)  
[API javadoc](https://jitpack.io/com/github/javaplugs/mybatis-jackson/-SNAPSHOT/javadoc/)

##How does it work
Because JDBC does not support JSON types, it transfer JSON to/from database as a string.
It serialize JSON to string on save and deserialize from string on read.
This feature means that we are really do not care if our DB can support JSON or not.

###Lazy reading
Type handler returns TreeNode wrapper that actually does not parse JSON from string.
It is waiting for you to call any of its methods - only then it will read JSON into structure.
But this approach may lead to **unexpected runtime exception** in a case if your database will return
invalid JSON string.

##Add to your project

You can add this artifact to your project using [JitPack](https://jitpack.io/#javaplugs/mybatis-jackson).  
All versions list, instructions for gradle, maven, ivy etc. can be found by link above.

To get latest commit use -SNAPSHOT instead version number.

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