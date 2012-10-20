package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;

import parsers.PodnapisiParser;
import parsers.TitloviParser;
import popup.MyEventQueue;
import utils.Subtitle;

import comboBox.ComboBoxRenderer;


public class MainFrame extends JFrame {

	private static final long serialVersionUID = 2674243678783539582L;
	ArrayList<Subtitle> subs = new ArrayList<Subtitle>();
	boolean podnapisi = true;
	PodnapisiParser pparser;
	TitloviParser tparser;
	
	JPanel titlePane;
	JTextField tfTitle = new JTextField(30);
	JComboBox cbLang;
	JComboBox cbSite;
	JButton btnSearch = new JButton();
	JTable dataTable;
	JScrollPane tableScroll;
	public JLabel lblMessage = new JLabel("");
	JButton btnDownload = new JButton();
	JPanel containerPane = new JPanel();
	DefaultTableModel model = new DefaultTableModel();
	HtmlCleaner cleaner;
	
	public MainFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Titlalica 2.0");
		setIconImage(Toolkit.getDefaultToolkit().getImage("img/gnome-subtitles.png"));
		setLayout(new MigLayout());
		
		//HiFi look and feel
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(getRootPane());
		
		//popup meni
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(new MyEventQueue());
		
		cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		
		init();
		
		pparser = new PodnapisiParser(cleaner, MainFrame.this);
		tparser = new TitloviParser(cleaner, MainFrame.this);
		
		add(titlePane, "span 4, wrap");
		add(tfTitle, "gapleft 80, height 30:30:30, width 300:300:300");
		add(cbLang, "width 120:120:120, height 31:31:31");
		add(cbSite,"width 120:120:120, height 31:31:31");
		add(btnSearch, "wrap,height 30:30:30");
		add(tableScroll, "span 4, width 780:780:780, wrap, gaptop 7");
		add(lblMessage, "span 3");
		add(btnDownload, "cell 3 3, split 2, gapleft 113");
		
		pack();
		setSize(800, 500);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	//inicijalizacija komponenti
	public void init() {
		
		//combo box za jezike
		ImageIcon srbIco = new ImageIcon("img/srb.png");
		srbIco.setDescription("Srpski");
		ImageIcon croIco = new ImageIcon("img/cro.png");
		croIco.setDescription("Hrvatski");
		ImageIcon engIco = new ImageIcon("img/eng.png");
		engIco.setDescription("Engleski");
		
		String strings[] = new String[]{"Srpski", "Engleski", "Hrvatski"};
		Integer indexes[] = new Integer[]{0,1,2};
		ImageIcon images[] = new ImageIcon[]{srbIco, engIco, croIco};
		
		cbLang= new JComboBox(indexes);
		ComboBoxRenderer renderer= new ComboBoxRenderer(images, strings);
        cbLang.setRenderer(renderer);
        cbLang.setBorder(BorderFactory.createLineBorder(Color.gray));
        
        //combo box za sajt
        cbSite = new JComboBox();
        cbSite.addItem("titlovi.com");
        cbSite.addItem("podnapisi.net");
        cbSite.setBorder(BorderFactory.createLineBorder(Color.gray));
        
        //panel sa naslovom i ikonom
        titlePane = new JPanel();
        
        Icon titlalicaIcon = new ImageIcon("img/gnome-subtitles.png");
		JLabel lblTitlalica = new JLabel(titlalicaIcon);
		JLabel lblTitlTekst = new JLabel("Titlalica 2");
		
		lblTitlTekst.setForeground(Color.GRAY);
		lblTitlTekst.setFont(new Font("Monospaced", Font.BOLD, 30));
		
		titlePane.setLayout(new MigLayout());
		titlePane.add(lblTitlalica, "gapleft 250");
		titlePane.add(lblTitlTekst);
        
        //text field za naslov
		tfTitle.requestFocus();
		tfTitle.setFont(new Font("Dialog", Font.BOLD, 18));
		tfTitle.setForeground(Color.gray);
		
		UIManager uim=new UIManager();
		uim.put("ToolTip.background", Color.BLACK);
		uim.put("ToolTip.foreground", Color.ORANGE);
		
		
		//botun za download
		btnDownload.setBackground(Color.black);
		Icon saveIcon = new ImageIcon("img/download.png");

		btnDownload.setIcon(saveIcon);
		btnDownload.setBorder(BorderFactory.createLineBorder(Color.gray));
		btnDownload.setEnabled(false);
		
		//botun za pretragu
		btnSearch.setBackground(Color.black);
		btnSearch.setBorder(BorderFactory.createLineBorder(Color.gray));
		Icon searchIcon = new ImageIcon("img/search1.png");
		btnSearch.setIcon(searchIcon);
		
		//tabela i njen scroll pane
		dataTable = new JTable(model) {
			
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
			
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
				if (c instanceof JComponent) {
					JComponent jc = (JComponent)c;
					if(podnapisi) {
						String tip = "<html>" + getValueAt(rowIndex, vColIndex).toString().replace(",", "<p>");
						jc.setToolTipText(tip);
					}else  {
						String tip = "<html>" + getValueAt(rowIndex, vColIndex).toString().replace("/", "<p>");
						jc.setToolTipText(tip);
					}
				}
				return c;
			}
			
		};
		
