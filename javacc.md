# javacc

## reference

[JavaCC官方文档](https://javacc.github.io/javacc/)

## Javacc

### 如何使用 javacc

#### 最简单的例子

> 查看 `javacc` 模块下，我们有一个最简单的例子。

#### 基于源码编译

```bash
wget https://github.com/javacc/javacc/archive/javacc-7.0.9.tar.gz
tar -xvf javacc-7.0.9.tar.gz
cd javacc-javacc-7.0.9

# 编译源码
ant
chmod 755 ./scripts/javacc

./scripts/javacc 
# Java Compiler Compiler Version 7.0.9 (Parser Generator)

# 添加javacc到环境变量，随后我们可以使用
javacc braces.jj
```

#### 基于maven插件

> [JavaCC Maven Plugin](https://www.mojohaus.org/javacc-maven-plugin/#usage.html)

在maven文件中引入 javacc plugin，如下配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>test-calcite</artifactId>
        <groupId>com.xxx.calcite</groupId>
        <version>1.0</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>test-javacc</artifactId>

    <dependencies>
    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>${basedir}/src/main/resources</directory>
            </resource>
        </resources>

        <!-- find and add *.jj then generate code -->
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>javacc-maven-plugin</artifactId>
                <version>3.0.0</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <id>javacc</id>
                        <goals>
                            <goal>javacc</goal>
                        </goals>
                        <configuration>
                            <sourceDirectory>${basedir}/src/main/javacc</sourceDirectory>
                            <includes>
                                <include>**/*.jj</include>
                            </includes>
                            <lookAhead>2</lookAhead>
                            <isStatic>false</isStatic>
                            <outputDirectory>${basedir}/generated-sources/</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- add generated sources code to classpath -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>3.4.0</version>
                <executions>
                    <execution>
                        <id>add-source</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>${basedir}/src/main/java/</source>
                                <source>${basedir}/generated-sources/</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- clean generated sources when run command 'mvn clean' -->
            <plugin>
                <artifactId>maven-clean-plugin</artifactId>
                <version>2.4.1</version>
                <configuration>
                    <filesets>
                        <fileset>
                            <directory>${basedir}/generated-sources</directory>
                            <includes>
                                <include>**/*</include>
                            </includes>
                            <followSymlinks>false</followSymlinks>
                        </fileset>
                    </filesets>
                </configuration>
            </plugin>
        </plugins>

    </build>
</project>
```

这里我们指定了sourceDirectory在 `src/main/javacc` 下，我们只需要把需要编译的javacc放在这个目录下。随后执行

```bash
mvn org.codehaus.mojo:javacc-maven-plugin:2.6:javacc
```

编译的文件会存放在目录 `target/generated-sources/javacc` 下

### 一个简单的例子

> 一个用于匹配括号的简单 javacc 源码；
>
> 程序分为三个部分：
>
> 1. 由 `PARSER_BEGIN(Example)` 开始 `PARSER_END(Example)` 结束的 Example 模块，模块是纯java代码，仅仅包含一个main函数。函数内初始化了一个实例，并且调用实际的 parser 逻辑；
> 2. `Input()` 函数， Input() 表示输入应该匹配 MatchedBraces()，后跟着零个或者多个换行，随后是EOF；
> 3. `MatchedBraces()` 函数使用了递归，表示 "{" 和 "}" 之间继续可以存在 MatchedBraces()。
>
> | 输入 | 描述    |
> | ---- | ------- |
> | {}   | valid   |
> | {{}} | valid   |
> | {}{} | invalid |

```java
PARSER_BEGIN(Example)

/** Simple brace matcher. */
public class Example {

  /** Main entry point. */
  public static void main(String args[]) throws ParseException {
    Example parser = new Example(System.in);
    parser.Input();
  }

}

PARSER_END(Example)

/** Root production. */
void Input() :
{}
{
  MatchedBraces() ("\n"|"\r")* <EOF>
}

/** Brace matching production. */
void MatchedBraces() :
{}
{
  "{" [ MatchedBraces() ] "}"
}
```

