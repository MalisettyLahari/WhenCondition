package com.contiautomotive.architecture.tool.handlers;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.contiautomotive.cidl.cidl.AbstractComponent;
import com.contiautomotive.cidl.cidl.AccessibleInterface;
import com.contiautomotive.cidl.cidl.Cluster;
import com.contiautomotive.cidl.cidl.CodegComponent;
import com.contiautomotive.cidl.cidl.Component;
import com.contiautomotive.cidl.cidl.CompositeComponent;
import com.contiautomotive.cidl.cidl.DelegateEntity;
import com.contiautomotive.cidl.cidl.DelegateInterface;
import com.contiautomotive.cidl.cidl.HeaderFileStrap;
import com.contiautomotive.cidl.cidl.OptionalControlStructure;
import com.contiautomotive.cidl.cidl.Partition;
import com.contiautomotive.cidl.cidl.Plugin;
import com.contiautomotive.cidl.cidl.PluginTemplate;
import com.contiautomotive.cidl.cidl.ProvidedElement;
import com.contiautomotive.cidl.cidl.ProvidedInterface;
import com.contiautomotive.cidl.cidl.ProvidedInterfaceElement;
import com.contiautomotive.cidl.cidl.ReqDocIdSpecification;
import com.contiautomotive.cidl.cidl.RequiredConstant;
import com.contiautomotive.cidl.cidl.RequiredFunction;
import com.contiautomotive.cidl.cidl.RequiredInterface;
import com.contiautomotive.cidl.cidl.RequiredInterfaceEntity;
import com.contiautomotive.cidl.cidl.RequiredPort;
import com.contiautomotive.cidl.cidl.RequiredVariable;
import com.contiautomotive.cidl.cidl.Software;
import com.contiautomotive.cidl.cidl.SubComponent;
import com.contiautomotive.cidl.cidl.Type;
import com.contiautomotive.cidl.cidl.TypeCollection;
import com.contiautomotive.cidl.cidl.impl.BaseTypeImpl;
import com.contiautomotive.cidl.cidl.impl.ModelImpl;
import com.contiautomotive.cidl.cidl.impl.NumericTypeImpl;
import com.contiautomotive.cidl.cidl.impl.PointerTypeImpl;
import com.contiautomotive.cidl.cidl.impl.StructTypeImpl;
import com.contiautomotive.common.GlobalVariables;
import com.contiautomotive.common.ProcessProvidedElements;
import com.contiautomotive.read.cidl.data.CollectCidlData;
import com.contiautomotive.strapbase.fREx.Expression;
import com.contiautomotive.strapbase.fREx.StrapRef;
import com.continental.plm.flavors.model.IFlavor;
import com.continental.plm.flavors.util.FlavorUtil;
import com.google.gson.JsonArray;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPConstraint;
import com.telelogic.rhapsody.core.IRPFlow;
import com.telelogic.rhapsody.core.IRPGeneralization;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPPort;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.IRPUnit;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

