import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LineNumberAndStations
{
    private String lineNumber;
    private List<String> stations = new LinkedList<>();

    public LineNumberAndStations()
    {
    }

    public String getLineNumber()
    {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber)
    {
        this.lineNumber = lineNumber;
    }

    public List<String> getStations()
    {
        return stations;
    }

    public void setStations(Collection<String> stations)
    {
        this.stations.addAll(stations);
    }

    public void setStation(String station)
    {
        this.stations.add(station);
    }
}
