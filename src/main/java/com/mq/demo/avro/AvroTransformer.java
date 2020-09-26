package com.mq.demo.avro;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AvroTransformer {

    private static final String xml = "<?xml version='1.0' standalone='yes'?>\n" +
            "<movies>\n" +
            " <movie>\n" +
            "  <title>PHP: Behind the Parser</title>\n" +
            "  <characters>\n" +
            "   <character>\n" +
            "    <name>Ms. Coder</name>\n" +
            "    <actor>Onlivia Actora</actor>\n" +
            "   </character>\n" +
            "   <character>\n" +
            "    <name>Mr. Coder</name>\n" +
            "    <actor>El Act&#211;r</actor>\n" +
            "   </character>\n" +
            "  </characters>\n" +
            "  <plot>\n" +
            "   So, this language. It's like, a programming language. Or is it a\n" +
            "   scripting language? All is revealed in this thrilling horror spoof\n" +
            "   of a documentary.\n" +
            "  </plot>\n" +
            "  <great-lines>\n" +
            "   <line>PHP solves all my web problems</line>\n" +
            "  </great-lines>\n" +
            "  <rating type=\"thumbs\">7</rating>\n" +
            "  <rating type=\"stars\">5</rating>\n" +
            " </movie>\n" +
            "</movies>";

    private static Protocol protocol;

    static {
        try {
            InputStream stream = AvroTransformer.class.getClassLoader().getResourceAsStream("xml.avsc");
            if (stream == null) throw new IllegalStateException("xml.avsc not in classpath, please check...");

            protocol = Protocol.parse(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        System.out.println("Enter AvroTransformer.main()...");
    }

    public void transformXmlToAvro(File xmlFile, File avroFile) throws IOException, SAXException {
        Schema schema = protocol.getType("Element");

        Document doc = parse(xmlFile);
        DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);

        try (DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(schema, avroFile);
            fileWriter.append(wrapElement(doc.getDocumentElement()));
        }
    }

    public GenericRecord transformXmlToAvro(String xmlContent) throws IOException, SAXException {
        Schema schema = protocol.getType("Element");

        Document doc = parse(xmlContent);
        DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);

//        try (DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(datumWriter)) {
//            fileWriter.create(schema, avroFile);
//            fileWriter.append(wrapElement(doc.getDocumentElement()));
//
//        }

        return wrapElement(doc.getDocumentElement());
    }

    private Document parse(String xmlContent) throws IOException, SAXException {
        try {
            InputStream is = new ByteArrayInputStream(xmlContent.getBytes());
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parse(File file) throws IOException, SAXException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(file);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private GenericData.Record wrapElement(Element el) {
        GenericData.Record record = new GenericData.Record(protocol.getType("Element"));
        record.put("name", el.getNodeName());

        NamedNodeMap attributeNodes = el.getAttributes();
        List<GenericData.Record> attrRecords = new ArrayList<>();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Attr attr = (Attr) attributeNodes.item(i);
            attrRecords.add(wrapAttr(attr));
        }
        record.put("attributes", attrRecords);

        List<Object> childArray = new ArrayList<>();
        NodeList childNodes = el.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                childArray.add(wrapElement((Element) node));

            if (node.getNodeType() == Node.TEXT_NODE)
                childArray.add(node.getTextContent());
        }
        record.put("children", childArray);

        return record;
    }

    private GenericData.Record wrapAttr(Attr attr) {
        GenericData.Record record = new GenericData.Record(protocol.getType("Attribute"));

        record.put("name", attr.getName());
        record.put("value", attr.getValue());

        return record;
    }

}
