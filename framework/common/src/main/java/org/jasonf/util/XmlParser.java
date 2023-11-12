package org.jasonf.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

/**
 * @Author jasonf
 * @Date 2023/11/12
 * @Description 解析XML文件
 */

public class XmlParser {
    private Document doc;
    private XPath xPath;

    public XmlParser(String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);   // 禁用DTD校验
            // 禁用外部实体解析
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(ClassLoader.getSystemResourceAsStream(path));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        xPath = XPathFactory.newInstance().newXPath();
    }

    public String parse(String expression) {
        try {
            XPathExpression expr = xPath.compile(expression);
            return expr.evaluate(doc);
        } catch (XPathExpressionException ex) {
            return null;
        }
    }
}
