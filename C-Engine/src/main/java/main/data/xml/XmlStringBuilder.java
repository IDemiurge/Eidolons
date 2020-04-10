package main.data.xml;

public class XmlStringBuilder {
    StringBuilder builder = new StringBuilder();


    public XmlStringBuilder close(String nodeName) {
        builder.append(XML_Converter.closeXmlFormatted(nodeName));
        return this;
    }

    public XmlStringBuilder open(String nodeName) {
        builder.append(XML_Converter.openXmlFormatted(nodeName));
        return this;
    }

    public void appendNode(String contents, String nodeName) {
        builder.append(XML_Converter.wrap(nodeName, contents));
    }

    public XmlStringBuilder append(String s) {
        builder.append(s);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public XmlStringBuilder append(Integer integer) {
        builder.append(integer);
        return this;
    }
}
