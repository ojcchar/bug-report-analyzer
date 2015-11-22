package seers.bugrepanalyzer;

public class MainIssueFileLinker {
	/*
	 * private static Logger LOGGER =
	 * LoggerFactory.getLogger(MainIssueFileLinker.class);
	 * 
	 * public static void main(String[] args) {
	 * 
	 * String[] sourceSubFolders; String projectName; String projectVersion;
	 * String logFilePath = null; String queriesFileInfoPath = null; String
	 * projectFolder = null; try { projectName = args[2]; sourceSubFolders =
	 * args[3].split(";"); projectVersion = args[4]; } catch (Exception e1) {
	 * LOGGER.error("Arguments error"); LOGGER.info(
	 * "Arguments: [git_repo_url] [base_folder] [project_name] [comma_separated_source_folders] [project_version] [git_tag]"
	 * ); return; }
	 * 
	 * LOGGER.info("Reading log"); try { Vector<CommitBean> commits =
	 * GitUtilities.readCommits(logFilePath);
	 * 
	 * HashMap<String, QueryInfo> issueInfo =
	 * readQueriesInfo(queriesFileInfoPath);
	 * 
	 * HashMap<String, IssueLink> links = getLinks(projectName,
	 * sourceSubFolders, commits, issueInfo, projectFolder);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * private static HashMap<String, IssueLink> getLinks(String projectName,
	 * String[] sourceSubFolders, Vector<CommitBean> commits, HashMap<String,
	 * QueryInfo> issueInfo, String projectFolder) throws IOException {
	 * 
	 * for (CommitBean commitBean : commits) {
	 * 
	 * List<String> classes = getClasses(commitBean, projectFolder);
	 * 
	 * if (classes.isEmpty()) { continue; } }
	 * 
	 * return null; }
	 * 
	 * private static List<String> getClasses(CommitBean commitBean, String
	 * projectFolder) throws IOException { List<String> cls =
	 * getClassesFromFiles(commitBean.getModifiedFiles(), projectFolder);
	 * List<String> cls2 = getClassesFromFiles(commitBean.getAddedFiles(),
	 * projectFolder); List<String> cls3 =
	 * getClassesFromFiles(commitBean.getDeletedFiles(), projectFolder);
	 * 
	 * cls.addAll(cls2); cls.addAll(cls3);
	 * 
	 * return cls; }
	 * 
	 * private static List<String> getClassesFromFiles(Vector<String> files,
	 * String projectFolder) throws IOException { List<String> cls = new
	 * ArrayList<String>();
	 * 
	 * for (String fileStr : files) {
	 * 
	 * if (!fileStr.endsWith(".java")) { continue; }
	 * 
	 * File file = new File(projectFolder + File.separator + fileStr);
	 * 
	 * if (!file.exists()) { continue; }
	 * 
	 * List<String> classes = filesClasses.get(file.getAbsolutePath()); if
	 * (classes == null) { // System.out.println(file.getAbsolutePath());
	 * 
	 * char[] fileContent = readFile(file); parser.setUnitName(file.getName());
	 * parser.setSource(fileContent);
	 * parser.setKind(ASTParser.K_COMPILATION_UNIT);
	 * 
	 * setParserConf();
	 * 
	 * CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	 * 
	 * // IProblem[] problems = cu.getProblems(); // // for (IProblem problem :
	 * problems) { // if (problem.isError()) { //
	 * LOGGER.error(problem.toString() + " - " // +
	 * problem.getSourceLineNumber()); // } // } // ---------------------
	 * 
	 * String pref = getSubFoldPref(fileStr); ClassVisitor vis = new
	 * ClassVisitor(pref, fileStr); cu.accept(vis); classes = vis.getClasses();
	 * 
	 * // java doc authors Map<String, List<String>> classesAuthors =
	 * vis.getClassesAuthors(); javadocClassAuthors.putAll(classesAuthors);
	 * 
	 * // files and classes per file filesClasses.put(file.getAbsolutePath(),
	 * classes);
	 * 
	 * } cls.addAll(classes);
	 * 
	 * } return cls; }
	 * 
	 * private String getSubFoldPref(String fileStr) { Set<Entry<String,
	 * String>> entrySet = subFoldPrefixes.entrySet();
	 * 
	 * fileStr = fileStr.replaceAll("/", "\\" + File.separator);
	 * 
	 * for (Entry<String, String> entry : entrySet) { if
	 * (fileStr.startsWith(entry.getKey())) { return entry.getValue(); } }
	 * return ""; }
	 * 
	 * public static HashMap<String, QueryInfo> readQueriesInfo(String
	 * queriesFileInfoPath) throws Exception {
	 * 
	 * HashMap<String, QueryInfo> infoList = new LinkedHashMap<>(); File fileIn
	 * = new File(queriesFileInfoPath);
	 * 
	 * if (!fileIn.isFile() || !fileIn.exists()) { throw new QueryException(
	 * "Query info file (" + queriesFileInfoPath + ") is not valid!"); }
	 * 
	 * CsvParser csvParser = new CsvParserBuilder().separator(';').build();
	 * CsvReader csvReader = new CsvReader(new FileReader(fileIn), csvParser);
	 * 
	 * SimpleDateFormat dateFormat = new
	 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); List<List<String>>
	 * readAll = csvReader.readAll();
	 * 
	 * for (List<String> list : readAll) {
	 * 
	 * String issueId = list.get(1); QueryInfo info = new QueryInfo(issueId);
	 * 
	 * info.setCreated(dateFormat.parse(list.get(6)));
	 * info.setDescription("NA".equals(list.get(12)) ? null : list.get(12));
	 * info.setSummary("NA".equals(list.get(14)) ? null : list.get(14));
	 * 
	 * infoList.put(issueId, info);
	 * 
	 * }
	 * 
	 * csvReader.close();
	 * 
	 * return infoList; }
	 */
}
