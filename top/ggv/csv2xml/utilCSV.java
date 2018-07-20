package top.ggv.csv2xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

// GGV
// 28/7/2014

// DECODAGE CSV

public class utilCSV {
	
	
	// REVUE 6/2017
// VERSION List 3/3/2017

static boolean trace_decode_csv=false;
	

//OK 2/5/2017
		public static List<String> inFichierLignes(String nomfichier) throws Exception
		{
		Vector<String> retour=new Vector<String>();

	Scanner sc = new Scanner(new File(nomfichier),"UTF-8"); 
	
		 
			 while (sc.hasNextLine()) 
				 {
				 String laligne=sc.nextLine();
				 retour.add(laligne);
				 }
		
		 sc.close();
		 
		 
		 // SI VIDE
		 // TENTATIVE 2 SANS UTF-8
		 
		 if (retour.size()==0)
		 {
					  //Scanner sc = new Scanner(new File(nomfichier),"UTF-8"); 
				Scanner sc2 = new Scanner(new File(nomfichier));
				 
					 while (sc2.hasNextLine()) 
						 {
						 String laligne=sc2.nextLine();
						 retour.add(laligne);
						 }				 
					 sc2.close();
		 }
		 
		return retour;
		}
		// Vector<String> LectureFichier(String nomfichier)
	
		
		
		// MODIF 28/3/2018
		// OK 27/3/2018
		// DECODAGE DE FICHIER CSV
		public static List<List<String>>  lignes(String _fichier, char _separateur, boolean sans_entete)
		{
			List<List<String>> retour=new ArrayList<List<String>>();
		
			
			String contenu=inFichierString(_fichier);

			int indice_ligne=0;
			
			try
			{
				// POUR UTF8
				InputStreamReader isr=new InputStreamReader(new FileInputStream(_fichier), StandardCharsets.UTF_8);
				
				// CHOIX SEPARATEUR ;
				CSVFormat csvFormat=CSVFormat.DEFAULT.withDelimiter(_separateur);

				CSVParser parser = csvFormat.parse(isr);
				
			 for (CSVRecord csvRecord : parser)
			 {
				 List<String> ligne=new ArrayList<String>();
				 
			for (int k=0;k<csvRecord.size();k++)
				ligne.add(csvRecord.get(k));

			if ((indice_ligne>0) || (sans_entete==false))
				retour.add(ligne);
			
			indice_ligne++;
			 }
			}
			catch (Exception aaa)
			{
			System.err.println("ERREUR utilCSV.lignes "+aaa.getMessage());	
			}
			
			return retour;
		}

		

		
		//OK 2/5/2017
		public static String inFichierString(String nomfichier) 
		{
		String retour="";
			
		try
		{
		List<String> lignes=inFichierLignes(nomfichier);
		
		for (String ligne: lignes)
			retour+=ligne;
		}
		catch (Exception aaa)
		{
		System.err.println("ERREUR M2U "+aaa.getMessage());	
		}
		
		
		return retour;
		}
		
		
		// OK 27/3/2018
		// Nettoyage
		public static String removeCarriageReturn(String _source)
		{
		return _source.replace("\r","");	
		}
		
		
		
		
		
}
