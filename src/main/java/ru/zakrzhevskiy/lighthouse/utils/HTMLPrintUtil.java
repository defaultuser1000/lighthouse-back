package ru.zakrzhevskiy.lighthouse.utils;

import com.openhtmltopdf.DOMBuilder;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.zakrzhevskiy.lighthouse.model.Order;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTMLPrintUtil {

    private static final String DEFAULT_FONT_NAME = "Open Sans";
    private static final String DEFAULT_FONT_PATH = "/fonts/open_sans.ttf";
    private static final String REPORTS_FOLDER = "/templates/";
    private static final String ENCODING = "UTF-8";
    private static final String ROW_TEMPLATE_CLASS = "row-template";


    public static byte[] printPdfReport(String reportTemplate, Order data) {

        Document report = fillReportTemplate(reportTemplate, data);
        org.w3c.dom.Document w3cDoc = DOMBuilder.jsoup2DOM(report);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(w3cDoc);
            bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            transformer.transform(source, result);
//            bos.toByteArray();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


//        return buildPdfReportFromDocument(w3cDoc);
        return bos.toByteArray();
    }

    private static Document fillReportTemplate(String reportName, Order data) {
        Map<String, String> contrastMap = new HashMap<String, String>() {
            {
                put("Высокий", "high-contrast");
            }
        };
        Document reportTemplate = getReportTemplate(reportName);

        reportTemplate.select("#fio").attr("value", data.getOrderOwner().getFirstName() + " " + data.getOrderOwner().getLastName());
        reportTemplate.select("#" + contrastMap.get(data.getContrast())).attr("checked", "checked");
        reportTemplate.select("#preferences-area").html(data.getSpecial());
        String qrCodeScript = reportTemplate.select("#container-for-qr").select("script").html().replace("#ORDER_ID#", data.getId().toString());
        reportTemplate.select("#container-for-qr").select("script").html(qrCodeScript);
//        reportTemplate.select("").html();
//        reportTemplate.select("").html();

//        data.keySet().forEach(key -> {
//
//            Object value = data.get(key);
//
//            if (value instanceof String) {
//                reportTemplate.select("#" + key).html((String) data.get(key));
//
//            } else if (value instanceof List) {
//
//                Element targetTable = reportTemplate.select("table#"+key).first();
//                if (targetTable != null)
//                    fillTableByData(targetTable, (List<Map<String, String>>) value);
//            }
//        });

        return reportTemplate;
    }

    private static Document getReportTemplate(String reportName) {

        InputStream inputStream = HTMLPrintUtil.class.getResourceAsStream(REPORTS_FOLDER + reportName);
        String html = null;
        try {
            html = IOUtils.toString(inputStream, ENCODING);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Jsoup.parse(html);
    }

    private static void fillTableByData(Element targetHtmlTable, List<Map<String, String>> data) {

        data.forEach(map -> {

            Element row = targetHtmlTable.getElementsByClass(ROW_TEMPLATE_CLASS).first().clone();

            row.removeClass(ROW_TEMPLATE_CLASS);

            map.keySet().forEach(mapKey -> {
                Element column = row.select("." + mapKey).first();
                column.html(map.get(mapKey));
            });

            targetHtmlTable.append(row.html());

        });

        targetHtmlTable.getElementsByClass(ROW_TEMPLATE_CLASS).remove();
    }


//    private static byte[] buildPdfReportFromDocument(org.w3c.dom.Document w3cDoc) {
//
//        PdfRendererBuilder builder = new PdfRendererBuilder();
//        builder.withW3cDocument(w3cDoc, w3cDoc.getBaseURI() != null ? w3cDoc.getBaseURI() : "");
//        builder.useFont(() -> HTMLPrintUtil.class.
//                getResourceAsStream(DEFAULT_FONT_PATH), DEFAULT_FONT_NAME);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        builder.toStream(out);
//        try {
//            builder.run();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return out.toByteArray();
//    }
}
