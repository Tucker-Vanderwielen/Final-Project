public abstract class Participant 
{
    private String name;

    public Participant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double getScore();

    public String displayInfo() 
    {
        return getName() + ": " + getScore();
    }
}