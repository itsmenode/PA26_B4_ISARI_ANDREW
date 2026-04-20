package report;

import dao.MovieReportDAO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import model.MovieReport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    private final MovieReportDAO reportDAO = new MovieReportDAO();

    public void generate(String outputPath) throws SQLException, IOException, TemplateException {
        List<MovieReport> movies = reportDAO.getAll();

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassForTemplateLoading(ReportGenerator.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("movies", movies);
        dataModel.put("generatedAt",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        Template template = cfg.getTemplate("report.ftl");
        try (Writer out = new BufferedWriter(new FileWriter(outputPath))) {
            template.process(dataModel, out);
        }

        System.out.println("Report generated: " + outputPath);
    }
}