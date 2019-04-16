package runtime;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.swing.JFrame;
import gen.GenerationFlag;
import gen.Generator;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;

public class UI extends JFrame {
	
	//Public UI components
	private JComboBox cmbWorldSize;
	private JTextField tfSeed;
	private JProgressBar progressBar;
	private JCheckBox btnPangea;
	
	//Attached Generator
	private Generator generator;

	//Main method to launch the UI
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Create JFrame in seperate thread
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	//Constructor for the UI
	@SuppressWarnings("unchecked")
	public UI() {
		//Set window size of 640 by 160 and block resizing
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 160);
		getContentPane().setLayout(null);
		setResizable(false);
		setTitle("MapGen3 by Sam Basile");
		
		try {
			//Set look and feel to that of the system for a cleaner look
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		//World Size label
		JLabel lblWorldSize = new JLabel("World Size");
		lblWorldSize.setBounds(10, 11, 106, 14);
		getContentPane().add(lblWorldSize);
		
		//World size selection box
		cmbWorldSize = new JComboBox(WorldSize.values());
		cmbWorldSize.setBounds(10, 30, 134, 25);
		cmbWorldSize.setRenderer(new DefaultListCellRenderer() {
			//Render selections to be the names defined in the WorldSize enum not the type itself
		    public Component getListCellRendererComponent(JList<?> list,
		            Object value,
		            int index,
		            boolean isSelected,
		            boolean cellHasFocus) {
		    	//Use typeName variable rather than enum identifier
		        value = ((WorldSize) value).getTypeName();
		        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    }

		});
		getContentPane().add(cmbWorldSize);
		
		//Text field for the seed
		tfSeed = new JTextField();
		tfSeed.setBounds(286, 30, 197, 25);
		getContentPane().add(tfSeed);
		tfSeed.setColumns(10);
		
		//Seed label
		JLabel lblSeed = new JLabel("Seed");
		lblSeed.setBounds(286, 11, 65, 14);
		getContentPane().add(lblSeed);
		
		//Button for generating a random seed
		JButton btnRandomSeed = new JButton("Random");
		btnRandomSeed.setBounds(510, 30, 104, 25);
		getContentPane().add(btnRandomSeed);
		
		//Button to start the generation process
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(10, 80, 110, 30);
		getContentPane().add(btnGenerate);
		
		//Progress bar for generation progress
		progressBar = new JProgressBar();
		progressBar.setBounds(130, 80, 484, 30);
		//Allow a custom progress string
		progressBar.setStringPainted(true);
		//Set progress to range from 0 to 100
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		getContentPane().add(progressBar);
		
		//Check box to select island or pangea mode
		btnPangea = new JCheckBox("Pangea");
		btnPangea.setHorizontalAlignment(SwingConstants.CENTER);
		btnPangea.setBounds(150, 30, 106, 25);
		getContentPane().add(btnPangea);
		
		/*
		 * Add action listeners
		 */
		
		//Action listener for the generate button
		btnGenerate.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		    	//Start the generation
		    	startGeneration();
		    }
		});
		
		//Action listener for the random seed button
		btnRandomSeed.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) {
		    	//Set the seed text to a random integer
		        tfSeed.setText(new String(new Random().nextInt() + ""));
		    }
		});
	}
	
	//Creates a new Generator instance from user inputs and hooks to the progress bar
	public void startGeneration() {
		//Get world parameters and start generation
		int worldSize = ((WorldSize)cmbWorldSize.getSelectedItem()).getSize();
		int worldSeed = Integer.parseInt(tfSeed.getText());
		this.generator = new Generator(worldSize, worldSeed);
		
		//Check for pangea selections
		if(btnPangea.isSelected()) {
			this.generator.setFlag(GenerationFlag.PANGEA);
		}
		
		//Start the generation thread
		Thread genThread = new Thread(this.generator);
		genThread.start();
		
		// Add thread to listen for UI updates
		new Thread() {
			public void run() {
				boolean rt = true;
				while (rt) {
					//Update status string and progress value
					progressBar.setString(generator.getStatus());
					progressBar.setValue((int) generator.getProgress());
					

					// Check for completion
					if (generator.getProgress() == 100.0D) {
						rt = false;
						
						//After completion inform the user of the image location
						StringBuilder message = new StringBuilder();
						message.append("World outputed as \"" + generator.getWorldOutputPath() + "\"!");
						message.append(System.getProperty("line.separator"));
						message.append("(" + new File(generator.getWorldOutputPath()).getAbsolutePath() + ")");
						JOptionPane.showMessageDialog(null, message.toString(), "Generation Completed", JOptionPane.PLAIN_MESSAGE);
					}

					try {
						//Wait 200ms (1/5 second)
						this.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
	}
}
