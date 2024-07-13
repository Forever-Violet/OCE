package io.ocepgen.modules.exam.utils;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import io.ocepgen.modules.exam.dto.EpExamPaperDTO;
import io.ocepgen.modules.exam.dto.ExamSubtitleDTO;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.dto.QbQuestionOptionDTO;
import io.ocepgen.modules.question.enums.QuestionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.ddr.poi.html.HtmlRenderPolicy;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 导出Word工具类
 * @author Roxy
 */
@Slf4j
public class ExportWordUtil {
    public static void exportPaperDTO(Map<String, Object> params, EpExamPaperDTO paperDTO, HttpServletResponse response) throws IOException {

        // Html渲染规则
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();

        // 计算所有题目的全局序号，并根据题型对题目的选项进行处理
        handleQuestion(paperDTO);
        // 处理导出规则
        handleExportRule(paperDTO, params);
        // 配置模板引擎，特别是对于列表的处理
        Configure config = Configure.builder()
                .bind("title", htmlRenderPolicy)
                .bind("content",htmlRenderPolicy)
                .bind("remark",htmlRenderPolicy)
                .bind("examSubtitleName",htmlRenderPolicy)
                .bind("analysis",htmlRenderPolicy)
                .bind("answer",htmlRenderPolicy)
                .useSpringEL(true)
                .build();


        Map<String, Object> data = paperDTO.toMap();
        data.putAll(params);
        log.warn("导出规则：{}", params);
        log.warn("导出WordUtil.exportPaperDTO:{}", data);

        // 获取模版文件路径 windows/linux通用
        Resource resource = new ClassPathResource("templates/word/template.docx");
        // 获取template.docx的绝对路径
        String path = resource.getFile().getAbsolutePath();
        // 加载模板文件
        XWPFTemplate template = XWPFTemplate.compile(path, config)
                .render(data);

        // 输出文件
        // 设置response header
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        // 获取文件名，将试卷标题从html转为纯文本 ， 对中文文件名使用url编码，不然会乱码
        String filename = Jsoup.parse(paperDTO.getTitle()).text();
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".docx");
        // 获取输出流
        OutputStream out = response.getOutputStream();
        // 写入到网络输出流
        template.write(out);
        out.flush();
        // 关闭
        template.close();
    }


    public static String exportPaperDTOToTempFile(Map<String, Object> params, EpExamPaperDTO paperDTO) throws Exception {
        // Html渲染规则
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();
        // 处理试题
        handleQuestion(paperDTO);
        // 处理导出规则
        handleExportRule(paperDTO, params);
        // 配置模板引擎
        Configure config = Configure.builder()
                .bind("title", htmlRenderPolicy)
                .bind("content", htmlRenderPolicy)
                .bind("remark",htmlRenderPolicy)
                .bind("examSubtitleName",htmlRenderPolicy)
                .bind("analysis",htmlRenderPolicy)
                .bind("answer",htmlRenderPolicy)
                .useSpringEL(true)
                .build();

        Map<String, Object> data = paperDTO.toMap();
        data.putAll(params);
        // 获取模板文件路径
        Resource resource = new ClassPathResource("templates/word/template.docx");
        String path = resource.getFile().getAbsolutePath();
        XWPFTemplate template = XWPFTemplate.compile(path, config).render(data);

        // 确定系统的默认临时文件夹
        Path tempDirectory = Files.createTempDirectory("examPapersWord");

        // 创建临时文件
        String filename = Jsoup.parse(paperDTO.getTitle()).text();
        // 使用URL编码避免中文乱码问题
        filename = URLEncoder.encode(filename, "UTF-8") + ".docx";
        Path tempFile = Paths.get(tempDirectory.toString(), filename);

        // 写入临时文件
        try (FileOutputStream out = new FileOutputStream(tempFile.toFile())) {
            template.write(out);

        } // try-with-resources will automatically flush & close the output stream
        template.close();

        // 返回临时文件的绝对路径
        return tempFile.toString();
    }

    public static void exportPaperAnswer(EpExamPaperDTO paperDTO, HttpServletResponse response) throws IOException {
        // Html渲染规则
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();
        // 计算所有题目的全局序号，并根据题型对题目的选项进行处理
        handleQuestion(paperDTO);
        // 配置模板引擎，特别是对于列表的处理
        Configure config = Configure.builder()
                .bind("title", htmlRenderPolicy)
                .bind("examSubtitleName",htmlRenderPolicy)
                .bind("answer",htmlRenderPolicy)
                .useSpringEL(true)
                .build();
        Map<String, Object> data = paperDTO.toMap();
        // 获取模版文件路径 windows/linux通用
        Resource resource = new ClassPathResource("templates/word/onlyAnswerTemplate.docx");
        // 获取template.docx的绝对路径
        String path = resource.getFile().getAbsolutePath();
        // 加载模板文件
        XWPFTemplate template = XWPFTemplate.compile(path, config)
                .render(data);
        // 输出文件
        // 设置response header
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        // 获取文件名，将试卷标题从html转为纯文本 ， 对中文文件名使用url编码，不然会乱码
        String filename = Jsoup.parse(paperDTO.getTitle()).text() + "-答案";

        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".docx");
        // 获取输出流
        OutputStream out = response.getOutputStream();
        // 写入到网络输出流
        template.write(out);
        out.flush();
        // 关闭
        template.close();
    }

    public static void exportQuestionDTO(String filename, List<QbQuestionDTO> questions, HttpServletResponse response) throws IOException {
        // 创建一个试卷和小标题，把题目列表加进去
        EpExamPaperDTO paperDTO = new EpExamPaperDTO();
        ExamSubtitleDTO subtitleDTO = new ExamSubtitleDTO();
        subtitleDTO.setQuestions(questions);
        paperDTO.setSubtitles(Collections.singletonList(subtitleDTO));

        // Html渲染规则
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();

        // 计算所有题目的全局序号，并根据题型对题目的选项进行处理
        handleQuestion(paperDTO);
        // 配置模板引擎，特别是对于列表的处理
        Configure config = Configure.builder()
                .bind("content",htmlRenderPolicy)
                .bind("analysis",htmlRenderPolicy)
                .bind("answer",htmlRenderPolicy)
                .useSpringEL(true)
                .build();

        Map<String, Object> data = paperDTO.toMap();

        log.warn("题目导出WordUtil.exportPaperDTO:{}", data);

        // 获取模版文件路径 windows/linux通用
        Resource resource = new ClassPathResource("templates/word/questionTemplate.docx");
        // 获取template.docx的绝对路径
        String path = resource.getFile().getAbsolutePath();
        // 加载模板文件
        XWPFTemplate template = XWPFTemplate.compile(path, config)
                .render(data);

        // 输出文件
        // 设置response header
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        // 获取文件名，将试卷标题从html转为纯文本 ， 对中文文件名使用url编码，不然会乱码
        filename = Jsoup.parse(filename).text();
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + ".docx");
        // 获取输出流
        OutputStream out = response.getOutputStream();
        // 写入到网络输出流
        template.write(out);
        out.flush();
        // 关闭
        template.close();
    }


    private static void handleQuestion(EpExamPaperDTO paperDTO) {
        int globalQuestionIndex = 0;
        Pattern pattern = Pattern.compile("<p.*?>.*?</p>"); // 匹配<p>标签及其内容
        for (ExamSubtitleDTO subtitle : paperDTO.getSubtitles()) {
            for (QbQuestionDTO question : subtitle.getQuestions()) {
                globalQuestionIndex++;
                question.setGlobalQuestionIndex(globalQuestionIndex);
                // 初始化问题答案 ，避免后续处理出现空指针
                question.setAnswer("");
                // 将题目内容中第一个p标签替换为span
                String questionContent = question.getContent();
                Matcher matcher = pattern.matcher(questionContent);
                if (matcher.find()) {
                    String matchedString = matcher.group();
                    String spanString = matchedString.replace("<p", "<span").replace("</p>", "</span>");
                    question.setContent(matcher.replaceFirst(spanString));
                }
                // 将题目解析中第一个p标签替换为span
                String analysis = question.getAnalysis();
                Matcher analysisMatcher = pattern.matcher(analysis);
                if (analysisMatcher.find()) {
                    String matchedString = analysisMatcher.group();
                    String spanString = matchedString.replace("<p", "<span").replace("</p>", "</span>");
                    question.setAnalysis(analysisMatcher.replaceFirst(spanString));
                }
                for (QbQuestionOptionDTO option : question.getOptions()) {
                    // 将项内容中第一个p标签替换为span
                    String optionContent = option.getContent();
                    Matcher matcher1 = pattern.matcher(optionContent);
                    if (matcher1.find() &&
                            (question.getType().equals(QuestionTypeEnum.SINGLE_CHOICE.value()) //单选题或多选题才进行选项内容和答案处理
                                    ||question.getType().equals(QuestionTypeEnum.MULTIPLE_CHOICE.value()))) {
                        String matchedString = matcher1.group();
                        String spanString = matchedString.replace("<p", "<span").replace("</p>", "</span>");
                        option.setContent(matcher1.replaceFirst(spanString));
                        // 如果option.answer为'true'，则覆盖option.answer为option.content中的第一个字符，即选项
                        if ("true".equals(option.getAnswer())) {
                            // 提取<p>标签内的内容
                            Pattern contentPattern = Pattern.compile(">(.*?)<");
                            Matcher contentMatcher = contentPattern.matcher(matchedString);
                            if (contentMatcher.find()) {
                                String innerContent = contentMatcher.group(1);
                                if (!innerContent.isEmpty()) {
                                    // 拼接 第一个标签内的第一个字符加顿号，即选项 到 题目答案
                                    question.setAnswer(question.getAnswer().concat(innerContent.substring(0, 2)));
                                }
                            }
                        }
                    }
                    Matcher answerMatcher = pattern.matcher(option.getAnswer());

                    if (question.getType().equals(QuestionTypeEnum.COMPLETION.value()) && answerMatcher.find()) { // 对填空题的答案进行处理
                        String matchedString = answerMatcher.group();
                        String spanString = matchedString.replace("<p", "<span").replace("</p>", "</span>");
                        // 拼接答案到 题目答案
                        question.setAnswer(question.getAnswer().concat(answerMatcher.replaceFirst(spanString) + "&nbsp&nbsp&nbsp"));
                    }
                    if (question.getType().equals(QuestionTypeEnum.SUBJECTIVE.value()) && answerMatcher.find()){ // 对主观题的答案进行处理
                        String matchedString = answerMatcher.group();
                        String spanString = matchedString.replace("<p", "<span").replace("</p>", "</span>");
                        // 拼接答案到 题目答案
                        question.setAnswer(answerMatcher.replaceFirst(spanString));

                    }
                    if (question.getType().equals(QuestionTypeEnum.JUDGMENTAL.value())) { // 对判断题的答案进行处理
                        // 在答案周围加上span标签
                        String answer = "<span>" + option.getAnswer() + "</span>";
                        question.setAnswer(answer);
                    }
                }

            }
        }
    }

    private static void handleExportRule(EpExamPaperDTO paperDTO, Map<String, Object> params) {
        // 首先处理是否显示试卷介绍
        if ("false".equals(params.get("remarkFlag"))) {
            paperDTO.setRemark(null);
        }
        for (ExamSubtitleDTO subtitle : paperDTO.getSubtitles()) {
            for (QbQuestionDTO question : subtitle.getQuestions()) {
                // 处理是否显示题目解析
                if ("false".equals(params.get("analysisFlag"))) {
                    question.setAnalysis(null);
                }
                // 处理是否显示题目分数
                if ("false".equals(params.get("scoreFlag"))) {
                    question.setScore(null);
                }
                for (QbQuestionOptionDTO option : question.getOptions()) {
                    // 处理是否显示答案
                    if ("false".equals(params.get("answerFlag"))) {
                        option.setAnswer(null);
                    }
                }
                // 处理是否显示答案
                if ("false".equals(params.get("answerFlag"))) {
                    question.setAnswer(null);
                }

            }
        }
    }
}
