import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;


import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractorFrame extends JFrame
{
    JFileChooser chooser = new JFileChooser();
    File selectedFile;

    private Map<String, Integer> tagFrequencyMap = new TreeMap<>();
    private Set<String> stopWords = new TreeSet<>();

    File workingDirectory = new File(System.getProperty("user.dir"));
    String fileName = "FrequenciesResults";
    String record = "";
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
    JButton saveFrequenciesToFile;
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

        tagExtractorLabel = new JLabel("Tag Extractor", JLabel.CENTER);
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

        chooseFileToRead = new JButton("Choose a file to read and begin tag extraction");
        chooseWordsToExtract = new JButton("Choose a text file with the noise words to ignore");
        saveFrequenciesToFile = new JButton("Save frequencies to a file");
        quit = new JButton("Quit");
        //Change this to edit the button:
//        readMyFortune.setFont(new Font("Verdana", Font.PLAIN, 20));
//        quit.setFont(new Font("Verdana", Font.PLAIN, 20));

        bottomPanel.add(chooseFileToRead);
        bottomPanel.add(chooseWordsToExtract);
        bottomPanel.add(quit);
        bottomPanel.add(saveFrequenciesToFile);

        chooseFileToRead.addActionListener((ActionEvent ae) ->
        {
            readFile();
            tagsDisplay.append("File Name: " + selectedFile.getName() + "\n");
            for(String key : tagFrequencyMap.keySet())
            {
                record = "Word: "+ "'" + key + "'";
                tagsDisplay.append(record);
                record = " | Frequency: " + tagFrequencyMap.get(key) + "\n";
                tagsDisplay.append(record);
            }

        });

        chooseWordsToExtract.addActionListener((ActionEvent ae) ->
        {
            loadFilterWords();
        });

        saveFrequenciesToFile.addActionListener((ActionEvent ae) ->
        {
            saveToFile();
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
                stopWords.clear();

                int line = 0;

                while(reader.ready())
                {
                    rec = reader.readLine().toLowerCase();
                    line++;
                    stopWords.add(rec);
                }
                reader.close(); // must close the file to seal it and flush buffer
                JOptionPane.showMessageDialog(null, "Noise words loaded!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            }
            else
            {
                //Refactor to a message
                JOptionPane.showMessageDialog(null, "You didnt choose a file!",
                        "Task Failed", JOptionPane.INFORMATION_MESSAGE);
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

                tagFrequencyMap.clear();
                int line = 0;

                while(reader.ready())
                {
                    rec = reader.readLine().toLowerCase();
                    rec = rec.replaceAll("[^a-zA-Z]", " ");
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
                tagFrequencyMap.remove("");
                reader.close(); // must close the file to seal it and flush buffer
                JOptionPane.showMessageDialog(null, "Data file read and tag words extracted!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                //Refactor to a message
                JOptionPane.showMessageDialog(null, "You didnt choose a file!",
                        "Task Failed", JOptionPane.INFORMATION_MESSAGE);
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
    private void saveToFile()
    {
        System.out.println();
        Path file = Paths.get(workingDirectory.getPath() + "\\" + fileName + ".txt");
        try
        {
            OutputStream out =
                    new BufferedOutputStream(Files.newOutputStream(file, CREATE));
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(out));

            for(String key : tagFrequencyMap.keySet())
            {
                record = "Word: "+ "'" + key + "'";
                writer.write(record, 0, record.length());
                record = " | Frequency: " + tagFrequencyMap.get(key) + "\n";
                writer.write(record, 0, record.length());
            }
            writer.close();
            JOptionPane.showMessageDialog(null, "Data File Written!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
