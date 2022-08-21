public class Athletes {
    static class Person {
        void speakTo(Person other) { System.out.println("kudos"); }
        void watch(SoccerPlayer other) { System.out.println("wow"); }
    }

    static class Athlete extends Person {
        void speakTo(Athlete other) { System.out.println("take notes"); }
        void watch(Athlete other) { System.out.println("game on"); }
    }

    static class SoccerPlayer extends Athlete {
        void speakTo(Athlete other) { System.out.println("respect"); }
        void speakTo(Person other) { System.out.println("hmph"); }
    }

    public static void main(String[] args){
        Person itai = new Person();

//        SoccerPlayer shivani = new Person(); // CE

        Athlete sohum = new SoccerPlayer();

        Person jack = new Athlete();

        Athlete anjali = new Athlete();

        SoccerPlayer chirasree = new SoccerPlayer();

        itai.watch(chirasree);

        // jack.watch(sohum);
        jack.watch((SoccerPlayer) sohum);
        ((Athlete) jack).watch(sohum);

        itai.speakTo(sohum);

        jack.speakTo(anjali);

        anjali.speakTo(chirasree);

        sohum.speakTo(itai);

        chirasree.speakTo((SoccerPlayer) sohum);

//        sohum.watch(itai); // compilation error

//        sohum.watch((Athlete) itai); // runtime error

        ((Athlete) jack).speakTo(anjali);

        jack.speakTo(chirasree);

        ((Person) chirasree).speakTo(itai);
    }
}
