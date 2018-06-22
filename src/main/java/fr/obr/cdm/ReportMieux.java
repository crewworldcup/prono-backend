package fr.obr.cdm;

import com.google.gson.Gson;
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

/**
 * Created by jkgm8814 on 14/06/2018.
 */
public class ReportMieux {

    public static final Path PATH_TO_FILES = Paths.get("PATH_TO_MASTER_FILE");

    public static void main(String[] args) throws IOException, InvalidFormatException {
        SimpleDateFormat sdf_filename = new SimpleDateFormat("dd-MM-yyyy");
        Map<String, DayPronos> pronos = new HashMap<>();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(PATH_TO_FILES, "*.xlsx")) {
            Iterator<Path> it = files.iterator();
            while (it.hasNext()) {
                Path path = it.next();
                System.out.println(path);
                File f = path.toFile();
                Workbook workbook = WorkbookFactory.create(f);
                String username = f.getName().split(("_"))[0];

                Iterator<Sheet> sheetIterator = workbook.sheetIterator();
                while (sheetIterator.hasNext()) {
                    Sheet sheet = sheetIterator.next();

                    if (sheet.getSheetName().startsWith("GROUPE A")) {
                        for (int i = 5; i <= 10; i++) {
                            Row row = sheet.getRow(i);
                            Cell cell = row.getCell(15);
                            Date date = cell.getDateCellValue();
                            String dateString = sdf_filename.format(date);
                            DayPronos dp = pronos.get(dateString);


                            if (dp == null) {
                                dp = new DayPronos();
                            }
                            Map<String, List<Prono>> dayrMatch = dp.pronos;
                            if (dayrMatch == null) {
                                dayrMatch = new HashMap<>();
                            }

                            String matchName = row.getCell(16).getStringCellValue().trim()+"-"+row.getCell(18).getStringCellValue().trim();
                            List<Prono> matchs = dayrMatch.get(matchName);
                            if (matchs == null) {
                                matchs = new ArrayList<>();
                            }
                            Prono prono = new Prono();
                            prono.username = username;
                            prono.but1 = row.getCell(17).getNumericCellValue();
                            prono.but2 = row.getCell(19).getNumericCellValue();
                            matchs.add(prono);
                            dayrMatch.put(matchName, matchs);
                            dp.pronos = dayrMatch;
                            pronos.put(dateString, dp);
                        }

                    } else if (sheet.getSheetName().startsWith("GROUPE")) {
                        for (int i = 4; i <= 9; i++) {
                            Row row = sheet.getRow(i);
                            Cell cell = row.getCell(15);
                            Date date = cell.getDateCellValue();
                            String dateString = sdf_filename.format(date);
                            DayPronos dp = pronos.get(dateString);


                            if (dp == null) {
                                dp = new DayPronos();
                            }
                            Map<String, List<Prono>> dayrMatch = dp.pronos;
                            if (dayrMatch == null) {
                                dayrMatch = new HashMap<>();
                            }

                            String matchName = row.getCell(16).getStringCellValue().trim()+"-"+row.getCell(18).getStringCellValue().trim();
                            List<Prono> matchs = dayrMatch.get(matchName);
                            if (matchs == null) {
                                matchs = new ArrayList<>();
                            }
                            Prono prono = new Prono();
                            prono.username = username;
                            prono.but1 = row.getCell(17).getNumericCellValue();
                            prono.but2 = row.getCell(19).getNumericCellValue();
                            matchs.add(prono);
                            dayrMatch.put(matchName, matchs);
                            dp.pronos = dayrMatch;
                            pronos.put(dateString, dp);

                        }
                    }
                }

            }
        } finally {
            try (Writer writer = new FileWriter("PATH_TO_RESULT_FILE")) {
                Gson gson = new Gson();
                gson.toJson(sortMap(pronos), writer);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }




    }

    public static Map<String, DayPronos> sortMap(Map<String, DayPronos> dp) throws ParseException {
        Map<String, DayPronos> sortedMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Set<String> dateString = dp.keySet();
        List<String> lDate = new ArrayList<>();
        lDate.addAll(dateString);

        Collections.sort(lDate, new Comparator<String>() {
            public int compare(String s1, String s2) {
                try {
                    return sdf.parse(s1).compareTo(sdf.parse(s2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        for (String sortD : lDate) {
            sortedMap.put(sortD, dp.get(sortD));
        }
        return sortedMap;
    }

}
