import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;

import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractorFrame extends JFrame
{
    JFileChooser chooser = new JFileChooser();
    File selectedFile;

    private Map<String, Integer> tagFrequencyMap = new TreeMap<>();
    private Set<String> stopWords = new TreeSet<>();
    Iterator<String> stopWordsIterator = stopWords.iterator();
    String rec = "";
    Toolkit kit = Toolkit.getDefaultToolkit();

    //Panels
    JPanel mainPanel;
    JPanel topPanel;
    JPanel midPanel;


    JPanel bottomPanel;

    JTextArea tagsDisplay;
    JScrollPane scroller;

    JButton chooseWordsToExtract;
    JButton chooseFileToRead;
    JButton quit;
    JButton extractAndCount;
    JLabel tagExtractorLabel;


    public TagExtractorFrame()
    {

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());


        createTopPanel();
        mainPanel.add(topPanel, BorderLayout. NORTH);

        createMidPanel();
        mainPanel.add(midPanel, BorderLayout.CENTER);

        createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        // center frame in screen
        setSize(screenWidth * 3/4 , screenHeight * 3/4);
        setLocation(screenWidth / 8, screenHeight / 8);
        // set frame icon and title
        setTitle("Tag Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createTopPanel()
    {
        topPanel = new JPanel();

        tagExtractorLabel = new JLabel("Fortune Teller", JLabel.CENTER);
        tagExtractorLabel.setVerticalTextPosition(JLabel.BOTTOM);
        tagExtractorLabel.setHorizontalTextPosition(JLabel.CENTER);
        tagExtractorLabel.setFont(new Font("Arial", Font.PLAIN, 48));

        topPanel.add(tagExtractorLabel);
    }
    private void createMidPanel()
    {
        midPanel = new JPanel();
        tagsDisplay = new JTextArea(10,40);
        tagsDisplay.setFont(new Font("Times New Roman", Font.PLAIN, 34));
        tagsDisplay.setEditable(false);
        scroller = new JScrollPane(tagsDisplay);
        midPanel.add(scroller);

    }
    private void createBottomPanel()
    {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 3));

        chooseFileToRead = new JButton("Choose a file to read");
        chooseWordsToExtract = new JButton("Choose a text file with the words to extract");
        extractAndCount = new JButton("Extract and count words");
        quit = new JButton("Quit");
        //Change this to edit the button:
//        readMyFortune.setFont(new Font("Verdana", Font.PLAIN, 20));
//        quit.setFont(new Font("Verdana", Font.PLAIN, 20));

        bottomPanel.add(chooseFileToRead);
        bottomPanel.add(chooseWordsToExtract);
        bottomPanel.add(extractAndCount);
        bottomPanel.add(quit);


        chooseFileToRead.addActionListener((ActionEvent ae) ->
        {
            readFile();

            System.out.println(tagFrequencyMap);

        });

        chooseWordsToExtract.addActionListener((ActionEvent ae) ->
        {
            loadFilterWords();
            System.out.println(stopWords);
        });

        quit.addActionListener((ActionEvent ae) -> System.exit(0));
    }

    private void loadFilterWords()
    {

        try
        {
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();
                InputStream in =
                        new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in));

                int line = 0;

                while(reader.ready())
                {
                    rec = reader.readLine();
                    line++;
                    stopWords.add(rec);
                }
                reader.close(); // must close the file to seal it and flush buffer

            }
            else
            {
                //Refactor to a message
                System.out.println("You didnt choose a file! Run the program again and choose a file!");
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!!!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    private void readFile()
    {
        try
        {
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();
                InputStream in =
                        new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in));

                int line = 0;

                while(reader.ready())
                {
                    rec = reader.readLine();
                    line++;
                    String[] fields = rec.split(" ");
                        for(int i = 0; i < fields.length; i++)
                        {
                            if (!stopWords.contains(fields[i]))
                            {
                                if (tagFrequencyMap.containsKey(fields[i]))
                                {
                                    int counter = tagFrequencyMap.get(fields[i]);
                                    counter++;
                                    tagFrequencyMap.put(fields[i], counter);
                                }
                                else
                                {
                                    tagFrequencyMap.put(fields[i], 1);
                                }
                            }
                        }
                }
                for(String key : tagFrequencyMap.keySet())
                {
                    System.out.println("Key: " + key);
                    System.out.println("Value: " + tagFrequencyMap.get(key) + "\n");  // get(Key) returns the value
                }
                reader.close(); // must close the file to seal it and flush buffer
            }
            else
            {
                //Refactor to a message
                System.out.println("You didnt choose a file! Run the program again and choose a file!");
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!!!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
