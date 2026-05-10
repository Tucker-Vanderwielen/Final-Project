/*

// ============================================================
// FILE: TestClimbing.java
// DESCRIPTION: Test driver for Problem, Climber, Participant,
//              and Scoreboard classes.
// ============================================================

public class TestClimbing
{
    // Prints PASS or FAIL with a descriptive label
    static void check(String label, boolean condition)
    {
        System.out.println((condition ? "[PASS] " : "[FAIL] ") + label);
    }

    public static void main(String[] args)
    {
        // ====================================================
        // SECTION 1: Problem
        // ====================================================
        System.out.println("--- Problem Tests ---");

        // Untouched problem should score 0
        Problem p1 = new Problem();
        check("Untouched problem scores 0.0", p1.getScore() == 0.0);
        check("Untouched problem is not attempted", !p1.isAttempted());

        // Zone only, 1 attempt -> 10.0
        Problem p2 = new Problem();
        p2.reset(1, true, false);
        check("Zone in 1 attempt = 10.0", p2.getScore() == 10.0);

        // Zone in 3 attempts -> 10.0 - 0.2 = 9.8
        Problem p3 = new Problem();
        p3.reset(3, true, false);
        check("Zone in 3 attempts = 9.8", Math.abs(p3.getScore() - 9.8) < 0.001);

        // Top in 1 attempt -> 25.0
        Problem p4 = new Problem();
        p4.reset(1, true, true);
        check("Top in 1 attempt = 25.0", p4.getScore() == 25.0);

        // Top in 2 attempts -> 24.9
        Problem p5 = new Problem();
        p5.reset(2, true, true);
        check("Top in 2 attempts = 24.9", Math.abs(p5.getScore() - 24.9) < 0.001);

        // setTopped() should automatically set zoned = true
        Problem p6 = new Problem();
        p6.reset(1, false, false);
        p6.setTopped(true);
        check("setTopped() implies zone", p6.isZoned());

        // addAttempt() increments the counter
        Problem p7 = new Problem();
        p7.addAttempt();
        p7.addAttempt();
        check("addAttempt() twice -> getAttempts() = 2", p7.getAttempts() == 2);

        // ====================================================
        // SECTION 2: Climber (also tests Participant base)
        // ====================================================
        System.out.println("\n--- Climber / Participant Tests ---");

        Climber c1 = new Climber("Alex", 3);
        check("New climber score = 0.0", c1.getScore() == 0.0);
        check("Climber name = Alex", c1.getName().equals("Alex"));
        check("Climber numProblems = 3", c1.getNumProblems() == 3);

        // Problem 0: zone in 1 attempt  (10.0)
        // Problem 1: top  in 2 attempts (24.9)
        // Problem 2: untouched          (0.0)
        // Expected total: 34.9
        c1.getProblem(0).reset(1, true, false);
        c1.getProblem(1).reset(2, true, true);
        check("Climber total score = 34.9", Math.abs(c1.getScore() - 34.9) < 0.001);

        // Max possible: problem 2 still available, best case = 25.0
        // Expected: 34.9 + 25.0 = 59.9
        check("Climber max possible score = 59.9",
              Math.abs(c1.getMaxPossibleScore() - 59.9) < 0.001);

        // displayInfo() comes from Participant and should include the name
        check("displayInfo() contains name", c1.displayInfo().contains("Alex"));

        // ====================================================
        // SECTION 3: Scoreboard
        // ====================================================
        System.out.println("\n--- Scoreboard Tests ---");

        Scoreboard sb = new Scoreboard("Test Round", 3);

        Climber alex   = new Climber("Alex",   3);
        Climber brooke = new Climber("Brooke", 3);
        Climber casey  = new Climber("Casey",  3);

        // Alex:   top in 1 attempt on P0      -> 25.0
        // Brooke: zone/1 on P0, top/3 on P1   -> 10.0 + 24.8 = 34.8
        // Casey:  no attempts                 ->  0.0
        alex.getProblem(0).reset(1, true, true);
        brooke.getProblem(0).reset(1, true, false);
        brooke.getProblem(1).reset(3, true, true);

        sb.addClimber(alex);
        sb.addClimber(brooke);
        sb.addClimber(casey);

        // Expected ranking: Brooke (1st, 34.8), Alex (2nd, 25.0), Casey (3rd, 0.0)
        check("Brooke ranked 1st",            sb.getRank(brooke) == 1);
        check("Alex ranked 2nd",              sb.getRank(alex)   == 2);
        check("Casey ranked 3rd",             sb.getRank(casey)  == 3);
        check("Leader score = 34.8",          Math.abs(sb.getLeaderScore() - 34.8) < 0.001);

        check("Alex problems climbed = 1",    sb.getProblemsClimbed(alex)  == 1);
        check("Casey problems climbed = 0",   sb.getProblemsClimbed(casey) == 0);
        check("Alex next problem index = 1",  sb.getNextProblemIndex(alex) == 1);
        check("Casey next problem index = 0", sb.getNextProblemIndex(casey) == 0);

        // Climber who finished all problems: next index should be -1
        Climber done = new Climber("Done", 3);
        done.getProblem(0).reset(1, true, true);
        done.getProblem(1).reset(1, true, true);
        done.getProblem(2).reset(1, true, true);
        sb.addClimber(done);
        check("All problems done -> next index = -1", sb.getNextProblemIndex(done) == -1);

        // ====================================================
        // SECTION 4: Scoreboard display (visual check)
        // ====================================================
        System.out.println("\n--- Leaderboard Output ---");
        sb.printLeaderboard();
        System.out.println();
        sb.printPossibleLeaderboard();

        // ====================================================
        // SECTION 5: getNeededString
        // ====================================================
        System.out.println("\n--- getNeededString Tests ---");

        // Casey is in last and has problems left — should get advice
        String caseyMsg = sb.getNeededString(casey);
        System.out.println(caseyMsg);
        check("Casey needed string is not empty", !caseyMsg.isEmpty());

        // "Done" climber has no problems left — should report locked rank
        String doneMsg = sb.getNeededString(done);
        System.out.println(doneMsg);
        check("Done climber message mentions 'no problems remaining'",
              doneMsg.contains("no problems remaining"));
    }
}

 */