import Jakarta.DRAttributes.component;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CidlCollectorHandler extends AbstractHandler {
	javax.swing.JFrame jFrame = new javax.swing.JFrame();
	private static final Logger logger = LogManager.getLogger(CidlCollectorHandler.class);
	private static CidlCollectorHandler cidlCollectorInstance;
	private IRPStereotype pvRestriction = null;
	// Variables declaration - do not modify
	javax.swing.JInternalFrame jInternalFrame = new javax.swing.JInternalFrame();
	javax.swing.JPanel jPanel_Main = new javax.swing.JPanel();
	javax.swing.JButton cancelButton = new javax.swing.JButton();
	javax.swing.JPanel jPanel_Sub = new javax.swing.JPanel();
	javax.swing.JLabel jLabel = new javax.swing.JLabel();
	javax.swing.JTextField textField = new javax.swing.JTextField();
	IRPUnit pkg = null;
	IRPModelElement element_toAdd = null;
	IRPModelElement existing_elementinModel = null;
	IRPModelElement userdefinedtypes = null;
	IRPApplication app = null;
	IRPProject prj = null;
	IRPClassifier datatype = null;
	IRPClass funcClass = null;
	IRPModelElement typesComponent = null;
	IRPUnit partition = null;
	IRPUnit plugin = null;
	IRPInstance partition_part = null;
	IRPInstance subplugin_part = null;
	IRPInstance plugin_part = null;
	IRPInstance software_part = null;
	IRPInstance sub_part = null;
	IRPInstance cluster_part = null;
	IRPUnit plugin_template = null;
	IRPUnit subplugin_template = null;
	IRPUnit subpartition = null;
	IRPUnit software = null;
	IRPUnit swcomponent = null;
	IRPUnit subcomponent = null;
	IRPUnit subsubcomponent = null;
	IRPUnit nestedsubcomponent = null;
	IRPClass pluginintfblck = null;
	IRPClass intfblck = null;
	IRPClass intfblckplugin = null;
	IRPUnit subcluster = null;
	IRPOperation operation = null;
	IRPAttribute operationprop = null;
	IRPUnit cluster = null;
	IRPPort port = null;
	boolean match = false;
	boolean flag = false;
	boolean isPresent = false;
	boolean portPresent = false;
	boolean portandConnectorExists = false;
	IRPPort reqport = null;
	IRPLink reqconnector = null;
	IRPAttribute flowpropertyport = null;
	IRPPort pluginport = null;
	IRPAttribute flowprop = null;
	IRPFlow flow = null;
	IRPModelElement existing_element = null;
	IRPClass subintfblck = null;
	IRPStereotype composite_stereotype = null;
	IRPStereotype thirdparty_stereotype = null;
	String flowpropfullname = null;
	String variablefullname = null;
	HashMap<String, List<String>> exists = new HashMap<>();
	boolean elementPresent = false;
	JsonArray jobjintfblcks = new JsonArray();
	String constantfullname = null;
	String operationfullname = null;
	String operationfullpath = null;
	String constantfullpath = null;
	String variablefullpath = null;
	String portfullpath = null;
	String strapExpressName = "";
	String resultStr;
	StringBuilder sb = new StringBuilder();
//	StringBuilder strapExpressName = new StringBuilder(strapExpressName_ref);
//	String strapName="";

	private StringBuilder strapName_ = new StringBuilder();
	HashMap<String, String> requiredoperationmap = new HashMap<String, String>();
	HashMap<String, String> requiredvariablemap = new HashMap<String, String>();
	HashMap<String, String> requiredportmap = new HashMap<String, String>();
	HashMap<String, String> requiredconstantmap = new HashMap<String, String>();
	// use Hashmap instead of String
	HashMap<AbstractComponent, HashMap<String, HashMap<String, String>>> providedInterfaces = new HashMap<>();
	HashMap<Plugin, HashMap<String, HashMap<String, String>>> providedpluginInterfaces = new HashMap<>();
	HashMap<PluginTemplate, HashMap<String, HashMap<String, String>>> pluginTemplateInterfaces = new HashMap<>();
	HashMap<String, List<String>> existingelements = new HashMap<>();
	HashMap<String, List<String>> existingPorts = new HashMap<>();
	HashMap<String, List<String>> existingComponents = new HashMap<>();
	HashMap<String, HashMap<String, String>> existingConnectors = new HashMap<>();
	HashMap<String, List<String>> subcomponenthashmap = new HashMap<>();
	HashMap<String, List<String>> plugintemplateshashmap = new HashMap<>();
	HashMap<String, List<String>> subplugintemplates = new HashMap<>();
	FileWriter file = null;
	AbstractComponent providedComponent = null;
	public static JSONArray listofinterfaceblocks = new JSONArray();
	HashMap<String, String> fullPathMap = new HashMap<>();
	String fullPathName = null;
	String CidlFileName = null;
	String CidlFileName_parent = null;
	String pluginFileName = null;
	String pluginTemplate_fileName = null;
	String pluginTemplate_fileName_parent = null;
	static HashMap<String, String> fileMapping;
	static ArrayList<String> allFilesPath;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			logger.info("Architecture tool is started...");

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL resource = classLoader.getResource("icons/Rhapsody.gif");
			ImageIcon icon = new ImageIcon(resource);
			jFrame.setIconImage(icon.getImage());
//			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("IntegrationStream");
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("CNRTest");
			if (project != null) {
				IFlavor flavor = FlavorUtil.getViewFlavor(project);
				logger.info(" flavor name " + flavor.getName());
				try {
					project.build(IncrementalProjectBuilder.AUTO_BUILD, "org.eclipse.xtext.ui.shared.xtextBuilder",
							null, new NullProgressMonitor());
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					CollectCidlData pluginXtend = new CollectCidlData(project);
					if (pluginXtend.getResource() != null) {
						Software sw = null;

						HashMap<String, EList<Plugin>> pluginMap = new HashMap<>();
						HashMap<String, EList<Plugin>> pluginMap_subComp = new HashMap<>();
						HashMap<String, EList<TypeCollection>> typeMap = new HashMap<>();
						// search for first software resource
						for (Resource res : pluginXtend.getResource()) {
							EList<EObject> contents = res.getContents();
							for (EObject obj : contents) {
								if (obj instanceof ModelImpl && ((ModelImpl) obj).getSoftware() != null) {
									sw = ((ModelImpl) obj).getSoftware();

								} else if (obj instanceof ModelImpl && ((ModelImpl) obj).getPlugins() != null
										&& !((ModelImpl) obj).getPlugins().isEmpty()) {
									EList<Plugin> plugin = ((ModelImpl) obj).getPlugins();
									plugin.forEach(pluginName -> {										
										String component_name = pluginName.getComponent().getName();
										pluginMap.put(component_name, plugin);									
									});

								} else if (obj instanceof ModelImpl && ((ModelImpl) obj).getTypeCollection() != null
										&& !((ModelImpl) obj).getTypeCollection().isEmpty()) {
									EList<TypeCollection> type = ((ModelImpl) obj).getTypeCollection();
									type.forEach(typeName -> {
										String component_name = typeName.getComponent().getName();
										typeMap.put(component_name, type);
									});

								}

							}
						}
						if (sw != null) {
							initComponents(sw, pluginMap, typeMap);
						} else {
							logger.info("No software resource found");
						}
					}
				} catch (Exception e) {
					logger.info("exception caught in incremental building of the project ");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}

	/**
	 * @param sw
	 * @param pluginList
	 * @param typeMap
	 * 
	 */
	private void initComponents(Software sw, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {
		javax.swing.JButton browseButton = new javax.swing.JButton();
		javax.swing.JButton okButton = new javax.swing.JButton();
		javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame.getContentPane());
		jFrame.getContentPane().setLayout(jFrame1Layout);
		jFrame1Layout.setHorizontalGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 400, Short.MAX_VALUE));
		jFrame1Layout.setVerticalGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 300, Short.MAX_VALUE));

		jInternalFrame.setVisible(true);

		javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame.getContentPane());
		jInternalFrame.getContentPane().setLayout(jInternalFrame1Layout);
		jInternalFrame1Layout.setHorizontalGroup(jInternalFrame1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		jInternalFrame1Layout.setVerticalGroup(jInternalFrame1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

		jFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		jFrame.setTitle("Architecture Tool");
		Font font = new Font("Tahoma", Font.BOLD, 11);
		jFrame.setFont(font);
		jFrame.setBounds(new java.awt.Rectangle(20, 20, 20, 20));
		jFrame.setForeground(new java.awt.Color(255, 102, 102));
		jFrame.setResizable(false);
		jFrame.setVisible(true);
		jFrame.setLocation(450, 300);
		jFrame.setAlwaysOnTop(true);

		jPanel_Main.setBackground(new java.awt.Color(204, 204, 204));
		jPanel_Main.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 4));
		jPanel_Main.setForeground(new java.awt.Color(204, 204, 204));

		okButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		okButton.setText("Start");
		okButton.setEnabled(false);
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String fillCheck = textField.getText();
				writeToFile(fillCheck);
				if (fillCheck.endsWith(".rpyx")) {
					try {

						jFrame.dispose();
						logger.info(textField.getText());
						logger.info("Start exporting cidl data to Rhapsody Model\n");
						IRPStereotype prj_stereotype = (IRPStereotype) prj.findNestedElementRecursive("EBSArchitecture",
								GlobalVariables.STEREOTYPE_METACLASS);
						prj.setStereotype(prj_stereotype);
						long starttime = System.currentTimeMillis();
//						createJsonFile();
						// provided
//						String filePath = "D:\\IntegrationStream2020-develop\\Src\\EBS";
						String filePath = "D:\\ArchitectureTools_Latest\\ArchitectureTools-master\\Software Architecture Tools\\08_CidlTest_SourceCode\\Test-SwArchitectureModel-master\\Src\\EBS";
						getAllCidLFiles(filePath);
						;
						getCIDLDataToRhapsody(sw, pluginMap, typeMap);
						// required
//						setTypesandFlows(sw, pluginMap);
//						CompareJson.performJsonCheck();
						long endtime = System.currentTimeMillis();
						System.out.println("Time for run" + (starttime - endtime));
						System.out.println("gg");
						logger.info("End exporting cidl data to Rhapsody\n");
						textField.setText("");
						logger.info("Exported cidl Data to Rhapsody");
						logger.info("Architecture tool ended...............");
						app.writeToOutputWindow("Exported Cidl Data", "Exported Cidl Data\n");
						app.writeToOutputWindow("Rhapsody Model Generated", "Rhapsody Model Generated");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// m_rhpApplication.writeToOutputWindow(null, "Please select excel file\n");
					logger.info("Please select rpyx file\n");
				}
			}

			private void createJsonFile() {
				// TODO Auto-generated method stub
				try {
					String path = System.getProperty("user.home");
					file = new FileWriter(path + "\\JsonFiles\\TE_ST1.json");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		cancelButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		cancelButton.setText("Cancel");
		cancelButton.setMaximumSize(new java.awt.Dimension(60, 20));
		cancelButton.setMinimumSize(new java.awt.Dimension(60, 20));
		cancelButton.setPreferredSize(new java.awt.Dimension(60, 20));
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!textField.getText().isEmpty())
					writeToFile(textField.getText());
				jFrame.dispose();
			}
		});

		jPanel_Sub.setBackground(new java.awt.Color(204, 204, 204));
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		jPanel_Sub.setBorder(javax.swing.BorderFactory.createTitledBorder(raisedetched, "Select Rhapsody rpyx File",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Tahoma", 1, 12))); // NOI18N

		jLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		jLabel.setText("File Path");

		textField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// textFieldActionPerformed(evt);
			}
		});

		browseButton.setBackground(new java.awt.Color(204, 204, 204));
		browseButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		browseButton.setText("...");
		browseButton.setMaximumSize(new java.awt.Dimension(45, 27));
		browseButton.setMinimumSize(new java.awt.Dimension(45, 27));
		browseButton.setPreferredSize(new java.awt.Dimension(45, 27));
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButton.setEnabled(true);
				try {
					browseButtonActionPerformed(evt, okButton);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel_Sub);
		jPanel_Sub.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(4, 4, 4)
						.addComponent(jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 501,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(browseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
						.addGap(4, 4, 4)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel2Layout.createSequentialGroup().addGap(3, 3, 3)
								.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(3, 3, 3)));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel_Main);
		jPanel_Main.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanel_Sub, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jPanel_Sub, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jFrame.getContentPane());
		jFrame.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel_Main,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel_Main,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		jFrame.pack();

	}

	/**
	 * 
	 * @param fillCheck
	 */
	private void writeToFile(String fillCheck) {
		Path filePath = getUserDir();
		try {
			filePath = createFile(filePath);
			Files.write(Paths.get(filePath.toUri()), fillCheck.getBytes());
		} catch (IOException e) {
			// m_rhpApplication.writeToOutputWindow(null, "Unable to write file\n");
			logger.info("Unable to write file\n");
		}

	}

	/**
	 * 
	 * @return
	 */
	private Path getUserDir() {
		String userDir = System.getProperty("user.home");
		Path filePath = Paths.get(userDir, "filePathInfo.txt");
		return filePath;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private Path createFile(Path filePath) throws IOException {
		if (!new File(filePath.toString()).exists())
			filePath = Files.createFile(filePath);
		return filePath;

	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt, JButton okButton) throws IOException {
		String textFieldValue = textField.getText();
		JFileChooser fileChooser;
		textField.setEditable(false);
		okButton.setEnabled(true);
		String filePath = readFilePath();
		if (textFieldValue.isEmpty()) {
			fileChooser = new JFileChooser(filePath);
		} else {
			fileChooser = new JFileChooser(textFieldValue);
		}

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Rpyx", "rpyx", "rpy"));

		fileChooser.setAcceptAllFileFilterUsed(false);
		String dllfilePath = FileLocator
				.toFileURL(Platform.getBundle("com.contiautomotive.architecture.tool").getEntry("/")).getFile();
		File file = new File(dllfilePath + "\\rhapsody.dll");
		System.load(file.toString());
		int result = fileChooser.showOpenDialog(jFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String selectedFilePath = selectedFile.toString();
			openProject(selectedFilePath);
			textField.setText(selectedFile.toString());
			textField.setToolTipText(textField.getText());
		}
	}

	private String readFilePath() {
		StringBuffer fileContenet = new StringBuffer("");
		try {
			Path filePath = getUserDir();
			filePath = createFile(filePath);
			try (Stream<String> lines = Files.lines(filePath)) {
				List<String> filteredLines = lines.collect(Collectors.toList());
				filteredLines.forEach(name -> {
					if (!name.isEmpty())
						fileContenet.append(name);
				});
			} catch (IOException e) {

				logger.info("Unable to read excel file path\n");
			}
		} catch (IOException e1) {

			logger.info("Unable to create text file path\n");
		}
		return fileContenet.toString();
	}

	/**
	 * 
	 * function to process cidl data and add the elements to model process provided
	 * elements
	 * 
	 * @param sw
	 * @param pluginList
	 * @param typeMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getCIDLDataToRhapsody(Software sw, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) throws FileNotFoundException {
		String sct = new String();
		FileWriter file = null;
//		try {
//			String path=System.getProperty("user.home") ;
//			file = new FileWriter(path+"\\JsonFiles\\TE_ST1.json");
//		} catch (IOException fn) {
//			// TODO Auto-generated catch block
//			fn.printStackTrace();
//		}
		JSONObject obj = new JSONObject();
		JSONArray componentarray = new JSONArray();
		JSONArray clusterarray = new JSONArray();
		JSONArray partitionarray = new JSONArray();
		JSONArray software = new JSONArray();

		try {

			processSoftware(sw);
			pvRestriction = (IRPStereotype) prj.getProject().findNestedElementRecursive("pvRestriction", "Stereotype");
			software.add(sw.getName());
			for (Partition p : sw.getPartitions()) {
				logger.info("Partition Name: " + p.getName());
				processPartition(p, sw);
				partitionarray.add(p.getName());
				for (Cluster cl : p.getCluster()) {
					if (cl.getName() != null) {
						logger.info("Cluster Name: " + cl.getName());
						processCluster(cl, p);
						clusterarray.add(cl.getName());
						for (Component c : cl.getComponents()) {
							List<String> subcomps = new ArrayList<String>();
							List<String> templates = new ArrayList<String>();
							List<String> subtemplates = new ArrayList<String>();
							if (c.getName() != null) {
								logger.info("Component Name:" + c.getName());

								processComponent(c, cl, pluginMap, typeMap);
								
								componentarray.add(c.getName() + ":" + cluster.getName());
							}
							for (PluginTemplate template : c.getTemplates()) {
								if (template.getName() != null) {
									logger.info("Template Names: " + template.getName());
									processTemplate(template, c);
									templates.add(template.getName());
								}
							}
							
							for (SubComponent sc : c.getSubcomponents()) {
								if (sc.getName() != null) {
									logger.info("SubComponent Name : " + sc.getName());
									processComponent(sc, c, pluginMap, typeMap);
									componentarray.add(sc.getName() + ":" + cluster.getName());
									subcomps.add(sc.getName());

								}
																
								for (PluginTemplate subtemplate : sc.getTemplates()) {
									if (subtemplate.getName() != null) {
										logger.info("Template Names:" + subtemplate.getName());
										processTemplate(subtemplate, sc);
										templates.add(subtemplate.getName());
									}
								}
								for (SubComponent subsub : sc.getSubcomponents()) {
									if (subsub.getName() != null) {
										logger.info("Sub Sub Component Name: " + subsub.getName());
										processComponent(subsub, sc, pluginMap, typeMap);
										componentarray.add(subsub.getName() + ":" + cluster.getName());
									}
									for (PluginTemplate subtemplate : subsub.getTemplates()) {
										if (subtemplate.getName() != null) {
											logger.info("Template Names: " + subtemplate.getName());
											processSubTemplate(subtemplate, subsub);
											templates.add(subtemplate.getName());
										}
									}
									for (SubComponent subcomp : subsub.getSubcomponents()) {
										if (subcomp.getName() != null) {
											logger.info("SubComponent Name: " + subcomp.getName());
											processSubSubComponent(subcomp, subsub,pluginMap, typeMap);
											componentarray.add(subcomp.getName() + ":" + cluster.getName());
										}
										for (PluginTemplate subtemplate : subcomp.getTemplates()) {
											if (subtemplate.getName() != null) {
												logger.info("Template Names: " + subtemplate.getName());
												processTemplate(subtemplate, subcomp);
												templates.add(subtemplate.getName());
											}
										}
									}
								}
								subplugintemplates.put(sc.getName(), subtemplates);
							}
							subcomponenthashmap.put(c.getName(), subcomps);
							plugintemplateshashmap.put(c.getName(), templates);
						}

					}
				}
				prj.save();
			}
			try {
				obj.put(GlobalVariables.SOFTWARE_METACLASS, software);
				obj.put(GlobalVariables.PARTITION_METACLASS, partitionarray);
				obj.put(GlobalVariables.CLUSTER_METACLASS, clusterarray);
				obj.put(GlobalVariables.COMPONENT_METACLASS, componentarray);
				obj.put(GlobalVariables.INTERFACE_BLOCK_METACLASS, listofinterfaceblocks);
				file.write(obj.toJSONString());

				int compCount = componentarray.size();
				int intfCount = listofinterfaceblocks.size();
				int clusterCount = clusterarray.size();
				System.out.println(
						"compCount :" + compCount + " intfCount :" + intfCount + " clusterCount :" + clusterCount);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("Error in adding elements to Json File");
			}
			file.close();

		} catch (Exception e) {
			logger.info("Error in adding elements to Rhapsody Model");
		}
		return sct;
	}

	public void getAllCidLFiles(String filePath) throws IOException {
		fileMapping = new HashMap<String, String>();
		allFilesPath = new ArrayList<String>();
		Files.walk(Paths.get(filePath)).filter(p -> p.toFile().isFile())
				.filter(p -> p.getFileName().toString().endsWith(".cidl"))
				.forEach(p -> fileMapping.put(p.getFileName().toString(),p.toString()));
//		System.out.println(fileMapping);
	}

	public String readCidlFileLineByLine_component(String componentFileName, String componentName) throws IOException {
		String eachFilePath = fileMapping.get(componentFileName);
		BufferedReader br = new BufferedReader(new FileReader(eachFilePath));
		BufferedReader br1 = new BufferedReader(new FileReader(eachFilePath));
		resultStr = "";
		try {

			String line = br.readLine();
			String nextline = br1.readLine();

			boolean flag = false;
			boolean whenFlag = false;
			boolean bracketFlag = false;

			while (line != null) {
				if ((line.contains("swcomponent") || line.contains("composite_swcomponent")) && line.contains(componentName) ) {
					sb.setLength(0);
					while (nextline != null && !nextline.equals(line)) {
						nextline = br1.readLine();
					}

					flag = true;

					while (flag == true && nextline != null) {
						String filteredLine = removeComments(nextline).trim();
						if(filteredLine.contains(" when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(filteredLine);
						}
						else if(filteredLine.startsWith("when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(" "+filteredLine);
						}
						
						else if (!filteredLine.contains(" when ") && !filteredLine.endsWith(" when") && !filteredLine.endsWith("when")
								&& !filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else if(filteredLine.contains(" when ") || filteredLine.endsWith(" when") && !filteredLine.contains("{") ) {
							whenFlag=true;
							sb.append(filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
						if(filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							whenFlag = true;
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
                        if(filteredLine.startsWith("when") && filteredLine.endsWith("when")) {
                        	whenFlag = true;
							sb.append(" "+filteredLine+" ");
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");	
                        }
                        else {
                        	whenFlag=false;
                        }
						if (!filteredLine.contains(" when ") && !filteredLine.startsWith("when ") && filteredLine.contains("{")) {
							if(line.contains("swcomponent") || line.contains("composite_swcomponent")  || line.endsWith(" when")) {
							bracketFlag = true;
							sb.append(" "+filteredLine);
							}
							else {
								bracketFlag = true;
								sb.append(filteredLine);	
							}
						} else {
							whenFlag = false;
						}

						if (whenFlag == false && bracketFlag == true) {
							flag = false;
						}
					}
					String pattern = " when([^\\sa-zA-Z0-9])";
			    	Pattern r = Pattern.compile(pattern);
			    	Matcher m = r.matcher(sb);
					if (sb.toString().contains(" when ") || m.find( )) {
					processWhenCondition(sb);
					break;
					}
				}
				line = br.readLine();
			}
		
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			br.close();
		}
		return resultStr;
	}

	public String readCidlFileLineByLine_subComponent(String componentFileName, String componentName)
			throws IOException {
		String eachFilePath = fileMapping.get(componentFileName);
		BufferedReader br = new BufferedReader(new FileReader(eachFilePath));
		BufferedReader br1 = new BufferedReader(new FileReader(eachFilePath));
		resultStr = "";
		try {

			String line = br.readLine();
			String nextline = br1.readLine();

			boolean flag = false;
			boolean whenFlag = false;
			boolean bracketFlag = false;

			while (line != null) {
				if (line.contains("subcomponent") && line.contains(componentName)) {
					sb.setLength(0);
					while (nextline != null && !nextline.equals(line)) {
						nextline = br1.readLine();
					}
					flag = true;

					while (flag == true && nextline != null) {
						String filteredLine = removeComments(nextline).trim();
						if(filteredLine.contains(" when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(filteredLine);
						}
						else if(filteredLine.startsWith("when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(" "+filteredLine);
						}
						
						else if (!filteredLine.contains(" when ") && !filteredLine.endsWith(" when") && !filteredLine.endsWith("when")
								&& !filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else if(filteredLine.contains(" when ") || filteredLine.endsWith(" when") && !filteredLine.contains("{") ) {
							whenFlag=true;
							sb.append(filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
						if(filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							whenFlag = true;
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
                        if(filteredLine.startsWith("when") && filteredLine.endsWith("when")) {
                        	whenFlag = true;
							sb.append(" "+filteredLine+" ");
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");	
                        }
                        else {
                        	whenFlag=false;
                        }
						if (!filteredLine.contains(" when ") && !filteredLine.startsWith("when ") && filteredLine.contains("{")) {
							if(line.contains("subcomponent")  || line.endsWith(" when")) {
							bracketFlag = true;
							sb.append(" "+filteredLine);
							}
							else {
								bracketFlag = true;
								sb.append(filteredLine);	
							}
						} else {
							whenFlag = false;
						}

						if (whenFlag == false && bracketFlag == true) {
							flag = false;
						}
					}
					String pattern = " when([^\\sa-zA-Z0-9])";
			    	Pattern r = Pattern.compile(pattern);
			    	Matcher m = r.matcher(sb);
					if (sb.toString().contains(" when ") || m.find( )) {
						processWhenCondition(sb);
						break;
					}
				}
				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			br.close();
		}
		return resultStr;
	}

	public String readCidlFileLineByLine_plugin(String pluginFileName, String pluginName) throws IOException {
		String eachFilePath = fileMapping.get(pluginFileName);
		BufferedReader br = new BufferedReader(new FileReader(eachFilePath));
		BufferedReader br1 = new BufferedReader(new FileReader(eachFilePath));
		resultStr = "";
		try {

			String line = br.readLine();
			String nextline = br1.readLine();

			boolean flag = false;
			boolean whenFlag = false;
			boolean bracketFlag = false;

			while (line != null) {
				if (line.contains("plugin") && line.contains(pluginName)) {
					sb.setLength(0);
					while (nextline != null && !nextline.equals(line)) {
						nextline = br1.readLine();
					}
					flag = true;

					while (flag == true && nextline != null) {
						String filteredLine = removeComments(nextline).trim();
						if(filteredLine.contains(" when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(filteredLine);
						}
						else if(filteredLine.startsWith("when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(" "+filteredLine);
						}
						
						else if (!filteredLine.contains(" when ") && !filteredLine.endsWith(" when") && !filteredLine.endsWith("when")
								&& !filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else if(filteredLine.contains(" when ") || filteredLine.endsWith(" when") && !filteredLine.contains("{") ) {
							whenFlag=true;
							sb.append(filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
						if(filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							whenFlag = true;
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
                        if(filteredLine.startsWith("when") && filteredLine.endsWith("when")) {
                        	whenFlag = true;
							sb.append(" "+filteredLine+" ");
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");	
                        }
                        else {
                        	whenFlag=false;
                        }
						if (!filteredLine.contains(" when ") && !filteredLine.startsWith("when ") && filteredLine.contains("{")) {
							if(line.contains("plugin")  || line.endsWith(" when")) {
							bracketFlag = true;
							sb.append(" "+filteredLine);
							}
							else {
								bracketFlag = true;
								sb.append(filteredLine);	
							}
						} else {
							whenFlag = false;
						}

						if (whenFlag == false && bracketFlag == true) {
							flag = false;
						}
					}
					String pattern = " when([^\\sa-zA-Z0-9])";
			    	Pattern r = Pattern.compile(pattern);
			    	Matcher m = r.matcher(sb);
					if (sb.toString().contains(" when ") || m.find( )) {
						processWhenCondition(sb);
						break;
					}
				}
				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			br.close();
		}
		return resultStr;
	}

	public String readCidlFileLineByLine_pluginTemplate(String pluginTemplateFileName, String pluginTemplateName)
			throws IOException {
		String eachFilePath = fileMapping.get(pluginTemplateFileName);
		BufferedReader br = new BufferedReader(new FileReader(eachFilePath));
		BufferedReader br1 = new BufferedReader(new FileReader(eachFilePath));
		resultStr = "";
		try {

			String line = br.readLine();
			String nextline = br1.readLine();

			boolean flag = false;
			boolean whenFlag = false;
			boolean bracketFlag = false;

			while (line != null) {
				if (line.contains("plugin_template") && line.contains(pluginTemplateName)) {
					sb.setLength(0);
					while (nextline != null && !nextline.equals(line)) {
						nextline = br1.readLine();
					}
					flag = true;

					while (flag == true && nextline != null) {
						String filteredLine = removeComments(nextline).trim();
						if(filteredLine.contains(" when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(filteredLine);
						}
						else if(filteredLine.startsWith("when ")  && filteredLine.contains("{")) {
							whenFlag=false;
							bracketFlag=true;
							sb.append(" "+filteredLine);
						}
						
						else if (!filteredLine.contains(" when ") && !filteredLine.endsWith(" when") && !filteredLine.endsWith("when")
								&& !filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else if(filteredLine.contains(" when ") || filteredLine.endsWith(" when") && !filteredLine.contains("{") ) {
							whenFlag=true;
							sb.append(filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
						if(filteredLine.startsWith("when ") && !filteredLine.contains("{")) {
							whenFlag = true;
							sb.append(" "+filteredLine);
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");
						}
						else {
							whenFlag=false;
						}
                        if(filteredLine.startsWith("when") && filteredLine.endsWith("when")) {
                        	whenFlag = true;
							sb.append(" "+filteredLine+" ");
							nextline = br1.readLine();
							if (nextline != null)
								nextline = nextline.replace("\\s+", "");	
                        }
                        else {
                        	whenFlag=false;
                        }
						if (!filteredLine.contains(" when ") && !filteredLine.startsWith("when ") && filteredLine.contains("{")) {
							if(line.contains("plugin_template")  || line.endsWith(" when")) {
							bracketFlag = true;
							sb.append(" "+filteredLine);
							}
							else {
								bracketFlag = true;
								sb.append(filteredLine);	
							}
						} else {
							whenFlag = false;
						}

						if (whenFlag == false && bracketFlag == true) {
							flag = false;
						}
					}
					String pattern = " when([^\\sa-zA-Z0-9])";
			    	Pattern r = Pattern.compile(pattern);
			    	Matcher m = r.matcher(sb);
					if (sb.toString().contains(" when ") || m.find( )) {
						processWhenCondition(sb);
						break;
					}
				}
				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			br.close();
		}
		return resultStr;
	}

	public String removeComments(String line) {
		String s = line.replaceAll("/\\*.*?\\*/", "") // block comment
				.replaceAll("//.*", "");
		
		return s;
	}

	private void processWhenCondition(StringBuilder sb) {
		// TODO Auto-generated method stub
		String andOPStr = "";
		String[] stringArray ;
		if(sb.toString().contains(" when ")) {
		stringArray = sb.toString().split(" when ");
		}else {
			stringArray = sb.toString().split(" when");
		}
        if(stringArray.length>0) {
        	String str=stringArray[1].replace("{", "").replace("FEAT_", "").replace("Feat_", "").replace("feat_", "")
    				.replace("thirdParty", "");
    		String splitStr[] = str.split("&&");
    		int ct = 1;
    		for (String andOp : splitStr) {
    			if (str.contains("&&") && str.contains("||")) {
    				if (andOp.contains("||")) {
    					String splitOROp[] = andOp.split("\\|\\|");
    					int ctOp = 1;
    					for (String subOROp : splitOROp) {
    						if (subOROp.contains("!") && !subOROp.contains("!=")) {
    							subOROp = subOROp.replace("!", "NOT(");
    							subOROp = subOROp.trim() + ")"+" ";
    						}
    						if (subOROp.contains("!=")) {
    							String value=processNotEqualOperator(subOROp);
    							subOROp=value+" ";
    						}
    						if (ctOp < splitOROp.length)
    							andOPStr = andOPStr.concat(subOROp).trim() + " OR ";
    						else
    							andOPStr = andOPStr.concat(subOROp.trim());
    						ctOp++;
    					}
    				}

    				else if (andOp.contains("!") && !andOp.contains("!=")) {
    					andOp = andOp.replace("!", "NOT(");
    					andOp = andOp.trim() + ")"+" ";
    				}
    				if (andOp.contains("!=")) {
    					String value=processNotEqualOperator(andOp);
    					andOp=value+" ";
    				}
    			}
    			if (ct < splitStr.length) {
    				if (andOp.contains("||")) {
    					andOp = andOp.replace(andOp, "");
    					andOPStr = andOPStr.concat(andOp).trim() + " AND ";
    				} else if (andOp.contains("!") && !andOp.contains("!=")) {
    					andOp = andOp.replace("!", "NOT(");
    					andOp = andOp.trim() + ")"+" ";
    					andOPStr = andOPStr.concat(andOp).trim() + " AND ";
    				} else if (andOp.contains("!=")) {
    					String value=processNotEqualOperator(andOp);
    					andOp=value+" ";
    					andOPStr = andOPStr.concat(andOp).trim() + " AND ";					
    				} else
    					andOPStr = andOPStr.concat(andOp).trim() + " AND ";
    			} else {
    				if (andOp.contains("||")) {
    					andOp = andOp.replace(andOp, "");
    					andOPStr = andOPStr.concat(andOp);
    				} else if (andOp.contains("!") && !andOp.contains("!=")) {
    					andOp = andOp.replace("!", "NOT(");
    					andOp = andOp.trim() + ")"+" ";
    					andOPStr = andOPStr.concat(andOp);					
    				} else if (andOp.contains("!=")) {
    					String value=processNotEqualOperator(andOp);
    					andOp=value+" ";
    					andOPStr = andOPStr.concat(andOp);					
    				} else
    					andOPStr = andOPStr.concat(andOp.trim());
    			}
    			ct++;
    		}

    		ct = 1;
    		String splitStrOR[] = andOPStr.split("\\|\\|");
    		for (String oROp : splitStrOR) {
    			if (str.contains("||")) {
    				if (oROp.contains("!") && !oROp.contains("!=")) {
    					oROp = oROp.replace("!", "NOT(");
    					oROp = oROp.trim() + ")"+" ";
    				}
    				if (oROp.contains("!=")) {
    					String value=processNotEqualOperator(oROp);
    					oROp=value+" ";
    				}
    			}
    			if (ct < splitStrOR.length)
    				resultStr = resultStr.concat(oROp).trim() + " OR ";
    			else
    				resultStr = resultStr.concat(oROp.trim());
    			ct++;
    		}

    		if (!str.contains("&&") && !str.contains("||")) {
    			if (str.contains("!") && !str.contains("!=")) {
    				str = str.replace("!", "NOT(");
    				str = str + ")";
    				resultStr = str;
    			}
    			else if (str.contains("!=")) {
    				String value=processNotEqualOperator(str);
    				resultStr=value;
    			}

    		}
    		if (str.contains("||") && !str.contains("&&")) {
    			resultStr = "";
    			String[] splitOr = str.split("\\|\\|");
    			int counter_oR = 1;
    			for (String oRCondi : splitOr) {
    				if (oRCondi.contains("!") && !oRCondi.contains("!=")) {
    					oRCondi = oRCondi.replace("!", "NOT(");
    					oRCondi = oRCondi.trim() + ")"+" ";
    					
    				}
    				else if (oRCondi.contains("!=")) {
    					String value=processNotEqualOperator(oRCondi);
    					oRCondi=value+" ";
    					
    				}
    				if (counter_oR < splitOr.length) {
    					resultStr = resultStr.concat(oRCondi).trim() + " OR ";
    				} else {
    					resultStr = resultStr.concat(oRCondi.trim());

    				}
    				counter_oR++;
    			}
		}
	}
}
	
	private static String processNotEqualOperator(String notEqual_Value) {
		// TODO Auto-generated method stub
		String filteredLine = filterBrackets(notEqual_Value).trim();
		if(filteredLine.equals("")) {
			notEqual_Value = notEqual_Value.replace("!=", "__");
			notEqual_Value="NOT("+notEqual_Value.trim()+")";
	    	
		}
		else if(filteredLine.equals("(")) {
			notEqual_Value = notEqual_Value.replace("!=", "__");
			notEqual_Value=notEqual_Value.replace("(", "");
			notEqual_Value="NOT("+notEqual_Value.trim()+")";
			notEqual_Value="("+notEqual_Value;
	    	
		}
		else if(filteredLine.equals("((")){
			notEqual_Value = notEqual_Value.replace("!=", "__");
			notEqual_Value=notEqual_Value.replace("(", "");
			notEqual_Value="NOT("+notEqual_Value.trim()+")";
			notEqual_Value="(("+notEqual_Value;
    	
		}
		else if(filteredLine.equals("(((")) {
			notEqual_Value = notEqual_Value.replace("!=", "__");
			notEqual_Value=notEqual_Value.replace("(", "");
			notEqual_Value="NOT("+notEqual_Value.trim()+")";
			notEqual_Value="((("+notEqual_Value;
	    	
		}
		else if(filteredLine.equals("((((")) {
			notEqual_Value = notEqual_Value.replace("!=", "__");
			notEqual_Value=notEqual_Value.replace("(", "");
			notEqual_Value="NOT("+notEqual_Value.trim()+")";
			notEqual_Value="(((("+notEqual_Value;
	    	
		}
		return notEqual_Value;
	}

	private static String filterBrackets(String line) {
		String s = line.replaceAll("[^(]", "").trim(); // block comment
		return s;
	}

	private HashMap<String, List<String>> checkElementsinModel(IRPUnit cluster, String component) {

		// TODO Auto-generated method stub
		existingelements.clear();
		IRPModelElement component_in_pkg = cluster.findNestedElement(component, GlobalVariables.CLASS_METACLASS);

		IRPModelElement element = null;
		List<String> elementnames = new ArrayList<String>();
		IRPCollection components = component_in_pkg.getNestedElementsByMetaClass(GlobalVariables.PORT_METACLASS, 0);
		for (Object obj : components.toList()) {
			element = (IRPModelElement) obj;
			if (element.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PROXY_PORT_METACLASS)) {
				elementnames.add(element.getName());
			}
		}
		existingelements.put(component, elementnames);
		return existingelements;
	}

	/**
	 * function to process required interfaces from cidl
	 * 
	 * @param sw
	 * @param pluginMap
	 * @return
	 */
	public String setTypesandFlows(Software sw, HashMap<String, EList<Plugin>> pluginMap) {
		String types = new String();
		try {
			processSoftware(sw);
			try {
				for (Partition p : sw.getPartitions()) {
					processPartition(p, sw);
					for (Cluster cl : p.getCluster()) {
						if (cl.getName() != null) {
							logger.info("Cluster Name: " + cl.getName());
							processCluster(cl, p);
							try {
								for (Component c : cl.getComponents()) {
									if (c.getName() != null) {
										logger.info("Component Name :" + c.getName());
										processComponentFlowsAndTypes(c, cl, pluginMap);
									}
									for (PluginTemplate template : c.getTemplates()) {
										if (template.getName() != null) {
											logger.info("Template Name:" + template.getName());
											processTemplateFlowsAndTypes(template, c);
										}
									}
									for (SubComponent sc : c.getSubcomponents()) {
										if (sc.getName() != null) {
											logger.info("SubComponent Name:" + sc.getName());
											processSubComponentFlowsAndTypes(sc, c);
										}
										for (PluginTemplate template : sc.getTemplates()) {
											if (template.getName() != null) {
												logger.info("Template Name: " + template.getName());
												processTemplateFlowsAndTypes(template, sc);
											}
										}
										for (SubComponent subsub : sc.getSubcomponents()) {
											if (subsub.getName() != null) {
												logger.info("SubComponent Name:" + subsub.getName());
												processSubSubComponentFlowsAndTypes(subsub, sc);
											}
											for (PluginTemplate template : subsub.getTemplates()) {
												if (template.getName() != null) {
													logger.info("Template Name: " + template.getName());
													processTemplateFlowsAndTypes(template, subsub);
												}
											}
											for (SubComponent subcomp : subsub.getSubcomponents()) {
												if (subcomp.getName() != null) {
													logger.info("SubComponent Name:" + subcomp.getName());
													processSubSubComponentFlowsAndTypes(subcomp, subsub);
												}
												for (PluginTemplate template : subcomp.getTemplates()) {
													if (template.getName() != null) {
														logger.info("Template Name:" + template.getName());
														processTemplateFlowsAndTypes(template, subcomp);
													}
												}
											}
										}
									}
								}
							} catch (Exception e) {
								logger.info("Error while processing Component\n");
							}

						}
					}
					prj.save();
				}
			} catch (Exception e) {
				logger.info("Error while processing partition \n");
			}
		} catch (Exception e) {
			logger.info("Error while adding required and Delegate Interfaces\n");
		}
		return types;
	}

	/**
	 * function to process Plugin templates of sub components
	 * 
	 * @param template
	 * @param sc
	 */

	private void processSubTemplate(PluginTemplate template, SubComponent sc) {
		// TODO Auto-generated method stub
		try {			
			plugin_template = (IRPUnit) checkifElementExists(subsubcomponent, template.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			if (plugin_template == null) {
				plugin_template = (IRPUnit) addElementtoModel(subsubcomponent,
						GlobalVariables.PLUGIN_TEMPLATE_METACLASS, template.getName());
				String description = template.getDesc();
				plugin_template.setDescription(description);

				subplugin_part = (IRPInstance) checkifElementExists(subcomponent,
						GlobalVariables.PART_KEYWORD + template.getName(), GlobalVariables.PART_USER_METACLASS);
				if (subplugin_part == null) {
					subplugin_part = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + template.getName());
					subplugin_part.setOtherClass((IRPClassifier) plugin_template);
				}
				IRPTag rl = (IRPTag) checkifElementExists(plugin_template, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(plugin_template, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
				}
				IRPTag reqid = (IRPTag) checkifElementExists(plugin_template, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(plugin_template, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
				}
			} else {
				logger.info("Plugin Template : " + template.getName() + " already exists");
			}
			// when condition
			if (template.getStrapControlStructure() != null) {
				pluginTemplate_fileName = template.eResource().getURI().lastSegment();
				pluginTemplate_fileName_parent = sc.eResource().getURI().lastSegment();
				try {
					if (fileMapping.containsKey(pluginTemplate_fileName)) {
						String whenValues = readCidlFileLineByLine_pluginTemplate(pluginTemplate_fileName,
								plugin_template.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) plugin_template
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin_template
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					} else {
						String whenValues = readCidlFileLineByLine_pluginTemplate(pluginTemplate_fileName_parent,
								plugin_template.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) plugin_template
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin_template
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					}
				}

				catch (Exception e) {
					logger.info("File not found in cIDL" + pluginTemplate_fileName);
					e.printStackTrace();
				}
			}
			// Provided Interface
			existingelements = checkElementsinModel(subsubcomponent, template.getName());

			try {

				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;

				for (AccessibleInterface ai : template.getProvidedInterfaces()) {

					elementPresent = false;

					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}

					ProcessProvidedElements.processPluginInterfaceBlockAndPort(pkg, ai, plugin_template, subcomponent,
							template, elementPresent);
					if (ai instanceof ProvidedInterface) {
						ProvidedInterface pi = (ProvidedInterface) ai;
						try {
							for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
								providedelements.put(pie.getName(), pie.getName());
								if (!elementPresent) {

									ProcessProvidedElements.processPluginInterface(prj, pkg, template, subcomponent, pi,
											pie, true);
								}
							}
						} catch (Exception e) {
							logger.info("Exception caught while adding provided interfaces");
							e.printStackTrace();
						}
					}

					interfaces.putIfAbsent(ai.getName(), providedelements);
				}

				pluginTemplateInterfaces.put(template, interfaces);

			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception caught while adding plugin template interfaces");
			}
		} catch (Exception e) {
			logger.info("Exception while adding Plugin Template\n");
		}
	}

	/**
	 * function to process Plugin templates of sub components
	 * 
	 * @param template
	 * @param sc
	 */

	@SuppressWarnings("unchecked")
	private void processTemplate(PluginTemplate template, SubComponent sc) {
		// TODO Auto-generated method stub
		try {			
			plugin_template = (IRPUnit) checkifElementExists(subcomponent, template.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			if (plugin_template == null) {
				plugin_template = (IRPUnit) addElementtoModel(subcomponent, GlobalVariables.PLUGIN_TEMPLATE_METACLASS,
						template.getName());
				String description = template.getDesc();
				plugin_template.setDescription(description);
				fullPathName = plugin_template.getFullPathName();
				fullPathMap.putIfAbsent(plugin_template.getName(), fullPathName);
				subplugin_part = (IRPInstance) checkifElementExists(subcomponent,
						GlobalVariables.PART_KEYWORD + template.getName(), GlobalVariables.PART_USER_METACLASS);
				if (subplugin_part == null) {
					subplugin_part = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + template.getName());
					subplugin_part.setOtherClass((IRPClassifier) plugin_template);
				}
				IRPTag rl = (IRPTag) checkifElementExists(plugin_template, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(plugin_template, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
				}
				IRPTag reqid = (IRPTag) checkifElementExists(plugin_template, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(plugin_template, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
				}
			} else {
				logger.info("Plugin Template : " + template.getName() + " already exists");
			}
			// when Condition
			if (template.getStrapControlStructure() != null) {
				pluginTemplate_fileName = template.eResource().getURI().lastSegment();
				pluginTemplate_fileName_parent = sc.eResource().getURI().lastSegment();
				try {
					if (fileMapping.containsKey(pluginTemplate_fileName)) {
						String whenValues = readCidlFileLineByLine_pluginTemplate(pluginTemplate_fileName,
								plugin_template.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) plugin_template
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin_template
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					} else {
						String whenValues = readCidlFileLineByLine_pluginTemplate(pluginTemplate_fileName_parent,
								plugin_template.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) plugin_template
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin_template
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					}
				}

				catch (Exception e) {
					logger.info("File not found in cIDL" + pluginTemplate_fileName);
					e.printStackTrace();
				}
			}
			// Provided Interface
			existingelements = checkElementsinModel(subcomponent, template.getName());

			try {

				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;

				for (AccessibleInterface ai : template.getProvidedInterfaces()) {

					elementPresent = false;

					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}

					ProcessProvidedElements.processPluginInterfaceBlockAndPort(pkg, ai, plugin_template, swcomponent,
							template, elementPresent);
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName() + ":" + template.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						try {
							for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
								providedelements.put(pie.getName(), pie.getName());
								if (!elementPresent) {

									ProcessProvidedElements.processPluginInterface(prj, pkg, template, swcomponent, pi,
											pie, true);
								}
							}
						} catch (Exception e) {
							logger.info("Exception caught while adding provided interfaces");
							e.printStackTrace();
						}
					}

					interfaces.putIfAbsent(ai.getName(), providedelements);
				}

				pluginTemplateInterfaces.put(template, interfaces);

			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception caught while adding plugin template interfaces");
			}
		} catch (Exception e) {
			logger.info("Exception while adding Plugin Template\n");
		}
	}

	/**
	 * function to check if element @param name already exists
	 * 
	 * @param element_in_model
	 * @param metaclass
	 */
	private IRPModelElement checkifElementExists(IRPUnit element_in_model, String name, String metaclass) {
		// TODO Auto-generated method stub
		existing_elementinModel = element_in_model.findNestedElement(name, metaclass);
		return existing_elementinModel;
	}

	/**
	 * function to add new element @param element_name to model
	 * 
	 * @param model_element
	 * @param metaClass
	 */
	private IRPModelElement addElementtoModel(IRPUnit model_element, String metaClass, String element_name) {
		try {
			element_toAdd = model_element.addNewAggr(metaClass, element_name);
		} catch (Exception e) {
			logger.info("exception caught while adding element to model ");
			e.printStackTrace();
		}
		return element_toAdd;
	}

	/**
	 * function to process Plugin Templates of Components
	 */

	@SuppressWarnings("unchecked")
	private void processTemplate(PluginTemplate template, Component c) {
		try {			
			plugin_template = (IRPClass) checkifElementExists(swcomponent, template.getName(),
					GlobalVariables.CLASS_METACLASS);
			if (plugin_template == null) {
				plugin_template = (IRPUnit) addElementtoModel(swcomponent, GlobalVariables.PLUGIN_TEMPLATE_METACLASS,
						template.getName());
				String description = template.getDesc();
				plugin_template.setDescription(description);
				fullPathName = plugin_template.getFullPathName();
				fullPathMap.putIfAbsent(plugin_template.getName(), fullPathName);
				plugin_part = (IRPInstance) checkifElementExists(swcomponent,
						GlobalVariables.PART_KEYWORD + template.getName(), GlobalVariables.PART_USER_METACLASS);
				if (plugin_part == null) {
					plugin_part = (IRPInstance) addElementtoModel(swcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + template.getName());
					plugin_part.setOtherClass((IRPClassifier) plugin_template);
				} else {
					logger.info("Element already exists\n");
				}
				IRPTag rl = (IRPTag) checkifElementExists(plugin_template, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(plugin_template, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
				}
				IRPTag reqid = (IRPTag) checkifElementExists(plugin_template, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(plugin_template, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
				}

			} else {
				logger.info("Plugin Template : " + template.getName() + " already exists");
			}
			// when Condition
			if (template.getStrapControlStructure() != null) {
				pluginTemplate_fileName = template.eResource().getURI().lastSegment();
				pluginTemplate_fileName_parent = c.eResource().getURI().lastSegment();
				try {
					if (fileMapping.containsKey(pluginTemplate_fileName)) {
						String whenValues = readCidlFileLineByLine_pluginTemplate(pluginTemplate_fileName,
								plugin_template.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) plugin_template
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin_template
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					} else {
						String whenValues = readCidlFileLineByLine_pluginTemplate(pluginTemplate_fileName_parent,
								plugin_template.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) plugin_template
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin_template
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					}
				}

				catch (Exception e) {
					e.printStackTrace();
					logger.info("File not found in cIDL" + pluginTemplate_fileName);
				}
			}
			// Provided Interface

			existingelements = checkElementsinModel(subcluster, c.getName());

			try {

				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;
				for (AccessibleInterface ai : template.getProvidedInterfaces()) {
					elementPresent = false;
					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}

					ProcessProvidedElements.processPluginInterfaceBlockAndPort(pkg, ai, plugin_template, swcomponent,
							template, elementPresent);
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName() + ":" + template.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						try {
							for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
								providedelements.put(pie.getName(), pie.getName());
								if (!elementPresent) {
									ProcessProvidedElements.processPluginInterface(prj, pkg, template, swcomponent, pi,
											pie, true);

								}
							}
						} catch (Exception e) {
							logger.info("Exception caught while adding provided interfaces");
							e.printStackTrace();
						}
					}

					interfaces.putIfAbsent(ai.getName(), providedelements);
				}

				pluginTemplateInterfaces.put(template, interfaces);

			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception caught while adding plugin template");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Element already exists");
		}

	}

	/**
	 * function to process required interfaces of Plugin Templates of Components
	 */
	private void processTemplateFlowsAndTypes(PluginTemplate template, SubComponent c) {
		// TODO Auto-generated method stub
		plugin_template = (IRPClass) subcomponent.findNestedElement(template.getName(),
				GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		// Required Interfaces
		existingConnectors = checkforConnectors(subcomponent, template.getName());
		portPresent = false;
		try {
			for (RequiredInterface ri : template.getRequiredInterfaces()) {
				portPresent = false;
				existingConnectors = checkforConnectors(subcomponent, template.getName());
				for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
					ProvidedElement conj_port = ri.getInterface();
					AbstractComponent compname = ri.getComponent();
					for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
						HashMap<String, String> connector = entry.getValue();
						String connector_entry = connector.get(compname.getName() + "__" + conj_port.getName());
						if (connector_entry != null) {
							portPresent = true;
							break;
						} else {
							portPresent = false;
						}
					}

					if (!portPresent) {
						processPluginInterfaces(plugin_template, ri, rie);
					}
				}
			}
		} catch (Exception e) {
			logger.info("Error while processing Required Interfaces\n");
		}
	}

	private void processPluginInterfaces(IRPUnit plugin_template, RequiredInterface ri, RequiredInterfaceEntity rie) {
		// TODO Auto-generated method stub
		if (rie instanceof RequiredConstant) {
			// processPluginRequiredConstant(plugin_template, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processPluginRequiredPort(plugin_template, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processPluginRequiredFunction(plugin_template, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processPluginRequiredVariable(plugin_template, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element");
		}
		// processPlugin(abscomp, provelem);
	}

	private void processPluginRequiredVariable(IRPUnit plugin_template, RequiredInterface ri,
			RequiredInterfaceEntity rie) {
		// TODO Auto-generated method stub

	}

	private void processPluginRequiredFunction(IRPUnit plugin_template, RequiredInterface ri,
			RequiredInterfaceEntity rie) {
		// TODO Auto-generated method stub

	}

	private void processPluginRequiredPort(IRPUnit plugin_template, RequiredInterface ri, RequiredInterfaceEntity rie) {
		// TODO Auto-generated method stub

	}

