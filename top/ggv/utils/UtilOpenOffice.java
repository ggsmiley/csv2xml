package top.ggv.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jopendocument.dom.spreadsheet.Range;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

// OK 7/4/2018

// <classpathentry kind="lib" path="V:/JAR/jOpenDocument-1.3.jar"/>

public class UtilOpenOffice
{
static final boolean trace=false;

	// OK 7/4/2018
	public static int odsCountTabs(String _fichier_ods) throws Exception
	{
	SpreadSheet spsh=SpreadSheet.createFromFile(new File(_fichier_ods));	
	int quantite=spsh.getSheetCount();
	return quantite;
	}
	
	// OK 7/4/2018
	public static List<List<String>> odsTabLinesSansEnTete(String _fichier_ods, int _indice_tab ) throws Exception
	{
		return odsTabLines(_fichier_ods,_indice_tab,true);

	}
	
	// OK 7/4/2018
	public static List<List<String>> odsTabLinesAvecEnTete(String _fichier_ods, int _indice_tab ) throws Exception
	{
		return odsTabLines(_fichier_ods,_indice_tab,false);

	}
	
	// OK 7/4/2018
	private static List<List<String>>  odsTabLines(String _fichier_ods, int _indice_tab,
			boolean sans_en_tete) throws Exception
	{
		List<List<String>> retour=new ArrayList<List<String>>();
		
		SpreadSheet spsh=SpreadSheet.createFromFile(new File(_fichier_ods));
		Sheet sheet=spsh.getSheet(_indice_tab);
		
		// LIGNES
	 	int nbrlignes=sheet.getRowCount();
	 	
	 	// COLONNES
	 	int nbrcol=sheet.getColumnCount();

	 
	 // DANS CERTAINS CAS  / TROP DE COLONNES / LIGNES
	 	if (trace)
	 System.out.println("ONGLET "+_indice_tab+ " "
			+" TITRE ["+sheet.getName()+"] "
			 +" LIGNES :"+nbrlignes
			 +" COLONNES :"+nbrcol
			 
			 );
 
	 	if (nbrlignes>5000)
	 		System.err.println("ALERTE : NOMBRE DE LIGNE TRES GRAND - ATTENTE DE QUELQUES SECONDES");
	 	
 	// TEST 1000 x 1000
	 if (trace)
	 System.out.println("VALIDE 1000, 1000:"+sheet.isCellValid(1000, 1000));
 //	Range range=sheet.getUsedRange();
 	
 	 //System.out.println("RANGE: "+range.toString());
	 
	 	Range range=sheet.getUsedRange();
	 	
	 	// SI RANGE NULL => VIDE
	 	if (range==null)
	 	{
	 	System.err.println("ONGLET ["+_indice_tab+"] VIDE");
	 	//throw new Exception("RANGE NULL");
	 	
	 	// VIDE
	 	return retour;
	 	}
	 	
	 	if (trace)
	System.out.println("RANGE: "+range.toString());

// DEBUT-FIN
	 	if (trace)
	System.out.println("getStartPoint: "+range.getStartPoint());
	 	if (trace)
	System.out.println("getEndPoint: "+range.getEndPoint());

	 	// PLUS SUR !
	 	nbrlignes=range.getEndPoint().y+1;
		nbrcol=range.getEndPoint().x+1;
	 	
		if (trace)
			System.out.println("LIGNES: "+nbrlignes+" COLONNES: "+nbrcol);
			
		// ????
		for (int indice_ligne=0;indice_ligne<nbrlignes;indice_ligne++)
			{
			if (sans_en_tete) if (indice_ligne==0) continue;
			
			List<String> nouvelle_ligne=new ArrayList<String>();	 	

			for (int indice_colonne=0;indice_colonne<nbrcol;indice_colonne++)
				{
				String cellule=sheet.getCellAt(indice_colonne, indice_ligne).getTextValue();	

				if (trace)
					System.out.println("(L "+indice_ligne+",C "+indice_colonne+") => "+cellule);
				
				nouvelle_ligne.add(cellule);
				}
			retour.add(nouvelle_ligne);
			}
		return retour;
	}
	
	
	// CREATION ODS
	// 
	// OK 14/4/2018
	public static void ListeTableaux2ODS(List<List<List<String>>> _listeTableaux, String _ods_fichier) throws Exception
	{
	SpreadSheet spsh=SpreadSheet.create(_listeTableaux.size(),20,50);
	
	int nbr_tableaux=_listeTableaux.size();
	for (int k=0;k<nbr_tableaux;k++)
		{
		List<List<String>> tableau=_listeTableaux.get(k);
		
		Sheet onglet=spsh.getSheet(k);
		
		onglet.setName("tab"+k);
	
		onglet.setRowCount(tableau.size());
		
		// Col max
		int taillecol=0;
		for (List<String> lg: tableau)
		{
		if (lg.size()>taillecol)
			taillecol=lg.size();
		}
		
		onglet.setColumnCount(taillecol);
		
		if (trace)
			System.out.println("ROW:"+onglet.getRowCount()+"COL:"+onglet.getColumnCount());
		
		int indice_ligne=0;
		
		for (List<String> lg: tableau)
			{
			int indice_col=0;
			for (String element: lg)
				{
				if (trace)
					System.out.println("SAVE:"+indice_ligne+","+indice_col);
				onglet.setValueAt(element, indice_col++, indice_ligne);
				}
			indice_ligne++;
			}
		}

	
	
	spsh.saveAs(new File(_ods_fichier));
	}
}


