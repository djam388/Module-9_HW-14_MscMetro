import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class Main
{
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static JSONObject obj = new JSONObject();
    private static JSONArray list = new JSONArray();
    private static HashMap<String, Line> parsedLines = new HashMap<>();

    public static void main(String[] args) {


        HashMap<String, Line> linesParsed = new HashMap<>();
        ArrayList<Line> sortedLinesLA = new ArrayList<>();


        String htmlString = null;
        try {
            htmlString = Jsoup.connect("https://ru.wikipedia.org/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA_%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D0%B9_%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_%D0%BC%D0%B5%D1%82%D1%80%D0%BE%D0%BF%D0%BE%D0%BB%D0%B8%D1%82%D0%B5%D0%BD%D0%B0")
                    .maxBodySize(0)
                    .get()
                    .html();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(htmlString);
        Element table = doc.select("table").get(3);
        Elements rows = table.select("tr");

        ArrayList<String> listStations = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++)
        {
            Element row = rows.get(i);

            Elements colLineDetails = row.select("td");

//            System.out.println(i + ": "
//                    + colLineDetails.get(0).text() //LineNumber
//                    + "->> "
//                    + colLineDetails.get(0).attr("style") //LineColor
//                    + "->> "
//                    + colLineDetails.get(0).select("span").attr("title") //LineDescription
//                    + "->> "
//                    + colLineDetails.get(1).select("a").attr("title") //StationDescription
//                    + "->> "
//                    + colLineDetails.get(1).select("a").attr("title").indexOf("("));

            int foundIndex = colLineDetails.get(1).select("a").attr("title").indexOf("(") > 0 ?
                    colLineDetails.get(1).select("a").attr("title").indexOf("(") :
                    colLineDetails.get(1).select("a").attr("title").length();

            Line lineToCheck;

            if (!colLineDetails.get(0).text().contains(" ")) // проверка на присутствие другой линии для одной станции
            { // если одна линия
                if (colLineDetails.get(0).text().substring(0,1).equals("0")) // проверяем есть ли "0" перед номером линии
                { // если есть, убираем "0"
                    listStations.add(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)) //формируется список станций с номером линии
                                    + ", "
                                    + colLineDetails.get(1).select("a").attr("title").substring(0, foundIndex - 1));
                    if (linesParsed.containsKey(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)))) //проверка на существовании линии в списке
                    { //если есть, получаем данные для проверки цвета -> для коррекции
                        lineToCheck = linesParsed.get(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)));
                        if(lineToCheck.getColor().equals("#000000"))
                        {//если не был определен цвет, меняем на правильный цвет
                            linesParsed.replace(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
                                    new Line(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
                                            colLineDetails.get(0).select("span").attr("title"),
                                            (colLineDetails.get(0).attr("style").length() > 0 ? colLineDetails.get(0).attr("style").replaceAll(("background:") , "") : "#000000"))); // #000000 используется, если при парсинге не был обнаружен цвет
                        }
                    }
                    else
                    {//добавляем новую линию
                        linesParsed.put(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
                                new Line(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
                                        colLineDetails.get(0).select("span").attr("title"),
                                        (colLineDetails.get(0).attr("style").length() > 0 ? colLineDetails.get(0).attr("style").replaceAll(("background:") , "") : "#000000"))); // #000000 используется, если при парсинге не был обнаружен цвет
                    }


                }
                else
                {//используем номер линии без корректировки
                    listStations.add(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)) //формируется список станций с номером линии
                            + ", "
                            + colLineDetails.get(1).select("a").attr("title").substring(0, foundIndex - 1));
                    if (linesParsed.containsKey(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)))) //проверка на существовании линии в списке
                    { // если есть, получаем данные для проверки цвета -> для коррекции
                        lineToCheck = linesParsed.get(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)));
                        if (lineToCheck.getColor().equals("#000000"))
                        { // если не был определен цвет, меняем на правильный цвет
                            linesParsed.replace(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
                                    new Line(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
                                            colLineDetails.get(0).select("span").attr("title"),
                                            (colLineDetails.get(0).attr("style").length() > 0 ? colLineDetails.get(0).attr("style").replaceAll(("background:") , "") : "#000000"))); // #000000 используется, если при парсинге не был обнаружен цвет
                        }
                    }
                    else
                    { // добавляем новую линию
                        linesParsed.put(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
                                new Line(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
                                        colLineDetails.get(0).select("span").attr("title"),
                                        (colLineDetails.get(0).attr("style").length() > 0 ? colLineDetails.get(0).attr("style").replaceAll(("background:") , "") : "#000000")));
                    }
                }
            }
            else
            { // если станция относится к двум линия, то разбиваем поле с двумя значениями на отдельные фрагменты
                String[] fragments = colLineDetails.get(0).text().split(" ");
                listStations.add(fragments[0] + ", " + colLineDetails.get(1).select("a").attr("title").substring(0, foundIndex - 1));
                listStations.add(fragments[1].substring(0, 2) + ", " + colLineDetails.get(1).select("a").attr("title").substring(0, foundIndex - 1));
            }
        }

        Line linesToEdit;

        for (String station : listStations) // добавляем станции к линиям соответственно
        {
            String[] fragments = station.split(", ");
            linesToEdit = linesParsed.get(fragments[0]);
            linesToEdit.addStation(new Station(fragments[1], linesParsed.get(fragments[0])));

        }

        for (String key : linesParsed.keySet()) //новый ArrayList с линиями для сортировки
        {
            sortedLinesLA.add(linesParsed.get(key));
        }

        Collections.sort(sortedLinesLA); //сортировка по номерам линий





        System.out.println("Sorting finished!");


        writeLinesToJSON(obj, sortedLinesLA);
        writeStationsToJSON(obj, sortedLinesLA);

        writeSortedLinesToJSON(sortedLinesLA);




        try (FileWriter file = new FileWriter("data/MoscowMetroSimpleJSON.json")) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Writing to JSON file is finished!");
        readFromJSON();
        System.out.println("Reading from JSON file is finished!");
        int stationsQuantity = 0;
        for (String key : parsedLines.keySet())
        {
            stationsQuantity += parsedLines.get(key).getLineStations().size();
        }
        System.out.println("Total lines number: " + parsedLines.size()
                + " ->> Total stations number: " + stationsQuantity);

    }

    public static void writeSortedLinesToJSON(List<Line> linesSorted)
    {
        LinesNumberSortred lines = new LinesNumberSortred();

        for (Line line : linesSorted)
        {
            LineNumberAndStations lineNumberAndStations = new LineNumberAndStations();
            lineNumberAndStations.setLineNumber(line.getNumber());
            for (Station station : line.getLineStations())
            {
                lineNumberAndStations.setStation(station.getName());
            }
            lines.addLineNumber(lineNumberAndStations);
        }

        try (FileWriter writer = new FileWriter("data/MoscowMetroSorted.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(lines, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void writeLinesToJSON(JSONObject jsonObject, ArrayList<Line> lines)
    {
        JSONArray list = new JSONArray();
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++)
        {
            JSONObject objectDetails = new JSONObject();
            objectDetails.put("number", lines.get(i).getNumber());
            objectDetails.put("name", lines.get(i).getName());
            objectDetails.put("color", lines.get(i).getColor());

            jsonObjectArrayList.add(objectDetails);
        }
        jsonObjectArrayList.stream().forEach(jo -> list.add(jo));
        jsonObject.put("lines", list);
    }

    public static void writeStationsToJSON(JSONObject jsonObject, ArrayList<Line> lines)
    {

        ArrayList<JSONObject>  objectDetails = new ArrayList<>();
        JSONObject mergedObject = new JSONObject();
        Boolean objectCreated = false;
        JSONObject objectDetail2 = new JSONObject();
        JSONObject objectDetail = new JSONObject();
        for (int i = 0; i < lines.size(); i++) {
            JSONArray list = new JSONArray();


            {
                lines.get(i).getLineStations().stream().forEach(station -> list.add(station.getName()));
                objectDetail.put(lines.get(i).getNumber(), list);

            }
        }

        jsonObject.put("stations", objectDetail);

    }

    public static void readFromJSON()
    {
        try
        {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile("data/MoscowMetroSimpleJSON.json"));

            JSONArray linesArray = (JSONArray) jsonData.get("lines");
            parseLines(linesArray);

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            parseStations(stationsObject);

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getJsonFile(String path)
    {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> builder.append(line));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void parseStations(JSONObject stationsObject)
    {
        //stationsObject.keySet().forEach(lineNumberObject ->
        for (Object lineNumberObject : stationsObject.keySet())
        {
            String lineNumber = (String) lineNumberObject;

            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            for (Object stationObject : stationsArray)
            {
                Station station = new Station((String) stationObject, parsedLines.get(lineNumber));
                parsedLines.get(lineNumber).addStation(station);
            }
        }
    }

    private static void parseLines(JSONArray linesArray)
    {
        linesArray.forEach(lineObject -> {
            JSONObject lineJsonObject = (JSONObject) lineObject;
            Line line = new Line(
                    (String) lineJsonObject.get("number"),
                    (String) lineJsonObject.get("name"),
                    (String) lineJsonObject.get("color")
            );
            parsedLines.put(line.getNumber(), line);
        });
    }


}
