package top.ggv.csv2xml;


public class Csv2XML_main {

	
		
	// Avec XML => CSV
	static final String VERSION_CSV2XML="CSV2XML Version 2.0 18/7/2018";
	
	
	public static void main(String[] args)
	{
		System.err.println(VERSION_CSV2XML);
		
		if (args.length<4)
			{
			System.err.println("USAGE: java -jar csv2xml.jar <fichier ods> <prefixe> <modele xml> <sortie xml>");
		
			System.err.println("TAGS STANDARDS:");
			System.err.println("<ordre>C2X_F3_C4</ordre> : C2X_F3_C4: prefixe C2X + fichier/onglet 3 + colonne 4");
			System.err.println("");
			System.err.println("SUFFIXES SPECIAUX:");
			System.err.println("C2X_F3_C4"+Csv2XML.REFERENCECSV_CARACTERE_MARQUEUR_VIDE_EXCLURE
					+" : vide exclu : si le champ est vide, supprime le tag");
			System.err.println("C2X_F3_C4"+Csv2XML.REFERENCECSV_CARACTERE_MARQUEUR_VIDE_INTERDIT
					+" : vide interdit : si le champ est vide, supprime le bloc");
			System.err.println("C2X_F3_C4"+Csv2XML.REFERENCECSV_CARACTERE_MARQUEUR_LISTE_MAP
					+" : liste: reproduit autant de fois le tag que d'elements dans la liste.");
			System.err.println("C2X_F3_C4"+Csv2XML.REFERENCECSV_CARACTERE_MARQUEUR_LISTE_MAP+"1 ou 2"
					+" : map: reproduit les couples de valeurs");
			System.err.println("C2X_F3_C4"+Csv2XML.REFERENCECSV_CARACTERE_MARQUEUR_CDATA
					+" : CDATA: insere le bloc entoure du marqueur CDATA pour chaines avec caracteres speciaux, retours chariots, ...");			

			System.err.println("<ordre>C2X_F3_C4"+Csv2XML.REFERENCECSV_CARACTERE_MARQUEUR_NIVEAU_ARBITRAIRE
					+"</ordre> : comme C2X_F3_C4, mais produit toutes les lignes au niveau de ce tag (normalement niveau 1 sous la racine)");

			return;
			}
		
		try
		{
		String nom_fichier_ods=args[0];
		String 	prefixe_champs=args[1];
		String  modele_xml=args[2];
		String fichier_sortie_xml=args[3];
		
    	Csv2XML.processModeleDonneesODS2XML(prefixe_champs, modele_xml, 
	    			nom_fichier_ods, fichier_sortie_xml);
	    	
    	System.out.println("Fichier "+fichier_sortie_xml+" créé");
		}
		catch (Exception eee)
			{
			System.err.println("args[0]="+args[0]);
			
			System.err.println("ERREUR :"+eee.getMessage());
			}
	}

}
