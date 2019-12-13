import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Station implements Comparable<Station>
{
    private Line line;
    private String name;

    public Station(String name, Line line)
    {
        this.name = name;
        this.line = line;
    }

    public Line getLine()
    {
        return line;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int compareTo(Station station)
    {
//        String regex = "\\D+";
//        int number1ToCompare, number2ToCompare;
//        boolean foundOne, foundTwo;
//
//        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
//        Matcher matcher1 = pattern.matcher(line.getNumber());
//        Matcher matcher2 = pattern.matcher(station.line.getNumber());
//        foundOne = matcher1.find();
//        foundTwo = matcher2.find();
//        if (foundOne || foundTwo)
//        {
//            if (foundOne && foundTwo)
//            {
//                number1ToCompare = Integer.parseInt(line.getNumber().substring(0, line.getNumber().length() - matcher1.group(0).length()));
//                number2ToCompare = Integer.parseInt(station.line.getNumber().substring(0, station.line.getNumber().length() - matcher2.group(0).length()));
//            }
//            else if (foundOne)
//            {
//                number1ToCompare = Integer.parseInt(line.getNumber().substring(0, line.getNumber().length() - matcher1.group(0).length()));
//                number2ToCompare = Integer.parseInt(station.line.getNumber());
//            }
//            else
//            {
//                number1ToCompare = Integer.parseInt(line.getNumber());
//                number2ToCompare = Integer.parseInt(station.line.getNumber().substring(0, station.line.getNumber().length() - matcher2.group(0).length()));
//            }
//
//            if (number1ToCompare == number2ToCompare)
//            {
//                return line.getNumber().compareTo(station.line.getNumber());
//            }
//            else
//            {
//                return Integer.compare(number1ToCompare, number2ToCompare);
//            }
//
//        }
//        else
//        {
//            return Integer.compare(Integer.parseInt(line.getNumber()), Integer.parseInt(station.line.getNumber()));
//        }

        int lineComparison = line.compareTo(station.getLine());
        if(lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        return compareTo((Station) obj) == 0;
    }

    @Override
    public String toString()
    {
        return name;
    }
}