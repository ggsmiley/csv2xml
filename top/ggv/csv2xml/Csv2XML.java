package top.ggv.csv2xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

//import org.gastor.utils.UtilOpenOffice;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

import top.ggv.csv2xml.Csv2XML.ReferenceCSV;

import org.w3c.dom.CharacterData;


public class Csv2XML
{
static final boolean trace=false;
static final boolean trace_motif=false;	

// FIXME: NE PAS TOUCHER 7/4/2018 => SINON, LES VIDES REAPPARAISSENT (isNonVide)
static final boolean trace_transformation=true;


// Racine codes
String code_racine="";

public Csv2XML(String _code_racine)
{
code_racine=_code_racine;	
}

	
// OK 22/3/2018
public static List<Node> XMLFile2elementsPremierNiveau(String _fichier_xml) throws Exception
{
return XMLReader2elementsPremierNiveau(new FileReader(_fichier_xml));
}

//OK 22/3/2018
public static List<Node> XMLString2elementsPremierNiveau(String _chaine_xml) throws Exception
{
return XMLReader2elementsPremierNiveau(new StringReader(_chaine_xml));
}

// OK 23/3/2018
public static List<Node> XMLReader2elementsPremierNiveau(Reader _reader) throws Exception
{
Document document = XMLReader2Document(_reader);
	
return XMLDocument2elementsPremierNiveau(document);
}

//OK 23/3/2018
public static Document XMLReader2Document(Reader _reader) throws Exception
{
	DocumentBuilderFactory builderFactory =DocumentBuilderFactory.newInstance();
	builderFactory.setNamespaceAware(true);
	DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
	// PARSE
	Document document = builder.parse(new InputSource(_reader));
	
return document;
}

// OK 22/3/2018
// Découpage XML => Détection d'éléments racine
//public static List<Node> XMLReader2elementsRacine(Reader _reader) throws Exception
public static List<Node> XMLDocument2elementsPremierNiveau(Document _document) throws Exception
{
	List<Node> retour=new ArrayList<Node>();
	
	System.out.println("==========================================");


	XPath xPath = XPathFactory.newInstance().newXPath();
	//read a string value
	String expression = "//sequence/*[@URI]";

	expression="//*";
	
	// NIVEAU 1
	expression="/*/*";
	
	NodeList nodes  = (NodeList)  xPath.compile(expression).evaluate(_document, XPathConstants.NODESET);
	

	if (trace_transformation)
		  System.out.println("Csv2XML.XMLDocument2elementsPremierNiveau ELEMENTS FOUND:"+nodes.getLength());
	
	for(int i=0; i<nodes.getLength(); i++)
	{
	 Node the_node = nodes.item(i);
	 
	 retour.add(the_node);
	 
	}
	
	return retour;
}

// CARACTERES SPECIAUX POUR ReferenceCSV

public final static char	REFERENCECSV_CARACTERE_MARQUEUR_LISTE_MAP='#';
public final static char 	REFERENCECSV_CARACTERE_MARQUEUR_CDATA='$';
public final static char	REFERENCECSV_CARACTERE_MARQUEUR_VIDE_INTERDIT='*';	


// CARACTERE: PRENDRE EN COMPTE SI NON VIDE
// ! ^ & ~ ? 
public final static char	REFERENCECSV_CARACTERE_MARQUEUR_VIDE_EXCLURE='?';	

// 
public final static char REFERENCECSV_CARACTERE_MARQUEUR_NIVEAU_ARBITRAIRE='^';


// OK 22/3/2018
// Pointeur
public class ReferenceCSV
{

	
boolean valide2 = false;
int indice_fichier = 0;
int indice_colonne = 0;
boolean marqueur_liste2 = false;

boolean marqueur_mapping2 = false;
int indice_mapping = 0; // Liste de mapping 1 => A, 2 => B, ...

boolean marqueur_cdata = false;

boolean marqueur_vide_interdit=false; // avec ce marqueur a true, on ne doit pas créer si le champ est vide

// OK ? 30/6/2018
boolean marqueur_vide_exclu=false; // si le champ est vide, on ne prend pas (différent du précédent)

boolean marqueur_niveau_arbitraire=false;


public void setValide()
{
valide2=true;	
}

public boolean isValide()
{
return valide2;
}

public void setListe()
{
marqueur_liste2 = true;	
}

public boolean isListe()
{
return marqueur_liste2;
}

public void setMapping(int _indice_mapping)
{
	marqueur_mapping2 = true;	
	indice_mapping = _indice_mapping;
}

public boolean isMapping()
{
return marqueur_mapping2;
}

public int getIndiceMapping()
{
return indice_mapping;
}

public boolean isVideInterdit()
{
return marqueur_vide_interdit;
}

public void setVideInterdit()
{
marqueur_vide_interdit = true;
}

public boolean isVideExclu()
{
return marqueur_vide_exclu;
}


public void setNiveauArbitraire()
{
	marqueur_niveau_arbitraire = true;
}

public boolean isNiveauArbitraire()
{
return marqueur_niveau_arbitraire;
}

// OK 18/7/2018
// OK 30/6/2018
// OK 24/3/2018
public String toString()
{
return
		"-----------\n"+
		"indice_fichier:"+indice_fichier+"\n"
		+"indice_colonne:"+indice_colonne+"\n"
		+"marqueur_liste:"+isListe()+"\n"
		+"marqueur_mapping:"+isMapping()+" "+indice_mapping+"\n"
		+"marqueur_cdata:"+marqueur_cdata+"\n"
		+"marqueur_vide_interdit:"+marqueur_vide_interdit+"\n"
		+"marqueur_vide_exclu:"+marqueur_vide_exclu+"\n"
		+"marqueur_niveau_arbitraire:"+marqueur_niveau_arbitraire+"\n"
		+"-----------\n"
		;
}

public int getIndiceFichier()
{
return indice_fichier;
}

public int getIndiceColonne()
{
return indice_colonne;
}
}

// REVU 18/7/2018 : ajout ^: niveau arbitraire
// REVU 30/6/2018: ajout ? : indique que le champs doit être exclu si vide (différent du point suivant)
// REVUE 30/3/2018: ajout * : indique que le champ doit être non vide (sinon ligne non prise en compte)
// REVUE 24/3/2018
//OK 22/3/2018
public ReferenceCSV fromExpression(String _expression_brute)
{
	
	String _expression=_expression_brute;
	
	if (trace_motif)
		System.out.println("EXPRESSION:"+_expression);
	
	// RETOUR
	ReferenceCSV refcsv=new ReferenceCSV();
	
	// MOTIF SANS #, sans rien
	// _F1_C3
	{
	String motif_sans_diese="^"+code_racine+"_F(\\d+)_C(\\d+)$";

	if (trace_motif)
		System.out.println("MOTIF:"+motif_sans_diese);

	    Pattern p=Pattern.compile(motif_sans_diese,Pattern.CASE_INSENSITIVE);

	    Matcher matcher = p.matcher(_expression);
	    if (matcher.find())
	    	{
	    	if (trace_motif)
		    	{
		    	System.out.println("BINGO : Le texte \"" + matcher.group() +
		                "\" débute à " +matcher.start() + " et termine à " + matcher.end());
		    	System.out.println("GROUP 1:["+matcher.group(1)+"]");
		    	System.out.println("GROUP 2:["+matcher.group(2)+"]");
		    	}
	    	
	    	refcsv.setValide();
	    	refcsv.indice_fichier=Integer.parseInt(matcher.group(1));
	    	refcsv.indice_colonne=Integer.parseInt(matcher.group(2));
	    	
	    	return refcsv;
	    	}
	}
	
	    // MOTIF AVEC * : NON VIDE, identique sinon au précédent + le marqueur
	 // ATTENTION: le * est escapé: d'où le \\ précédent
	    {
		String motif_vide_interdit="^"+code_racine+"_F(\\d+)_C(\\d+)\\"+REFERENCECSV_CARACTERE_MARQUEUR_VIDE_INTERDIT+"$";

		if (trace_motif)
			System.out.println("MOTIF VIDE INTERDIT:"+motif_vide_interdit);

		    Pattern p_vide_interdit=Pattern.compile(motif_vide_interdit,Pattern.CASE_INSENSITIVE);

		    Matcher matcher_vide_interdit = p_vide_interdit.matcher(_expression);
		    if (matcher_vide_interdit.find())
		    	{
		    	if (trace_motif)
			    	{
			    	System.out.println("BINGO : Le texte \"" + matcher_vide_interdit.group() +
			                "\" débute à " +matcher_vide_interdit.start() + " et termine à " + matcher_vide_interdit.end());
			    	System.out.println("GROUP 1:["+matcher_vide_interdit.group(1)+"]");
			    	System.out.println("GROUP 2:["+matcher_vide_interdit.group(2)+"]");
			    	}
		    	
		    	refcsv.setValide();
		    	refcsv.indice_fichier=Integer.parseInt(matcher_vide_interdit.group(1));
		    	refcsv.indice_colonne=Integer.parseInt(matcher_vide_interdit.group(2));
		    	
		    	refcsv.marqueur_vide_interdit=true;
		    	return refcsv;
		    	}
	    }
	    
	    // OK ? 30/6/2018
	    // MOTIF AVEC * : NON VIDE, identique sinon au précédent + le marqueur
	 // ATTENTION: le ? est escapé: d'où le \\ précédent
	    {
		String motif_vide_exclu="^"+code_racine+"_F(\\d+)_C(\\d+)\\"+REFERENCECSV_CARACTERE_MARQUEUR_VIDE_EXCLURE+"$";

		if (trace_motif)
			System.out.println("MOTIF VIDE EXCLU:"+motif_vide_exclu);

		    Pattern p_vide_exclu=Pattern.compile(motif_vide_exclu,Pattern.CASE_INSENSITIVE);

		    Matcher matcher_vide_exclu = p_vide_exclu.matcher(_expression);
		    if (matcher_vide_exclu.find())
		    	{
		    	if (trace_motif)
			    	{
			    	System.out.println("BINGO : Le texte \"" + matcher_vide_exclu.group() +
			                "\" débute à " +matcher_vide_exclu.start() + " et termine à " + matcher_vide_exclu.end());
			    	System.out.println("GROUP 1:["+matcher_vide_exclu.group(1)+"]");
			    	System.out.println("GROUP 2:["+matcher_vide_exclu.group(2)+"]");
			    	}
		    	
		    	refcsv.setValide();
		    	refcsv.indice_fichier=Integer.parseInt(matcher_vide_exclu.group(1));
		    	refcsv.indice_colonne=Integer.parseInt(matcher_vide_exclu.group(2));
		    	
		    	refcsv.marqueur_vide_exclu=true;
		    	return refcsv;
		    	}
	    }
	    
	      
	    
	    
	    // TENTATIVE AVEC DIESE SIMPLE : liste
		// _F1_C3#
	    {
		String motif_avec_diese="^"+code_racine+"_F(\\d+)_C(\\d+)"+REFERENCECSV_CARACTERE_MARQUEUR_LISTE_MAP+"$";

		if (trace_motif)
			System.out.println("MOTIF:"+motif_avec_diese);

		    Pattern p2=Pattern.compile(motif_avec_diese,Pattern.CASE_INSENSITIVE);

		    Matcher matcher2 = p2.matcher(_expression);
		    if (matcher2.find())
		    	{
		    	if (trace_motif)
			    	{
			    	System.out.println("BINGO : Le texte \"" + matcher2.group() +
			                "\" débute à " +matcher2.start() + " et termine à " + matcher2.end());
			    	System.out.println("GROUP 1:["+matcher2.group(1)+"]");
			    	System.out.println("GROUP 2:["+matcher2.group(2)+"]");
			    	}
		    	
		    	refcsv.setValide();
		    	refcsv.indice_fichier=Integer.parseInt(matcher2.group(1));
		    	refcsv.indice_colonne=Integer.parseInt(matcher2.group(2));
		    	// A cause du #
		    	refcsv.setListe();
		    	
		    	return refcsv;
		    	}
	    }   
		    
		    // TENTATIVE AVEC DIESE MULTIPLE : mapping
			// _F1_C3#1 
	    {
			String motif_avec_diese_multiple="^"+code_racine+"_F(\\d+)_C(\\d+)"+REFERENCECSV_CARACTERE_MARQUEUR_LISTE_MAP+"(\\d+)$";

			if (trace_motif)
				System.out.println("MOTIF:"+motif_avec_diese_multiple);

			    Pattern p3=Pattern.compile(motif_avec_diese_multiple,Pattern.CASE_INSENSITIVE);

			    Matcher matcher3 = p3.matcher(_expression);
			    if (matcher3.find())
			    	{
			    	if (trace_motif)
				    	{
				    	System.out.println("BINGO : Le texte \"" + matcher3.group() +
				                "\" débute à " +matcher3.start() + " et termine à " + matcher3.end());
				    	System.out.println("GROUP 1:["+matcher3.group(1)+"]");
				    	System.out.println("GROUP 2:["+matcher3.group(2)+"]");
				    	System.out.println("GROUP 3:["+matcher3.group(3)+"]");
				    	}
			    	
			    	refcsv.setValide();
			    	refcsv.indice_fichier = Integer.parseInt(matcher3.group(1));
			    	refcsv.indice_colonne = Integer.parseInt(matcher3.group(2));
			    	// A cause du #
			    	refcsv.setMapping(Integer.parseInt(matcher3.group(3)));
			    	
			    	return refcsv;
			    	}

	    }
	    
			  // TENTATIVE CDATA  
			// _F1_C3$
	    {
			    // ATTENTION: le $ est escapé: d'où le \\ précédent
			String motif_avec_cdata="^"+code_racine+"_F(\\d+)_C(\\d+)\\"+REFERENCECSV_CARACTERE_MARQUEUR_CDATA+"$";

			if (trace_motif)
				System.out.println("MOTIF:"+motif_avec_cdata);

			    Pattern p4=Pattern.compile(motif_avec_cdata,Pattern.CASE_INSENSITIVE);

			    Matcher matcher4 = p4.matcher(_expression);
			    if (matcher4.find())
			    	{
			    	if (trace_motif)
				    	{
				    	System.out.println("BINGO : Le texte \"" + matcher4.group() +
				                "\" débute à " + matcher4.start() + " et termine à " +  matcher4.end());
				    	System.out.println("GROUP 1:["+ matcher4.group(1)+"]");
				    	System.out.println("GROUP 2:["+ matcher4.group(2)+"]");
				    	}
			    	
			    	refcsv.setValide();
			    	refcsv.indice_fichier=Integer.parseInt( matcher4.group(1));
			    	refcsv.indice_colonne=Integer.parseInt( matcher4.group(2));
			    	// A cause du $
			    	refcsv.marqueur_cdata = true;
			    	
			    	return refcsv;
			    	}
	    }    
	    
	    
	    // TENTATIVE NIVEAU ARBITRAIRE
		  // TENTATIVE CDATA  
		// _F1_C3^
  {
		    // ATTENTION: le ^ est escapé: d'où le \\ précédent
		String motif_avec_niveau_arbitraire="^"+code_racine+"_F(\\d+)_C(\\d+)\\"+REFERENCECSV_CARACTERE_MARQUEUR_NIVEAU_ARBITRAIRE+"$";

		if (trace_motif)
			System.out.println("MOTIF:"+motif_avec_niveau_arbitraire);

		    Pattern p5=Pattern.compile(motif_avec_niveau_arbitraire,Pattern.CASE_INSENSITIVE);

		    Matcher matcher5 = p5.matcher(_expression);
		    if (matcher5.find())
		    	{
		    	if (trace_motif)
			    	{
			    	System.out.println("BINGO : Le texte \"" + matcher5.group() +
			                "\" débute à " + matcher5.start() + " et termine à " +  matcher5.end());
			    	System.out.println("GROUP 1:["+ matcher5.group(1)+"]");
			    	System.out.println("GROUP 2:["+ matcher5.group(2)+"]");
			    	}
		    	
		    	refcsv.setValide();
		    	refcsv.indice_fichier=Integer.parseInt( matcher5.group(1));
		    	refcsv.indice_colonne=Integer.parseInt( matcher5.group(2));
		    	// A cause du $
		    	refcsv.setNiveauArbitraire();
	    	
		    	return refcsv;
		    	}
  }    

		    
		//    ECHEC !!!
		return refcsv;
}


// FICHIERS CSV

// Ok 23/3/2018
public class TableauValeurs
{
private List<List<String>> valeurs=new ArrayList<List<String>>();


public List<List<String>> getValeurs()
{
return valeurs;
}

public void setValeurs(List<List<String>>  _valeurs)
{
valeurs=_valeurs;
}

public String toString()
	{
	return
			"Lignes:"+valeurs.size();
	}

// OK 23/3/2018
public int countLignes()
{
return valeurs.size();
}

// MAX COLONNES
// 14/4/2018
public int getMaxCol()
{
int maxcol=0;

for (List<String> ligne: getValeurs())
	{
	int nbrcol=ligne.size();
	if (nbrcol>maxcol) maxcol=nbrcol;
	}

return maxcol;
}

public List<String> getLigne(int _indice)
{
return valeurs.get(_indice);	
}

public void addLigne(List<String> _nouvelle_ligne)
{
valeurs.add(_nouvelle_ligne);
}

//OK 23/3/2018
// Valeurs
public String getElementAt(int _ligne, int _colonne) throws Exception
{
return valeurs.get(_ligne).get(_colonne);
}

public boolean compareCommente(TableauValeurs tv2)
{
return compareInterne(tv2,true);
}

public boolean compare(TableauValeurs tv2)
{
return compareInterne(tv2,false);
}


// OK 27/3/2018
// COMPARAISON
private boolean compareInterne(TableauValeurs tv2, boolean avec_commentaires)
{
boolean result=true;

try
{
	int indiceligne=0;
	for (List<String> ligneX: getValeurs())
	{
	if (tv2.countLignes()<=indiceligne) 
		{
			if (avec_commentaires)
				System.out.println("ICI: "+countLignes()+" lignes, AUTRE: "+tv2.countLignes()+" lignes:\n");
		break;
		}
		
	for (int k=0;k<ligneX.size();k++)
		{
		String lu_ici=ligneX.get(k);
		String lu_autre=tv2.getElementAt(indiceligne, k);
		
		String lu_ici_sans_cr=utilCSV.removeCarriageReturn(lu_ici);
		String lu_autre_sans_cr=utilCSV.removeCarriageReturn(lu_autre);
		
		if (!lu_ici_sans_cr.contentEquals(lu_autre_sans_cr))
				{
				if (avec_commentaires)
					System.out.println("ERREUR ("+indiceligne+","+k+"):\n"
						+"ICI:"+lu_ici
						+"\nAUTRE:"+lu_autre
						+"\n------------\n");
				result=false;
				}
		}
	indiceligne++;
	}
}
catch (Exception eee)
	{
	if (avec_commentaires)
	System.out.println("EXCEPTION:"+eee.getMessage());
	return false;	
	}

return result;
}

}

// REVU 27/3/2018
// OK ? 23/3/2018
public TableauValeurs fromFichierCSV(String _nom_fichier_csv, char _separateur, boolean _sans_en_tete)
{
	TableauValeurs retour=new TableauValeurs();
	
	//List<List<String>> lignes=CSV2Lignes(_nom_fichier_csv,_separateur, _sans_en_tete);
	
	List<List<String>> lignes=utilCSV.lignes(_nom_fichier_csv,_separateur, _sans_en_tete);
	
	retour.setValeurs(lignes);
	
	return retour;
}

// OK 14/4/2018
public static boolean tableauValeurstoFichierCSV(TableauValeurs _tableau, String _nom_fichier_csv, 
		char _separateur, boolean _sans_en_tete)
{
	List<List<String>> lignes=_tableau.getValeurs();
	
	
		try
		{
		String fEncoding="UTF-8";
		Writer out = new OutputStreamWriter(new FileOutputStream(_nom_fichier_csv), fEncoding);	
		    try {
		    	boolean premiere_ligne=true;
		    	for (List<String> une_ligne: lignes)
		    		{
		    		if ((!premiere_ligne) || (!_sans_en_tete))
		    			{
		    			boolean debut_ligne=true;
		    			
			    		for (String un_element: une_ligne)
			    			{
			    			if (!debut_ligne)
			    				out.write(_separateur);
			    			
			    			out.write("\""+escapeGuillemetsPourSortieCSV(un_element)+"\"");
			    			
			    			debut_ligne=false;
			    			}
			    		
			    		out.write("\n");
		    			}
		    		
		    		premiere_ligne=false;
		    		}
		    }
		    
		    finally {
		      out.close();
		    		}
		}
		
		    catch (Exception ex)
						{
				 		System.err.println("EXCEPTION Guidage.exportToCSV :"+ex.getMessage());
				 		//ex.printStackTrace();
				    	}

	return false;
}

// OK 14/4/2018
public static String escapeGuillemetsPourSortieCSV(String _source)
{
String retour="";

retour=_source.replace("\"", "\"\"");

return retour;
}



// OK ? 24/3/2018
public TableauValeurs fromListListString(List<List<String>> _listliststring)
{
	TableauValeurs retour=new TableauValeurs();
		
	retour.setValeurs(_listliststring);
	
	return retour;
}


// ATTENTION AUX BIBLIOTHEQUES: PRENDRE TIKA COMPLET (ou rien)
// OK 22/3/2018
public static String node2XMLString(Node the_node)
{
	try
	{
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();

			StringWriter buffer = new StringWriter();
			
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
			 
			transformer.transform(new DOMSource(the_node),
			      new StreamResult(buffer));
			String sortie = buffer.toString();

			return sortie;
	}
	catch (Exception eee)
	{
		System.err.println("Csv2XML.node2XML EXCEPTION:"+eee.getMessage());
		return "";
	}
}

// OK 22/3/2018
// NOEUD => TOUS LES SOUS-NOEUDS
public static List<Node> node2InsideNodesRecursif(Node node_racine)
{
	List<Node> retour=new ArrayList<Node>();
	
	try
	{
	XPath xPath = XPathFactory.newInstance().newXPath();
	String expression="//*";
	
	// TOUS NIVEAUX SOUS LE NOEUD
	expression=".//*";
	
	NodeList nodes  = (NodeList)  xPath.compile(expression).evaluate(node_racine, XPathConstants.NODESET);

	if (trace_transformation)
	  System.out.println("Csv2XML.node2InsideNodes ELEMENTS FOUND:"+nodes.getLength());
	
	for(int i=0; i<nodes.getLength(); i++)
		{
		 Node the_node = nodes.item(i);
		 retour.add(the_node);
		}
	
	}
	catch (Exception eee)
	{
	System.err.println("EXCEPTION Csv2XML.node2InsideNodes"+eee.getMessage());
	}
	
	return retour;
}


//NOEUD => TOUS LES SOUS-NOEUDS DE PREMIER NIVEAU
public static List<Node> node2ChildNodes(Node node_racine)
{
	List<Node> retour=new ArrayList<Node>();
	
	try
	{
	XPath xPath = XPathFactory.newInstance().newXPath();
	String expression="//*";
	
	// TOUS NIVEAUX SOUS LE NOEUD
	expression="./*";
	
	NodeList nodes  = (NodeList)  xPath.compile(expression).evaluate(node_racine, XPathConstants.NODESET);

	if (trace_transformation)
		  System.out.println("Csv2XML.node2ChildNodes ELEMENTS FOUND:"+nodes.getLength());
	
	for(int i=0; i<nodes.getLength(); i++)
		{
		 Node the_node = nodes.item(i);
		 retour.add(the_node);
		}
	
	}
	catch (Exception eee)
	{
	System.err.println("EXCEPTION Csv2XML.node2InsideNodes"+eee.getMessage());
	}
	
	return retour;
}

// OK 24/3/2018
//REMPLACEMENTS SOUS-NOEUDS ET SORTIE
public void DocumentTransformedWithCSV_virgule(Document _document, 
		Csv2XML _csv2xml,
		List<String> _fichiers_CSV) throws Exception
{
	// Fichiers CSV =>  TABLEAUX
	List<TableauValeurs> tableaux=new ArrayList<TableauValeurs>();
	
	for (String _un_fichier: _fichiers_CSV)
		tableaux.add(fromFichierCSV(_un_fichier, ',', true));
	
	DocumentTransformedWithTableauxValeurs(_document,_csv2xml,tableaux);
}

public void DocumentTransformedWithCSV_point_virgule(Document _document, 
		Csv2XML _csv2xml,
		List<String> _fichiers_CSV) throws Exception
{
	// Fichiers CSV =>  TABLEAUX
	List<TableauValeurs> tableaux=new ArrayList<TableauValeurs>();
	
	for (String _un_fichier: _fichiers_CSV)
		tableaux.add(fromFichierCSV(_un_fichier, ';', true));
	
	DocumentTransformedWithTableauxValeurs(_document,_csv2xml,tableaux);
}

// OK 7/4/2018
public void DocumentTransformedWithODS(Document _document, 
		Csv2XML _csv2xml,
		String _nom_fichier_ODS) throws Exception
{
	// Fichiers CSV =>  TABLEAUX
	List<TableauValeurs> tableaux=
			OOO2XML.fichierODS2TableauxValeurs(_nom_fichier_ODS);
			
	DocumentTransformedWithTableauxValeurs(_document,_csv2xml,tableaux);
}


// 18/7/2018 => avec niveau arbitraire
// OK 24/3/2018
// REMPLACEMENTS SOUS-NOEUDS ET SORTIE
public void DocumentTransformedWithTableauxValeurs(Document _document, 
		Csv2XML _csv2xml,
		List<TableauValeurs> _tableaux) throws Exception
{
	// Noeuds Racines
	List<Node> noeuds_premier_niveau=XMLDocument2elementsPremierNiveau(_document);
	
	int nbr_noeuds_racine=noeuds_premier_niveau.size();

	if (trace_transformation)
		{
		System.out.println("===============================");
		System.out.println("DTWTV A1 Noeuds:"+nbr_noeuds_racine);
		}
	
	if (nbr_noeuds_racine==0)
		throw new Exception("DTWTV A2 0 LIGNE");
	
	// PARCOURS DE TOUS LES NOEUDS RACINE
	for (Node noeud_premier_niveau: noeuds_premier_niveau)
		{
		if (trace_transformation)
			{
			System.out.println("===============================");
			System.out.println("DTWTV B1 NOEUD:\n"+Csv2XML.node2XMLString(noeud_premier_niveau));
			System.out.println("-------------------------------");
			System.out.println("DTWTV B2 CONTENU VALUE:"+ noeud_premier_niveau.getNodeValue());
			System.out.println("-------------------------------");
			System.out.println("DTWTV B3 TEXTE CONTENT:"+ noeud_premier_niveau.getTextContent());
			System.out.println("-------------------------------");
			}
		
		
		// EST-CE QUE CE NOEUD EST DUPLICABLE ?
		if (estDuplicable(noeud_premier_niveau))
			{
			int indicefichier=getDuplicablePremierIndiceFichierTrouve(noeud_premier_niveau);

			if (trace_transformation)
				{
				System.out.println("DTWTV C1 Duplicable fichier"+indicefichier);
				System.out.println("-------------------------------");
				}
			
			// Toutes lignes
			// L'indice commence à 1
			TableauValeurs tableau=_tableaux.get(indicefichier-1);
			int nbr_lignes=tableau.countLignes();
			if (trace_transformation)
				System.out.println("DTWTV C2 FICHIER "+indicefichier+":NOMBRE DE LIGNES:"+nbr_lignes);

			boolean doit_etre_supprime_a_la_fin=false;
			
			// Y A T-IL DES NON NULS
			boolean unObligatoire=existeParametreFilsOuAttributsObligatoireNonVide(noeud_premier_niveau);
			List<String> lesobligatoiresnonvides=listParametresFilsOuAttributsObligatoiresNonVides(noeud_premier_niveau);
			
			for (int indice_ligne=0;indice_ligne<nbr_lignes;indice_ligne++)
				{
				List<String> ligne_traitee=tableau.getLigne(indice_ligne);
				
				if (trace_transformation)
					{
					System.out.println("-------------------------------");
					System.out.println("DTWTV D0 Traitement ligne "+indice_ligne+" =>"
							+ligne_traitee.toString());
					System.out.println("-------------------------------");
					}
				
				// Y A T-IL DES NON NULS
				if (unObligatoire)
					{
					boolean obligatoireNonFourni=false;
					for (String unobligatoire:lesobligatoiresnonvides)
						{
						ReferenceCSV refcsv=fromExpression(unobligatoire);
						int lacolonne=refcsv.getIndiceColonne();
						String valeur=ligne_traitee.get(lacolonne-1);
						if (valeur.contentEquals(""))
							obligatoireNonFourni=true;
						}
					
					if (obligatoireNonFourni)
						{
						if (trace_transformation)
						{
						System.out.println("OBLIGATOIRE NON FOURNI "+lesobligatoiresnonvides.toString()+": LIGNE SAUTEE");
						continue;
						}
					}
					// 	if (unObligatoire)

					}
					// if (unObligatoire)
				
				try
				{
				Node nouveau_noeud=declinaisonNoeudNiveauPremierCloneEtAppend(noeud_premier_niveau,
						ligne_traitee);
				
				if (trace_transformation)
					System.out.println("===============================");
				if (trace_transformation)
					System.out.println("DTWTV D1 NOEUD DECLINE:\n"+node2XMLString(nouveau_noeud));
				
				doit_etre_supprime_a_la_fin=true;
				}
				catch (Exception eee)
					{
					if (trace_transformation)
						System.out.println("DTWTV E1 MAUVAISE LIGNE "+indice_ligne+":"+eee.getMessage());
					System.err.println("DTWTV E1 MAUVAISE LIGNE "+indice_ligne+":"+eee.getMessage());
					eee.printStackTrace();
					
					}
				//_document.appendChild(nouveau_noeud);
				}
			
			if (doit_etre_supprime_a_la_fin)
				removeNoeudPrototype(noeud_premier_niveau);
			
			}
			// 		if (estDuplicable(noeud_premier_niveau))
		
		
		else
			// EST-CE QUE CE NOEUD EST DUPLICABLE AVEC UN NIVEAU ARBITRAIRE ?
			if (estDuplicableNiveauArbitraire(noeud_premier_niveau))
			{

				System.out.println("DTWTV D4 NOEUD DUPLICABLE NIVEAU ARBITRAIRE "+noeud_premier_niveau.getLocalName());
				System.out.println("-------------------------------");
				
				List<Node> sousnoeudsrecursifs=Csv2XML.node2InsideNodesRecursif(noeud_premier_niveau);
				for (Node sousnoeud_duplicable_niveau_arbitraire: sousnoeudsrecursifs) 
					{
					System.out.println("DTWTV D5 SOUS-NOEUD ["+sousnoeud_duplicable_niveau_arbitraire.getLocalName()+"]");
					
					if (_csv2xml.isContentReferenceCSVDuplicableNiveauArbitraire
								(sousnoeud_duplicable_niveau_arbitraire))
						{
						System.out.println("DTWTV D6 SOUS-NOEUD REPRODUCTIBLE NIVEAU ARBITRAIRE ["+sousnoeud_duplicable_niveau_arbitraire.getLocalName()+"]");
							
						// Le prendre, et le dupliquer dedans ?

						String contenu_sous_noeud_duplicable = node2ExpressionCandidate(sousnoeud_duplicable_niveau_arbitraire);
						ReferenceCSV refcsv_sous_noeud_duplicable=fromExpression(contenu_sous_noeud_duplicable);
						int indicefichier=refcsv_sous_noeud_duplicable.getIndiceFichier();
					
						if (trace_transformation)
							{
							System.out.println("DTWTV E1 Duplicable fichier"+indicefichier);
							System.out.println("-------------------------------");
							}
						
						// Toutes lignes
						// L'indice commence à 1
						TableauValeurs tableau=_tableaux.get(indicefichier-1);
						int nbr_lignes=tableau.countLignes();
						if (trace_transformation)
							System.out.println("DTWTV E2 FICHIER "+indicefichier+":NOMBRE DE LIGNES:"+nbr_lignes);

						boolean doit_etre_supprime_a_la_fin=false;
						
						for (int indice_ligne=0;indice_ligne<nbr_lignes;indice_ligne++)
							{
							List<String> ligne_traitee=tableau.getLigne(indice_ligne);
							
							if (trace_transformation)
								{
								System.out.println("-------------------------------");
								System.out.println("DTWTV E3 Traitement ligne "+indice_ligne+" =>"
										+ligne_traitee.toString());
								System.out.println("-------------------------------");
								}
							
							try
							{
							Node nouveau_noeud=declinaisonNoeudNiveauArbitraireCloneEtAppend(
									sousnoeud_duplicable_niveau_arbitraire,
									ligne_traitee);
							
							if (trace_transformation)
								System.out.println("===============================");
							if (trace_transformation)
								System.out.println("DTWTV E4 NOEUD DECLINE:\n"+node2XMLString(nouveau_noeud));
							
							doit_etre_supprime_a_la_fin=true;
							}
							catch (Exception eee)
								{
								if (trace_transformation)
									System.out.println("DTWTV E10 MAUVAISE LIGNE "+indice_ligne+":"+eee.getMessage());
								System.err.println("DTWTV E11 MAUVAISE LIGNE "+indice_ligne+":"+eee.getMessage());
								eee.printStackTrace();
								
								}
							//_document.appendChild(nouveau_noeud);
							}

						if (doit_etre_supprime_a_la_fin)
							removeNoeudPrototype(sousnoeud_duplicable_niveau_arbitraire);
								
						}
					}
					// 				for (Node sousnoeud: sousnoeuds) 
			
			
			}
		
		else
			{
			if (trace_transformation)
				System.out.println("DTWTV C3 NOT Duplicable");
			}

		}
		// 	for (Node nd: noeuds)
	
	
	if (trace_transformation)
		System.out.println("===============================");
	if (trace_transformation)
		System.out.println("DTWTV K1 TOTAL APRES:\n"+Csv2XML.node2XMLString(_document));
}


// OK 23/3/2018
public boolean estDuplicable(Node _noeud_premier_niveau)
{
// Si contient un code à l'intérieur
List<Node> nodesinside=node2InsideNodesRecursif(_noeud_premier_niveau);
	
for (Node node_inferieur: nodesinside)
	{
	if (isContentReferenceCSVDuplicable(node_inferieur)) return true;

	if (isAttributesReferenceCSV(node_inferieur)) return true;
	}

// Echec
return false;
}



public boolean estDuplicableNiveauArbitraire(Node _noeud_premier_niveau)
{
// Si contient un code à l'intérieur
List<Node> nodesinside=node2InsideNodesRecursif(_noeud_premier_niveau);
	
for (Node node_inferieur: nodesinside)
	{
	if (isContentReferenceCSVDuplicableNiveauArbitraire(node_inferieur)) return true;
	}

// Echec
return false;
}




// 25/3/2018 => NE SERT PLUS A RIEN ?
// Duplicable en dessous => Transformable
public boolean estSousDuplicableListeOuMapping(Node _noeud_premier_niveau)
{

	if (trace_transformation)
		{
		System.out.println("===============================");
		System.out.println("estSousDuplicableListeOuMapping");
		}
	
// Si contient un code à l'intérieur
List<Node> nodesinside=node2InsideNodesRecursif(_noeud_premier_niveau);
	
if (trace_transformation)
	System.out.println("estSousDuplicableListeOuMapping noeuds inside:"+nodesinside.size());

for (Node node_inferieur: nodesinside)
	{
	if (isContentReferenceCSVSousDuplicable(node_inferieur)) return true;
	}

// Echec
return false;
}


// OK 25/3/2018
public boolean isContentReferenceCSVDuplicable(Node _node)
{
	String contenu = node2ExpressionCandidate(_node);
	
	ReferenceCSV refcsv=fromExpression(contenu);
	if (((refcsv.isValide()))
		
		//	&& ((!refcsv.isListe()) && (!refcsv.isMapping())))
		
			// 18/7/2018
			&& (!refcsv.isNiveauArbitraire()))
		return true;
		
	else
	return false;
}

public boolean isContentReferenceCSVDuplicableNiveauArbitraire(Node _node)
{
	String contenu = node2ExpressionCandidate(_node);
	
	ReferenceCSV refcsv=fromExpression(contenu);
	if (((refcsv.isValide()))
		
			&& (refcsv.isNiveauArbitraire()))
		return true;
		
	else
	return false;
}




// OK 24/3/2018
public boolean isAttributesReferenceCSV(Node _node)
{
	
	NamedNodeMap nnm=_node.getAttributes();
	for (int i = 0; i < nnm.getLength(); ++i)
		{
	    Node attr = nnm.item(i);
	    String nodename=attr.getNodeName();
	    //   String nodevalue=attr.getNodeValue().trim();
	    String nodevalue=Value2ExpressionCandidate(attr);
	    
	    if (trace_transformation)
	    	System.out.println("Attribute:" + nodename + " = \"" + nodevalue + "\"");
		
		// Valeur OK ?
	    ReferenceCSV refcsvattribute=fromExpression(nodevalue);
		if (refcsvattribute.isValide())
			return true;
		}
	
return false;
}


//OK 25/3/2018
public boolean isContentReferenceCSVSousDuplicable(Node _node)
{
	String contenu = node2ExpressionCandidate(_node);
	
	ReferenceCSV refcsv=fromExpression(contenu);
	if ((refcsv.isValide())
		&& ((refcsv.isListe()) || (refcsv.isMapping())))
		return true;

	else
	return false;
}


// VOIR
// https://stackoverflow.com/questions/439298/best-way-to-encode-text-data-for-xml-in-java

// OK 27/3/2017
public String node2ExpressionCandidate(Node _node)
{
return getFirstLevelTextContent(_node).trim();
}

// => RECOPIE DANS utilXML
//CONTENU EXACT
//MERCI A 
//https://stackoverflow.com/questions/12191414/node-gettextcontent-is-there-a-way-to-get-text-content-of-the-current-node-no
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



// OK 23/3/2018
public int getDuplicablePremierIndiceFichierTrouve(Node _noeud_premier_niveau) throws Exception
{
//Si contient un code

List<Node> nodesinside=node2InsideNodesRecursif(_noeud_premier_niveau);
	
for (Node node: nodesinside)
	{
	String contenu = node2ExpressionCandidate(node);
	
	ReferenceCSV refcsv=fromExpression(contenu);
	
	if (refcsv.isValide())
		return refcsv.getIndiceFichier();
	}

throw new Exception("NO Noeud Duplicable");
}

// VERSION OK POUR PREMIER NIVEAU
// OK 24/3/2018
public Node declinaisonNoeudNiveauPremierCloneEtAppend(Node _noeud_premier_niveau, 
		List<String> _ligne)
{
	Document doc=_noeud_premier_niveau.getOwnerDocument();
	
	Node copyOfinitial = doc.importNode(_noeud_premier_niveau,true);
	
	changeNoeudNiveauPremierAvecLigneValeurs(copyOfinitial,_ligne);

	_noeud_premier_niveau.getParentNode().insertBefore(copyOfinitial,_noeud_premier_niveau);
	
	return copyOfinitial;
}


// OK 20/7/2018 POUR NOEUD POSITION ARBITRAIRE
public Node declinaisonNoeudNiveauArbitraireCloneEtAppend(Node _noeud_niveau_arbitraire, 
		List<String> _ligne)
{
	Document doc=_noeud_niveau_arbitraire.getOwnerDocument();
	
	Node copyOfinitial = doc.importNode(_noeud_niveau_arbitraire,true);
	
	changeNoeudNiveauArbitraireAvecLigneValeurs(copyOfinitial,_ligne);

	_noeud_niveau_arbitraire.getParentNode().insertBefore(copyOfinitial,_noeud_niveau_arbitraire);
	
	return copyOfinitial;
}


public void changeNoeudNiveauArbitraireAvecLigneValeurs(
		Node _nouveau_noeud_copy, 
		List<String> _ligne)
{
	
	if (trace_transformation)
		System.out.println("CNPNAALV A1 LIGNE VALEURS:"+_ligne.toString());
	
	String contenu=node2ExpressionCandidate(_nouveau_noeud_copy);
	
	ReferenceCSV refcsv=fromExpression(contenu);

	String nouvelle_valeur=_ligne.get(refcsv.getIndiceColonne()-1);
	
	_nouveau_noeud_copy.setTextContent(nouvelle_valeur);

	
}




// OK ?: MODIFICATION POUR VIDE EXCLU
// 23/3/2018
public void changeNoeudNiveauPremierAvecLigneValeurs(Node _noeud_premier_niveau, 
		List<String> _ligne)
{
	
	changeNoeudParametresAvecLigneValeurs(_noeud_premier_niveau,_ligne);
	
	// SOUS-NOEUDS
			List<Node> sousnoeuds=Csv2XML.node2InsideNodesRecursif(_noeud_premier_niveau);
			int nbr_sous_noeuds=sousnoeuds.size();
			
			if (trace_transformation)
				System.out.println("CNPNALV A1 LIGNE VALEURS:"+_ligne.toString());
			
			
			if (trace_transformation)
				System.out.println("CNPNALV A2 SOUS Noeuds:"+nbr_sous_noeuds);
			
			for (Node sous_noeud: sousnoeuds)
				{
				String contenu=node2ExpressionCandidate(sous_noeud);

				ReferenceCSV refcsv=fromExpression(contenu);

				if (trace_transformation)
					{
					System.out.println("------------------------");
					System.out.println("CNPNALV B1 CONTENU SOUS_NOEUD="+contenu);
					System.out.println("CNPNALV B2 CONTENU SOUS_NOEUD:\n"+
							"###############\n"+
							this.node2XMLString(sous_noeud)+"\n###############\n");
					System.out.println("CNPNALV B3 REFCSV=\n"+refcsv.toString());
					}
			
				
				if (refcsv.isValide())
					{
					if (trace_transformation)
					System.out.println("CNPNALV C1 CODE:"+refcsv.toString());
				
					if (refcsv.isListe())
						{
						if (trace_transformation)
						{
						System.out.println("------------------------");
						System.out.println("CNPNALV D1 SOUS_NOEUD LISTE="+contenu);
						}
						
						// LISTE DE VALEURS
						String laliste_agregee=
								_ligne.get(refcsv.getIndiceColonne()-1);
						
						if (trace_transformation)
							System.out.println("CNPNALV D2 REMPLACEMENT="+laliste_agregee);

						
						List<String> liste_valeurs=UtilParametres.crochetsListe2ListString(laliste_agregee);
						// List<String> liste_valeurs=Arrays.asList("XA","xB");
						
						declinaison_sous_partie_liste_removePrototype(sous_noeud,liste_valeurs);

						}
					
					else
						
					// FAIRE COMME AU DESSUS
					if (refcsv.isMapping())
						{
						// ON NE FAIT RIEN !
						if (trace_transformation)
							System.out.println("CNPNALV E1 MAPPING FILS : rien");
						}

					else	
						{
						// OK CDATA
						if (refcsv.marqueur_cdata)
							{
							if (trace_transformation)
								System.out.println("CNPNALV F1 CDATA");
							
							String nouvelle_valeur=_ligne.get(refcsv.getIndiceColonne()-1);
							
							//sous_noeud.setTextContent("![CDATA["+nouvelle_valeur+"]]");
							sous_noeud.setTextContent("");
							XMLaddChildCDATA(sous_noeud, nouvelle_valeur);
							
							if (trace_transformation)
								System.out.println("#$#$#$#$ "+nouvelle_valeur);
							}
					

						// isVideExclu
						else	
						if (refcsv.isVideExclu())
							{
							if (trace_transformation)
								System.out.println("CNPNALV F2 VIDE EXCLU");
							
							String nouvelle_valeur=_ligne.get(refcsv.getIndiceColonne()-1);
							
							// CONTENU NON VIDE => comme STANDARD
							if (!nouvelle_valeur.contentEquals(""))
								{
								sous_noeud.setTextContent(nouvelle_valeur);
								
								if (trace_transformation)
									System.out.println("#$#$#$#$ "+nouvelle_valeur);
								}
							
							else
								removeNoeudPrototype(sous_noeud);
							}
						
						// OK TEXT STANDARD
						else
							{
							if (trace_transformation)
								System.out.println("CNPNALV G1 TEXTE");

							String nouvelle_valeur=_ligne.get(refcsv.getIndiceColonne()-1);
							
							sous_noeud.setTextContent(nouvelle_valeur);
							
							if (trace_transformation)
								System.out.println("CNPNALV G2 remplacement par ["+nouvelle_valeur+"]");
							}
						}
					}
					// 				if (refcsv.valide)
				else
					
					if (isParentMapping(sous_noeud))
						//if (false)
						{
						// Récupération du refcsv (pas bon jusque là)
						ReferenceCSV refcsvmapping=referenceCSVFromParentMapping(sous_noeud);
						
						if (trace_transformation)
							{
							System.out.println("CNPNALV H1 MAPPING CONTENU="+contenu);
							System.out.println("LIGNE:"+_ligne);
							System.out.println("COLONNE:"+refcsvmapping.getIndiceColonne());
							}
						
						// LISTE DE VALEURS
						String lemap_agregee=
								_ligne.get(refcsvmapping.getIndiceColonne()-1);
						
								// OK
								// FACTICE 26/3/2018
								//"{1: \"maison individuelle\", 2: \"piscine\"}";
								
						if (trace_transformation)
							System.out.println("CNPNALV H2 REMPLACEMENT="+lemap_agregee);

						Map<String,String> map_valeurs=UtilParametres.serieVarStringAccolades(lemap_agregee);

						declinaison_sous_partie_map_removePrototype(sous_noeud, map_valeurs);

						if (trace_transformation)
							System.out.println("CNPNALV H3 REMPLACEMENT OK");
						}
				
				
				if (trace_transformation)
					System.out.println("CNPNALV J1 REMPLACEMENT PARAMETRES SOUS-NOEUD");
				
				changeNoeudParametresAvecLigneValeurs(sous_noeud,_ligne);
				
				}
				// 		for (Node snd: sousnoeuds)

}

// OK 30/3/2018
public boolean existeParametreFilsOuAttributsObligatoireNonVide(Node _noeud_premier_niveau)
{
	List<String> tousParametresFilsEtAttributs=tousParametresFilsEtAttributs(_noeud_premier_niveau);
	
	for (String unparametre:tousParametresFilsEtAttributs)
	{
		ReferenceCSV refcsv=fromExpression(unparametre);
		if (!refcsv.isValide()) throw new RuntimeException("Csv2XML.existeParametreFilsOuAttributsObligatoireNonVide INVALIDE ["
				+unparametre+"]");
		
		if (refcsv.isVideInterdit()) return true;
	}
	
// aucun
return false;
}


//OK 30/3/2018
public List<String>  listParametresFilsOuAttributsObligatoiresNonVides(Node _noeud_premier_niveau)
{
	List<String> retour=new ArrayList<String> ();
	
	List<String> tousParametresFilsEtAttributs=tousParametresFilsEtAttributs(_noeud_premier_niveau);
	
	for (String unparametre:tousParametresFilsEtAttributs)
	{
		ReferenceCSV refcsv=fromExpression(unparametre);
		if (!refcsv.isValide()) throw new RuntimeException("Csv2XML.existeParametreFilsOuAttributsObligatoireNonVide INVALIDE ["
				+unparametre+"]");
		
		if (refcsv.isVideInterdit()) retour.add(unparametre);
	}
	
return retour;
}


// OK 30/3/2018
public List<String> tousParametresFilsEtAttributs(Node _noeud_premier_niveau)
{
	List<String> retour=new ArrayList<String>();
	
	// GENERAL
	retour.addAll(tousParametresReferenceCSV(_noeud_premier_niveau));
	
	// SOUS-NOEUDS
		List<Node> sousnoeuds=Csv2XML.node2InsideNodesRecursif(_noeud_premier_niveau);
		int nbr_sous_noeuds=sousnoeuds.size();
		
	
		if (trace_transformation)
			System.out.println("CNPNALV A2 SOUS Noeuds:"+nbr_sous_noeuds);
		
		for (Node sous_noeud: sousnoeuds)
			{
			String contenu=node2ExpressionCandidate(sous_noeud);

			ReferenceCSV refcsv=fromExpression(contenu);
			
			if (refcsv.isValide())
				retour.add(contenu);
				
			// Attributs
			retour.addAll(tousParametresReferenceCSV(sous_noeud));
			}
	
	return retour;
}



// OK 30/3/2018
public String 	Value2ExpressionCandidate(Node attribute)
{
return attribute.getNodeValue().trim();
}


// OK 25/3/2018
// Parametres
public void changeNoeudParametresAvecLigneValeurs(Node _noeud, 
		List<String> _ligne)

{
	// Chaque attribute
	NamedNodeMap nnm=_noeud.getAttributes();
	for (int i = 0; i < nnm.getLength(); ++i)
		{
	    Node attr = nnm.item(i);
	    String nodename=attr.getNodeName();
	    //   String nodevalue=attr.getNodeValue().trim();
	    String nodevalue=Value2ExpressionCandidate(attr);
	    
	    if (trace_transformation)
	    	System.out.println("Attribute:" + nodename + " = \"" + nodevalue + "\"");
		
		// Valeur OK ?
	    ReferenceCSV refcsvattribute=fromExpression(nodevalue);
		
		if (refcsvattribute.isValide())
			{
			String nouvelle_valeur_attribute=_ligne.get(refcsvattribute.getIndiceColonne()-1);
			attr.setNodeValue(nouvelle_valeur_attribute);	
			}
		}
}


// IDENTIQUE : ATTRIBUTS
public List<String> tousParametresReferenceCSV(Node _noeud)
{
	List<String> retour=new ArrayList<String>();
	
	// Chaque attribute
	NamedNodeMap nnm=_noeud.getAttributes();
	for (int i = 0; i < nnm.getLength(); ++i)
		{
	    Node attr = nnm.item(i);
	    String nodename=attr.getNodeName();
	    //   String nodevalue=attr.getNodeValue().trim();
	    String nodevalue=Value2ExpressionCandidate(attr);
	    
	    if (trace_transformation)
	    	System.out.println("Attribute:" + nodename + " = \"" + nodevalue + "\"");
		
		// Valeur OK ?
	    ReferenceCSV refcsvattribute=fromExpression(nodevalue);
		
		if (refcsvattribute.isValide())
			{
			retour.add(nodevalue);
			}
		}
	
	return retour;
}



// OK 25/3/2018
public void declinaisonListeContentNoeudSansEnfantCloneEtAppend(Node _node, List<String> _valeurs_remplacement)
{
	Document doc=_node.getOwnerDocument();
	
	if (trace_transformation)
		System.out.println("DLCNSECEA NODE A tag ["+_node.getNodeName()+"] content ["+_node.getTextContent()+"]");
	
	if (trace_transformation)
		System.out.println("DLCNSECEA NODE B Valeurs de remplacement :"+_valeurs_remplacement.size());
	
	
	for (String _une_valeur_remplacement: _valeurs_remplacement)
		{
		// CORRECTION 11/6/2018
		if (_une_valeur_remplacement.contentEquals(""))
			{
			if (trace_transformation)
				System.out.println("DLCNSECEA NODE C0 VIDE STOP tag ["+_node.getNodeName()+"] remplacement content ["+_une_valeur_remplacement+"]");
			continue;
			}
		
		Node copyOfinitial = doc.importNode(_node,true);
		
		// Remplacement du contenu
		copyOfinitial.setTextContent(_une_valeur_remplacement);
		
		if (trace_transformation)
			System.out.println("DLCNSECEA NODE C1 tag ["+_node.getNodeName()+"] remplacement content ["+_une_valeur_remplacement+"]");
		
		_node.getParentNode().appendChild(copyOfinitial);
		}
}


// OK 25/3/2018
// 
public Node DocumentAndXPath2NodeUnique(Document _document, String _xpath_expression) throws Exception
{
	XPath xpath = XPathFactory.newInstance().newXPath();
	XPathExpression expr = xpath.compile(_xpath_expression) ; 
	NodeList nodes  = (NodeList) expr.evaluate(_document, XPathConstants.NODESET);
	
	if (nodes.getLength()!=1)
		throw new Exception("NON 1 NOEUD");
	
	return nodes.item(0);
}

// OK
public void removeNoeudPrototype(Node _noeud_prototype)
{
	_noeud_prototype.getParentNode().removeChild(_noeud_prototype);
}


// OK 25/3/2018
// REMPLACEMENT SOUS-LISTE 
public void declinaison_sous_partie_liste_removePrototype(Node _noeud, List<String> valeurs_remplacement)
{
	
	if (trace_transformation)
		System.out.println("DSPLRP declinaison ["+_noeud.getNodeName()+"] content ["+_noeud.getTextContent()+"]");

	 declinaisonListeContentNoeudSansEnfantCloneEtAppend(_noeud, valeurs_remplacement);

		if (trace_transformation)
			System.out.println("DSPLRP remove proto ["+_noeud.getNodeName()+"] content ["+_noeud.getTextContent()+"]");

	removeNoeudPrototype(_noeud);
}

// OK 26/3/2018
public void declinaison_sous_partie_map_removePrototype(Node _noeud_parent, Map<String,String> valeurs_remplacement)
{
declinaisonMapContentNoeudAvecEnfant1NiveauCloneEtAppend(_noeud_parent, valeurs_remplacement);

//SUPPRESSION PROTOTYPE
removeNoeudPrototype(_noeud_parent);
}


// OK 27/3/2018
public boolean isParentMapping(Node _noeud_candidat)
{
List<Node> noeuds_internes=node2ChildNodes(_noeud_candidat);	

boolean retour=false;
for (Node node_fils: noeuds_internes)
	{
	if (isNodeMapping(node_fils))
		return true;	
	}

return retour;
}

//OK 25/3/2018
public boolean isNodeMapping(Node _noeud_candidat)
{
String expression=node2ExpressionCandidate(_noeud_candidat);	
ReferenceCSV refcsv=fromExpression(expression);

if (refcsv.isValide())
	if (refcsv.isMapping())
		return true;

// Non
return false;
}

// Combinaison des 2 Précédents !
// OK 26/3/2018
public ReferenceCSV referenceCSVFromParentMapping(Node _noeud_candidat)
{
	List<Node> noeuds_internes=node2InsideNodesRecursif(_noeud_candidat);	
	
	for (Node node_fils: noeuds_internes)
	{
	String expression=node2ExpressionCandidate(node_fils);	
	ReferenceCSV refcsv=fromExpression(expression);	
	if (refcsv.isValide())
		if (refcsv.isMapping())
			return refcsv; // Le premier est suffisant !
	}
	
// Echec
throw new RuntimeException("NODE NON FILS ParentMapping");
	
}



// OK 25/3/2018
// NE FONCTIONNE QU'AVEC NOEUD ET SOUS-NOEUD
public void declinaisonMapContentNoeudAvecEnfant1NiveauCloneEtAppend(Node _noeud_parent, Map<String,String> valeurs_remplacement)
{
	if (trace_transformation)
		{
		System.out.println("Csv2XML.declinaisonMapContentNoeudAvecEnfant1NiveauCloneEtAppend");
		System.out.println("MAPPING:"+valeurs_remplacement.toString());
		}
	
Document doc=_noeud_parent.getOwnerDocument();

Node _noeud_prototype=null;

for (String _une_cle_remplacement_1: valeurs_remplacement.keySet())
	{
	// #1 => key
	// #2 => valeur
	String _une_valeur_remplacement_2 = valeurs_remplacement.get(_une_cle_remplacement_1);
	
	
	// Clonage
	Node clone_noeud_parent = doc.importNode(_noeud_parent,true);
	
	// Tous les sous-noeuds
	List<Node> noeuds_internes=node2InsideNodesRecursif(clone_noeud_parent);
	
	if (trace_transformation)
	System.out.println("Csv2XML Noeuds Internes:"+noeuds_internes.size());
	
	for (Node un_sous_noeud: noeuds_internes)
		{
		// Contenu ?
		String expression=node2ExpressionCandidate(un_sous_noeud);
		
		if (trace_transformation)
			System.out.println("Csv2XML Noeud Interne:"+expression);

		ReferenceCSV refcsv=fromExpression(expression);
		
		if ((refcsv.isValide())
				&& (refcsv.isMapping()))
			{
			String nouvelle_valeur="";
			
			// #1 => key
			// #2 => valeur
			if (refcsv.getIndiceMapping()==1)
				nouvelle_valeur=_une_cle_remplacement_1;
			if (refcsv.getIndiceMapping()==2)
				nouvelle_valeur=_une_valeur_remplacement_2;
			
			un_sous_noeud.setTextContent(nouvelle_valeur);
			}
		}
	
	
	if (trace_transformation)
		{
		String sortie2=Csv2XML.node2XMLString(clone_noeud_parent);
		System.out.println("CLONE NOEUD PARENT:\n"+sortie2);
		}
	
	_noeud_parent.getParentNode().appendChild(clone_noeud_parent);
	}

}

// OK 27/3/2018
//
public static void document2XMLFile(Document _document, String _filename) throws Exception
{
	DOMSource domSource = new DOMSource(_document);
	Writer out = new OutputStreamWriter(new FileOutputStream(_filename), "UTF8");

	Transformer transformer = TransformerFactory.newInstance().newTransformer();
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	transformer.transform(domSource, new StreamResult(out));
	
	out.close();
	
}


// FIXME : KO 30/6/2018
// OK 26/3/2018
public static boolean validateXMLSchema(String xsdPath, String xmlPath){
try {
	SchemaFactory factory =
	SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	Schema schema = factory.newSchema(new File(xsdPath));
	Validator validator = schema.newValidator();
	validator.validate(new StreamSource(new File(xmlPath)));
	} 

catch (IOException | SAXException e)
	{
	System.out.println("Exception: "+e.getMessage());
	return false;
	}

return true;
}


// OK 26/3/2018
public static void XMLaddChildCDATA(Node _noeud_origine, String _contenu_a_inserer)
{
	Document document = _noeud_origine.getOwnerDocument();
	
	Node cdata = document.createCDATASection(_contenu_a_inserer);
	_noeud_origine.appendChild(cdata);
}

// OK 7/4/2018
public static Document processModeleDonneesCSV(String _prefixe_champs,
			String _fichier_modele_xml,
			String ... _fichiers_csv) throws Exception
{
	Csv2XML csv2xml=new Csv2XML(_prefixe_champs);

	List<String> listeFichiersCSV=Arrays.asList(_fichiers_csv);

	Document document=Csv2XML.XMLReader2Document(new FileReader(_fichier_modele_xml));

	csv2xml.DocumentTransformedWithCSV_point_virgule(document, csv2xml, listeFichiersCSV);
	
	return document;
}


// OK 7/4/2018
public static void processModeleDonneesODS2XML(String _prefixe_champs,
		String _fichier_modele_xml,
		String _fichier_ods,
		String _fichier_sortie_xml) throws Exception
{
	Document document=Csv2XML.processModeleDonneesODS(_prefixe_champs, _fichier_modele_xml,
			_fichier_ods);
	
	Csv2XML.document2XMLFile(document, _fichier_sortie_xml);
}


// OK 7/4/2018
public static Document processModeleDonneesODS(String _prefixe_champs,
			String _fichier_modele_xml,
			String _fichier_ods) throws Exception
{
	Csv2XML csv2xml=new Csv2XML(_prefixe_champs);

	Document document=Csv2XML.XMLReader2Document(new FileReader(_fichier_modele_xml));

	csv2xml.DocumentTransformedWithODS(document, csv2xml, _fichier_ods);
	
	return document;
}





}




