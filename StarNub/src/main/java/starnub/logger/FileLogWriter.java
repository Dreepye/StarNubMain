package starnub.logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import starnub.StarNub;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileLogWriter {


    private volatile FileOutputStream fileWriter;


    private volatile OutputStreamWriter outputStream;


    private volatile BufferedWriter bufferedWriter;

    public FileLogWriter(String filePathString) {
        File file =new File(filePathString);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            }
        }
        try {
            this.fileWriter = new FileOutputStream(file, true);
            this.outputStream = new OutputStreamWriter(Files.newOutputStream(Paths.get(filePathString), StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            this.bufferedWriter = new BufferedWriter(outputStream, 8 * 1024);
        } catch (IOException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    public void writeToBuffer(String string) {
        try {
            this.bufferedWriter.write(string);
            this.bufferedWriter.newLine();
        } catch (IOException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    public void flushLogs(){
        try {
            this.bufferedWriter.flush();
        } catch (IOException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }

    public void closeFileLogWriter(){
        try {
            this.bufferedWriter.close();
            this.outputStream.close();
            this.fileWriter.close();
        } catch (IOException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
    }
}
