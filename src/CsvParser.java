import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public static List<Person> fromCsv(String filePath) throws IOException, NegativeLifespanException, AmbiguousPersonException {
        List<Person> people = new ArrayList<>();
        List<String> seenNames = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Person person = Person.fromCsvLine(line);
                String fullName = person.getFirstName() + " " + person.getLastName();

                if (seenNames.contains(fullName)) {
                    throw new AmbiguousPersonException("Duplicate name found: " + fullName);
                }

                seenNames.add(fullName);
                people.add(person);
            }
        }

        return people;
    }
}
