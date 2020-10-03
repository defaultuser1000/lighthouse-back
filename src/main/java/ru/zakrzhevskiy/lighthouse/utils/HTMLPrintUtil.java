package ru.zakrzhevskiy.lighthouse.utils;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class HTMLPrintUtil {

    public static byte[] htmlFileToPDF(String htmlFilePath, String outputFilePath) {
        WrapperConfig config = new WrapperConfig(String.format("wkhtmltopdf --enable-local-file-access %s %s", htmlFilePath, outputFilePath));
        Pdf pdf = new Pdf(config);

        return getPdf(pdf);
    }

    @SneakyThrows
    public static void generatePdfAsProcess(String htmlFilePath, String outputFilePath) {
        Process wkhtml; // Create uninitialized process
        String command = String.format("wkhtmltopdf --enable-local-file-access --print-media-type --disable-smart-shrinking -B 3 %s %s", htmlFilePath, outputFilePath);

        wkhtml = Runtime.getRuntime().exec(command); // Start process
        IOUtils.copy(wkhtml.getErrorStream(), System.err); // Print output to console

        wkhtml.waitFor(); // Allow process to run
    }

    private static byte[] getPdf(Pdf pdf) {
        try {
            return pdf.getPDF();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
