import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main
{
    //private static ArrayList<Station> stations = new ArrayList<>();
    //private static ArrayList<Line> lines = new ArrayList<>();
    private static JSONObject obj = new JSONObject();
    private static JSONArray list = new JSONArray();

    public static void main(String[] args) {


        HashMap<String, Line> lines = new HashMap<>();

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
        { //first row is the col names so skip it.
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
                    if (lines.containsKey(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)))) //проверка на существовании линии в списке
                    { //если есть, получаем данные для проверки цвета -> для коррекции
                        lineToCheck = lines.get(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)));
                        if(lineToCheck.getColor().equals("#000000"))
                        {//если не был определен цвет, меняем на правильный цвет
                            lines.replace(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
                                    new Line(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
                                            colLineDetails.get(0).select("span").attr("title"),
                                            (colLineDetails.get(0).attr("style").length() > 0 ? colLineDetails.get(0).attr("style").replaceAll(("background:") , "") : "#000000"))); // #000000 используется, если при парсинге не был обнаружен цвет
                        }
                    }
                    else
                    {//добавляем новую линию
                        lines.put(colLineDetails.get(0).text().substring(1, (colLineDetails.get(0).text().length()-2)),
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
                    if (lines.containsKey(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)))) //проверка на существовании линии в списке
                    { // если есть, получаем данные для проверки цвета -> для коррекции
                        lineToCheck = lines.get(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)));
                        if (lineToCheck.getColor().equals("#000000"))
                        { // если не был определен цвет, меняем на правильный цвет
                            lines.replace(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
                                    new Line(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
                                            colLineDetails.get(0).select("span").attr("title"),
                                            (colLineDetails.get(0).attr("style").length() > 0 ? colLineDetails.get(0).attr("style").replaceAll(("background:") , "") : "#000000"))); // #000000 используется, если при парсинге не был обнаружен цвет
                        }
                    }
                    else
                    { // добавляем новую линию
                        lines.put(colLineDetails.get(0).text().substring(0, (colLineDetails.get(0).text().length()-2)),
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
            linesToEdit = lines.get(fragments[0]);
            linesToEdit.addStation(new Station(fragments[1], lines.get(fragments[0])));

        }

        parseLinesToJSON(obj, lines);
        parseStationsToJSON(obj, lines);

        try (FileWriter file = new FileWriter("data/MoscowMetro.json")) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Parsing finished!");

    }

    public static void parseLinesToJSON(JSONObject jsonObject, Map<String, Line> lines)
    {
        JSONArray list = new JSONArray();
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        for (String key : lines.keySet())
        {
            JSONObject objectDetails = new JSONObject();
            objectDetails.put("number", lines.get(key).getNumber());
            objectDetails.put("name", lines.get(key).getName());
            objectDetails.put("color", lines.get(key).getColor());

            jsonObjectArrayList.add(objectDetails);
        }
        jsonObjectArrayList.stream().forEach(jo -> list.add(jo));
        jsonObject.put("Lines", list);
    }

    public static void parseStationsToJSON(JSONObject jsonObject, Map<String, Line> lines)
    {

        JSONObject objectDetails = new JSONObject();
        Line lineDetail;
        for (String key : lines.keySet())
        {
            JSONArray list = new JSONArray();
            lineDetail = lines.get(key);
            lineDetail.getLineStations().stream().forEach(station -> list.add(station.getName()));
            objectDetails.put(key, list);
        }
        System.out.println(objectDetails.toString());
        jsonObject.put("stations", objectDetails);
    }
}
