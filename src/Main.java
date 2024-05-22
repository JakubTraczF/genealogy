import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.io.IOException;
import java.time.LocalDate;
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        PlantUMLRunner.setPlantUMLJarPath("path/to/plantuml.jar");

        Person person1 = new Person("John", "Doe", LocalDate.of(1980, 1, 1), null, "Jane", "Doe");
        Person person2 = new Person("Jane", "Doe", LocalDate.of(1960, 1, 1), LocalDate.of(2000, 1, 1), "", "");
        person1.getParents().add(person2);

        List<Person> people = Arrays.asList(person1, person2);

        // Zadanie 2
        String umlData = person1.toPlantUML();
        PlantUMLRunner.generateDiagram(umlData, "output", "person1");

        // Zadanie 3
        String umlDataAll = Person.toPlantUML(people);
        PlantUMLRunner.generateDiagram(umlDataAll, "output", "all_people");

        // Zadanie 8
        Function<String, String> colorize = line -> line.replace("object", "object #Yellow");
        String umlDataColor = person1.toPlantUML(colorize);
        PlantUMLRunner.generateDiagram(umlDataColor, "output", "person1_color");

        // Zadanie 9
        Predicate<Person> isNamedDoe = person -> person.getLastName().equals("Doe");
        String umlDataCondition = person1.toPlantUML(colorize, isNamedDoe);
        PlantUMLRunner.generateDiagram(umlDataCondition, "output", "person1_condition");
    }
}
