package commonPriorities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class  Statistics {
    static String excelPath=System.getProperty("user.dir")+"/experimentResults/statistics.xlsx";
    static String experiment4appsExcelPath=System.getProperty("user.dir")+"/experimentResults/experimentStats.xlsx";
    static XSSFSheet experimentInfosSheet;
    static XSSFSheet experimentSheet;
    static XSSFSheet experimentResultsSheet;
    static  XSSFWorkbook workbook;
    static File file;
    public static Map<String, Double> appsDropRates;
    static{
        appsDropRates=new HashMap<String, Double>();
        try {
            //file = new File(excelPath);
            file = new File(experiment4appsExcelPath);
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
           //workbook = new XSSFWorkbook();
            //throw new RuntimeException(e);
            workbook = new XSSFWorkbook();
        }

            experimentSheet=workbook.getSheet("Experiments");
            experimentInfosSheet=workbook.getSheet("Experiment's Infos");
            experimentResultsSheet=workbook.getSheet("Experiments' Results");
            if(experimentSheet==null){
                experimentSheet= workbook.createSheet("Experiments");
            }
            if(experimentInfosSheet==null)
                // Sheet that contains general infos about experiments such us nb of publishers
                experimentInfosSheet = workbook.createSheet("Experiment's Infos");
            if(experimentResultsSheet==null){
                experimentResultsSheet= workbook.createSheet("Experiments' Results");
        }


    }

    public Statistics() {
    }

    public static void createExperimentSheet() throws IOException {

        XSSFRow row= experimentInfosSheet.createRow(0);
        row.createCell(0).setCellValue("nbPublishers");
        row.createCell(1).setCellValue("nbMsg/pub");
        row.createCell(2).setCellValue("dropRates");
        row.createCell(3).setCellValue("nbApps");
        // creating a experiments sheet
        XSSFRow ExperimentRow= experimentSheet.createRow(0);
        ExperimentRow.createCell(0).setCellValue("appId");
        ExperimentRow.createCell(1).setCellValue("Topic");
        ExperimentRow.createCell(2).setCellValue("dropRate");
        ExperimentRow.createCell(3).setCellValue("priority");
        ExperimentRow.createCell(4).setCellValue("delay(ms)");
        ExperimentRow.createCell(5).setCellValue("nbDroppedMessages");
        // creating a experiments' result sheet
        XSSFRow ExperimentResRow= experimentResultsSheet.createRow(0);
        ExperimentResRow.createCell(0).setCellValue("appId");
        ExperimentResRow.createCell(1).setCellValue("Topic");
        ExperimentResRow.createCell(2).setCellValue("dropRate");
        ExperimentResRow.createCell(3).setCellValue("priority");
        ExperimentResRow.createCell(4).setCellValue("Average delay(ms)");
        ExperimentResRow.createCell(5).setCellValue("nbDroppedMessages");
        ExperimentResRow.createCell(6).setCellValue("nbSentMsgsPerTopic");
        ExperimentResRow.createCell(7).setCellValue("nbMsgsReceived");
        //FileOutputStream out = new FileOutputStream(new File(excelPath));
        FileOutputStream out = new FileOutputStream(new File(experiment4appsExcelPath));
        workbook.write(out);
        out.close();
    }
    public synchronized static void addSeperation() throws IOException {
        XSSFRow row= experimentResultsSheet.createRow(Statistics.experimentResultsSheet.getLastRowNum()+1);
        row.createCell(0).setCellValue("xxxxx");
        row.createCell(1).setCellValue("xxxxxx");
        row.createCell(2).setCellValue("xxxxxx");
        row.createCell(3).setCellValue("xxxxxx");
        row.createCell(4).setCellValue("xxxxxx");
        row.createCell(5).setCellValue("xxxxxx");
        row.createCell(6).setCellValue("xxxxxx");
        row.createCell(7).setCellValue("xxxxxx");
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }
     public synchronized static void  addExperimentRow(String appId,String Topic,String dropRate,int priority,long responseTime,int nbDroppedMessages) throws IOException {
        XSSFRow row= experimentSheet.createRow(Statistics.experimentSheet.getLastRowNum()+1);
        row.createCell(0).setCellValue(appId);
        row.createCell(1).setCellValue(Topic);
        row.createCell(2).setCellValue(dropRate);
        row.createCell(3).setCellValue(priority);
        row.createCell(4).setCellValue(responseTime);
        row.createCell(5).setCellValue(nbDroppedMessages);
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }
    public synchronized static void addExperimentInfoRow(int nbPublishers,int nbMsgPerPub,int dropRates,int nbApps) throws IOException {
        XSSFRow row= experimentInfosSheet.createRow(Statistics.experimentInfosSheet.getLastRowNum()+1);
        row.createCell(0).setCellValue(nbPublishers);
        row.createCell(1).setCellValue(nbMsgPerPub);
        row.createCell(2).setCellValue(dropRates);
        row.createCell(3).setCellValue(nbApps);
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }
    public synchronized static void  addExperimentResultsRow(String appId, String Topic, String dropRate, int priority, double AverageDelay, int nbDroppedMessages,int nbSentMsgsPerTopic,int nbMsgsReceived) throws IOException {
        XSSFRow row= experimentResultsSheet.createRow(Statistics.experimentResultsSheet.getLastRowNum()+1);
        row.createCell(0).setCellValue(appId);
        row.createCell(1).setCellValue(Topic);
        row.createCell(2).setCellValue(dropRate);
        row.createCell(3).setCellValue(priority);
        row.createCell(4).setCellValue(AverageDelay);
        row.createCell(5).setCellValue(nbDroppedMessages);
        row.createCell(6).setCellValue(nbSentMsgsPerTopic);
        row.createCell(7).setCellValue(nbMsgsReceived);
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }
    public static void main(String[] args)
    {
        try {
            Statistics.createExperimentSheet();
            /*for (int i=0; i<10;i++){
                Statistics.addExperimentInfoRow(1,1,1,5);
                Statistics.addExperimentRow("ghh","sdfsd","0",0,0,0);
            }*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
