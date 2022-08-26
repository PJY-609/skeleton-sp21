import java.util.Comparator;

public class DMSComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal o1, Animal o2){
        int first = o1.speak(new Animal());
        int second = o2.speak(new Animal());
        int third = o1.speak(new Dog());
        int fourth = o2.speak(new Dog());

        if (first == second && third == fourth) {
            return 0;
        } else if (first > second || third > fourth) {
            return 1;
        } else {
            return-1;
        }
    }

    public static void main(String[] args){
        Animal animal = new Animal();
        Animal dog = new Dog();
        Animal poodle = new Poodle();

        DMSComparator comparator = new DMSComparator();

        System.out.println(comparator.compare(animal, dog) + ""); // negative number
        System.out.println(comparator.compare(dog, dog) + ""); // zero
        System.out.println(comparator.compare(poodle, dog) + ""); // positive number
    }
}
