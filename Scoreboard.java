import java.util.ArrayList;

public class Scoreboard 
{
    private String name;
    private int numProblems;
    private ArrayList<Climber> climbers;

    public Scoreboard(String name, int numProblems) 
    {
        this.name = name;
        this.numProblems = numProblems;
        climbers = new ArrayList<>();
    }

    public void addClimber(Climber c) 
    {
        climbers.add(c);
    }

    public int getNumProblems() 
    {
        return numProblems;
    }

    public String getName() 
    {
        return name;
    }
    public ArrayList<Climber> getClimbers() 
    {
    return climbers;
    }
    // Returns climbers sorted by score, highest first
    public ArrayList<Climber> getRankedClimbers() 
    {
        ArrayList<Climber> ranked = new ArrayList<>(climbers);
        ranked.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return ranked;
    }

    public ArrayList<Climber> getPossibleRankedClimbers() 
    {
        ArrayList<Climber> ranked = new ArrayList<>(climbers);
        ranked.sort((a, b) -> Double.compare(b.getMaxPossibleScore(), a.getMaxPossibleScore()));
        return ranked;
    }

    public int getRank(Climber climber) 
    {
        ArrayList<Climber> ranked = getRankedClimbers();
        return ranked.indexOf(climber) + 1;
    }

    public double getLeaderScore() 
    {
        if (climbers.isEmpty()) return 0;
        return getRankedClimbers().get(0).getScore();
    }

    // How many problems has this climber actually attempted/completed
    public int getProblemsClimbed(Climber climber) 
    {
        int count = 0;
        for (int i = 0; i < numProblems; i++) 
        {
            if (climber.getProblem(i).isAttempted()) count++;
        }
        return count;
    }

    // Returns the next problem index this climber hasn't touched yet
    public int getNextProblemIndex(Climber climber) 
    {
        for (int i = 0; i < numProblems; i++) 
        {
            if (!climber.getProblem(i).isAttempted()) return i;
        }
        return -1; // all problems attempted
    }

    public String getNeededString(Climber climber) 
{
    int nextIndex = getNextProblemIndex(climber);
    int currentRank = getRank(climber);
    if (nextIndex == -1) return climber.getName() + " has no problems remaining, their best rank possible is " + currentRank + ".";

    // Walk up from current rank to find the best reachable rank
    int bestRank = currentRank;
    int bestAttempts = -1;
    String bestType = "";

    for (int rank = 1; rank < currentRank; rank++) 
    {
        double needed = getScoreAtRank(climber, rank) - climber.getScore();

        for (int attempts = 20; attempts >= 1; attempts--) 
        {
            double zoneScore = 10.0 - (attempts - 1) * 0.1;
            if (zoneScore >= needed) 
            {
                bestRank = rank;
                bestAttempts = attempts;
                bestType = "zone";
                break;
            }
        }

        if (bestRank == rank) break; // found something, stop here

        for (int attempts = 20; attempts >= 1; attempts--) 
        {
            double topScore = 25.0 - (attempts - 1) * 0.1;
            if (topScore >= needed) 
            {
                bestRank = rank;
                bestAttempts = attempts;
                bestType = "top";
                break;
            }
        }

        if (bestRank == rank) break;
    }

    if (bestRank == currentRank) 
    {
        return climber.getName() + " cannot improve their rank on this problem.";
    }

    String attemptStr = (bestAttempts == 20)
    ? "a " + bestType + (bestType.equals("top") ? "" : " or a top")
    : "a " + bestType + " in " + bestAttempts + (bestAttempts == 1 ? " attempt" : " attempts") + (bestType.equals("top") ? "" : " or a top");

return climber.getName() + " needs " + attemptStr + " to reach rank " + bestRank + ".";
}
    // Returns the score the climber needs to beat to reach targetRank
    // (i.e. the score of whoever is currently at targetRank - 1)
    private double getScoreAtRank(Climber climber, int targetRank) {
        ArrayList<Climber> ranked = getRankedClimbers();
        if (targetRank <= 1) {
            // Need to beat the leader (unless they are the leader)
            Climber leader = ranked.get(0);
            if (leader == climber && ranked.size() > 1) {
                return ranked.get(1).getScore();
            }
            return leader.getScore() + 0.1; // needs to exceed leader
        }
        // Need to beat whoever is currently at targetRank - 1
        int idx = targetRank - 2; // 0-indexed, one spot above target
        if (idx >= ranked.size()) return 0;
        Climber blocker = ranked.get(idx);
        if (blocker == climber) {
            idx++;
            if (idx >= ranked.size()) return 0;
            blocker = ranked.get(idx);
        }
        return blocker.getScore() + 0.1;
    }

    public void printLeaderboard() {
        ArrayList<Climber> ranked = getRankedClimbers();
        System.out.println("==== " + name + " Leaderboard ====");
        System.out.printf("%-5s %-15s %-8s %-8s%n", "Rank", "Name", "Score", "Climbed");
        for (int i = 0; i < ranked.size(); i++) {
            Climber c = ranked.get(i);
            System.out.printf("%-5d %-15s %-8.1f %-8d%n",
                i + 1,
                c.getName(),
                c.getScore(),
                getProblemsClimbed(c));
        }
    }

    public void printPossibleLeaderboard() {
    ArrayList<Climber> ranked = getPossibleRankedClimbers();
    System.out.println("======= " + name + " Possible Leaderboard =======");
    System.out.printf("%-5s %-15s %-8s %-12s %-8s%n", "Rank", "Name", "Score", "Pts Left", "Climbed");
    for (int i = 0; i < ranked.size(); i++) {
        Climber c = ranked.get(i);
        System.out.printf("%-5d %-15s %-8.1f %-12.1f %-8d%n",
            i + 1,
            c.getName(),
            c.getScore(),
            (c.getMaxPossibleScore() - c.getScore()),
            getProblemsClimbed(c));
    }
}
}