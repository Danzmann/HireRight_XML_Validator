package com.hireright;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.XMLReader;


public class XML {

    public String path; //Path of this xml file
    public String dtdpath; //Path of the DTD or XSD file associated
    private int type; //It is either DTD (0) or XSD (1)

    XML(String Path, String dtdPath, int Type)
    {
        this.path = Path;
        this.dtdpath = dtdPath;
        this.type = Type;
    }
    int validator() {
        if (this.type == 0) //file is of type dtd
        {
            doctypeBuilder(); //This will put the dtd file on !DOCTYPE of the xml file, for it to be validated.
            try {

                //Validation using SAX parser.
                //SAX is slower then DOM but uses less memory.
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setValidating(true);
                factory.setNamespaceAware(true);

                SAXParser parser = factory.newSAXParser();

                XMLReader reader = parser.getXMLReader();
                reader.setErrorHandler(
                        new ErrorHandler() {
                            public void warning(SAXParseException e) throws SAXException {
                                System.out.println("WARNING : " + e.getMessage()); // do nothing
                            }

                            public void error(SAXParseException e) throws SAXException {
                                System.out.println("ERROR : " + e.getMessage());
                                throw e;
                            }

                            public void fatalError(SAXParseException e) throws SAXException {
                                System.out.println("FATAL : " + e.getMessage());
                                throw e;
                            }
                        }
                );
                reader.parse(new InputSource(this.path));
                return 1; //parsing successfull, valid!
            }
            catch (ParserConfigurationException pce)
            {
                pce.printStackTrace();
            }
            catch (IOException io)
            {
                io.printStackTrace();
            }
            catch (SAXException se)
            {
                return 0; //XML File is not valid!
            }

        }
        else //file is of type XSD
        {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setValidating(false);
                factory.setNamespaceAware(true);

                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                SAXParser parser = null;
                try {
                    factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource( this.dtdpath )}));
                    parser = factory.newSAXParser();
                }
                catch (SAXException se) {
                    System.out.println("SCHEMA : " + se.getMessage());
                    return 0; // problem in the XSD itself
                }

                XMLReader reader = parser.getXMLReader();
                reader.setErrorHandler(
                        new ErrorHandler() {
                            public void warning(SAXParseException e) throws SAXException {
                                System.out.println("WARNING: " + e.getMessage()); // do nothing
                            }

                            public void error(SAXParseException e) throws SAXException {
                                System.out.println("ERROR : " + e.getMessage());
                                throw e;
                            }

                            public void fatalError(SAXParseException e) throws SAXException {
                                System.out.println("FATAL : " + e.getMessage());
                                throw e;
                            }
                        }
                );
                reader.parse(new InputSource(this.path));
                return 1; //Parsing sucessfull, valid!
            }
            catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            }
            catch (IOException io) {
                io.printStackTrace();
            }
            catch (SAXException se){
                return 0; //XML file is not valid!
            }
        }
        return 0;
    }

    void doctypeBuilder()
    {
        try{

                DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(this.path);
                //  Create transformer
                Transformer tFormer =
                        TransformerFactory.newInstance().newTransformer();
                //  Set system id
                tFormer.setOutputProperty(
                        OutputKeys.DOCTYPE_SYSTEM, this.dtdpath);

                Source source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(this.path));
                tFormer.setOutputProperty(OutputKeys.INDENT, "yes");
                tFormer.transform(source, result);
        }
        catch (Exception e){
            e.getMessage();
        }
    }


    int countElements(String element) //Counts the elements in the file, as specified in parameter.
    {
        try {
            String filepath = this.path;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            NodeList list = doc.getElementsByTagName(element);

            return list.getLength();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
        return -1;
    }
}
