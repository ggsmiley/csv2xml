package top.ggv.utils;


/**
 * 
 */

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


/**
 * @author bchild
 *
 */
public class XPathUtils {
	
	static final boolean tracage=false;


	
	    // ATTENTION => IL s'agit des noeuds #text
	    // POUR AVOIR L'ELEMENT QUI LA PORTE => PRENDRE LE PERE
	    // 
	    // OK 21/1/2015
	    /**
	     * @param _doc document XML initial
	     * @return  Le Hashmap Chemin / Valeur
	     */
	    public static Map<Node,String> getNodesTextXPath(Document _doc)
	    {
	    	Map<Node,String> lemap=new LinkedHashMap<Node,String>();

	    	Element root = _doc.getDocumentElement();
	    	getNodesTextXPath(lemap, "/"+root.getNodeName()+"[1]", root );
	    	
	    	return lemap;
	    }
	    // 	    Map<Node,String> getNodesXPath(Document _doc)

	    
	    // 13/4/2018
	    public static Map<Node,String> getNodesXPath(Document _doc)
	    {
	    	Map<Node,String> lemap=new LinkedHashMap<Node,String>();

	    	Element root = _doc.getDocumentElement();
	    	getNodesXPath(lemap, "/"+root.getNodeName()+"[1]", root, false);
	    	
	    	return lemap;
	    }
	    // 	    Map<Node,String> getNodesXPath(Document _doc)
	    
	    
	 
	    
	    // OK
	    // VERSION RECURSIVE
	    public static void getNodesTextXPath(Map<Node,String> _map, String parentXPath, Element parent)
	    {
	       getNodesXPath(_map,parentXPath,parent,true);
	    }
	    
	    
	    
	    // OK
	    // VERSION RECURSIVE
	    public static void getNodesXPath
	    	(Map<Node,String> _map, String parentXPath, Element parent,
	    			boolean _seulement_text)
	    {
	        NamedNodeMap attrs = parent.getAttributes();
	        
	        String lasuite="";
	        for( int i = 0; i < attrs.getLength(); i++ ) 
	        	{
	            Attr attr = (Attr)attrs.item( i );
	               lasuite=lasuite+"[@"+attr.getName()+"='"+attr.getValue()+"']";
	        	}
	        
	     
	        HashMap<String, Integer> nameMap = new HashMap<String, Integer>();
	        
	        NodeList children = parent.getChildNodes();
	        
	    
	        boolean keep_node_text=true;
	        
	        for( int i = 0; i < children.getLength(); i++ )
        	{
            Node child = children.item( i );
            if( child instanceof Element ) 
            	{
            	keep_node_text=false;
            	break;
            	}
        	}
	        
	        // ATTENTION: NODE DE TYPE ELEMENT
	        // => DONC N'EST PAS SORTI EN TANT QUE TEXTE !
	        // MODIF 29/2/2016
	        // SI PAS D'ENFANT => vide ?
	        if (children.getLength()==0)
	        	{
	        	//System.out.println("SOLO:"+parentXPath+lasuite);
	        	_map.put(parent, parentXPath+lasuite);
	        	}
	        
	        for( int i = 0; i < children.getLength(); i++ )
	        	{
	            Node child = children.item( i );
	            
	            if( child instanceof Text ) 
            	{
            	if ((keep_node_text)
            		)// (!_seulement_text))
            	
            	{
	                // ?: escape child value
	            	_map.put((Node) child, parentXPath+lasuite); // +"='"+((Text)child).getData()+"'" );
	            	}
	            	
            	
            	} 
            
            else if( child instanceof Element ) 
            	{
                String childName = child.getNodeName();
                Integer nameCount = nameMap.get( childName );
                nameCount = nameCount == null ? 1 : nameCount + 1;
                nameMap.put( child.getNodeName(), nameCount );
                
                String cettesuite="/"+childName+"["+nameCount+"]";
                
            	if (!_seulement_text)
	            	{
	            	_map.put((Node) child, parentXPath+cettesuite); 
	            	//System.out.println("STOCKAGE : "+ parentXPath+cettesuite+"=>"+child.getNodeType());
	            	}
	            	
                getNodesXPath( _map, parentXPath+cettesuite, (Element)child,
                		_seulement_text);
            	}
	            
	        }
	    }
	    // 	    Map<Node,String> getNodesXPath(Document _doc)
	    
	
	    
	    
	    
	    
	    
	    
}