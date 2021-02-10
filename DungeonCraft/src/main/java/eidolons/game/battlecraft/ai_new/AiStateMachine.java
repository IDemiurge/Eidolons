package eidolons.game.battlecraft.ai_new;

public class AiStateMachine {
    /*
    support INTENTIONS
    easy to debug (including VISUAL)
    customizable with simple parametration
    good grasp over complex abilities and spells
    solid basics - positioning, waiting, modes,

     */

    public enum ai_parameter {
        aggression, //
        restlessness, // high means AI wants to change strategy even if it's good for the moment as is
        intelligence, // core parameter for rational optimization, the higher - the closer to ideal AI
        courage, // there will be some internal checks when need to atk or gets a blow / ally-kill
        teamwork, // switch strategy together with leader/team or not
        empathy //will they help allies? will they hit downed enemies?
    }

    public enum ai_template {
        brute(100, 80, 25, 80, 35, 10),
        warrior(70, 70, 45, 70, 45, 30),
        brawler(60, 80, 35, 80, 15, 50),
        natural(50, 50, 35, 50, 35, 35),
        predator(70, 40, 45, 70, 15, 10),
        sneak(60, 40, 65, 30, 45, 20),
        professional(50, 15, 75, 65, 45, 15),
        soldier(70, 40, 25, 50, 75, 30),
        support(20, 20, 65, 30, 100, 60),
        mastermind(40, 50, 100, 40, 65, 20),
        sissy(10, 30, 50, 20, 75, 100),
        ;

        ai_template(float aggression, float restlessness, float intelligence, float courage, float teamwork, float empathy) {
            this.aggression = aggression;
            this.restlessness = restlessness;
            this.intelligence = intelligence;
            this.courage = courage;
            this.teamwork = teamwork;
            this.empathy = empathy;
        }

        float aggression, restlessness, intelligence, courage, teamwork, empathy;
    }

    public enum ai_strategy {
        Berserk,
        Offense,
        Engage,
        Defense,
        Retreat,
        Flight,
        Panic
    }
}