//	private void processPluginRequiredConstant(IRPUnit plugin_template, RequiredInterface ri,
//			RequiredInterfaceEntity rie) {
//		// TODO Auto-generated method stub
//
//		try {
//			match = false;
//			AbstractComponent providedComponent = null;
//			RequiredConstant rc = (RequiredConstant) rie;
//			AbstractComponent maincomp = ri.getComponent();
//			ProvidedElement provelem = ri.getInterface();
//			for (Entry<AbstractComponent, List<String>> entry : providedInterfaces.entrySet()) {
//				if (!match) {
//					List<String> interfacename = entry.getValue();
//					for (String interface_name : interfacename) {
//						if (interface_name.equalsIgnoreCase(provelem.getName())) {
//							providedComponent = entry.getKey();
//							if (providedComponent.getName().equalsIgnoreCase(maincomp.getName())
//									|| providedComponent instanceof SubComponent) {
//								match = true;
//								break;
//							}
//						}
//
//					}
//				} else {
//					break;
//				}
//			}
//			ProvidedInterface pi = (ProvidedInterface) provelem;
//			for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
//				if (pie instanceof ProvidedConstant) {
//					if (pie.getName().equalsIgnoreCase(rc.getConstant().getName())) {
//						reqport = (IRPPort) plugin_template.findNestedElement(pi.getName(),
//								GlobalVariables.PROXY_PORT_METACLASS);
//						reqconnector = (IRPLink) plugin_template.findNestedElement(
//								providedComponent.getName() + "__" + pi.getName(), GlobalVariables.CONNECTOR_METACLASS);
//						if (reqport == null) {
//							reqport = (IRPPort) plugin_template.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
//									provelem.getName());
//							funcClass = getRequiredPortContract(pkg, provelem.getName());
//							if (funcClass != null) {
//								reqport.setContract(funcClass);
//							}
//						}
//						reqport.setIsReversed(1);
//						if (reqconnector == null) {
//							if (providedComponent instanceof SubComponent
//									|| providedComponent instanceof PluginTemplate) {
//								processTopLevelSubConnectors(providedComponent, maincomp, provelem, plugin_template);
//							} else {
//								processTopLevelConnectors(providedComponent, maincomp, provelem, plugin_template);
//							}
//
//						} else {
//							logger.info("Connector already exists");
//						}
//						match = true;
//						break;
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			logger.info("Error in creating connectors");
//		}
//
//	}

	/**
	 * function to process required interfaces of Plugin Templates of Components
	 */
	private void processTemplateFlowsAndTypes(PluginTemplate template, Component c) {
		// TODO Auto-generated method stub
		plugin_template = (IRPClass) swcomponent.findNestedElement(template.getName(),
				GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		// Required Interfaces
		existingConnectors = checkforConnectors(swcomponent, template.getName());
		portPresent = false;
		try {
			for (RequiredInterface ri : template.getRequiredInterfaces()) {
				portPresent = false;
				existingConnectors = checkforConnectors(swcomponent, template.getName());
				for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
					ProvidedElement conj_port = ri.getInterface();
					AbstractComponent compname = ri.getComponent();
					for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
						HashMap<String, String> connector = entry.getValue();
						String connector_entry = connector.get(compname.getName() + "__" + conj_port.getName());

						if (connector_entry != null) {
							portPresent = true;
							break;
						} else {
							portPresent = false;
						}
					}

					if (!portPresent) {
						processRequiredInterfaces(plugin_template, ri, rie);
						// processPluginRequiredInterface(plugin_template, ri, template, null, rie,
						// false);
					}
				}
			}
		} catch (Exception e) {
			logger.info("Error while processing Required Interfaces\n");
		}
	}

	/**
	 * function to process software
	 */
	private void processSoftware(Software sw) {
		// TODO Auto-generated method stub
		try {
			software = (IRPClass) checkifElementExists(pkg, ((Software) sw).getName(),
					GlobalVariables.SOFTWARE_METACLASS);
			if (software == null) {
				software = (IRPClass) addElementtoModel(pkg, GlobalVariables.SOFTWARE_METACLASS,
						((Software) sw).getName());
				String description = sw.getDesc();
				software.setDescription(description);
			}
		} catch (Exception e) {
			logger.info("Exception while adding Software to Model");
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * function to process Nested Subcomponents
	 * 
	 * @param subComponent
	 * @param typeMap
	 */
	private void processSubSubComponent(SubComponent subComponent, SubComponent c, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {
		try {
			nestedsubcomponent = (IRPClass) checkifElementExists(subsubcomponent,
					((SubComponent) subComponent).getName(), GlobalVariables.CLASS_METACLASS);
			if (nestedsubcomponent == null) {
				nestedsubcomponent = (IRPUnit) addElementtoModel(subsubcomponent, GlobalVariables.COMPONENT_METACLASS,
						((SubComponent) subComponent).getName());
				String description = subComponent.getDesc();
				String label = subComponent.getLongName();
				nestedsubcomponent.setDescription(description);
				nestedsubcomponent.setDisplayName(label);

				nestedsubcomponent.setSeparateSaveUnit(1);
				if (typeMap.containsKey(nestedsubcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(nestedsubcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl) {
								if (!datatype.getName().equalsIgnoreCase("void")) {
									IRPClassifier data = (IRPClassifier) nestedsubcomponent
											.findNestedElement(datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
									if (data == null) {
										data = (IRPClassifier) addElementtoModel(nestedsubcomponent,
												GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());
									}
								}
							}
						});

					}
				}
				String thirdParty = subComponent.getThirdParty();
				if (thirdParty != null) {
					if (thirdparty_stereotype != null) {
						nestedsubcomponent.addSpecificStereotype(thirdparty_stereotype);
					}
				}
				if (subComponent instanceof CompositeComponent) {

					if (composite_stereotype != null) {
						nestedsubcomponent.addSpecificStereotype(composite_stereotype);
					}

				}
				String archi = subComponent.getArchitect();
				IRPTag rl = (IRPTag) checkifElementExists(nestedsubcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(nestedsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					if (archi != null) {
						nestedsubcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(nestedsubcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(nestedsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = subComponent.getReq();
					for (Integer id : req.getReqDocIds()) {
						nestedsubcomponent.setTagValue(reqid, id.toString());
					}
				}
				sub_part = (IRPInstance) checkifElementExists(subsubcomponent,
						GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (sub_part == null) {
					sub_part = (IRPInstance) addElementtoModel(subsubcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName());
					sub_part.setOtherClass((IRPClassifier) nestedsubcomponent);
				}
			} else {
				logger.info("SubComponent : " + subComponent.getName() + " already exists");
			}
			// Provided Interfaces
			existingelements = checkElementsinModel(subsubcomponent, subComponent.getName());
			
			if (pluginMap.containsKey(nestedsubcomponent.getName())) {
				EList<Plugin> plugin = pluginMap.get(nestedsubcomponent.getName());
				if (plugin != null) {
					for (Plugin pluginname : plugin) {
						System.out.println("Plugin:" + pluginname.getName());
						plugin.forEach(pluginComponent -> {
							processNestedSubCompPlugin(pluginComponent);

						});
					}
				}
			}
			
			// when condition
			if (subComponent.getStrapControlStructure() != null) {
				CidlFileName = subComponent.eResource().getURI().lastSegment();
				CidlFileName_parent = c.eResource().getURI().lastSegment();
				try {
					if (fileMapping.containsKey(CidlFileName)) {
						String whenValues = readCidlFileLineByLine_subComponent(CidlFileName,
								nestedsubcomponent.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) nestedsubcomponent
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) nestedsubcomponent
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					} else {
						String whenValues = readCidlFileLineByLine_subComponent(CidlFileName_parent,
								nestedsubcomponent.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) nestedsubcomponent
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) nestedsubcomponent
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					}
				} catch (Exception e) {
					logger.info("File not found Exception" + c.getName() + ".cidl" + " /" + subComponent.getName()
							+ ".cidl");
					e.printStackTrace();
				}
			}
			
			try {
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();
				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;
				for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
					elementPresent = false;
					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}
					processNestedSubProvidedInterfaceBlockAndPort(ai, c, elementPresent);
					if (ai instanceof ProvidedInterface) {
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(ai.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processInterface(prj, pkg, nestedsubcomponent, subsubcomponent,
										c, pi, pie, true);
							}
						}
					}

				}

				providedInterfaces.put(subComponent, interfaces);

			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception while processing Provided Elements of Component" + subComponent.getName());
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.info("Exception caught while adding Sub Components\n");
		}
	}

	

	private void processNestedSubProvidedInterfaceBlockAndPort(AccessibleInterface ai, SubComponent c,
			boolean elementPresent) {
		// TODO Auto-generated method stub
		if (!elementPresent) {
			if (ai instanceof DelegateInterface) {
				subintfblck = (IRPClass) nestedsubcomponent.findNestedElement(
						"_" + ((AccessibleInterface) ai).getName(), GlobalVariables.DELEGATEINTERFACE_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) nestedsubcomponent.addNewAggr(GlobalVariables.DELEGATEINTERFACE_METACLASS,
							"_" + ((AccessibleInterface) ai).getName());
				}
				port = (IRPPort) nestedsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) nestedsubcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getPortContract(pkg, port, ai, prj);
				port.setContract(funcClass);
			} else {
				subintfblck = (IRPClass) nestedsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) nestedsubcomponent.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				// SynchronizeElements.synchronizeInterfaceGroups(subComponent2, subcomponent);
				port = (IRPPort) nestedsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) nestedsubcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getIntfBlockFunctionClass(pkg, port, (IRPProject) prj);
				port.setContract(funcClass);
			}
		}

	}

	/**
	 * 
	 * function to process Nested Subcomponents
	 * 
	 * @param subComponent
	 * @param typeMap
	 */
	@SuppressWarnings("unchecked")
	private void processComponent(SubComponent subComponent, SubComponent c, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {
		try {			
			subsubcomponent = (IRPClass) checkifElementExists(subcomponent, ((SubComponent) subComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (subsubcomponent == null) {
				subsubcomponent = (IRPUnit) addElementtoModel(subcomponent, GlobalVariables.COMPONENT_METACLASS,
						((SubComponent) subComponent).getName());
				String description = subComponent.getDesc();
				String label = subComponent.getLongName();
				subsubcomponent.setDescription(description);
				subsubcomponent.setDisplayName(label);

				subsubcomponent.setSeparateSaveUnit(1);
				if (typeMap.containsKey(subsubcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(subsubcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl) {
								if (!datatype.getName().equalsIgnoreCase("void")) {
									IRPClassifier data = (IRPClassifier) subsubcomponent
											.findNestedElement(datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
									if (data == null) {
										data = (IRPClassifier) addElementtoModel(subsubcomponent,
												GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());
									}
								}
							}
						});

					}
				}
				String thirdParty = subComponent.getThirdParty();
				if (thirdParty != null) {
					if (thirdparty_stereotype != null) {
						subsubcomponent.addSpecificStereotype(thirdparty_stereotype);
					}
				}
				if (subComponent instanceof CompositeComponent) {

					if (composite_stereotype != null) {
						subsubcomponent.addSpecificStereotype(composite_stereotype);
					}

				}
				String archi = subComponent.getArchitect();
				IRPTag rl = (IRPTag) checkifElementExists(subsubcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(subsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					if (archi != null) {
						subsubcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(subsubcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(subsubcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = subComponent.getReq();
					for (Integer id : req.getReqDocIds()) {
						subsubcomponent.setTagValue(reqid, id.toString());
					}
				}
				sub_part = (IRPInstance) checkifElementExists(subcomponent,
						GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (sub_part == null) {
					sub_part = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName());
					sub_part.setOtherClass((IRPClassifier) subsubcomponent);
				}
			} else {
				logger.info("SubComponent : " + subComponent.getName() + " already exists");
			}
			// Provided Interfaces
			existingelements = checkElementsinModel(subcomponent, subComponent.getName());
			
			if (pluginMap.containsKey(subsubcomponent.getName())) {
				EList<Plugin> plugin = pluginMap.get(subsubcomponent.getName());
				if (plugin != null) {
					for (Plugin pluginname : plugin) {
						System.out.println("Plugin:" + pluginname.getName());
						plugin.forEach(pluginComponent -> {
							processSubSubCompPlugin(pluginComponent);

						});
					}
				}
			}
            //when Condition
			if (subComponent.getStrapControlStructure() != null) {
				CidlFileName = subComponent.eResource().getURI().lastSegment();
				CidlFileName_parent = c.eResource().getURI().lastSegment();
				try {
					if (fileMapping.containsKey(CidlFileName)) {
						String whenValues = readCidlFileLineByLine_subComponent(CidlFileName,
								subsubcomponent.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) subsubcomponent
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) subsubcomponent
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					} else {
						String whenValues = readCidlFileLineByLine_subComponent(CidlFileName_parent,
								subsubcomponent.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) subsubcomponent
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) subsubcomponent
										.addNewAggr("Constraint", "pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					}
				} catch (Exception e) {
					logger.info("File not found exception" + subComponent.getName() + ".cidl" + " /" + c.getName()
							+ ".cidl");
					e.printStackTrace();
				}
			}
			
			try {
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();
				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;
				for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
					elementPresent = false;
					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}
					processSubProvidedInterfaceBlockAndPort(ai, c, elementPresent);
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName() + ":" + subComponent.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(ai.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processInterface(prj, pkg, subsubcomponent, subcomponent, c, pi,
										pie, true);
							}
						}
					}

				}

				providedInterfaces.put(subComponent, interfaces);

			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception while processing Provided Elements of Component" + subComponent.getName());
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.info("Exception caught while adding Sub Components\n");
		}
	}

	
	/**
	 * function to process Nested Subcomponents
	 * 
	 * @param subComponent
	 * @param typeMap
	 */

	@SuppressWarnings("unchecked")
	private void processComponent(SubComponent subComponent, Component c, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {

		try {			
			subcomponent = (IRPClass) checkifElementExists(swcomponent, ((SubComponent) subComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (subcomponent == null) {
				subcomponent = (IRPUnit) addElementtoModel(swcomponent, GlobalVariables.COMPONENT_METACLASS,
						((SubComponent) subComponent).getName());
				String description = subComponent.getDesc();
				String label = subComponent.getLongName();
				subcomponent.setDescription(description);
				subcomponent.setDisplayName(label);
				fullPathName = subcomponent.getFullPathName();
				fullPathMap.putIfAbsent(subcomponent.getName(), fullPathName);
				subcomponent.setSeparateSaveUnit(1);
				if (typeMap.containsKey(subcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(subcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl) {
								if (!datatype.getName().equalsIgnoreCase("void")) {
									IRPClassifier data = (IRPClassifier) subcomponent
											.findNestedElement(datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
									if (data == null) {
										data = (IRPClassifier) subcomponent
												.addNewAggr(GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());

									}
								}
							}
						});

					}
				}
				String thirdParty = subComponent.getThirdParty();
				if (thirdParty != null) {
					if (thirdparty_stereotype != null) {
						subcomponent.addSpecificStereotype(thirdparty_stereotype);
					}
				}
				if (subComponent instanceof CompositeComponent) {
					if (composite_stereotype != null) {
						subcomponent.addSpecificStereotype(composite_stereotype);
					}
				}
				String archi = subComponent.getArchitect();
				IRPTag rl = (IRPTag) checkifElementExists(subcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(subcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					if (archi != null) {
						subcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(subcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(subcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = subComponent.getReq();
					for (Integer id : req.getReqDocIds()) {
						subcomponent.setTagValue(reqid, id.toString());
					}
				}
				sub_part = (IRPInstance) checkifElementExists(swcomponent,
						GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (sub_part == null) {
					sub_part = (IRPInstance) addElementtoModel(swcomponent, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((SubComponent) subComponent).getName());
					sub_part.setOtherClass((IRPClassifier) subcomponent);
				}
			} else {
				fullPathName = subcomponent.getFullPathName();
				fullPathMap.putIfAbsent(subcomponent.getName(), fullPathName);
				logger.info("SubComponent : " + subComponent.getName() + " already exists");
			}

			// Provided Interfaces
			existingelements = checkElementsinModel(swcomponent, subComponent.getName());
			
			if (pluginMap.containsKey(subcomponent.getName())) {
				EList<Plugin> plugin = pluginMap.get(subcomponent.getName());
				if (plugin != null) {
					for (Plugin pluginname : plugin) {
						System.out.println("Plugin:" + pluginname.getName());
						plugin.forEach(pluginComponent -> {
							processSubCompPlugin(pluginComponent);

						});
					}
				}
			}
			//when condition			
			if (subComponent.getStrapControlStructure() != null) {
				CidlFileName = subComponent.eResource().getURI().lastSegment();
				CidlFileName_parent = c.eResource().getURI().lastSegment();
				try {
					if (fileMapping.containsKey(CidlFileName)) {
						String whenValues = readCidlFileLineByLine_subComponent(CidlFileName, subcomponent.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) subcomponent
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) subcomponent.addNewAggr("Constraint",
										"pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					} else {
						String whenValues = readCidlFileLineByLine_subComponent(CidlFileName_parent,
								subcomponent.getName());
						if (whenValues != "") {
							IRPModelElement constraint = (IRPConstraint) subcomponent
									.findNestedElement("pv_Restriction", "Constraint");
							if (constraint == null) {
								IRPConstraint pv_Restriction_Ele = (IRPConstraint) subcomponent.addNewAggr("Constraint",
										"pv_Restriction");
								if (pvRestriction != null) {
									pv_Restriction_Ele.setStereotype(pvRestriction);
									pv_Restriction_Ele.setSpecification(whenValues);
								}
							}
							System.out.println("when condition :" + whenValues);
						}

					}
				} catch (Exception e) {
					logger.info("File not found exception" + subComponent.getName() + ".cidl" + " / " + c.getName()
							+ ".cidl");
					e.printStackTrace();
				}
			}
			try {
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;
				for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {

					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}

					ProcessProvidedElements.processProvidedInterfaceBlockAndPort(pkg, ai, subComponent, subcomponent,
							elementPresent);
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName() + ":" + subComponent.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(pie.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processInterface(prj, pkg, subcomponent, swcomponent,
										subComponent, pi, pie, true);
							}
						}
					}

					interfaces.put(ai.getName(), providedelements);
				}
				// json synch
				providedInterfaces.put(subComponent, interfaces);

			} catch (Exception e) {
				// TODO: handle exception
				logger.info("Exception while processing Provided Interfaces of Subcomponents");
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception while processing SubComponent : " + subComponent.getName());
		}
	}

	/**
	 * function to process required Interfaces of Nested Subcomponents
	 * 
	 * @author uic38326
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void processSubSubComponentFlowsAndTypes(SubComponent subComponent, SubComponent c) {
		// TODO Auto-generated method stub
		try {
			IRPModelElement swcomponent = subcluster.findNestedElementRecursive(((SubComponent) c).getName(),
					GlobalVariables.COMPONENT_METACLASS);
			subcomponent = (IRPClass) swcomponent.findNestedElement(((SubComponent) subComponent).getName(),
					GlobalVariables.COMPONENT_METACLASS);

			for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
				if (ai instanceof DelegateInterface) {
					for (DelegateEntity rie : ((DelegateInterface) ai).getDelegateEntities()) {
						AbstractComponent dcomp = rie.getDelegateComponent();
						ProvidedInterface pintf = rie.getDelegateInterface();
						IRPModelElement baseClass = pkg.findNestedElementRecursive(dcomp.getName(),
								GlobalVariables.CLASS_METACLASS);
						if (baseClass != null) {
							IRPModelElement mo = baseClass.findNestedElementRecursive(pintf.getName(),
									GlobalVariables.INTERFACE_BLOCK_METACLASS);
							intfblck = (IRPClass) pkg.findNestedElementRecursive(ai.getName(),
									GlobalVariables.CLASS_METACLASS);

							if (mo != null && intfblck != null) {
								IRPGeneralization myGen = intfblck.findGeneralization(mo.getName());
								if (myGen != null) {
									IRPClassifier baseclass = myGen.getBaseClass();
									if (!baseclass.equals(dcomp)) {
										intfblck.addGeneralization((IRPClassifier) mo);
										IRPGeneralization myNewGen = intfblck.findGeneralization(mo.getName());
										if (!baseclass.getName().equals(baseClass.getName())) {
											myNewGen.changeTo(GlobalVariables.REALIZATION_TAG);
										}
									}
								} else {
									intfblck.addGeneralization((IRPClassifier) mo);
									myGen = intfblck.findGeneralization(mo.getName());
									myGen.changeTo(GlobalVariables.REALIZATION_TAG);
								}
							}
							IRPCollection myGeneralizations = intfblck.getGeneralizations();
							for (Object generalizationelement : myGeneralizations.toList()) {
								IRPModelElement generalizationmodelelement = (IRPModelElement) generalizationelement;
								IRPGeneralization general = (IRPGeneralization) generalizationmodelelement;
								general.changeTo(GlobalVariables.REALIZATION_TAG);
							}
						}
					}
				}
			}

			// Required Interfaces
			existingConnectors = checkforConnectors(swcomponent, subComponent.getName());
			portPresent = false;
			for (RequiredInterface ri : subComponent.getRequiredInterfaces()) {
				portPresent = false;
				existingConnectors = checkforConnectors(swcomponent, subComponent.getName());
				for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
					ProvidedElement conj_port = ri.getInterface();
					AbstractComponent compname = ri.getComponent();
					for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
						HashMap<String, String> connector = entry.getValue();
						String connector_entry = connector.get(compname.getName() + "__" + conj_port.getName());
						if (connector_entry != null) {
							portPresent = true;
							break;
						} else {
							portPresent = false;
						}
					}

					if (!portPresent) {
						processRequiredSubSubInterfaces(subcomponent, swcomponent, ri, rie);
					}

				}
			}
		} catch (Exception e) {
			logger.info("Error while processing Required Interface of SubComponent : " + subComponent.getName() + "\n");
		}
	}

	/**
	 * function to process required Interfaces of Subcomponents
	 * 
	 * @author uic38326
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void processSubComponentFlowsAndTypes(SubComponent subComponent, Component c) {
		// TODO Auto-generated method stub
		try {

			subcomponent = (IRPClass) swcomponent.findNestedElementRecursive(((SubComponent) subComponent).getName(),
					GlobalVariables.COMPONENT_METACLASS);
			if (subcomponent == null) {
				processComponent(subComponent, c, null, null);
			}
			for (AccessibleInterface ai : subComponent.getProvidedInterfaces()) {
				if (ai instanceof DelegateInterface) {
					match = false;
					intfblck = (IRPClass) subcomponent.findNestedElement("d_" + ai.getName(),
							GlobalVariables.DELEGATEINTERFACE_METACLASS);
					IRPCollection myGeneralizations = intfblck.getGeneralizations();
					for (DelegateEntity rie : ((DelegateInterface) ai).getDelegateEntities()) {
						AbstractComponent dcomp = rie.getDelegateComponent();
						ProvidedInterface pintf = rie.getDelegateInterface();
						for (Object generalizationelement : myGeneralizations.toList()) {
							IRPGeneralization generalizationmodelelement = (IRPGeneralization) generalizationelement;
							if (generalizationmodelelement.getBaseClass().getName().equalsIgnoreCase(dcomp.getName())) {
								match = true;
								break;
							}
						}
						if (!match) {
							IRPModelElement baseClass = findSourceandDestination(dcomp.getName());
							IRPPort reqport = (IRPPort) subcomponent.findNestedElement(
									dcomp.getName() + "__" + ai.getName(), GlobalVariables.PROXY_PORT_METACLASS);
							IRPLink reqconnector = (IRPLink) subcomponent
									.findNestedElement(dcomp.getName() + "__" + ai.getName(), GlobalVariables.LINK_TAG);

							if (reqport == null) {
								reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
										dcomp.getName() + "__" + ai.getName());
							}

							reqport.setIsReversed(1);
							if (reqconnector == null) {
								IRPModelElement fpartowner = findSourceandDestination(dcomp.getName());
								IRPInstance fpart = findPart(fpartowner);
								IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(pintf.getName(),
										GlobalVariables.PROXY_PORT_METACLASS);
								IRPModelElement tpartowner = pkg.findNestedElementRecursive(subcomponent.getName(),
										GlobalVariables.COMPONENT_METACLASS);
								IRPInstance tpart = findPart(tpartowner);
								IRPPort toport = (IRPPort) subcomponent.findNestedElementRecursive(ai.getName(),
										GlobalVariables.PROXY_PORT_METACLASS);
								if (reqconnector == null) {
									reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport,
											dcomp.getName(), ai.getName());
								}
							}

							if (baseClass != null) {
								IRPModelElement mo = baseClass.findNestedElementRecursive(pintf.getName(),
										GlobalVariables.INTERFACE_BLOCK_METACLASS);
								intfblck = (IRPClass) pkg.findNestedElementRecursive("d_" + ai.getName(),
										GlobalVariables.DELEGATEINTERFACE_METACLASS);
								if (mo != null && intfblck != null) {
									IRPGeneralization myGen = intfblck.findGeneralization(mo.getName());
									if (myGen != null) {
										IRPClassifier baseclass = myGen.getBaseClass();
										if (!baseclass.equals(dcomp)) {
											intfblck.addGeneralization((IRPClassifier) mo);
											IRPGeneralization myNewGen = intfblck.findGeneralization(mo.getName());
											if (!baseclass.getName().equals(baseClass.getName())) {
												myNewGen.changeTo(GlobalVariables.REALIZATION_TAG);
											}
										}
									} else {
										intfblck.addGeneralization((IRPClassifier) mo);
										myGen = intfblck.findGeneralization(mo.getName());
										myGen.changeTo(GlobalVariables.REALIZATION_TAG);
									}
								}
								IRPCollection myGeneralization = intfblck.getGeneralizations();
								for (Object generalizationelement : myGeneralization.toList()) {
									IRPModelElement generalizationmodelelement = (IRPModelElement) generalizationelement;
									IRPGeneralization general = (IRPGeneralization) generalizationmodelelement;
									general.changeTo(GlobalVariables.REALIZATION_TAG);
								}
							} else {
								logger.info("Delegate interface cannot be resolved (REALIZATION)");
							}
							funcClass = (IRPClass) getPortContractforDelegate(pkg, intfblck);
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}
					}
				}
			}
			// Required Interfaces
			existingConnectors = checkforConnectors(swcomponent, subComponent.getName());

			portPresent = false;
			try {
				for (RequiredInterface ri : subComponent.getRequiredInterfaces()) {
					portPresent = false;
					existingConnectors = checkforConnectors(swcomponent, subComponent.getName());
					for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
						ProvidedElement conj_port = ri.getInterface();
						AbstractComponent compname = ri.getComponent();
						List<SubComponent> subcomps = compname.getSubcomponents();
						List<PluginTemplate> templates = compname.getTemplates();
						for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
							HashMap<String, String> connector = entry.getValue();
							String connector_entry = connector.get(compname.getName() + "__" + conj_port.getName());
							if (connector_entry != null) {
								portPresent = true;
								break;
							} else {
								portPresent = false;
								break;
							}
						}
						if (!portPresent) {
							for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {

								HashMap<String, String> connector = entry.getValue();
								for (SubComponent subcompname : subcomps) {
									String connector_entry = connector
											.get(subcompname.getName() + "__" + conj_port.getName());
									if (connector_entry != null) {
										portPresent = true;
										break;
									} else {
										portPresent = false;
									}
								}
								for (PluginTemplate subcompname : templates) {
									Collection<String> value = connector.values();
									for (String name : value) {
										if (name.contains(subcompname.getName())) {
											portPresent = true;
											break;
										} else {
											portPresent = false;
										}
									}
								}

							}
							if (!portPresent) {
								if (rie instanceof RequiredConstant || rie instanceof RequiredFunction
										|| rie instanceof RequiredVariable || rie instanceof RequiredPort)
									processRequiredSubInterfaces(subcomponent, ri, rie);
							}
						}
					}

				}
			} catch (Exception e) {
				logger.info("Error while processing Sub Component Required Interfaces\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception while processing SubComponent : " + subComponent.getName());
		}
	}

	private void processTopLevelDelegations(AbstractComponent abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		// TODO Auto-generated method stub

		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}

		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.COMPONENT_METACLASS);
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						abscomp.getName(), provelem.getName());
			}
		}
	}

	/**
	 * 
	 * @param component
	 * @param cl
	 * @param pluginList
	 * @param typeMap
	 */

	@SuppressWarnings("unchecked")
	private void processComponent(Component component, Cluster cl, HashMap<String, EList<Plugin>> pluginMap,
			HashMap<String, EList<TypeCollection>> typeMap) {

		try {
			swcomponent = (IRPClass) checkifElementExists(subcluster, ((Component) component).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (swcomponent == null) {
				swcomponent = (IRPUnit) addElementtoModel(subcluster, GlobalVariables.COMPONENT_METACLASS,
						((Component) component).getName());
				String description = component.getDesc();
				String label = component.getLongName();
				swcomponent.setDescription(description);
				swcomponent.setDisplayName(label);
				fullPathName = swcomponent.getFullPathName();
				fullPathMap.putIfAbsent(swcomponent.getName(), fullPathName);
				swcomponent.setSeparateSaveUnit(1);

				if (typeMap.containsKey(swcomponent.getName())) {
					EList<TypeCollection> type = typeMap.get(swcomponent.getName());
					for (TypeCollection types : type) {
						EList<Type> abscomp = types.getTypes();
						abscomp.forEach(datatype -> {
							if (datatype instanceof NumericTypeImpl || datatype instanceof StructTypeImpl
									|| datatype instanceof PointerTypeImpl || datatype instanceof BaseTypeImpl) {
								IRPClassifier data = (IRPClassifier) checkifElementExists(swcomponent,
										datatype.getName(), GlobalVariables.DATA_TYPE_METACLASS);
								if (!datatype.getName().equalsIgnoreCase("void")) {
									if (data == null) {
										data = (IRPClassifier) addElementtoModel(swcomponent,
												GlobalVariables.DATA_TYPE_METACLASS, datatype.getName());
									}
								}
							}
						});

					}
				}
				String thirdParty = component.getThirdParty();
				if (thirdParty != null) {
					if (thirdparty_stereotype != null) {
						swcomponent.addSpecificStereotype(thirdparty_stereotype);
					}
				}
				if (component instanceof CompositeComponent) {

					if (composite_stereotype != null) {
						swcomponent.addSpecificStereotype(composite_stereotype);
					}

				}
//				if (pluginMap.containsKey(swcomponent.getName())) {
//					EList<Plugin> plugin = pluginMap.get(swcomponent.getName());
//					if (plugin != null) {
//						for (Plugin pluginname : plugin) {
//							System.out.println("Plugin:" + pluginname.getName());
//							plugin.forEach(pluginComponent -> {
//								processPlugin(pluginComponent);
//
//							});
//						}
//					}
//				}
				IRPTag rl = (IRPTag) checkifElementExists(swcomponent, GlobalVariables.ARCHITECT_TAG,
						GlobalVariables.TAG_METACLASS);
				if (rl == null) {
					rl = (IRPTag) addElementtoModel(swcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.ARCHITECT_TAG);
					String archi = component.getArchitect();
					if (archi != null) {
						swcomponent.setTagValue(rl, archi);
					}
				}
				IRPTag reqid = (IRPTag) checkifElementExists(swcomponent, GlobalVariables.DOCID_TAG,
						GlobalVariables.TAG_METACLASS);
				if (reqid == null) {
					reqid = (IRPTag) addElementtoModel(swcomponent, GlobalVariables.TAG_METACLASS,
							GlobalVariables.DOCID_TAG);
					ReqDocIdSpecification req = component.getReq();
					for (Integer id : req.getReqDocIds()) {
						swcomponent.setTagValue(reqid, id.toString());
					}
				}
				cluster_part = (IRPInstance) subcluster.findNestedElementRecursive(
						GlobalVariables.PART_KEYWORD + ((Component) component).getName(),
						GlobalVariables.PART_USER_METACLASS);
				if (cluster_part == null) {
					cluster_part = (IRPInstance) addElementtoModel(subcluster, GlobalVariables.PART_USER_METACLASS,
							GlobalVariables.PART_KEYWORD + ((Component) component).getName());
					cluster_part.setOtherClass((IRPClassifier) swcomponent);
				}

			} else {
//                String flavorname=flavor.getName();
//				  String flavor_name=flavorname.replace("/", "_");
//				  String genericlink= "http://frcvshare001.conti.de/artifacts/CV/" + flavor_name + "_develop/lastFullFlight/DetailedDesign/html/";
//                IRPHyperLink link=(IRPHyperLink) swcomponent.addNewAggr("HyperLink", swcomponent.getName()+".html");
//                String particularlink= partition.getName()+"/"+ cluster.getName()+"/"+ swcomponent.getName()+"/"+swcomponent.getName()+ ".html";
//                link.setURL(genericlink+particularlink);
				fullPathName = swcomponent.getFullPathName();
				fullPathMap.putIfAbsent(swcomponent.getName(), fullPathName);
				logger.info("Component : " + component.getName() + " already exists");
			}
			// Add Hyperlink to component
			IRPModelElement hyperlink = (IRPHyperLink) swcomponent.findNestedElement(swcomponent.getName() + ".html",
					"HyperLink");
			if (hyperlink == null) {
				IRPHyperLink link = (IRPHyperLink) swcomponent.addNewAggr("HyperLink", swcomponent.getName() + ".html");
				String genericlink = "https://github.geo.conti.de/VED-EBS/IntegrationStream2022/blob/develop/Src/EBS/";
				String particularlink = partition.getName() + "/" + cluster.getName() + "/" + cluster.getName()
						+ "_generic" + "/" + swcomponent.getName() + "/" + "DDesign" + "/" + swcomponent.getName()
						+ "_frame.adoc";
				link.setURL(genericlink + particularlink);
			}
			
			if (pluginMap.containsKey(swcomponent.getName())) {
				EList<Plugin> plugin = pluginMap.get(swcomponent.getName());
				if (plugin != null) {
					for (Plugin pluginname : plugin) {
						System.out.println("Plugin:" + pluginname.getName());
						plugin.forEach(pluginComponent -> {
							processPlugin(pluginComponent);

						});
					}
				}
			}
			// when condition
			if (component.getStrapControlStructure() != null) {
				CidlFileName = component.eResource().getURI().lastSegment();
				try {
					String whenValues = readCidlFileLineByLine_component(CidlFileName, swcomponent.getName());
					if (whenValues != "") {
						IRPModelElement constraint = (IRPConstraint) swcomponent.findNestedElement("pv_Restriction",
								"Constraint");
						if (constraint == null) {
							IRPConstraint pv_Restriction_Ele = (IRPConstraint) swcomponent.addNewAggr("Constraint",
									"pv_Restriction");
							if (pvRestriction != null) {
								pv_Restriction_Ele.setStereotype(pvRestriction);
								pv_Restriction_Ele.setSpecification(whenValues);
							}
						}
						System.out.println("when condition :" + whenValues);
					}

				} catch (Exception e) {
					logger.info("File not found in cIDL " + component.getName() + ".cidl");
					e.printStackTrace();
				}
			}
			// Provided Interfaces
			existingelements = checkElementsinModel(subcluster, component.getName());
			try {
				HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();
				HashMap<String, String> providedelements = new HashMap<String, String>();
				elementPresent = false;
				for (AccessibleInterface ai : component.getProvidedInterfaces()) {

					for (List<String> value : existingelements.values()) {
						if (value.contains(ai.getName())) {
							elementPresent = true;
							break;
						}
					}
					ProcessProvidedElements.processProvidedInterfaceBlockAndPort(pkg, ai, component, swcomponent,
							elementPresent);
					if (ai instanceof ProvidedInterface) {
						listofinterfaceblocks.add(ai.getName() + ":" + component.getName());
						ProvidedInterface pi = (ProvidedInterface) ai;
						for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
							providedelements.put(pie.getName(), pie.getName());
							if (!elementPresent) {
								ProcessProvidedElements.processInterface(prj, pkg, swcomponent, component, pi, pie,
										true);
							}
//							ProcessProvidedElements.processInterface(prj, pkg, swcomponent, component, pi, pie,
//									true);
						}
					}
					interfaces.putIfAbsent(ai.getName(), providedelements);
				}

				providedInterfaces.put(component, interfaces);

			} catch (Exception e) {
				// TODO: handle exception

				logger.info("Exception while processing Provided Elements of Component" + component.getName());
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception while processing Component : " + component.getName());
		}
	}

	@SuppressWarnings("unchecked")
	private void processPlugin(Plugin pluginComponent) {
		try {
			plugin = (IRPClass) checkifElementExists(swcomponent, ((Plugin) pluginComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (plugin == null) {
				plugin = (IRPUnit) addElementtoModel(swcomponent, GlobalVariables.PLUGIN_METACLASS,
						((Plugin) pluginComponent).getName());
				String description = pluginComponent.getDesc();
				plugin.setDescription(description);
			} else {
				logger.info("Plugin" + pluginComponent.getName() + " already exists");
			}
			cluster_part = (IRPInstance) checkifElementExists(swcomponent,
					GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName(),
					GlobalVariables.PART_USER_METACLASS);
			if (cluster_part == null) {
				cluster_part = (IRPInstance) addElementtoModel(swcomponent, GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName());
				cluster_part.setOtherClass((IRPClassifier) plugin);
			}
			// check if exist in model
			existingelements = checkElementsinModel(swcomponent, pluginComponent.getName());
			
			// when condition
			if (pluginComponent.getStrapControlStructure() != null) {
				pluginFileName = pluginComponent.eResource().getURI().lastSegment();
				try {
					String whenValues = readCidlFileLineByLine_plugin(pluginFileName, plugin.getName());
					if (whenValues != "") {
						IRPModelElement constraint = (IRPConstraint) plugin.findNestedElement("pv_Restriction",
								"Constraint");
						if (constraint == null) {
							IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin.addNewAggr("Constraint",
									"pv_Restriction");
							if (pvRestriction != null) {
								pv_Restriction_Ele.setStereotype(pvRestriction);
								pv_Restriction_Ele.setSpecification(whenValues);
							}
						}
						System.out.println("when condition :" + whenValues);
					}

				} catch (Exception e) {
					logger.info("Exception while processing when condition for plugin " + pluginFileName);
				}
			}
		
			HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

			HashMap<String, String> providedelements = new HashMap<String, String>();
			elementPresent = false;

			for (AccessibleInterface ai : pluginComponent.getProvidedInterfaces()) {
				elementPresent = false;
				for (List<String> value : existingelements.values()) {
					if (value.contains(ai.getName())) {
						elementPresent = true;
						break;
					}
				}

				ProcessProvidedElements.processPluginBlockAndPort(pkg, ai, plugin, swcomponent, pluginComponent,
						elementPresent);
				if (ai instanceof ProvidedInterface) {
					listofinterfaceblocks.add(ai.getName() + ":" + pluginComponent.getName());
					ProvidedInterface pi = (ProvidedInterface) ai;
					for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
						providedelements.put(pie.getName(), pie.getName());
						if (!elementPresent) {
							ProcessProvidedElements.processInterface(prj, pkg, pluginComponent, plugin, swcomponent, pi,
									pie, true);

						}
					}
				}

				interfaces.putIfAbsent(ai.getName(), providedelements);
			}

			providedpluginInterfaces.put(pluginComponent, interfaces);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in processing Plugin" + pluginComponent.getName());
		}
	}
	

	private void processSubCompPlugin(Plugin pluginComponent) {
		try {
			plugin = (IRPClass) checkifElementExists(subcomponent, ((Plugin) pluginComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (plugin == null) {
				plugin = (IRPUnit) addElementtoModel(subcomponent, GlobalVariables.PLUGIN_METACLASS,
						((Plugin) pluginComponent).getName());
				String description = pluginComponent.getDesc();
				plugin.setDescription(description);
			} else {
				logger.info("Plugin" + pluginComponent.getName() + " already exists");
			}
			cluster_part = (IRPInstance) checkifElementExists(subcomponent,
					GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName(),
					GlobalVariables.PART_USER_METACLASS);
			if (cluster_part == null) {
				cluster_part = (IRPInstance) addElementtoModel(subcomponent, GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName());
				cluster_part.setOtherClass((IRPClassifier) plugin);
			}
			// check if exist in model
			existingelements = checkElementsinModel(subcomponent, pluginComponent.getName());
			
			// when condition
			if (pluginComponent.getStrapControlStructure() != null) {
				pluginFileName = pluginComponent.eResource().getURI().lastSegment();
				try {
					String whenValues = readCidlFileLineByLine_plugin(pluginFileName, plugin.getName());
					if (whenValues != "") {
						IRPModelElement constraint = (IRPConstraint) plugin.findNestedElement("pv_Restriction",
								"Constraint");
						if (constraint == null) {
							IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin.addNewAggr("Constraint",
									"pv_Restriction");
							if (pvRestriction != null) {
								pv_Restriction_Ele.setStereotype(pvRestriction);
								pv_Restriction_Ele.setSpecification(whenValues);
							}
						}
						System.out.println("when condition :" + whenValues);
					}

				} catch (Exception e) {
					logger.info("Exception while processing when condition for plugin " + pluginFileName);
				}
			}
		
			HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

			HashMap<String, String> providedelements = new HashMap<String, String>();
			elementPresent = false;

			for (AccessibleInterface ai : pluginComponent.getProvidedInterfaces()) {
				elementPresent = false;
				for (List<String> value : existingelements.values()) {
					if (value.contains(ai.getName())) {
						elementPresent = true;
						break;
					}
				}

				ProcessProvidedElements.processPluginBlockAndPort(pkg, ai, plugin, subcomponent, pluginComponent,
						elementPresent);
				if (ai instanceof ProvidedInterface) {
					listofinterfaceblocks.add(ai.getName() + ":" + pluginComponent.getName());
					ProvidedInterface pi = (ProvidedInterface) ai;
					for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
						providedelements.put(pie.getName(), pie.getName());
						if (!elementPresent) {
							ProcessProvidedElements.processInterface(prj, pkg, pluginComponent, plugin, subcomponent, pi,
									pie, true);

						}
					}
				}

				interfaces.putIfAbsent(ai.getName(), providedelements);
			}

			providedpluginInterfaces.put(pluginComponent, interfaces);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in processing Plugin" + pluginComponent.getName());
		}
		
	}

	private void processSubSubCompPlugin(Plugin pluginComponent) {
		try {
			plugin = (IRPClass) checkifElementExists(subsubcomponent, ((Plugin) pluginComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (plugin == null) {
				plugin = (IRPUnit) addElementtoModel(subsubcomponent, GlobalVariables.PLUGIN_METACLASS,
						((Plugin) pluginComponent).getName());
				String description = pluginComponent.getDesc();
				plugin.setDescription(description);
			} else {
				logger.info("Plugin" + pluginComponent.getName() + " already exists");
			}
			cluster_part = (IRPInstance) checkifElementExists(subsubcomponent,
					GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName(),
					GlobalVariables.PART_USER_METACLASS);
			if (cluster_part == null) {
				cluster_part = (IRPInstance) addElementtoModel(subsubcomponent, GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName());
				cluster_part.setOtherClass((IRPClassifier) plugin);
			}
			// check if exist in model
			existingelements = checkElementsinModel(subsubcomponent, pluginComponent.getName());
			
			// when condition
			if (pluginComponent.getStrapControlStructure() != null) {
				pluginFileName = pluginComponent.eResource().getURI().lastSegment();
				try {
					String whenValues = readCidlFileLineByLine_plugin(pluginFileName, plugin.getName());
					if (whenValues != "") {
						IRPModelElement constraint = (IRPConstraint) plugin.findNestedElement("pv_Restriction",
								"Constraint");
						if (constraint == null) {
							IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin.addNewAggr("Constraint",
									"pv_Restriction");
							if (pvRestriction != null) {
								pv_Restriction_Ele.setStereotype(pvRestriction);
								pv_Restriction_Ele.setSpecification(whenValues);
							}
						}
						System.out.println("when condition :" + whenValues);
					}

				} catch (Exception e) {
					logger.info("Exception while processing when condition for plugin " + pluginFileName);
				}
			}
		
			HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

			HashMap<String, String> providedelements = new HashMap<String, String>();
			elementPresent = false;

			for (AccessibleInterface ai : pluginComponent.getProvidedInterfaces()) {
				elementPresent = false;
				for (List<String> value : existingelements.values()) {
					if (value.contains(ai.getName())) {
						elementPresent = true;
						break;
					}
				}

				ProcessProvidedElements.processPluginBlockAndPort(pkg, ai, plugin, subsubcomponent, pluginComponent,
						elementPresent);
				if (ai instanceof ProvidedInterface) {
					listofinterfaceblocks.add(ai.getName() + ":" + pluginComponent.getName());
					ProvidedInterface pi = (ProvidedInterface) ai;
					for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
						providedelements.put(pie.getName(), pie.getName());
						if (!elementPresent) {
							ProcessProvidedElements.processInterface(prj, pkg, pluginComponent, plugin, subsubcomponent, pi,
									pie, true);

						}
					}
				}

				interfaces.putIfAbsent(ai.getName(), providedelements);
			}

			providedpluginInterfaces.put(pluginComponent, interfaces);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in processing Plugin" + pluginComponent.getName());
		}
		
	}

	private void processNestedSubCompPlugin(Plugin pluginComponent) {
		try {
			plugin = (IRPClass) checkifElementExists(nestedsubcomponent, ((Plugin) pluginComponent).getName(),
					GlobalVariables.CLASS_METACLASS);
			if (plugin == null) {
				plugin = (IRPUnit) addElementtoModel(nestedsubcomponent, GlobalVariables.PLUGIN_METACLASS,
						((Plugin) pluginComponent).getName());
				String description = pluginComponent.getDesc();
				plugin.setDescription(description);
			} else {
				logger.info("Plugin" + pluginComponent.getName() + " already exists");
			}
			cluster_part = (IRPInstance) checkifElementExists(nestedsubcomponent,
					GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName(),
					GlobalVariables.PART_USER_METACLASS);
			if (cluster_part == null) {
				cluster_part = (IRPInstance) addElementtoModel(nestedsubcomponent, GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Plugin) pluginComponent).getName());
				cluster_part.setOtherClass((IRPClassifier) plugin);
			}
			// check if exist in model
			existingelements = checkElementsinModel(nestedsubcomponent, pluginComponent.getName());
			
			// when condition
			if (pluginComponent.getStrapControlStructure() != null) {
				pluginFileName = pluginComponent.eResource().getURI().lastSegment();
				try {
					String whenValues = readCidlFileLineByLine_plugin(pluginFileName, plugin.getName());
					if (whenValues != "") {
						IRPModelElement constraint = (IRPConstraint) plugin.findNestedElement("pv_Restriction",
								"Constraint");
						if (constraint == null) {
							IRPConstraint pv_Restriction_Ele = (IRPConstraint) plugin.addNewAggr("Constraint",
									"pv_Restriction");
							if (pvRestriction != null) {
								pv_Restriction_Ele.setStereotype(pvRestriction);
								pv_Restriction_Ele.setSpecification(whenValues);
							}
						}
						System.out.println("when condition :" + whenValues);
					}

				} catch (Exception e) {
					logger.info("Exception while processing when condition for plugin " + pluginFileName);
				}
			}
		
			HashMap<String, HashMap<String, String>> interfaces = new HashMap<String, HashMap<String, String>>();

			HashMap<String, String> providedelements = new HashMap<String, String>();
			elementPresent = false;

			for (AccessibleInterface ai : pluginComponent.getProvidedInterfaces()) {
				elementPresent = false;
				for (List<String> value : existingelements.values()) {
					if (value.contains(ai.getName())) {
						elementPresent = true;
						break;
					}
				}

				ProcessProvidedElements.processPluginBlockAndPort(pkg, ai, plugin, nestedsubcomponent, pluginComponent,
						elementPresent);
				if (ai instanceof ProvidedInterface) {
					listofinterfaceblocks.add(ai.getName() + ":" + pluginComponent.getName());
					ProvidedInterface pi = (ProvidedInterface) ai;
					for (ProvidedInterfaceElement pie : pi.getProvidedEntities()) {
						providedelements.put(pie.getName(), pie.getName());
						if (!elementPresent) {
							ProcessProvidedElements.processInterface(prj, pkg, pluginComponent, plugin, nestedsubcomponent, pi,
									pie, true);

						}
					}
				}

				interfaces.putIfAbsent(ai.getName(), providedelements);
			}

			providedpluginInterfaces.put(pluginComponent, interfaces);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in processing Plugin" + pluginComponent.getName());
		}
		
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private void processComponentFlowsAndTypes(Component component, Cluster cl,
			HashMap<String, EList<Plugin>> pluginMap) {

		try {
			swcomponent = (IRPClass) subcluster.findNestedElementRecursive(((Component) component).getName(),
					GlobalVariables.COMPONENT_METACLASS);
			if (swcomponent != null) {
				if (pluginMap.containsKey(swcomponent.getName())) {
					EList<Plugin> pluginlist = pluginMap.get(swcomponent.getName());
					if (pluginlist != null) {
						for (Plugin pluginname : pluginlist) {
							plugin = (IRPClass) checkifElementExists(swcomponent, ((Plugin) pluginname).getName(),
									GlobalVariables.PLUGIN_METACLASS);
							if (plugin == null) {
								processPlugin(pluginname);
							}

							// Required Plugin Interfaces
							for (RequiredInterface ri : pluginname.getRequiredInterfaces()) {
								portPresent = false;
								existingConnectors = checkforConnectors(swcomponent, plugin.getName());
								for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
									ProvidedElement conj_port = ri.getInterface();
									AbstractComponent compname = ri.getComponent();
									List<SubComponent> subcomps = compname.getSubcomponents();
									for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
										HashMap<String, String> connector = entry.getValue();
										String connector_entry = connector
												.get(compname.getName() + "__" + conj_port.getName());
										if (connector_entry != null) {
											portPresent = true;
											break;
										} else {
											portPresent = false;
											break;
										}
									}
									if (!portPresent) {
										for (Entry<String, HashMap<String, String>> entry : existingConnectors
												.entrySet()) {
											HashMap<String, String> connector = entry.getValue();
											for (SubComponent subcompname : subcomps) {
												String connector_entry = connector
														.get(subcompname.getName() + "__" + conj_port.getName());
												if (connector_entry != null) {
													portPresent = true;
													break;
												} else {
													portPresent = false;
												}
											}
											if (true) {
												break;
											}
										}

										if (!portPresent) {
											processRequiredInterfaces(plugin, ri, rie);
										}
									}

								}
							}
						}
					}
				}
			}
			// Delegate interfaces
			for (AccessibleInterface ai : component.getProvidedInterfaces()) {
				try {
					if (ai instanceof DelegateInterface) {
						match = false;
						intfblck = (IRPClass) swcomponent.findNestedElement("d_" + ai.getName(),
								GlobalVariables.DELEGATEINTERFACE_METACLASS);
						if (intfblck == null) {
							ProcessProvidedElements.processProvidedInterfaceBlockAndPort(pkg, ai, component,
									swcomponent, elementPresent);
							intfblck = (IRPClass) swcomponent.findNestedElement("d_" + ai.getName(),
									GlobalVariables.DELEGATEINTERFACE_METACLASS);
						}
						IRPCollection myGeneralizations = intfblck.getGeneralizations();
						for (DelegateEntity rie : ((DelegateInterface) ai).getDelegateEntities()) {
							AbstractComponent dcomp = rie.getDelegateComponent();
							ProvidedInterface pintf = rie.getDelegateInterface();
							for (Object generalizationelement : myGeneralizations.toList()) {
								IRPGeneralization generalizationmodelelement = (IRPGeneralization) generalizationelement;
								if (generalizationmodelelement.getBaseClass().getName()
										.equalsIgnoreCase(dcomp.getName())) {
									match = true;
									break;
								}
							}
							if (!match) {
								IRPModelElement baseClass = findSourceandDestination(dcomp.getName());
								IRPPort reqport = (IRPPort) swcomponent.findNestedElement(ai.getName(),
										GlobalVariables.PROXY_PORT_METACLASS);
								IRPLink reqconnector = (IRPLink) swcomponent.findNestedElement(
										dcomp.getName() + "__" + ai.getName(), GlobalVariables.LINK_TAG);
								if (reqport == null) {
									reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
											ai.getName());
								}
								reqport.setIsReversed(1);
								if (reqconnector == null) {
									IRPModelElement fpartowner = findSourceandDestination(dcomp.getName());
									if (fpartowner == null) {
										fpartowner = pkg.findNestedElementRecursive(dcomp.getName(),
												GlobalVariables.CLASS_METACLASS);
									}

									IRPInstance fpart = findPart(fpartowner);
									IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(pintf.getName(),
											GlobalVariables.PROXY_PORT_METACLASS);
									IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
									IRPInstance tpart = findPart(tpartowner);
									IRPPort toport = (IRPPort) swcomponent.findNestedElementRecursive(ai.getName(),
											GlobalVariables.PROXY_PORT_METACLASS);
									if (reqconnector == null) {
										reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport,
												dcomp.getName(), ai.getName());
									}
								}

								if (baseClass != null) {
									IRPModelElement mo = baseClass.findNestedElementRecursive(pintf.getName(),
											GlobalVariables.INTERFACE_BLOCK_METACLASS);

									if (mo != null && intfblck != null) {
										IRPGeneralization myGen = intfblck.findGeneralization(mo.getName());
										if (myGen != null) {
											IRPClassifier baseclass = myGen.getBaseClass();
											if (!baseclass.equals(dcomp)) {
												intfblck.addGeneralization((IRPClassifier) mo);
												IRPGeneralization myNewGen = intfblck.findGeneralization(mo.getName());
												if (!baseclass.getName().equals(baseClass.getName())) {
													myNewGen.changeTo(GlobalVariables.REALIZATION_TAG);
												}
											}
										} else {
											intfblck.addGeneralization((IRPClassifier) mo);
											myGen = intfblck.findGeneralization(mo.getName());
											myGen.changeTo(GlobalVariables.REALIZATION_TAG);
										}

										IRPCollection myGeneralization = intfblck.getGeneralizations();
										for (Object generalizationelement : myGeneralization.toList()) {
											IRPModelElement generalizationmodelelement = (IRPModelElement) generalizationelement;
											IRPGeneralization general = (IRPGeneralization) generalizationmodelelement;
											general.changeTo(GlobalVariables.REALIZATION_TAG);
										}
									}
								} else {
									logger.info("Delegate interface cannot be resolved (REALIZATION)");
								}
								funcClass = (IRPClass) getPortContractforDelegate(pkg, intfblck);
								if (funcClass != null) {
									reqport.setContract(funcClass);
								}
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception

					logger.info("Error while processing Delegate Interface Elements\n");
				}
			}
			// Required Interfaces

			existingConnectors = checkforConnectors(subcluster, component.getName());

			portPresent = false;
			try {
				for (RequiredInterface ri : component.getRequiredInterfaces()) {
					portPresent = false;
					existingConnectors = checkforConnectors(subcluster, component.getName());
					for (RequiredInterfaceEntity rie : ri.getRequiredEntities()) {
						ProvidedElement conj_port = ri.getInterface();
						AbstractComponent compname = ri.getComponent();

						List<SubComponent> subcomps = compname.getSubcomponents();
						List<PluginTemplate> templates = compname.getTemplates();
						for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
							HashMap<String, String> connector = entry.getValue();
							String connector_entry = connector.get(compname.getName() + "__" + conj_port.getName());
							if (connector_entry != null) {
								portPresent = true;
								break;
							} else {
								portPresent = false;
								break;
							}
						}
						if (!portPresent) {
							for (Entry<String, HashMap<String, String>> entry : existingConnectors.entrySet()) {
								HashMap<String, String> connector = entry.getValue();
								for (SubComponent subcompname : subcomps) {
									String connector_entry = connector
											.get(subcompname.getName() + "__" + conj_port.getName());
									if (connector_entry != null) {
										portPresent = true;
										break;
									} else {
										portPresent = false;
									}
								}
								for (PluginTemplate subcompname : templates) {
									Collection<String> value = connector.values();
									for (String name : value) {
										if (name.contains(subcompname.getName())) {
											portPresent = true;
											break;
										} else {
											portPresent = false;
										}
									}
								}
							}

							if (!portPresent) {
								if (rie instanceof RequiredFunction || rie instanceof RequiredConstant
										|| rie instanceof RequiredPort || rie instanceof RequiredVariable)
									processRequiredInterfaces(swcomponent, ri, rie);
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Error while processing Required Interface Elements\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info("Exception while processing Component : " + component.getName());
		}

	}

	private HashMap<String, HashMap<String, String>> checkforConnectors(IRPModelElement pkg, String component) {

		// TODO Auto-generated method stub
		try {
			existingConnectors.clear();
			IRPModelElement component_in_pkg = pkg.findNestedElementRecursive(component,
					GlobalVariables.CLASS_METACLASS);
			IRPModelElement element = null;
			HashMap<String, String> connectorsinModel = new HashMap<String, String>();
			IRPCollection components = component_in_pkg.getNestedElementsByMetaClass("Link", 0);
			for (Object obj : components.toList()) {
				element = (IRPModelElement) obj;
				if (element.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.CONNECTOR_METACLASS)) {
					connectorsinModel.put(element.getName(), element.getName());
				}
			}
			existingConnectors.put(component, connectorsinModel);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return existingConnectors;
	}

	private void processRequiredInterfaces(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub
		AbstractComponent abscomp = ri.getComponent();
		ProvidedElement provelem = ri.getInterface();

		if (rie instanceof RequiredConstant) {
			processRequiredConstant(swcomponent, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processRequiredPort(swcomponent, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processRequiredFunction(swcomponent, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processRequiredVariable(swcomponent, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element ");
		}
		processPlugin(abscomp, provelem);
	}

	private void processRequiredSubSubInterfaces(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		// TODO Auto-generated method stub
		if (rie instanceof RequiredConstant) {
			processSubSubRequiredConstant(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processSubSubRequiredPort(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processSubSubRequiredFunction(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processSubSubRequiredVariable(swcomponent, swswcomp, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element ");
		}
		// processPlugin(abscomp, provelem);
	}

	private void processSubSubRequiredConstant(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			providedComponent = null;
			isPresent = false;
			RequiredConstant rc = (RequiredConstant) rie;
			String requiredconstant = rc.getConstant().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredconstant)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredconstant)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredconstant)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredconstant)) {
										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getConstant().getName(), swswcomp);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getConstant().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getConstant().getName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processSubSubRequiredFunction(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			providedComponent = null;
			isPresent = false;
			RequiredFunction rc = (RequiredFunction) rie;
			String reqfunc = rc.getFunction().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqfunc)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(reqfunc)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqfunc)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqfunc)) {
										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getFunction().getName(), swswcomp);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getFunction().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getFunction().getName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processSubSubRequiredVariable(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredVariable rc = (RequiredVariable) rie;
			String reqvar = rc.getVariable().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqvar)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(reqvar)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqvar)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqvar)) {
										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getVariable().getName(), swswcomp);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getVariable().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getVariable().getName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processSubSubRequiredPort(IRPUnit swcomponent, IRPModelElement swswcomp, RequiredInterface ri,
			Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredPort rc = (RequiredPort) rie;
			String reqport = rc.getPort().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqport)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(reqport)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqport)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqport)) {
										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getPort().getName(), swswcomp);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getPort().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getPort().getName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processRequiredSubInterfaces(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub
		if (rie instanceof RequiredConstant) {
			processSubRequiredConstant(swcomponent, ri, rie);
		} else if (rie instanceof RequiredPort) {
			processSubRequiredPort(swcomponent, ri, rie);
		} else if (rie instanceof RequiredFunction) {
			processSubRequiredFunction(swcomponent, ri, rie);
		} else if (rie instanceof RequiredVariable) {
			processSubRequiredVariable(swcomponent, ri, rie);
		} else if (rie instanceof CodegComponent) {
			logger.info("A codeg Element ");
		}
		// processPlugin(abscomp, provelem);
	}

	private void processSubRequiredVariable(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub
		try {
			flag = false;
			match = false;
			isPresent = false;
			providedComponent = null;
			RequiredVariable rc = (RequiredVariable) rie;
			String reqvar = rc.getVariable().getName();
			AbstractComponent dcomp = null;
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(reqvar)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(reqvar)) {
									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
									}
								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}

				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqvar)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqvar)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}

				if (providedComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {

						createportsforSubComponents(provelem, dcomp, providedComponent, maincomp,
								rc.getVariable().getName());
					}
				}

			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getVariable().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getVariable().getName());
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}
	}

	private void processSubComponentDelegations(AbstractComponent providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		// TODO Auto-generated method stub
		reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		IRPInstance mainpart = null;
		IRPPort mainport = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		if (providedComponent instanceof SubComponent) {

			String main_comp = null;
			for (Entry<String, List<String>> entry : subcomponenthashmap.entrySet()) {
				List<String> subcomponentname = entry.getValue();
				if (subcomponentname.contains(providedComponent.getName())) {
					main_comp = entry.getKey();
					break;
				}
			}
			if (!maincomp.getName().equalsIgnoreCase(main_comp)) {
				if (main_comp.equalsIgnoreCase(swcomponent.getName())) {
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						delegateconnector = addConnector(delegateelement, tpart, delegatepart, toport, delegateport,
								providedComponent.getName(), provelem.getName());
					}
				} else {
					IRPModelElement mainelement = findSourceandDestination(main_comp);
					mainpart = findPart(mainelement);
					mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					if (mainport == null) {
						mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								provelem.getName());
						funcClass = getRequiredPortContract(pkg, provelem.getName());
						if (funcClass != null) {
							mainport.setContract(funcClass);
						}

					}
					IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (mainConnector == null) {
						mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport,
								providedComponent.getName(), provelem.getName());
					}
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport,
								delegateport, providedComponent.getName(), provelem.getName());
					}
				}
			} else {
				IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
				mainpart = findPart(mainelement);
				mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (mainport == null) {
					mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						mainport.setContract(funcClass);
					}

				}
				IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
				if (mainConnector == null) {
					mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport,
							providedComponent.getName(), provelem.getName());
				}
			}
		}

		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
			}
		}

	}

	private void processSubRequiredFunction(IRPUnit subcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub
		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredFunction rc = (RequiredFunction) rie;
			String requiredfunc = rc.getFunction().getName();
			AbstractComponent maincomp = ri.getComponent();
			AbstractComponent dcomp = null;
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) subcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredfunc)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) subcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
											}
										}
										if (flag || isPresent) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}
						if (flag || isPresent) {
							break;
						}
					}

				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredfunc)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) subcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;

									}
								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredfunc)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;

										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredfunc)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;

										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (flag || isPresent) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getFunction().getName());
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getFunction().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getFunction().getName());
				}
			}
		} catch (

		Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}
	}

	private void processSubRequiredPort(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub
		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredPort rc = (RequiredPort) rie;
			String requiredport = rc.getPort().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredport)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredport)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredport)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredport)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getPort().getName());
						}
					}

				}

				if (pluginComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getPort().getName());
					}
				}
				if (pluginTemplateComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
								rc.getPort().getName());
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processSubRequiredConstant(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredConstant rc = (RequiredConstant) rie;
			String requiredconstant = rc.getConstant().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredconstant)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredconstant)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredconstant)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredconstant)) {
										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) subcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforSubComponents(provelem, dcomp, providedComponent, maincomp,
									rc.getConstant().getName());
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubPlugins(provelem, pientity, pluginComponent, maincomp, rc.getConstant().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforSubTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getConstant().getName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void createportsforSubTemplates(ProvidedElement provelem, String pientity, PluginTemplate providedComponent,
			AbstractComponent maincomp, String requiredelement) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubComponentDelegations(providedComponent, maincomp, provelem, subcomponent);
				} else {
					processTopLevelDelegations(providedComponent, maincomp, provelem, subcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}
		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
				reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provelem.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElementRecursive(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null) {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}

					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelDelegations(PluginTemplate abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(abscomp.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						abscomp.getName(), provelem.getName());
			}
		}

	}

	private void processSubComponentDelegations(PluginTemplate providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		// TODO Auto-generated method stub
		reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		IRPInstance mainpart = null;
		IRPPort mainport = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {

			String main_comp = null;
			for (Entry<String, List<String>> entry : plugintemplateshashmap.entrySet()) {
				List<String> subcomponentname = entry.getValue();
				if (subcomponentname.contains(providedComponent.getName())) {
					main_comp = entry.getKey();
					break;
				}
			}
			if (!maincomp.getName().equalsIgnoreCase(main_comp)) {
				if (main_comp.equalsIgnoreCase(swcomponent.getName())) {
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						delegateconnector = addConnector(delegateelement, tpart, delegatepart, toport, delegateport,
								providedComponent.getName(), provelem.getName());
					}
				} else {
					IRPModelElement mainelement = findSourceandDestination(main_comp);
					mainpart = findPart(mainelement);
					mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					if (mainport == null) {
						mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								provelem.getName());
						funcClass = getRequiredPortContract(pkg, provelem.getName());
						if (funcClass != null) {
							mainport.setContract(funcClass);
						}

					}
					IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (mainConnector == null) {
						mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport,
								providedComponent.getName(), provelem.getName());
					}
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport,
								delegateport, providedComponent.getName(), provelem.getName());
					}
				}
			} else {
				IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
				mainpart = findPart(mainelement);
				mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (mainport == null) {
					mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						mainport.setContract(funcClass);
					}

				}
				IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
				if (mainConnector == null) {
					mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport,
							providedComponent.getName(), provelem.getName());
				}
			}
		}

		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
			}
		}

	}

	private void createportsforSubPlugins(ProvidedElement provelem, String pie, Plugin providedComponent,
			AbstractComponent maincomp, String requiredelement) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			ProvidedInterface pi = (ProvidedInterface) provelem;
			reqport = (IRPPort) subcomponent.findNestedElement(pi.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + pi.getName(),
					GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubComponentDelegations(providedComponent, maincomp, provelem, subcomponent);
				} else {
					processTopLevelDelegations(providedComponent, maincomp, provelem, subcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
				reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provelem.getName())
						&& dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null && fpart != null && fromport != null) {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}

					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelDelegations(Plugin abscomp, AbstractComponent maincomp, ProvidedElement provelem,
			IRPUnit subcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(abscomp.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						abscomp.getName(), provelem.getName());
			}
		}

	}

	private void processSubComponentDelegations(Plugin providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null && fromport != null && fpart != null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
				providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(mainelement, tpart, mainpart, toport, mainport, providedComponent.getName(),
					provelem.getName());
		}
		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
			}
		}
	}

	private void createportsforSubSubComponents(ProvidedElement provelem, AbstractComponent dcomp,
			AbstractComponent providedComponent, AbstractComponent maincomp, String requiredelement,
			IRPModelElement swswcomp) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);

				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubSubComponentDelegations(providedComponent, maincomp, provelem, subcomponent, swswcomp);
				} else {
					processSubTopLevelDelegations(providedComponent, maincomp, provelem, subcomponent, swswcomp);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				dcomp = die.getDelegateComponent();
				reqport = (IRPPort) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									providedComponent.getName() + "__" + provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}
						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null) {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}

					} else {
						logger.info("Connector already exists");
					}
				} else if (provelem instanceof DelegateInterface) {
					if (!match) {
						reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(),
								GlobalVariables.CONNECTOR_METACLASS);

						if (reqconnector == null) {
							if (reqport == null) {
								reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
										provelem.getName());
								funcClass = getRequiredPortContract(pkg, provelem.getName());
								if (funcClass != null) {
									reqport.setContract(funcClass);
								}
							}
							reqport.setIsReversed(1);
							processDelegationConnectors(providedComponent, provelem, dcomp, subcomponent);
						} else {
							logger.info("Connnector exists already");
						}

					}

				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

		logger.info("Required Element :" + requiredelement);

	}

	private void processSubTopLevelDelegations(AbstractComponent abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent, IRPModelElement swswcomp) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		IRPInstance mainpart = null;
		IRPPort mainport = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(subcomponent.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement subowner = findSourceandDestination(swswcomp.getName());
		IRPInstance spart = findPart(subowner);
		IRPPort sport = (IRPPort) subowner.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
		if (sport == null) {
			sport = (IRPPort) swswcomp.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				sport.setContract(funcClass);
			}

		}
		sport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, spart, fromport, sport, providedComponent.getName(),
					provelem.getName());
		}

		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		@SuppressWarnings("unused")
		IRPLink subreqconnector = addConnector(swswcomp, spart, tpart, sport, toport, abscomp.getName(),
				provelem.getName());

		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		mainpart = findPart(mainelement);
		mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPUnit delegateelement = (IRPUnit) pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.COMPONENT_METACLASS);
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						abscomp.getName(), provelem.getName());
			}
		}
	}

	private void processSubSubComponentDelegations(AbstractComponent providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit subcomponent, IRPModelElement swswcomp) {
		// TODO Auto-generated method stub
		reqconnector = (IRPLink) subcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);

		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		IRPInstance mainpart = null;
		IRPPort mainport = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(subcomponent.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement subowner = findSourceandDestination(swswcomp.getName());
		IRPInstance spart = findPart(subowner);
		IRPPort sport = (IRPPort) subowner.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
		if (sport == null) {
			sport = (IRPPort) swswcomp.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				sport.setContract(funcClass);
			}

		}
		sport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, spart, fromport, sport, providedComponent.getName(),
					provelem.getName());
		}

		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);

		@SuppressWarnings("unused")
		IRPLink subreqconnector = addConnector(swswcomp, spart, tpart, sport, toport, providedComponent.getName(),
				provelem.getName());

		if (providedComponent instanceof SubComponent) {

			String main_comp = null;
			for (Entry<String, List<String>> entry : subcomponenthashmap.entrySet()) {
				List<String> subcomponentname = entry.getValue();
				if (subcomponentname.contains(providedComponent.getName())) {
					main_comp = entry.getKey();
					break;
				}
			}
			if (!maincomp.getName().equalsIgnoreCase(main_comp)) {
				if (main_comp.equalsIgnoreCase(swcomponent.getName())) {
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						delegateconnector = addConnector(delegateelement, tpart, delegatepart, toport, delegateport,
								providedComponent.getName(), provelem.getName());
					}
				} else {
					IRPModelElement mainelement = findSourceandDestination(main_comp);
					mainpart = findPart(mainelement);
					mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					if (mainport == null) {
						mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
								provelem.getName());
						funcClass = getRequiredPortContract(pkg, provelem.getName());
						if (funcClass != null) {
							mainport.setContract(funcClass);
						}

					}
					IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (mainConnector == null) {
						mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport,
								providedComponent.getName(), provelem.getName());
					}
					IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
					IRPInstance delegatepart = findPart(delegateelement);
					IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
							GlobalVariables.PROXY_PORT_METACLASS);
					IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
							providedComponent.getName() + "__" + provelem.getName(),
							GlobalVariables.CONNECTOR_METACLASS);
					if (delegateconnector == null) {
						delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport,
								delegateport, providedComponent.getName(), provelem.getName());
					}
				}
			} else {
				IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
				mainpart = findPart(mainelement);
				mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (mainport == null) {
					mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						mainport.setContract(funcClass);
					}

				}
				IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
				if (mainConnector == null) {
					mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport,
							providedComponent.getName(), provelem.getName());
				}
			}
		}

		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
			}
		}

	}

	private void createportsforSubComponents(ProvidedElement provelem, AbstractComponent dcomp,
			AbstractComponent providedComponent, AbstractComponent maincomp, String requiredelement) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) subcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);

				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processSubComponentDelegations(providedComponent, maincomp, provelem, subcomponent);
				} else {
					processTopLevelDelegations(providedComponent, maincomp, provelem, subcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				dcomp = die.getDelegateComponent();
				reqport = (IRPPort) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) subcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									providedComponent.getName() + "__" + provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}
						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(subcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null) {
							reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}

					} else {
						logger.info("Connector already exists");
					}
				} else if (provelem instanceof DelegateInterface) {
					if (!match) {
						reqport = (IRPPort) subcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						reqconnector = (IRPLink) subcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(),
								GlobalVariables.CONNECTOR_METACLASS);

						if (reqconnector == null) {
							if (reqport == null) {
								reqport = (IRPPort) subcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
										provelem.getName());
								funcClass = getRequiredPortContract(pkg, provelem.getName());
								if (funcClass != null) {
									reqport.setContract(funcClass);
								}
							}
							reqport.setIsReversed(1);
							processDelegationConnectors(providedComponent, provelem, dcomp, subcomponent);
						} else {
							logger.info("Connnector exists already");
						}

					}

				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

		logger.info("Required Element :" + requiredelement);

	}

	private void processRequiredVariable(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredVariable rc = (RequiredVariable) rie;
			String requiredvar = rc.getVariable().getName();
			AbstractComponent maincomp = ri.getComponent();
			AbstractComponent dcomp = null;
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredvar)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredvar)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredvar)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredvar)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(provelem, pientity, dcomp, providedComponent, maincomp,
									rc.getVariable().getName(), swcomponent);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforPlugins(provelem, pientity, pluginComponent, maincomp, rc.getVariable().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getVariable().getName());
				}
			}

		} catch (

		Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	@SuppressWarnings("null")
	private void processRequiredFunction(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub

		try {
			match = false;
			flag = false;
			isPresent = false;
			providedComponent = null;
			AbstractComponent dcomp = null;
			RequiredFunction rc = (RequiredFunction) rie;
			String reqfuncion = rc.getFunction().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;

			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (!flag) {
					if (provelem instanceof DelegateInterface) {
						DelegateInterface pi = (DelegateInterface) provelem;
						for (DelegateEntity die : pi.getDelegateEntities()) {
							dcomp = die.getDelegateComponent();
							reqconnector = (IRPLink) swcomponent.findNestedElement(
									dcomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
							if (reqconnector == null) {
								ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
								for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
										.entrySet()) {
									if (!flag && !isPresent) {
										HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
										if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
											for (Entry<String, HashMap<String, String>> intfname : interfacename
													.entrySet()) {
												HashMap<String, String> intfs = intfname.getValue();
												if (intfs.containsValue(reqfuncion)) {
													providedComponent = entry.getKey();
													provelem = di;
													reqconnector = (IRPLink) swcomponent.findNestedElement(
															dcomp.getName() + "__" + provelem.getName(),
															GlobalVariables.CONNECTOR_METACLASS);
													if (reqconnector == null) {
														flag = true;
													} else {
														isPresent = true;
														break;
													}
												}
												if (flag) {
													break;
												}

											}
										}
									}
									if (flag || isPresent) {
										break;
									}
								}

							} else {
								flag = true;
							}
						}
					}
					for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqfuncion)) {

										pientity = interfaceentry.getKey();
										providedComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												providedComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}

				}

				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqfuncion)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(reqfuncion)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag || isPresent) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(provelem, pientity, dcomp, providedComponent, maincomp,
									rc.getFunction().getName(), swcomponent);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforPlugins(provelem, pientity, pluginComponent, maincomp, rc.getFunction().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getFunction().getName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void createportsforTemplates(ProvidedElement provelem, String pie, PluginTemplate providedComponent,
			AbstractComponent maincomp, String requiredelement) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processTopLevelSubConnectors(providedComponent, maincomp, provelem, swcomponent);
				} else {
					processTopLevelConnectors(providedComponent, maincomp, provelem, swcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}
		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();

				reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provelem.getName())
						&& dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null && fpart != null && fromport != null) {
							reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}

					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelConnectors(PluginTemplate abscomp, AbstractComponent maincomp, ProvidedElement provelem,
			IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(maincomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPModelElement mainelement = findSourceandDestination(abscomp.getName());
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
			}
		}
		if (abscomp instanceof PluginTemplate) {
			IRPModelElement mainelement = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
			}
		}

	}

	private void processTopLevelSubConnectors(PluginTemplate subcomp, AbstractComponent abscomp,
			ProvidedElement provelem, IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(abscomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {

				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (fromport == null) {
			fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				fromport.setContract(funcClass);
			}
		}
		fromport.setIsReversed(1);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) tpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, subcomp.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = pkg.findNestedElementRecursive(subcomp.getName(),
				GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}
		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(subcomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, subcomp.getName(),
					provelem.getName());
		}

	}

	private void createportsforPlugins(ProvidedElement provelem, String pie, Plugin providedComponent,
			AbstractComponent maincomp, String requiredelement) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processTopLevelSubConnectors(providedComponent, maincomp, provelem, swcomponent);
				} else {
					processTopLevelConnectors(providedComponent, maincomp, provelem, swcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}

		} else if (provelem instanceof DelegateInterface) {
			for (DelegateEntity die : ((DelegateInterface) provelem).getDelegateEntities()) {
				AbstractComponent dcomp = die.getDelegateComponent();
				ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
				reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
						GlobalVariables.LINK_TAG);
				if (di.getName().equalsIgnoreCase(provelem.getName())
						&& dcomp.getName().equalsIgnoreCase(providedComponent.getName())) {

					if (reqconnector == null) {
						if (reqport == null) {
							reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = getRequiredPortContract(pkg, provelem.getName());
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						IRPModelElement fpartowner = null;
						IRPInstance fpart = null;
						fpartowner = findSourceandDestination(providedComponent.getName());
						if (fpartowner != null) {
							fpart = findPart(fpartowner);
						}
						if (fpart == null) {
							fpartowner = pkg.findNestedElementRecursive(provelem.getName(),
									GlobalVariables.CLASS_METACLASS);
							if (fpartowner != null) {
								fpart = findPart(fpartowner);
							}
						}
						IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
						IRPInstance tpart = findPart(tpartowner);
						IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						if (reqconnector == null && fpart != null && fromport != null) {
							reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, dcomp.getName(),
									provelem.getName() + "__" + provelem.getName());
						}

					} else {
						logger.info("Connector already exists");
					}
				}
			}
		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

	}

	private void processTopLevelConnectors(Plugin abscomp, AbstractComponent maincomp, ProvidedElement provelem,
			IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(maincomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPModelElement mainelement = findSourceandDestination(abscomp.getName());
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
			}
		}
		if (abscomp instanceof PluginTemplate) {
			IRPModelElement mainelement = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
			}
		}
	}

	private void processTopLevelSubConnectors(Plugin subcomp, AbstractComponent abscomp, ProvidedElement provelem,
			IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(abscomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {

				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (fromport == null) {
			fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				fromport.setContract(funcClass);
			}
		}
		fromport.setIsReversed(1);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) tpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, subcomp.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(subcomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}
		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(subcomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, subcomp.getName(),
					provelem.getName());
		}
	}

	private void createportsforComponents(ProvidedElement provelem, String pie, AbstractComponent dcomp,
			AbstractComponent providedComponent, AbstractComponent maincomp, String requiredelement,
			IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		if (provelem instanceof ProvidedInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);

			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				if (providedComponent instanceof SubComponent || providedComponent instanceof PluginTemplate) {
					processTopLevelSubConnectors(providedComponent, maincomp, provelem, swcomponent);
				} else {
					processTopLevelConnectors(providedComponent, maincomp, provelem, swcomponent);
				}
			} else {
				logger.info("Connector already exists");
			}
		} else if (provelem instanceof DelegateInterface) {
			reqport = (IRPPort) swcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			reqconnector = (IRPLink) swcomponent.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
					GlobalVariables.LINK_TAG);
			if (reqconnector == null) {
				if (reqport == null) {
					reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							providedComponent.getName() + "__" + provelem.getName());
					funcClass = getRequiredPortContract(pkg, provelem.getName());
					if (funcClass != null) {
						reqport.setContract(funcClass);
					}
				}
				reqport.setIsReversed(1);
				IRPModelElement fpartowner = null;
				IRPInstance fpart = null;
				fpartowner = findSourceandDestination(providedComponent.getName());
				if (fpartowner != null) {
					fpart = findPart(fpartowner);
				}
				IRPPort fromport = (IRPPort) fpartowner.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);

				if (fromport == null) {
					fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							providedComponent.getName() + "__" + provelem.getName());
				}
				IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
				IRPInstance tpart = findPart(tpartowner);
				IRPPort toport = (IRPPort) swcomponent.findNestedElement(
						providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.PROXY_PORT_METACLASS);
				if (reqconnector == null) {
					if (providedComponent instanceof SubComponent) {
						processDelegations(providedComponent, maincomp, provelem, swcomponent);
					} else {
						reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport,
								providedComponent.getName(), provelem.getName() + "__" + provelem.getName());
					}
				}

			} else {
				logger.info("Connector already exists");
			}

		} else {
			logger.info("Neither a delegate interface nor provided interface");
		}

		logger.info("Required Element :" + requiredelement);
	}

	private void processDelegations(AbstractComponent providedComponent, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		reqport = (IRPPort) swcomponent.findNestedElement(providedComponent.getName() + "__" + provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		reqconnector = (IRPLink) swcomponent.findNestedElement(
				providedComponent.getName() + "__" + provelem.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);

		if (reqport == null) {
			reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
					providedComponent.getName() + "__" + provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				reqport.setContract(funcClass);
			}
		}
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(swcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}

		}
		toport.setIsReversed(1);
		if (reqconnector == null && fromport != null && fpart != null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, providedComponent.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(maincomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(
				providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport, providedComponent.getName(),
					provelem.getName());
		}
		if (!maincomp.getName().equalsIgnoreCase(providedComponent.getName())) {
			IRPUnit delegateelement = (IRPUnit) findSourceandDestination(providedComponent.getName());
			IRPInstance delegatepart = findPart(delegateelement);
			IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			IRPLink delegateconnector = (IRPLink) delegateelement.findNestedElement(
					providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (delegateconnector == null) {
				delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
						providedComponent.getName(), provelem.getName());
			}
		}
	}

	private void processRequiredPort(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub

		try {
			flag = false;
			isPresent = false;
			providedComponent = null;
			RequiredPort rc = (RequiredPort) rie;
			String requiredport = rc.getPort().getName();
			AbstractComponent maincomp = ri.getComponent();
			ProvidedElement provelem = ri.getInterface();
			AbstractComponent dcomp = null;
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredport)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredport)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredport)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredport)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (flag || isPresent) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(provelem, pientity, dcomp, providedComponent, maincomp,
									rc.getPort().getName(), swcomponent);
						}
					}
				}
			}
			if (pluginComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforPlugins(provelem, pientity, pluginComponent, maincomp, rc.getPort().getName());
				}
			}
			if (pluginTemplateComponent != null) {
				reqconnector = (IRPLink) swcomponent.findNestedElement(
						pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
				if (reqconnector == null) {
					createportsforTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
							rc.getPort().getName());
				}
			}

		} catch (

		Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processRequiredConstant(IRPUnit swcomponent, RequiredInterface ri, Object rie) {
		// TODO Auto-generated method stub
		try {
			flag = false;
			providedComponent = null;
			isPresent = false;
			RequiredConstant rc = (RequiredConstant) rie;
			String requiredconstant = rc.getConstant().getName();
			AbstractComponent maincomp = ri.getComponent();
			AbstractComponent dcomp = null;
			ProvidedElement provelem = ri.getInterface();
			Plugin pluginComponent = null;
			PluginTemplate pluginTemplateComponent = null;
			String pientity = null;
			reqconnector = (IRPLink) swcomponent.findNestedElement(maincomp.getName() + "__" + provelem.getName(),
					GlobalVariables.CONNECTOR_METACLASS);
			if (reqconnector == null) {
				if (provelem instanceof DelegateInterface) {
					DelegateInterface pi = (DelegateInterface) provelem;
					for (DelegateEntity die : pi.getDelegateEntities()) {
						dcomp = die.getDelegateComponent();
						ProvidedInterface di = (ProvidedInterface) die.getDelegateInterface();
						for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
								.entrySet()) {
							if (!flag && !isPresent) {
								HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
								if (entry.getKey().getName().equalsIgnoreCase(dcomp.getName())) {
									for (Entry<String, HashMap<String, String>> intfname : interfacename.entrySet()) {
										HashMap<String, String> intfs = intfname.getValue();
										if (intfs.containsValue(requiredconstant)) {
											providedComponent = entry.getKey();
											provelem = di;
											reqconnector = (IRPLink) swcomponent.findNestedElement(
													dcomp.getName() + "__" + provelem.getName(),
													GlobalVariables.CONNECTOR_METACLASS);
											if (reqconnector == null) {
												flag = true;
											} else {
												isPresent = true;
												break;
											}
										}
										if (flag) {
											break;
										}

									}
								}
							}
							if (flag || isPresent) {
								break;
							}
						}

					}
				}
				for (Entry<AbstractComponent, HashMap<String, HashMap<String, String>>> entry : providedInterfaces
						.entrySet()) {
					if (!flag && !isPresent) {
						HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
						for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
							if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
								HashMap<String, String> intfelems = interfaceentry.getValue();
								if (intfelems.containsValue(requiredconstant)) {

									pientity = interfaceentry.getKey();
									providedComponent = entry.getKey();
									reqconnector = (IRPLink) swcomponent.findNestedElement(
											providedComponent.getName() + "__" + provelem.getName(),
											GlobalVariables.CONNECTOR_METACLASS);
									if (reqconnector == null) {
										flag = true;
									} else {
										isPresent = true;
										break;
									}

								}
							}
							if (flag) {
								break;
							}
						}
					}
					if (flag || isPresent) {
						break;
					}
				}
				if (!flag) {
					for (Entry<PluginTemplate, HashMap<String, HashMap<String, String>>> entry : pluginTemplateInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredconstant)) {

										pientity = interfaceentry.getKey();
										pluginTemplateComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginTemplateComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (!flag) {
					for (Entry<Plugin, HashMap<String, HashMap<String, String>>> entry : providedpluginInterfaces
							.entrySet()) {
						if (!flag && !isPresent) {
							HashMap<String, HashMap<String, String>> interfacename = entry.getValue();
							for (Entry<String, HashMap<String, String>> interfaceentry : interfacename.entrySet()) {
								if (interfaceentry.getKey().equalsIgnoreCase(provelem.getName())) {
									HashMap<String, String> intfelems = interfaceentry.getValue();
									if (intfelems.containsValue(requiredconstant)) {

										pientity = interfaceentry.getKey();
										pluginComponent = entry.getKey();
										reqconnector = (IRPLink) swcomponent.findNestedElement(
												pluginComponent.getName() + "__" + provelem.getName(),
												GlobalVariables.CONNECTOR_METACLASS);
										if (reqconnector == null) {
											flag = true;
										} else {
											isPresent = true;
											break;
										}
									}
								}
								if (flag || isPresent) {
									break;
								}
							}
						}
						if (flag || isPresent) {
							break;
						}
					}
				}
				if (flag || isPresent) {
					if (providedComponent != null) {
						reqconnector = (IRPLink) swcomponent.findNestedElement(
								providedComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
						if (reqconnector == null) {
							createportsforComponents(provelem, pientity, dcomp, providedComponent, maincomp,
									rc.getConstant().getName(), swcomponent);
						}
					}
				}
			}

			if (flag) {
				if (pluginComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforPlugins(provelem, pientity, pluginComponent, maincomp,
								rc.getConstant().getName());
					}
				}
				if (pluginTemplateComponent != null) {
					reqconnector = (IRPLink) swcomponent.findNestedElement(
							pluginTemplateComponent.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);
					if (reqconnector == null) {
						createportsforTemplates(provelem, pientity, pluginTemplateComponent, maincomp,
								rc.getConstant().getName());
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in creating connectors");
		}

	}

	private void processTopLevelSubConnectors(AbstractComponent subcomp, AbstractComponent abscomp,
			ProvidedElement provelem, IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		String main_comp = null;
		for (Entry<String, List<String>> entry : subcomponenthashmap.entrySet()) {
			List<String> subcomponentname = entry.getValue();
			if (subcomponentname.contains(providedComponent.getName())) {
				main_comp = entry.getKey();
				break;
			}
		}
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(main_comp);
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {

				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (fromport == null) {
			fromport = (IRPPort) fpartowner.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				fromport.setContract(funcClass);
			}
		}
		fromport.setIsReversed(1);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		if (tpartowner == null) {
			tpartowner = subcluster.findNestedElementRecursive(swcomponent.getName(), GlobalVariables.CLASS_METACLASS);
		}
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) tpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, subcomp.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(subcomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}
		}
		IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(subcomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, subcomp.getName(),
					provelem.getName());
		}

	}

	private void processPlugin(AbstractComponent abscomp, ProvidedElement provelem) {
		// TODO Auto-generated method stub
		try {
			IRPModelElement pluginelemname = null;
			IRPModelElement fpartelem = findSourceandDestination(abscomp.getName());
			if (fpartelem != null) {
				IRPCollection plugins = fpartelem.getNestedElementsByMetaClass("Class ", 0);
				for (Object pluginelem : plugins.toList()) {
					pluginelemname = (IRPModelElement) pluginelem;
					if (pluginelemname.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PLUGIN_METACLASS)) {
						IRPPort reqport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
								GlobalVariables.PROXY_PORT_METACLASS);
						IRPLink reqconnector = (IRPLink) swcomponent.findNestedElement(
								pluginelemname.getName() + "__" + provelem.getName(), GlobalVariables.LINK_TAG);

						if (reqport == null) {
							reqport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
									provelem.getName());
							funcClass = (IRPClass) getIntfBlockFunctionClas(pkg, reqport, abscomp, provelem, prj);
							if (funcClass != null) {
								reqport.setContract(funcClass);
							}
						}

						reqport.setIsReversed(1);
						if (reqconnector == null) {
							IRPModelElement fpartowner = pkg.findNestedElement(pluginelemname.getName(),
									GlobalVariables.PLUGIN_METACLASS);
							IRPInstance fpart = findPart(fpartowner);
							IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
									GlobalVariables.PROXY_PORT_METACLASS);
							IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
							IRPInstance tpart = findPart(tpartowner);
							IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
									GlobalVariables.PROXY_PORT_METACLASS);
							if (reqconnector == null) {
								reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport,
										abscomp.getName(), provelem.getName());
							}
						}

					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Exception while processing Plugin" + plugin.getName());
		}
	}

	private IRPLink addConnector(IRPModelElement fpartowner, IRPInstance fpart, IRPInstance tpart, IRPPort fromport,
			IRPPort toport, String component_name, String interface_name) {
		try {
			IRPLink existingconnector = (IRPLink) fpartowner.findNestedElement(component_name + "__" + interface_name,
					GlobalVariables.CONNECTOR_METACLASS);
			if (existingconnector == null) {
				IRPLink reqconnector = fpart.addLinkToElement(tpart, null, fromport, toport);
				reqconnector.changeTo(GlobalVariables.CONNECTOR_METACLASS);
				reqconnector.setOwner(fpartowner);
				reqconnector.setName(component_name + "__" + interface_name);
			}
		} catch (Exception e) {
			logger.info("Connector already exists");
		}
		return reqconnector;
	}

	private IRPModelElement findSourceandDestination(String componentname) {
		// TODO Auto-generated method stub
		String fpath = fullPathMap.get(componentname);
		IRPModelElement fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.COMPONENT_METACLASS);
		if (fpartowner == null) {
			fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.CLASS_METACLASS);
		}
		if (fpartowner == null) {
			fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.PLUGIN_METACLASS);
		}
		if (fpartowner == null) {
			fpartowner = pkg.findElementsByFullName(fpath, GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
		}
		if (fpartowner == null) {
			fpartowner = pkg.findNestedElementRecursive(componentname, GlobalVariables.COMPONENT_METACLASS);
		}
		return fpartowner;
	}

	private void processDelegationConnectors(AbstractComponent abscomp, ProvidedElement provelem,
			AbstractComponent dcomp, IRPUnit subcomponent) {
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(subcomponent.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (toport == null) {
			toport = (IRPPort) swcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				toport.setContract(funcClass);
			}
		}
		toport.setIsReversed(1);
		if (reqconnector == null) {
			reqconnector = addConnector(subcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}
		IRPModelElement mainelement = findSourceandDestination(abscomp.getName());
		IRPInstance mainpart = findPart(mainelement);
		IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (mainport == null) {
			mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
			funcClass = getRequiredPortContract(pkg, provelem.getName());
			if (funcClass != null) {
				mainport.setContract(funcClass);
			}

		}
		IRPLink mainConnector = (IRPLink) swcomponent.findNestedElement(abscomp.getName() + "__" + provelem.getName(),
				GlobalVariables.CONNECTOR_METACLASS);
		if (mainConnector == null) {
			mainConnector = addConnector(swcomponent, tpart, mainpart, toport, mainport, abscomp.getName(),
					provelem.getName());
		}
		IRPUnit delegateelement = (IRPUnit) findSourceandDestination(dcomp.getName());
		IRPInstance delegatepart = findPart(delegateelement);
		IRPPort delegateport = (IRPPort) delegateelement.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPLink delegateconnector = (IRPLink) delegateelement
				.findNestedElement(dcomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
		if (delegateconnector == null) {
			delegateconnector = addConnector(delegateelement, mainpart, delegatepart, mainport, delegateport,
					abscomp.getName(), provelem.getName());
		}
		// TODO Auto-generated method stub

	}

	private void processTopLevelConnectors(AbstractComponent abscomp, AbstractComponent maincomp,
			ProvidedElement provelem, IRPUnit swcomponent) {
		// TODO Auto-generated method stub
		IRPModelElement fpartowner = null;
		IRPInstance fpart = null;
		fpartowner = findSourceandDestination(maincomp.getName());
		if (fpartowner != null) {
			fpart = findPart(fpartowner);
		}
		if (fpart == null) {
			fpartowner = pkg.findNestedElementRecursive(provelem.getName(), GlobalVariables.CLASS_METACLASS);
			if (fpartowner != null) {
				fpart = findPart(fpartowner);
			}
		}
		IRPPort fromport = (IRPPort) fpartowner.findNestedElementRecursive(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		IRPModelElement tpartowner = findSourceandDestination(swcomponent.getName());
		if (tpartowner == null) {
			tpartowner = subcluster.findNestedElementRecursive(swcomponent.getName(), GlobalVariables.CLASS_METACLASS);
		}
		IRPInstance tpart = findPart(tpartowner);
		IRPPort toport = (IRPPort) swcomponent.findNestedElement(provelem.getName(),
				GlobalVariables.PROXY_PORT_METACLASS);
		if (reqconnector == null) {
			reqconnector = addConnector(swcomponent, fpart, tpart, fromport, toport, abscomp.getName(),
					provelem.getName());
		}

		if (abscomp instanceof SubComponent) {
			IRPModelElement mainelement = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.COMPONENT_METACLASS);
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
			}
		}
		if (abscomp instanceof PluginTemplate) {
			IRPModelElement mainelement = pkg.findNestedElementRecursive(abscomp.getName(),
					GlobalVariables.PLUGIN_TEMPLATE_METACLASS);
			IRPInstance mainpart = findPart(mainelement);
			IRPPort mainport = (IRPPort) mainelement.findNestedElement(provelem.getName(),
					GlobalVariables.PROXY_PORT_METACLASS);
			if (mainport == null) {
				mainport = (IRPPort) mainelement.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS, provelem.getName());
				funcClass = getRequiredPortContract(pkg, provelem.getName());
				if (funcClass != null) {
					mainport.setContract(funcClass);
				}
			}
			IRPLink mainConnector = (IRPLink) mainelement.findNestedElement(
					abscomp.getName() + "__" + provelem.getName(), GlobalVariables.CONNECTOR_METACLASS);
			if (mainConnector == null) {
				mainConnector = addConnector(mainelement, fpart, mainpart, fromport, mainport, abscomp.getName(),
						provelem.getName());
			}
		}

	}

	private IRPInstance findPart(IRPModelElement me) {

		for (Object o : me.getReferences().toList()) {
			if (o instanceof IRPInstance) {
				if (((IRPInstance) o).getUserDefinedMetaClass().equals("Object")) {
					return (IRPInstance) o;
				}
			}
		}
		return null;
	}

	private void processSubProvidedInterfaceBlockAndPort(AccessibleInterface ai, SubComponent subComponent,
			boolean elementPresent) {
		// TODO Auto-generated method stub
		if (!elementPresent) {
			if (ai instanceof DelegateInterface) {
				subintfblck = (IRPClass) subsubcomponent.findNestedElement("_" + ((AccessibleInterface) ai).getName(),
						GlobalVariables.DELEGATEINTERFACE_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) subsubcomponent.addNewAggr(GlobalVariables.DELEGATEINTERFACE_METACLASS,
							"_" + ((AccessibleInterface) ai).getName());
				}
				port = (IRPPort) subsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) subsubcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getPortContract(pkg, port, ai, prj);
				port.setContract(funcClass);
			} else {
				subintfblck = (IRPClass) subsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
				if (subintfblck == null) {
					subintfblck = (IRPClass) subsubcomponent.addNewAggr(GlobalVariables.INTERFACE_BLOCK_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				// SynchronizeElements.synchronizeInterfaceGroups(subComponent2, subcomponent);
				port = (IRPPort) subsubcomponent.findNestedElement(((AccessibleInterface) ai).getName(),
						GlobalVariables.PROXY_PORT_METACLASS);
				if (port == null) {
					port = (IRPPort) subsubcomponent.addNewAggr(GlobalVariables.PROXY_PORT_METACLASS,
							((AccessibleInterface) ai).getName());
				}
				funcClass = (IRPClass) getIntfBlockFunctionClass(pkg, port, (IRPProject) prj);
				port.setContract(funcClass);
			}
		}
	}

	public void openProject(String selectedFilePath) {
		File prj_file = new File(selectedFilePath);
		if (prj_file.exists()) {
			app = RhapsodyAppServer.createRhapsodyApplication();
			app.setHiddenUI(false);
			prj = app.openProject(selectedFilePath);
			pkg = (IRPPackage) prj.findNestedElement("Model", GlobalVariables.PACKAGE_METACLASS);
			if (pkg == null) {
				pkg = (IRPPackage) prj.addNewAggr(GlobalVariables.PACKAGE_METACLASS, "Model");
			}
			composite_stereotype = (IRPStereotype) prj.findNestedElementRecursive(GlobalVariables.COMPOSITE_METACLASS,
					GlobalVariables.STEREOTYPE_METACLASS);
			thirdparty_stereotype = (IRPStereotype) prj.findNestedElementRecursive(GlobalVariables.THIRDPARTY_METACLASS,
					GlobalVariables.STEREOTYPE_METACLASS);

		}
	}

	public void processPartition(Partition p, Software sw) {

		partition = (IRPUnit) checkifElementExists(pkg, ((Partition) p).getName(), GlobalVariables.PACKAGE_METACLASS);

		if (partition == null) {
			partition = (IRPUnit) addElementtoModel(pkg, GlobalVariables.PACKAGE_METACLASS, ((Partition) p).getName());
		} else {
			logger.info("Partition : " + p.getName() + " already exists");
		}

//		partition.setSeparateSaveUnit(1);
		// SynchronizeElements.synchronizePartitions(p, sw, pkg);
		subpartition = (IRPClass) checkifElementExists(partition, ((Partition) p).getName(),
				GlobalVariables.PARTITION_METACLASS);
		if (subpartition == null) {
			subpartition = (IRPUnit) addElementtoModel(partition, GlobalVariables.PARTITION_METACLASS,
					((Partition) p).getName());
			String description = p.getDesc();
			String label = p.getLongName();
			subpartition.setDescription(description);
			subpartition.setDisplayName(label);
		}
//		subpartition.setSeparateSaveUnit(1);
		software_part = (IRPInstance) checkifElementExists(software,
				GlobalVariables.PART_KEYWORD + ((Partition) p).getName(), GlobalVariables.PART_USER_METACLASS);
		if (software_part == null) {
			software_part = (IRPInstance) addElementtoModel(software, GlobalVariables.PART_USER_METACLASS,
					GlobalVariables.PART_KEYWORD + ((Partition) p).getName());
			software_part.setOtherClass((IRPClassifier) subpartition);
		}
	}

	public void processCluster(Cluster cl, Partition p) {
		try {
			cluster = (IRPUnit) checkifElementExists(partition, ((Cluster) cl).getName(),
					GlobalVariables.PACKAGE_METACLASS);

			if (cluster == null) {
				cluster = (IRPUnit) addElementtoModel(partition, GlobalVariables.PACKAGE_METACLASS,
						((Cluster) cl).getName());
			} else {
				logger.info("Cluster : " + cl.getName() + " already exists");
			}

//			cluster.setSeparateSaveUnit(1);
			subcluster = (IRPClass) checkifElementExists(cluster, ((Cluster) cl).getName(),
					GlobalVariables.CLUSTER_METACLASS);
			if (subcluster == null) {
				subcluster = (IRPUnit) addElementtoModel(cluster, GlobalVariables.CLUSTER_METACLASS,
						((Cluster) cl).getName());
				String description = cl.getDesc();
				String label = cl.getLongName();
				subcluster.setDescription(description);
				subcluster.setDisplayName(label);

			}
//			subcluster.setSeparateSaveUnit(1);
			IRPTag rl = (IRPTag) checkifElementExists(subcluster, GlobalVariables.ARCHITECT_TAG,
					GlobalVariables.TAG_METACLASS);
			if (rl == null) {
				rl = (IRPTag) addElementtoModel(subcluster, GlobalVariables.TAG_METACLASS,
						GlobalVariables.ARCHITECT_TAG);
				String archi = cl.getArchitect();
				if (archi != null) {
					subcluster.setTagValue(rl, archi);
				}
			}
			IRPTag reqid = (IRPTag) checkifElementExists(subcluster, GlobalVariables.DOCID_TAG,
					GlobalVariables.TAG_METACLASS);
			if (reqid == null) {
				reqid = (IRPTag) addElementtoModel(subcluster, GlobalVariables.TAG_METACLASS,
						GlobalVariables.DOCID_TAG);

			}
			partition_part = (IRPInstance) checkifElementExists(subpartition,
					GlobalVariables.PART_KEYWORD + ((Cluster) cl).getName(), GlobalVariables.PART_USER_METACLASS);
			if (partition_part == null) {
				partition_part = (IRPInstance) subpartition.addNewAggr(GlobalVariables.PART_USER_METACLASS,
						GlobalVariables.PART_KEYWORD + ((Cluster) cl).getName());
				partition_part.setOtherClass((IRPClassifier) subcluster);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Error in processing Cluster" + cl.getName());
		}
	}

	@SuppressWarnings({ "unused", "null" })
	public IRPClass getIntfBlockFunctionClass(IRPModelElement owner, IRPPort p, IRPProject app) {
		IRPCollection groups = null;
		IRPModelElement lastOwner = null;
		if (owner != null) {
			if (lastOwner == null) {
				lastOwner = owner;
			}
			if (lastOwner.equals(owner)) {
				// get the interface block from the map
				if (groups == null) {
					groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					funcClass = getFuncClassInterfaceBlock(owner, p, groups);

				} else {
					funcClass = getFuncClassInterfaceBlock(owner, p, groups);
					String funcname = funcClass.getName();
				}
			} else {
				lastOwner = owner;
				groups.empty();
				groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
				funcClass = getFuncClassInterfaceBlock(owner, p, groups);
			}
		}
		if (funcClass == null) {
			funcClass = app.findClass(lastOwner.getName());
		}
		return funcClass;
	}

	private IRPClass getRequiredPortContract(IRPModelElement owner, String intfblck) {
		// TODO Auto-generated method stub
		funcClass = (IRPClass) owner.findNestedElementRecursive(intfblck, GlobalVariables.INTERFACE_BLOCK_METACLASS);
		if (funcClass == null) {
			funcClass = (IRPClass) owner.findNestedElementRecursive("d_" + intfblck,
					GlobalVariables.DELEGATEINTERFACE_METACLASS);
		}
		return funcClass;
	}

	private IRPClass getPortContractforDelegate(IRPModelElement owner, IRPClass intfblck) {
		// TODO Auto-generated method stub
		if (intfblck != null) {
			funcClass = (IRPClass) owner.findNestedElementRecursive(intfblck.getName(),
					GlobalVariables.DELEGATEINTERFACE_METACLASS);
		}
		return funcClass;

	}

	@SuppressWarnings({ "unused", "null" })
	public IRPClass getPortContract(IRPModelElement owner, IRPPort p, ProvidedElement provelem, IRPProject app) {
		IRPCollection groups = null;
		IRPModelElement lastOwner = null;
		if (owner != null) {
			if (lastOwner == null) {
				lastOwner = owner;
			}
			if (lastOwner.equals(owner)) {
				// get the interface block7 from the map
				if (groups == null) {
					groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					funcClass = getPortContractFuncClass(owner, p, provelem, groups);

				} else {
					funcClass = getPortContractFuncClass(owner, p, provelem, groups);
					String funcname = funcClass.getName();
				}
			} else {
				lastOwner = owner;
				groups.empty();
				groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
				funcClass = getPortContractFuncClass(owner, p, provelem, groups);
			}
		}
		if (funcClass == null) {
			funcClass = app.findClass(lastOwner.getName());
		}
		return funcClass;
	}

	private IRPClass getPortContractFuncClass(IRPModelElement owner, IRPPort p, ProvidedElement provelem,
			IRPCollection groups) {
		IRPClass funcClass = null;
		for (int i = 1; i <= groups.getCount(); i++) {
			IRPClass group = (IRPClass) groups.getItem(i);
			if (group.getUserDefinedMetaClass() == null) {
				System.out.println("null for " + p.getName());
			}

			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)) {
				funcClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.DELEGATEINTERFACE_METACLASS)) {
				funcClass = (IRPClass) group.findNestedElement(provelem.getName(),
						GlobalVariables.DELEGATEINTERFACE_METACLASS);
			}

			if (funcClass != null) {
				break;
			}

		}
		return funcClass;
	}

	@SuppressWarnings({ "unused", "null" })
	public IRPClass getIntfBlockFunctionClas(IRPModelElement owner, IRPPort p, AbstractComponent abscomp,
			ProvidedElement provelem, IRPProject app) {

		IRPCollection groups = null;
		IRPModelElement lastOwner = null;
		String ownername = owner.getName();
		if (owner != null) {
			if (lastOwner == null) {
				lastOwner = owner;
			}
			if (lastOwner.equals(owner)) {
				// get the interface block from the map
				String lastownername = lastOwner.getName();
				if (groups == null) {
					groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
					funcClass = getFuncClasInterfaceBlock(owner, p, abscomp, provelem, groups);

				} else {
					funcClass = getFuncClasInterfaceBlock(owner, p, abscomp, provelem, groups);
					String funcname = funcClass.getName();
				}
			} else {
				lastOwner = owner;
				groups.empty();
				groups = owner.getNestedElementsByMetaClass(GlobalVariables.CLASS_METACLASS, 1);
				funcClass = getFuncClasInterfaceBlock(owner, p, abscomp, provelem, groups);
			}
		}
		if (funcClass == null) {
			funcClass = app.findClass(lastOwner.getName());
		}
		return funcClass;
	}

	private IRPClass getFuncClassInterfaceBlock(IRPModelElement owner, IRPPort p, IRPCollection groups) {
		IRPClass funcClass = null;
		for (int i = 1; i <= groups.getCount(); i++) {
			IRPClass group = (IRPClass) groups.getItem(i);
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)) {
				funcClass = (IRPClass) group.findNestedElementRecursive(p.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (funcClass != null) {
				break;
			}
		}
		return funcClass;
	}

	private IRPClass getFuncClasInterfaceBlock(IRPModelElement owner, IRPPort p, AbstractComponent abscomp,
			ProvidedElement provelem, IRPCollection groups) {
		IRPClass funcClass = null;
		@SuppressWarnings("unchecked")
		List<IRPModelElement> groupList = groups.toList();
		IRPModelElement group = groupList.stream().filter(x -> provelem.getName().equalsIgnoreCase(x.getName()))
				.findAny().orElse(null);
		if (group != null) {
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.PLUGIN_TEMPLATE_METACLASS)) {
				funcClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.INTERFACE_BLOCK_METACLASS)) {
				funcClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.INTERFACE_BLOCK_METACLASS);
			}
			if (group.getUserDefinedMetaClass().equalsIgnoreCase(GlobalVariables.DELEGATEINTERFACE_METACLASS)) {
				funcClass = (IRPClass) group.findNestedElementRecursive(provelem.getName(),
						GlobalVariables.DELEGATEINTERFACE_METACLASS);
			}
		}

		return funcClass;
	}

	public boolean hasConveyed(IRPFlow flow, IRPModelElement conveyed) {

		IRPCollection co = flow.getConveyed();
		for (int i = 1; i <= co.getCount(); ++i) {

			Object o = co.getItem(i);

			if (o instanceof IRPModelElement) {
				if (conveyed.equals(o)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public static CidlCollectorHandler getInstance() {

		// Only instantiate the object when needed.
		if (cidlCollectorInstance == null) {
			cidlCollectorInstance = new CidlCollectorHandler();
		}
		return cidlCollectorInstance;
	}

	/**
	 * 
	 */
	public void saveAndClose() {
		if (app != null) {
			app.saveAll();
			app.activeProject().close();
			app.quit();
		}
	}
}
