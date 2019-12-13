import java.util.LinkedList;
import java.util.List;

public class LinesNumberSortred
{
    List<LineNumberAndStations> lines = new LinkedList<>();

    public List<LineNumberAndStations> getLineNumbersSorted() {
        return lines;
    }

    public void addLineNumber (LineNumberAndStations lineNumberAndStations)
    {
        this.lines.add(lineNumberAndStations);
    }
}
