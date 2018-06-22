package fr.obr.cdm;

import com.google.gson.Gson;
import javafx.collections.transformation.SortedList;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by jkgm8814 on 14/06/2018.
 */
public class ReportAll {

    //public static final String PATH_TO_FILES_Y = "C:\\WORK\\experimentation\\cdm\\src\\main\\resources\\master\\15062018_MASTER_Pronos_Russie2018(1).xlsx";
    public static final String PATH_TO_FILES_T = "C:\\WORK\\experimentation\\cdm\\src\\main\\resources\\master\\21062018_MASTER_Pronos_Russie2018.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        int offset=22;
        int journee=6;
        File file = new File(PATH_TO_FILES_T);
        Map<String, Double> evolrY = new HashMap<>();
        Map<String, Double> evolpY = new HashMap<>();
        Workbook workbook = WorkbookFactory.create(file);
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            if (sheet.getSheetName().startsWith("Au fil ")) {
                List<Rank> classement = new ArrayList<>();
                for (int i = 57+(offset*journee); i < 77+(offset*journee); i++) {
                    Row row = sheet.getRow(i);
                    Cell cell = row.getCell(1);
                    Rank r = new Rank();
                    r.r = cell.getNumericCellValue();
                    cell = row.getCell(2);
                    r.username = cell.getStringCellValue();
                    cell = row.getCell(3);
                    r.nbPoint = cell.getNumericCellValue();
                    classement.add(r);
                    evolrY.put(r.username, r.r);
                    evolpY.put(r.username, r.nbPoint);
                    r.evolRank = 0;
                    r.evolPoint = 0;
                    System.out.println(r.username + ":" + r.nbPoint);

                }
            }
        }

        File fileT = new File(PATH_TO_FILES_T);
        Map<String, Integer> evol = new HashMap<>();
        Workbook workbookT = WorkbookFactory.create(fileT);
        Iterator<Sheet> sheetIteratorT = workbookT.sheetIterator();
        while (sheetIteratorT.hasNext()) {
            Sheet sheet = sheetIteratorT.next();
            if (sheet.getSheetName().startsWith("Au fil ")) {
                List<Rank> classement = new ArrayList<>();
                for (int i = 57+(offset*(journee+1)); i < 77+(offset*(journee+1)); i++) {
                    Row row = sheet.getRow(i);
                    Cell cell = row.getCell(1);
                    Rank r = new Rank();
                    r.r = cell.getNumericCellValue();
                    cell = row.getCell(2);
                    r.username = cell.getStringCellValue();
                    cell = row.getCell(3);
                    r.nbPoint = cell.getNumericCellValue();
                    classement.add(r);
                    Double rY = evolrY.get(r.username);
                    Double pY = evolpY.get(r.username);
                    r.evolRank = rY - r.r;
                    r.evolPoint = r.nbPoint - pY;
                    System.out.println(r.username + ":" + r.nbPoint);
                }
                try (Writer writer = new FileWriter("C:\\WORK\\experimentation\\cdm\\src\\main\\resources\\master\\rank.json")) {
                    Gson gson = new Gson();
                    gson.toJson(sortClassement(classement), writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static List<Rank> sortClassement(List<Rank> classement) {
        final int first = 0;
        if (classement == null) {

            Collections.sort(classement, new Comparator(){
                public int compare (Object o1, Object o2){
                    if (((Rank) o1).r < ((Rank) o2).r) {

                        return 0;
                    }
                    return 1;

                }
            });
        }
        return classement;
    }

}
