import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.function.Function;
import java.util.function.Predicate;
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;


    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String parent1Name;
    private String parent2Name;
    private List<Person> parents;

    public Person(String firstName, String lastName, LocalDate birthDate, LocalDate deathDate, String parent1Name, String parent2Name) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.parent1Name = parent1Name;
        this.parent2Name = parent2Name;
        this.parents = new ArrayList<>();
    }

    public String toPlantUML(Function<String, String> postProcess, Predicate<Person> condition) {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        if (condition.test(this)) {
            String personLine = "object \"" + firstName + " " + lastName + "\" as " + getId() + "\n";
            sb.append(postProcess.apply(personLine));
        }
        for (Person parent : parents) {
            if (condition.test(parent)) {
                String parentLine = "object \"" + parent.firstName + " " + parent.lastName + "\" as " + parent.getId() + "\n";
                sb.append(postProcess.apply(parentLine));
            } else {
                sb.append("object \"" + parent.firstName + " " + parent.lastName + "\" as " + parent.getId() + "\n");
            }
            sb.append(parent.getId() + " --> " + getId() + "\n");
        }
        sb.append("@enduml\n");
        return sb.toString();
    }
    public static List<Person> filterBySubstring(List<Person> people, String substring) {
        List<Person> filtered = new ArrayList<>();
        for (Person person : people) {
            if ((person.firstName + " " + person.lastName).contains(substring)) {
                filtered.add(person);
            }
        }
        return filtered;
    }
    public static List<Person> sortByBirthYear(List<Person> people) {
        people.sort((p1, p2) -> p1.birthDate.compareTo(p2.birthDate));
        return people;
    }
    public static List<Person> sortDeceasedByLifespan(List<Person> people) {
        List<Person> deceased = new ArrayList<>();
        for (Person person : people) {
            if (person.deathDate != null) {
                deceased.add(person);
            }
        }
        deceased.sort((p1, p2) -> {
            int lifespan1 = p1.deathDate.getYear() - p1.birthDate.getYear();
            int lifespan2 = p2.deathDate.getYear() - p2.birthDate.getYear();
            return Integer.compare(lifespan2, lifespan1);
        });
        return deceased;
    }
    public static Person findOldestLivingPerson(List<Person> people) {
        return people.stream()
                .filter(person -> person.deathDate == null)
                .min((p1, p2) -> p1.birthDate.compareTo(p2.birthDate))
                .orElse(null);
    }
    private String getId() {
        return firstName + "_" + lastName;
    }
    public static Person fromCsvLine(String csvLine) throws NegativeLifespanException {
        String[] parts = csvLine.split(",");
        String firstName = parts[0].trim();
        String lastName = parts[1].trim();
        LocalDate birthDate = LocalDate.parse(parts[2].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate deathDate = parts[3].trim().isEmpty() ? null : LocalDate.parse(parts[3].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String parent1Name = parts.length > 4 ? parts[4].trim() : "";
        String parent2Name = parts.length > 5 ? parts[5].trim() : "";

        if (deathDate != null && deathDate.isBefore(birthDate)) {
            throw new NegativeLifespanException("Death date is before birth date for " + firstName + " " + lastName);
        }

        return new Person(firstName, lastName, birthDate, deathDate, parent1Name, parent2Name);
    }

    public static List<Person> fromCsv(String filePath) throws IOException, NegativeLifespanException, AmbiguousPersonException, ParentingAgeException {
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


        for (Person child : people) {
            List<Person> parents = new ArrayList<>();
            for (Person potentialParent : people) {
                if (child.getParent1Name().equals(potentialParent.getFirstName()) ||
                        child.getParent2Name().equals(potentialParent.getFirstName())) {
                    if (isValidParent(potentialParent, child)) {
                        parents.add(potentialParent);
                    }
                }
            }
            child.setParents(parents);
        }

        return people;
    }

    private static boolean isValidParent(Person parent, Person child) throws ParentingAgeException {

        if (parent.getBirthDate().plusYears(15).isAfter(child.getBirthDate())) {
            throw new ParentingAgeException("Parent " + parent.getFirstName() + " " + parent.getLastName() + " is younger than 15 years when child " + child.getFirstName() + " " + child.getLastName() + " is born.");
        }


        if (parent.getDeathDate() != null && parent.getDeathDate().isBefore(child.getBirthDate())) {
            throw new ParentingAgeException("Parent " + parent.getFirstName() + " " + parent.getLastName() + " is dead when child " + child.getFirstName() + " " + child.getLastName() + " is born.");
        }

        return true;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getParent1Name() {
        return parent1Name;
    }

    public void setParent1Name(String parent1Name) {
        this.parent1Name = parent1Name;
    }

    public String getParent2Name() {
        return parent2Name;
    }

    public void setParent2Name(String parent2Name) {
        this.parent2Name = parent2Name;
    }

    public List<Person> getParents() {
        return parents;
    }

    public void setParents(List<Person> parents) {
        this.parents = parents;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", deathDate=" + deathDate +
                ", parent1Name='" + parent1Name + '\'' +
                ", parent2Name='" + parent2Name + '\'' +
                ", parents=" + parents +
                '}';
    }
    public static void toBinaryFile(List<Person> people, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(people);
        }
    }

    public static List<Person> fromBinaryFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<Person>) ois.readObject();
        }
    }
}

