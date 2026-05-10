public class Climber extends Participant 
{
    private Problem[] problems;

    public Climber(String name, int numProblems) 
    {
        super(name);
        problems = new Problem[numProblems];
        for (int i = 0; i < numProblems; i++) 
        {
            problems[i] = new Problem();
        }
    }

    @Override
    public double getScore() 
    {
        double total = 0;
        for (Problem p : problems) 
        {
            total += p.getScore();
        }
        return total;
    }

    public Problem getProblem(int index) 
    {
        return problems[index];
    }

    public int getNumProblems() 
    {
        return problems.length;
    }

    public double getMaxPossibleScore() 
    {
        double total = 0;
        for (Problem p : problems) 
        {
            if (p.isAttempted())
            {
                total += p.getScore();
            }
            else
            {
                // Best case: top in 1 attempt on remaining problems
                total += (25.0 - (p.getAttempts() * 0.1));
            }
        }
        return total;
    }
}