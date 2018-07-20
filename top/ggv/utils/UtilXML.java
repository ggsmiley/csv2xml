package top.ggv.utils;


import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.IOException;
//EXTRACTION XML
//SAX / XML
import java.io.StringReader;

import org.xml.sax.*;
//ATTENTION: special gastor
// import org.gastor.application.ApplicationWorkflowButton;
import org.w3c.dom.*;
import org.w3c.dom.CharacterData;

import javax.xml.parsers.*;
// RESSORTIE
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UtilXML {

	private static final boolean AffichageEtapes=false;
	private static final boolean trace_specifique=false;
	
	
	// OK ? 10/8/2016
	//
	static DocumentBuilderFactory builderFactory = null;
	static DocumentBuilder builder = null;
   
   private static void init_builder() throws Exception
   {
	if (builderFactory==null)
		{
		builderFactory = DocumentBuilderFactory.newInstance();
			
		// BOF: A QUOI SERT ?
		builderFactory.setNamespaceAware(true);
		}
	
	if (builder==null)
		builder = builderFactory.newDocumentBuilder();
   }


	
	

// OK 21/8/2017
// 26/9/2013
// VALIDITE
public static boolean validite(String contenuxml)
{
	String retour="";

	try
	{
	retour+="UtilsXML::check_dump FABRICATION DOM\n";
	
	init_builder();

	 // PARSE
		// OK 
	 Document document = builder.parse(
	 		new InputSource(new StringReader(contenuxml)));

	 // NE PAS FAIRE CE QUI SUIT (N'ANALYSE PAS LE CODAGE ET CONSIDERE UTF 8 PAR DEFAUT CE QUI EST EN REALITE SUR PC
	 //	new ByteArrayInputStream(contenu.getBytes()));
	}
	catch (Exception lexception){
		System.err.println("EXCEPTION: utilXML::validite:"+lexception.getMessage());
		return false;
		}

	// OK
	return true;	
}
// public static boolean validite(String contenuxml)



// FOnction non utilisée ?
// FIXME 11/11/2017 => voir _dtd_2_ignore
// EVOLUTION: TROP GROS 3Go
// OK ?: avec ISO 
// OK ? 2/7/2017
// FEN => DOC
public static Document getFichierXml2Doc(String _fichier_xml,
		String _dtd_2_ignore) throws Exception
{
	 
	  DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    

 docBuilder.setEntityResolver(new EntityResolver() {
	    @Override
	    public InputSource resolveEntity(String publicId, String systemId)
	            throws SAXException, IOException {
	    	
	     
	    	if (false)
	        	{
	        	return new InputSource(new StringReader(""));
	        	} 
	        	else
	           return null;
	    }
	});   
 
	    Document doc1 = docBuilder.parse(_fichier_xml);
	    
	    return doc1;

}

// OK 23/8/2017
//Extrait node => Contenu XML
public static List<Element> xml2elements(Document _document, String _xpath_expression)
{
// Extrait de nodes par xpath	
	
	List<Element> retour=new ArrayList<Element>();
	
	try
	{
	// TOUS LES TAGS AVEC REGLE
	XPath xpath = XPathFactory.newInstance().newXPath();
	
	XPathExpression expr = xpath.compile(_xpath_expression) ; 
	NodeList nodes  = (NodeList) expr.evaluate(_document, XPathConstants.NODESET);
	  
	  for (int indice = 0; indice < nodes.getLength(); indice++)
	  	{
		 Node node=nodes.item(indice);
		 
		 if (node.getNodeType() == Node.ELEMENT_NODE)
		  retour.add((Element) node);
	  	}
	}
	catch (Exception eee)
	{
	System.err.println("EXCEPTION Escalade.verifyArbre"+eee.getMessage());	
		
	return retour;
	}

return retour;
}


public static boolean deleteTagsWithVoidContent(Document _document) throws Exception
{
boolean suppression=false;

while (deleteTagsWithVoidContentPourIteration(_document))
	{	
	System.out.println(">>> ITERATION");
	suppression=true;
	}

return suppression;	
}

// OK 10/4/2018
// Suppression des balises vides
private static boolean deleteTagsWithVoidContentPourIteration(Document _document) throws Exception
{
	boolean suppression=false;
	
	System.out.println("======== SUPPRESSION ==========");
	{
	// NODES
	List<Element> tous_noeuds=UtilXML.xml2elements(_document, "//*");
	
	for (Element un_noeud: tous_noeuds)
		{
		// SI ELEMENT
		// if (un_noeud.getNodeType() == Node.ELEMENT_NODE)
			{
			Element lelement=(Element) un_noeud;
			
			String contentTrim=un_noeud.getTextContent().trim();
		    
		    // SI VIDE => SUPPRESSION
		    if (contentTrim.length()==0)
				    {
			    	Node parent=un_noeud.getParentNode();
			    	
			    	parent.removeChild(un_noeud);
			    	suppression=true;
				    }
	
				}
				// if (un_noeud instanceof Element)
		}
		// for (Node un_noeud: tous_noeuds)
	}

	return suppression;
}



//CONTENU EXACT
// MERCI A 
// https://stackoverflow.com/questions/12191414/node-gettextcontent-is-there-a-way-to-get-text-content-of-the-current-node-no
public static String getFirstLevelTextContent(Node node) {
    NodeList list = node.getChildNodes();
    StringBuilder textContent = new StringBuilder();
    for (int i = 0; i < list.getLength(); ++i) {
        Node child = list.item(i);
        if (child.getNodeType() == Node.TEXT_NODE)
            textContent.append(child.getTextContent());
    }
    return textContent.toString();
}


// TODO: A TESTER 
// NODE => attributs
//
public static Map<String,String> node2attributs(Node _node)
{
	Map<String,String> retour=new HashMap<String,String>();
	

	 // ATTRIBUTS
     NamedNodeMap attributs = _node.getAttributes();
     
     if (attributs==null) return retour;
     
     for (int posattr = 0; posattr < attributs.getLength(); posattr++)
 		{
  		Attr attribute = (Attr) attributs.item(posattr);
     	// QU'ON AFFICHE
     	attribute.getName();
     	String lenom=attribute.getNodeName();
     	String lavaleur=attribute.getNodeValue();
     //	System.out.println("     ATTRIBUT ("+lenom+")="+lavaleur);
        retour.put(lenom, lavaleur);
 		}
	
	return retour;
}




}

