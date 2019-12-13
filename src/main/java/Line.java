import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Line implements Comparable<Line>
{
    private String number;
    private String name;
    private String color;
    private List<Station> stations;

    public Line(String number, String name, String color)
    {
        this.number = number;
        this.name = name;
        this.color = color;
        stations = new ArrayList<>();
    }

    public String getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void addStation(Station station)
    {
        stations.add(station);
    }

    public List<Station> getLineStations()
    {
        return stations;
    }

    @Override
    public int compareTo(Line line)
    {
        String regex = "\\D+";
        int number1ToCompare, number2ToCompare;
        boolean foundOne, foundTwo;

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher1 = pattern.matcher(number);
        Matcher matcher2 = pattern.matcher(line.getNumber());
        foundOne = matcher1.find();
        foundTwo = matcher2.find();
        if (foundOne || foundTwo)
        {
            if (foundOne && foundTwo)
            {
                number1ToCompare = Integer.parseInt(number.substring(0, number.length() - matcher1.group(0).length()));
                number2ToCompare = Integer.parseInt(line.getNumber().substring(0, line.getNumber().length() - matcher2.group(0).length()));
            }
            else if (foundOne)
            {
                number1ToCompare = Integer.parseInt(number.substring(0, number.length() - matcher1.group(0).length()));
                number2ToCompare = Integer.parseInt(line.getNumber());
            }
            else
            {
                number1ToCompare = Integer.parseInt(number);
                number2ToCompare = Integer.parseInt(line.getNumber().substring(0, line.getNumber().length() - matcher2.group(0).length()));
            }

            if (number1ToCompare == number2ToCompare)
            {
                return number.compareTo(line.getNumber());
            }
            else
            {
                return Integer.compare(number1ToCompare, number2ToCompare);
            }

        }
        else
        {
            return Integer.compare(Integer.parseInt(number), Integer.parseInt(line.getNumber()));
        }


    }

    @Override
    public boolean equals(Object obj)
    {
        return compareTo((Line) obj) == 0;
    }

    @Override
    public String toString()
    {
        return this.number + " " + this.name + " " + this.color;
    }
}