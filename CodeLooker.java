import javax.swing.*;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class CodeLooker implements ActionListener, KeyListener
{
  private JFrame frame;
  private JTextArea codebox;
  private JScrollPane scrollbox;
  private JTextField pathToLabField;
  private JTextField defaultFileField;
  private FileScroller<String> fileScroller;
  private FileScroller<StudentComboElement> studentScroller;
  private JLabel studentCounter;  // "# of #"

  private JFileChooser fc;

  private File settingsFile;
  private File gradesFile;

  private File lab;
  private File studentFolder;

  private Set<Integer> pressedKeys;


  public CodeLooker()
  {
    pressedKeys = new HashSet<>();

    settingsFile = new File("settings.ini");

    fc = new JFileChooser();
    fc.addActionListener(this);
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    frame = new JFrame("Code Looker Atter");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800, 600);

    frame.getContentPane().setLayout(new BorderLayout());

    frame.getContentPane().add(createCodeViewComponent(), BorderLayout.CENTER);
    frame.getContentPane().add(createControlPanelComponent(), BorderLayout.EAST);

    frame.addKeyListener(this);
    frame.setFocusable(true);
    frame.setFocusTraversalKeysEnabled(false);

    frame.setVisible(true);

    triggerLoadSettings();
  }

  private Component createCodeViewComponent()
  {
    codebox = new JTextArea();
    scrollbox = new JScrollPane(codebox);

    codebox.setEditable(false);
    codebox.setFocusable(false);
    scrollbox.setFocusable(false);

    return scrollbox;
  }

  private Component createControlPanelComponent()
  {
    JPanel controlpane = new JPanel(new BorderLayout());

    controlpane.add(createFolderBrowserComponent(), BorderLayout.NORTH);
    controlpane.add(createStudentFileBrowserComponent(), BorderLayout.CENTER);

    return controlpane;
  }

  private Component createFolderBrowserComponent()
  {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel label = new JLabel("Folder");
    pathToLabField = new JTextField();
    pathToLabField.setActionCommand("labpath");
    pathToLabField.addActionListener(this);

    JButton button = new JButton("Browse");
    button.addActionListener(this);
    button.setActionCommand("browse");

    panel.add(label, BorderLayout.WEST);
    panel.add(pathToLabField, BorderLayout.CENTER);
    panel.add(button, BorderLayout.EAST);

    return panel;
  }

  private Component createStudentFileBrowserComponent()
  {
    JPanel panel =  new JPanel(new GridLayout(1, 2));

    panel.add(createStudentBrowserComponent());
    panel.add(createFileBrowserComponent());

    return panel;
  }

  private Component createFileBrowserComponent()
  {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel label = new JLabel("File");
    defaultFileField = new JTextField();
    defaultFileField.setActionCommand("defaultfile");
    defaultFileField.addActionListener(this);

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(label, BorderLayout.WEST);
    topPanel.add(defaultFileField, BorderLayout.CENTER);

    fileScroller = new FileScroller<>("filescroller");
    fileScroller.addActionListener(this);
    Component middlePanel = fileScroller.getComponent();

    JPanel bottomPanel = new JPanel();
    JButton passButton = new JButton("Pass");
    JButton failButton = new JButton("Fail");

    passButton.addActionListener(this);
    failButton.addActionListener(this);

    passButton.setActionCommand("pass");
    failButton.setActionCommand("fail");

    bottomPanel.add(passButton);
    bottomPanel.add(failButton);

    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(middlePanel, BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    return panel;
  }

  private Component createStudentBrowserComponent()
  {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel label = new JLabel("Student");
    studentCounter = new JLabel("# of #");

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(label, BorderLayout.WEST);
    topPanel.add(studentCounter, BorderLayout.CENTER);

    studentScroller = new FileScroller<>("studentscroller");
    studentScroller.addActionListener(this);
    Component middlePanel = studentScroller.getComponent();

    JPanel bottomPanel = new JPanel();
    JButton prevButton = new JButton("< Prev");
    JButton nextButton = new JButton("Next >");

    prevButton.addActionListener(this);
    nextButton.addActionListener(this);

    prevButton.setActionCommand("prev");
    nextButton.setActionCommand("next");

    bottomPanel.add(prevButton);
    bottomPanel.add(nextButton);

    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(middlePanel, BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    return panel;
  }

  /********************
   * ACTION LISTENERS *
   ********************/
  public void actionPerformed(ActionEvent e)
  {
    switch(e.getActionCommand())
    {
      case "labpath":
        triggerOpenLabFolder();
        break;
      case "defaultfile":
        triggerSetDefaultFile();
        break;
      case "pass":
        triggerPass();
        break;
      case "fail":
        triggerFail();
        break;
      case "prev":
        triggerPrev();
        break;
      case "next":
        triggerNext();
        break;
      case "filescroller":
        triggerNewFile();
        break;
      case "studentscroller":
        triggerNewStudent();
        break;
      case "browse":
        triggerBrowseForLab();
        break;
      case "ApproveSelection":
        File file = fc.getSelectedFile();
        if(null != file)
        {
          pathToLabField.setText(file.getPath());
          triggerOpenLabFolder();
        }
        break;
      default:
        System.out.println("Unknown action command: " + e.toString());
    }

    frame.requestFocus();
  }

  public void	keyPressed(KeyEvent e)
  {
    if(pressedKeys.contains(e.getKeyCode())) return;
    pressedKeys.add(e.getKeyCode());

    int key = e.getKeyCode();

    // Pass
    if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_KP_UP)
    {
      triggerPass();
    }

    // Fail
    if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_KP_DOWN)
    {
      triggerFail();
    }

    // Prev
    if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_KP_LEFT)
    {
      triggerPrev();
    }

    // Next
    if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_KP_RIGHT)
    {
      triggerNext();
    }
  }

  public void	keyReleased(KeyEvent e)
  {
    pressedKeys.remove(e.getKeyCode());
  }

  public void	keyTyped(KeyEvent e)
  {
    // noop
  }

  /*******************
   * BUTTON CONTROLS *
   *******************/
  public void triggerOpenLabFolder()
  {
      String pathToLab = pathToLabField.getText().trim();
      lab = new File(pathToLab);
      gradesFile = new File(lab, "grades.csv");

      //clear data
      fileScroller.clear();
      studentScroller.clear();
      codebox.setText("");

      if(!lab.exists()) return;

      File[] studentFolders = getFolders(lab);
      StudentComboElement[] elements = filesToStudentComboElements(studentFolders);

      //load previously saved grades
      Map<String, String> nameToGrades = new HashMap<>();
      if(gradesFile.exists())
      {
        String data = readFile(gradesFile).trim();
        String[] lines = data.split(System.lineSeparator());
        for(String line : lines)
        {
          String[] parts = line.split(",");
          String name = parts[0].trim();
          String grade = parts[1].trim();

          nameToGrades.put(name, grade);
        }
      }

      for(StudentComboElement elem : elements)
      {
        String name = elem.getName();
        if(nameToGrades.containsKey(name))
        {
          elem.setScore(nameToGrades.get(name));
        }
      }

      studentScroller.populate(elements);
      triggerSaveSettings();
  }

  public void triggerSetDefaultFile()
  {
      fileScroller.select(defaultFileField.getText().trim());
      triggerSaveSettings();
  }

  public void triggerNext()
  {
    studentScroller.next();
  }

  public void triggerPrev()
  {
    studentScroller.prev();
  }

  public void triggerPass()
  {
    studentScroller.getSelected().setScore("P");
    triggerSave();
    triggerNext();
  }

  public void triggerFail()
  {
    studentScroller.getSelected().setScore("F");
    triggerSave();
    triggerNext();
  }

  public void triggerNewFile()
  {
    if(null == studentFolder) return;

    File file = new File(studentFolder, fileScroller.getSelected());
    String data = readFile(file);

    codebox.setText(data);
    scrollbox.getVerticalScrollBar().setValue(0);
  }

  public void triggerNewStudent()
  {
    if(null == lab) return;

    studentFolder = new File(lab, studentScroller.getSelected().getName());

    fileScroller.clear();
    codebox.setText("");

    if(!studentFolder.exists()) return;

    File[] studentFiles = getFiles(studentFolder);
    String[] elements = filesToNames(studentFiles);

    fileScroller.populate(elements);
    fileScroller.select(defaultFileField.getText().trim());


    int num = studentScroller.getSelectedIndex() + 1;
    int total = studentScroller.getSize();
    studentCounter.setText(num + " of " + total);
  }

  public void triggerSave()
  {
    String data = String.format("Student, Grade%n");

    File[] studentFolders = getFolders(lab);
    Object[] elements = studentScroller.getElements();

    for(Object e : elements)
    {
      StudentComboElement elem = (StudentComboElement)e;
      data += String.format("%s, %s%n", elem.getName(), elem.getScore());
    }

    saveFile(gradesFile, data);
  }

  public void triggerSaveSettings()
  {
    String data = "";
    data += String.format("lab=%s%n", lab.getPath());
    data += String.format("file=%s%n", defaultFileField.getText().trim());

    saveFile(settingsFile, data);
  }

  public void triggerLoadSettings()
  {
    String settings = readFile(settingsFile).trim();
    String[] lines = settings.split(System.lineSeparator());

    for(String line : lines)
    {
      String[] parts = line.split("=");
      switch(parts[0])
      {
        case "lab":
          pathToLabField.setText(parts[1].trim());
          triggerOpenLabFolder();
          break;
        case "file":
          defaultFileField.setText(parts[1].trim());
          triggerSetDefaultFile();
          break;
        default:
          System.out.println("Unknown setting: " + line);
          break;
      }
    }
  }

  public void triggerBrowseForLab()
  {
    String path = pathToLabField.getText().trim();

    if(null != lab && lab.exists() && lab.isDirectory())
    {
      fc.setCurrentDirectory(lab);
    }

    fc.showOpenDialog(frame);
  }

  /***********************
   * FILE SYSTEM METHODS *
   ***********************/
   public static void saveFile(File dest, String data)
   {
     if(null == dest) return;

     try
     {
       Files.write(dest.toPath(), data.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
     }
     catch(Exception e){
       e.printStackTrace();
     }
   }

  public static String readFile(File file)
  {
    if(null == file || !file.exists() || file.isDirectory())
    {
      return "";
    }

    String ret = "";

    try
    {
      List<String> lines = Files.readAllLines(file.toPath());
      for(String line : lines)
      {
        ret += line + System.lineSeparator();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    return ret;
  }

  public static File[] getFolders(File dir)
  {
    if(dir == null || !dir.exists())
    {
      return new File[0];
    }

    List<File> ret = new LinkedList<>();
    File[] files = dir.listFiles();

    for(File file : files)
    {
      if(file.isDirectory())
      {
        String name = file.getName();
        if(!".".equals(name) && !"..".equals(name))
        {
          ret.add(file);
        }
      }
    }

    return ret.toArray(new File[0]);
  }

  public static File[] getFiles(File dir)
  {
    if(dir == null || !dir.exists() || !dir.isDirectory())
    {
      return new File[0];
    }

    List<File> ret = new LinkedList<>();
    File[] files = dir.listFiles();

    if(null == files) return new File[0];

    for(File file : files)
    {
      if(!file.isDirectory())
      {
        ret.add(file);
      }
    }

    return ret.toArray(new File[0]);
  }

  public static String[] filesToNames(File[] files)
  {
    List<String> ret = new LinkedList<>();

    for(File file : files)
    {
      ret.add(file.getName());
    }

    return ret.toArray(new String[0]);
  }

  public static StudentComboElement[] filesToStudentComboElements(File[] files)
  {
    List<StudentComboElement> ret = new LinkedList<>();

    for(File file : files)
    {
      ret.add(new StudentComboElement(file.getName()));
    }

    return ret.toArray(new StudentComboElement[0]);
  }

  /***************
   * MAIN METHOD *
   ***************/
  public static void main(String[] args)
  {
    new CodeLooker();
  }
}
