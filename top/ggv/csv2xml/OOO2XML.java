package top.ggv.csv2xml;

import top.ggv.utils.*;


import java.util.ArrayList;
import java.util.List;



import top.ggv.csv2xml.Csv2XML.TableauValeurs;

// 7/4/2018
// POUR OOO
public class OOO2XML
{
	
	public static int fichierODS2CountTabs(String _nom_fichier_ODS) throws Exception
	{
	return	UtilOpenOffice.odsCountTabs(_nom_fichier_ODS);
	}
	
	public static TableauValeurs fichierODSTab2TableauValeurs(String _nom_fichier_ODS, int _indice_tab)  throws Exception
	{
	Csv2XML csv2xml=new Csv2XML("");	
		
	TableauValeurs retour=csv2xml.new TableauValeurs();
			
	List<List<String>> lignes=
			UtilOpenOffice.odsTabLinesSansEnTete(_nom_fichier_ODS, _indice_tab);
		
		retour.setValeurs(lignes);
		
		return retour;
	}
	

	public static List<TableauValeurs> fichierODS2TableauxValeurs(String _nom_fichier_ODS)  throws Exception
	{
		List<TableauValeurs> liste=new ArrayList<TableauValeurs> ();
		
		int nbr_tabs=UtilOpenOffice.odsCountTabs(_nom_fichier_ODS);
		
		for (int indice_tab=0;indice_tab<nbr_tabs;indice_tab++)
			liste.add(fichierODSTab2TableauValeurs(_nom_fichier_ODS,indice_tab));
		
		return liste;
	}
	
	
	// TODO 14/4/2018
	public static void tableauxValeurs2fichierODS2(List<TableauValeurs> _tableauxValeurs, 
			String _nom_fichier_ODS)  throws Exception
	{
		List<TableauValeurs> liste=new ArrayList<TableauValeurs> ();
		
		List<List<List<String>>> llls=new ArrayList<List<List<String>>>();
		
		for (int k=0;k<_tableauxValeurs.size();k++)
			llls.add(_tableauxValeurs.get(k).getValeurs());
		
		UtilOpenOffice.ListeTableaux2ODS(llls, _nom_fichier_ODS);
	}
	
	
}
