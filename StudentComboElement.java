public class StudentComboElement
{
  private String name;
  private String score;

  public StudentComboElement(String name)
  {
    this(name, " ");
  }

  public StudentComboElement(String name, String score)
  {
    this.name = name;
    this.score = score;
  }

  public String getName()
  {
    return name;
  }

  public String getScore()
  {
    return this.score;
  }

  public void setScore(String score)
  {
    this.score = score;
  }

  public String toString()
  {
    return "["+score+"] " + name;
  }

  public boolean equals(Object other)
  {
    if(null == other) return false;
    if(other instanceof String) return other.equals(getName());

    if(!(other instanceof StudentComboElement)) return false;

    StudentComboElement o = (StudentComboElement)other;
    if(null == name) return name == o.getName();
    return name.equals(o.getName());
  }
}
