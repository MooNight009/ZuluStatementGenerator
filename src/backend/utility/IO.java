package backend.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import backend.entity.NGram;
import backend.entity.POSEntity;

public class IO{

	public static List<NGram> readNGrams(String mainFolderDir){
		List<NGram> nGrams= new ArrayList<>();

		for (File file : (new File(mainFolderDir)).listFiles()){
			if (!file.getName().contains("gram"))
				continue;
			try{
				BufferedReader myReader= new BufferedReader(new FileReader(file));
				String myRead;

				while ((myRead= myReader.readLine())!= null){
					int n= 1;
					for (int i= 0; i< myRead.length(); i++)
						if (myRead.charAt(i)== ' ')
							n++;

					nGrams.add(new NGram(myRead.split("=")[0], Integer.parseInt(myRead.split("=")[1]), n));
				}

				myReader.close();
			} catch (Exception e){
				// TODO: handle exception
			}
		}

		return nGrams;
	}
	
	public static List<POSEntity> readPOS(String mainFolderDir){
		List<POSEntity> posList= new ArrayList<>();

		for (File file : (new File(mainFolderDir)).listFiles()){
			if (!file.getName().contains("pos"))
				continue;
			try{
				BufferedReader myReader= new BufferedReader(new FileReader(file));
				String myRead;

				while ((myRead= myReader.readLine())!= null){
					int n= 1;
					for (int i= 0; i< myRead.length(); i++)
						if (myRead.charAt(i)== '_')
							n++;

					posList.add(new POSEntity(myRead.split("=")[0], Integer.parseInt(myRead.split("=")[1]), n));
				}

				myReader.close();
			} catch (Exception e){
				// TODO: handle exception
			}
		}

		return posList;
	}
	
	public static List<String> readStart(String mainFolderDir){
		List<String> startList= new ArrayList<>();

		for (File file : (new File(mainFolderDir)).listFiles()){
			if (!file.getName().contains("start"))
				continue;
			try{
				BufferedReader myReader= new BufferedReader(new FileReader(file));
				String myRead;

				while ((myRead= myReader.readLine())!= null){
					startList.add(myRead);
				}

				myReader.close();
			} catch (Exception e){
				// TODO: handle exception
			}
		}

		return startList;
	}
	
	public static List<String> readEnd(String mainFolderDir){
		List<String> startEnd= new ArrayList<>();

		for (File file : (new File(mainFolderDir)).listFiles()){
			if (!file.getName().contains("end"))
				continue;
			try{
				BufferedReader myReader= new BufferedReader(new FileReader(file));
				String myRead;

				while ((myRead= myReader.readLine())!= null){
					startEnd.add(myRead);
				}

				myReader.close();
			} catch (Exception e){
				// TODO: handle exception
			}
		}

		return startEnd;
	}
}
