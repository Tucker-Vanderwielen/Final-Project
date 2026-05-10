public class Problem 
{
    private int attempts;
    private boolean zoned;
    private boolean topped;
    private boolean attempted;

    public Problem() 
    {
        attempts = 0;
        zoned = false;
        attempted = false;
        topped = false;
    }

    public void reset(int attempts, boolean zoned, boolean topped) 
    {
    this.attempts = attempts;
    this.zoned = zoned;
    this.topped = topped;
    this.attempted = attempts > 0 || zoned || topped;
    }

    public void addAttempt() 
    {
        attempts++;
    }

    public void setZoned(boolean zoned) 
    {
        this.zoned = zoned;
    }

    public void setTopped(boolean topped) 
    {
        this.topped = topped;
        if (topped) this.zoned = true; // topping implies zone
    }
    public void setAttempted(boolean attempted) 
    {
        this.attempted = attempted;
    }

    public boolean isZoned() { return zoned; }
    public boolean isTopped() { return topped; }
    public boolean isAttempted() { return attempted; }
    public int getAttempts() { return attempts; }

    public double getScore() 
    {
        if (!zoned && !topped) return 0;

        double score = 0;
        if (zoned) score += 10.0;
        if (topped) score += 15.0;
        // First attempt is free, each additional deducts .1
        if (attempts > 1) {
            score -= (attempts - 1) * 0.1;
        }

        return score;
    }
}