		dataTable.getTableHeader().setBackground(Color.BLACK);
		dataTable.getTableHeader().setForeground(Color.WHITE);
		dataTable.getTableHeader().setFont(new Font("Dialog" , Font.BOLD, 13));
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		model.setColumnIdentifiers(new String[] {"#", "Naslov", "Verzija", "CD#", "FPS"});
		
		dataTable.getColumnModel().getColumn(0).setMaxWidth(30);
		dataTable.getColumnModel().getColumn(1).setMinWidth(180);
		dataTable.getColumnModel().getColumn(2).setMinWidth(300);
		dataTable.getColumnModel().getColumn(3).setMaxWidth(40);
		dataTable.getColumnModel().getColumn(4).setMaxWidth(50);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment( JLabel.CENTER );
		dataTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
		
		tableScroll = new JScrollPane(dataTable);
		tableScroll.setSize(600, 200);
		tableScroll.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		
		//listeneri za botune
		btnSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSearch();
			}
		});
		
		btnDownload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doDownload();
			}
		});
		
		tfTitle.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(tfTitle.getText().equalsIgnoreCase("")) {
					btnSearch.setEnabled(false);
				}else {
					btnSearch.setEnabled(true);
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						doSearch();
					}
				}
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		
		//listeneri za tablu
		dataTable.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() ==2) {
					doDownload();
				}
				
			}
		});
		
		dataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(dataTable.getSelectedRow() != -1) {
					btnDownload.setEnabled(true);
				}
			}
		});
		
	}
	
	
	public void doSearch() {
		if(btnSearch.isEnabled()) {
			final String tit = tfTitle.getText().trim();
			if(!tit.equals("")) {
				
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						//String lang = cbLang.getSelectedItem().toString();
						int indes = cbLang.getSelectedIndex();
						
						int siteind = cbSite.getSelectedIndex();
						if(siteind == 1) { //podnapisi.net
							podnapisi = true;
							int lan = 36;
							if(indes == 1) {
								lan = 2;
							}else if(indes == 2){
								lan = 38;
							}
							
							subs = pparser.getSubtitles(tit, String.valueOf(lan));
							
						}else if(siteind == 0) { //titlovi.com
							podnapisi = false;
							String lang = "";
							if (indes == 1) {
								lang = "english";
							}else if(indes == 2) {
								lang = "hrvatski";
							}else {
								lang = "srpski";
							}
							subs = tparser.getSubtitles(tit, lang);
						}
						
						
						if(subs != null) {
							for(int i=0; i<subs.size(); i++) {
								model.addRow(new String[] {String.valueOf(i+1), subs.get(i).getTitle(), subs.get(i).getVersion(), String.valueOf(subs.get(i).getNumberOfDiscs()), subs.get(i).getFps()});
							}
							
							
							int nr = subs.size()%10;
							
							// SRPSKI
							if(nr==1) {
								if(subs.size() != 11) {
									lblMessage.setText("Pronadjen je " + subs.size() + " titl.");
								}else {
									lblMessage.setText("Pronadjeno je " + subs.size() + " titlova.");
								}
							}else if(nr>1 && nr<5){
								if(subs.size() == 12 || subs.size() == 13) {
									lblMessage.setText("Pronadjeno je " + subs.size() + " titlova.");
								}else {
									lblMessage.setText("Pronadjeno je " + subs.size() + " titla.");
								}
								
							}else{
								lblMessage.setText("Pronadjeno je " + subs.size() + " titlova.");
							}
							
							
							 /*// HRVATSKI
							 if(nr==1) {
								if(subs.size() != 11) {
									lblMessage.setText("Pronadjen je " + subs.size() + " prijevod.");
								}else {
									lblMessage.setText("Pronadjeno je " + subs.size() + " prijevoda.");
								}
							}else{
								lblMessage.setText("Pronadjeno je " + subs.size() + " prijevoda.");
							}*/
							
						}else {
							lblMessage.setForeground(Color.RED);
							lblMessage.setText("GRE�KA! Proverite internet konekciju.");
						}

						MainFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						tfTitle.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
						btnSearch.setEnabled(true);
					}
					
				});
				
				lblMessage.setForeground(Color.ORANGE);
				lblMessage.setText("Pretraga u toku");
				model.getDataVector().removeAllElements();
				dataTable.paintImmediately(dataTable.getBounds());
				MainFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				tfTitle.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				tfTitle.selectAll();
				tfTitle.requestFocus();
				btnSearch.setEnabled(false);
				
				thread.setPriority(Thread.NORM_PRIORITY);
			    thread.start();
			}else {
				lblMessage.setForeground(Color.RED);
				lblMessage.setText("Morate uneti naziv filma!");
				tfTitle.requestFocus();
			}
		}
	}
	
	public void doDownload() {
		final int index = dataTable.getSelectedRow();
		if(index != -1) {
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					Subtitle selected = subs.get(index);
					String url = selected.getDownloadURL();
					String title = selected.getTitle();
					
					FileSystemView fsv = FileSystemView.getFileSystemView();
					JFileChooser chooser = new JFileChooser(fsv.getRoots()[0]);
					chooser.setDialogTitle("Izaberite lokaciju");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
				    chooser.setApproveButtonText("Download");
				    
				    if (chooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
				    	lblMessage.setForeground(Color.ORANGE);
						lblMessage.setText("Preuzimanje datoteke u toku...");
						MainFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						tfTitle.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						btnSearch.setEnabled(false);
						btnDownload.setEnabled(false);
				    	String filename = "";
				    	if(podnapisi) {
				    		filename = chooser.getSelectedFile().getAbsolutePath() + File.separator + pparser.getDownloadURL(url).substring(60) + ".zip";
				    	}else {
				    		filename = chooser.getSelectedFile().getAbsolutePath() + File.separator + title + tparser.getDownloadURL(url).substring(57) + ".zip";
				    	}
						try {
							if(podnapisi) {
								URL fname = new URL(pparser.getDownloadURL(url));
								loadFile(fname, filename);
							}else {
								URL fname = new URL(tparser.getDownloadURL(url));
								loadFile(fname, filename);
							}
							lblMessage.setText("Titl skinut u: " + chooser.getSelectedFile().getAbsolutePath());
							MainFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							tfTitle.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							btnDownload.setEnabled(true);
							btnSearch.setEnabled(true);
						} catch (MalformedURLException e) {
						} catch (IOException e) {
							lblMessage.setForeground(Color.RED);
							lblMessage.setText("Gre�ka prikikom skidanja datoteke.");
						}
				    }
				}
			});
			t.setPriority(Thread.NORM_PRIORITY);
			t.start();
		}
	}
	
	public void loadFile(URL remoteFile, String fname) throws IOException {
		URLConnection connection = remoteFile.openConnection(); 
		final InputStream inputStream = connection.getInputStream(); 

		int length = connection.getContentLength(); 

		FileOutputStream fout = new FileOutputStream(fname);

		copy(inputStream, fout);
		
	} 

	public void copy( final InputStream inputStream, final FileOutputStream fout) {	
		try {
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			int current = 0;

			while((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {   
				fout.write(buffer, 0, bytesRead); 
				current += bytesRead; 
			}
			inputStream.close(); 
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
