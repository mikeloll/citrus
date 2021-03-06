### Transform

The ***`<transform>`*** action transforms XML fragments with XSLT in order to construct various XML representations. The transformation result is stored into a test variable for further usage. The property **xml-data** defines the XML source, that is going to be transformed, while **xslt-data** defines the XSLT transformation rules. The attribute **variable** specifies the target test variable which receives the transformation result. The tester might use the action to transform XML messages as shown in the next code example:

**XML DSL** 

```xml

  <testcase name="transformTest">
      <actions>
          <transform variable="result">
              <xml-data>
                  <![CDATA[
                      <TestRequest>
                          <Message>Hello World!</Message>
                      </TestRequest>
                  ]]>
              </xml-data>
              <xslt-data>
                  <![CDATA[
                      <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                      <xsl:template match="/">
                          <html>
                              <body>
                                  <h2>Test Request</h2>
                                  <p>Message: <xsl:value-of select="TestRequest/Message"/></p>
                              </body>
                          </html>
                      </xsl:template>
                      </xsl:stylesheet>
                  ]]>
              </xslt-data>
          </transform>
          <echo>
              <message>${result}</message>
          </echo>
      </actions>
  </testcase>
    
```

The transformation above results to:

```xml

  <html>
      <body>
          <h2>Test Request</h2>
          <p>Message: Hello World!</p>
      </body>
  </html>
    
```

In the example we used CDATA sections to define the transformation source as well as the XSL transformation rules. As usual you can also use external file resources here. The transform action with external file resources looks like follows:

```xml

  <transform variable="result">
      <xml-resource file="classpath:transform-source.xml"/>
      <xslt-resource file="classpath:transform.xslt"/>
  </transform>
    
```

The Java DSL alternative for transforming data via XSTL in Citrus looks like follows:

**Java DSL designer** 

```java
@CitrusTest
public void transformTest() {
    transform()
        .source("<TestRequest>" +
                    "<Message>Hello World!</Message>" +
                "</TestRequest>")
        .xslt("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
                    "<xsl:template match=\"/\">\n" +
                    "<html>\n" +
                        "<body>\n" +
                            "<h2>Test Request</h2>\n" +
                            "<p>Message: <xsl:value-of select=\"TestRequest/Message\"/></p>\n" +
                        "</body>\n" +  
                    "</html>\n" +
                    "</xsl:template>\n" +
                "</xsl:stylesheet>")
        .result("result");
    
    echo("${result}");
    
    transform()
        .source(new ClassPathResource("com/consol/citrus/actions/transform-source.xml"))
        .xslt(new ClassPathResource("com/consol/citrus/actions/transform.xslt"))
        .result("result");
    
    echo("${result}");
}
```

**Java DSL runner** 

```java
@CitrusTest
public void transformTest() {
    transform(action ->
        action.source("<TestRequest>" +
                        "<Message>Hello World!</Message>" +
                    "</TestRequest>")
        .xslt("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
                "<xsl:template match=\"/\">\n" +
                "<html>\n" +
                    "<body>\n" +
                        "<h2>Test Request</h2>\n" +
                        "<p>Message: <xsl:value-of select=\"TestRequest/Message\"/></p>\n" +
                    "</body>\n" +
                "</html>\n" +
                "</xsl:template>\n" +
            "</xsl:stylesheet>")
        .result("result"));

    echo("${result}");

    transform(action ->
        action.source(new ClassPathResource("com/consol/citrus/actions/transform-source.xml"))
              .xslt(new ClassPathResource("com/consol/citrus/actions/transform.xslt"))
              .result("result"));

    echo("${result}");
}
```

Defining multi-line Strings with nested quotes is no fun in Java. So you may want to use external file resources for your scripts as shown in the second part of the example. In fact you could also use script languages like Groovy or Scala that have much better support for multi-line Strings.

