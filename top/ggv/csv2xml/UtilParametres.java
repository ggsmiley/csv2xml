package top.ggv.csv2xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 25/3/2018

public class UtilParametres
{

	// OK 26/7/2017
	// PARSE LISTE  [] => List<String>
	public static List<String> crochetsListe2ListString(String _source)
	{
		//System.out.println("CROCHETS: ORIGINE [["+_source+"]]");
		
		// On prend crochets
		int crochet_gauche=_source.indexOf('[');
		if (crochet_gauche<0)
			{
			System.err.println("ERREUR A crochetsListe2ListString sans crochet gauche - chaine:"+_source);
			return new ArrayList<String>();
			}
		int crochet_droit=_source.indexOf(']',crochet_gauche);
		if (crochet_droit<0)
			{
			System.err.println("ERREUR B crochetsListe2ListString sans crochet droit - chaine:"+_source);
			return new ArrayList<String>();
			}
		
		String str=_source.substring(crochet_gauche+1,crochet_droit);

		return ListeVirgules2ListString(str);
		
	}


	// OK
	// Liste , => liste String
	public static List<String> ListeVirgules2ListString(String _source)
	{
	List<String> items = Arrays.asList(_source.split("\\s*,\\s*"));
		
	return items;
	}

	// merci à 
	// https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
	// OK 26/4/2018
	// Version avec guillemets possibles et virgules internes
	public static List<String> ListeVirgules2ListString_guillemetsPossiblesAvecVirgules(String _source)
	{
	List<String> items = Arrays.asList(
	_source.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
			);
	
	return items;
	}


	//A FINIR (suppose pas de , de { } de : internes
	// OK 26/7/2017
	//PARSE LISTE  {code:"val", ...} => List<String> // COMME MAP
	public static Map<String,String> serieVarStringAccolades(String _source)
	{
		Map<String,String> retour=new LinkedHashMap<String,String>();
		
		// On prend accolades
		int accolade_gauche=_source.indexOf('{');
		if (accolade_gauche<0)
			{
			System.err.println("ERREUR serieVarString sans accolade gauche - chaine:"+_source);
			return new  HashMap<String,String>();
			}
		int accolade_droit=_source.indexOf('}',accolade_gauche);
		if (accolade_droit<0)
			{
			System.err.println("ERREUR serieVarString sans accolade droite - chaine:"+_source);
			return new  HashMap<String,String>();
			}
		
		String str=_source.substring(accolade_gauche+1,accolade_droit);
		
		// Usage procedure suivante
		retour=serieVarStringInterne(str);
	/*
		// ON DECOUPE L'INTERIEUR
		List<String> items = Arrays.asList(str.split("\\s*,\\s*"));

		// DANS CHACUN, ON DOIT AVOIR:
		// var:"chaine"
		for (String item: items)
			{
			String parties[]=item.split(":");
			
			if (parties.length!=2)
				// PAS CA !
				continue;
			
			String code=parties[0].trim();
			String contenu=parties[1].trim();
			
			// Et normalement, avec des guillemets
			if (contenu.startsWith("\""))
				contenu=contenu.substring(1);
			
			// Si idem à la fin
			if (contenu.endsWith("\""))
				contenu=contenu.substring(0,contenu.length()-1);
			
			retour.put(code,contenu);
			}
		
		*/
		return retour;
	}

	// 11/9/2017
	// MEME VERSION SANS EXTERIEUR
	//A FINIR (suppose pas de , de { } de : internes
	//PARSE LISTE  code:"val", ... => List<String> // COMME MAP
	public static Map<String,String> serieVarStringInterne(String _source_interne)
	{
		Map<String,String> retour=new LinkedHashMap<String,String>();

		// ON DECOUPE L'INTERIEUR
		//List<String> items = ListeVirgules2ListString(_source_interne);
		//Arrays.asList(_source_interne.split("\\s*,\\s*"));
		List<String> items = ListeVirgules2ListString_guillemetsPossiblesAvecVirgules(_source_interne);
		
		// DANS CHACUN, ON DOIT AVOIR:
		// var:"chaine"
		for (String item: items)
			{
			String parties[]=item.split(":");
			
			if (parties.length!=2)
				// PAS CA !
				continue;
			
			String code=parties[0].trim();
			String contenu=parties[1].trim();
			
			// Et normalement, avec des guillemets
			if (contenu.startsWith("\""))
				contenu=contenu.substring(1);
			
			// Si idem à la fin
			if (contenu.endsWith("\""))
				contenu=contenu.substring(0,contenu.length()-1);
			
			retour.put(code,contenu);
			}
		return retour;
	}
	
	
	
}
