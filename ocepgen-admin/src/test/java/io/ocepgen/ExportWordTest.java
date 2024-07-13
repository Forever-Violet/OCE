package io.ocepgen;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import io.ocepgen.modules.exam.dto.EpExamPaperDTO;
import io.ocepgen.modules.exam.dto.ExamSubtitleDTO;
import io.ocepgen.modules.exam.service.EpExamPaperService;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.dto.QbQuestionOptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.ddr.poi.html.HtmlRenderPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ExportWordTest {

    @Resource
    EpExamPaperService epExamPaperService;

    public static void main(String[] args) throws IOException {

        testPaperTemplateStatic();
    }

    @Test
    public void testPaperTemplate() throws IOException  {

        //EpExamPaperDTO paperDTO = getPaperDTO();
        EpExamPaperDTO paperDTO = epExamPaperService.get(1776991788863705089L);
        //log.error(String.valueOf(paperDTO));
        // Html渲染规则
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();

        // 计算所有题目的全局序号，并根据题型对题目的选项进行处理
        handleQuestion(paperDTO);
        // 配置模板引擎，特别是对于列表的处理
        Configure config = Configure.builder()
                .bind("title", htmlRenderPolicy)
                .bind("content",htmlRenderPolicy)
                .useSpringEL(true)
                .build();

        Map<String, Object> data = paperDTO.toMap();

        log.warn("导出WordUtil.exportPaperDTO:{}", data);

        // 加载模板文件
        XWPFTemplate template = XWPFTemplate.compile("ocepgen-admin/src/main/resources/template/template.docx", config)
                .render(data);

        // 输出文件
        template.writeAndClose(Files.newOutputStream(Paths.get("output.docx")));

    }

    public static void testPaperTemplateStatic() throws IOException  {

        EpExamPaperDTO paperDTO = getPaperDTO();

        //log.error(String.valueOf(paperDTO));
        // Html渲染规则
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();

        // 计算所有题目的全局序号，并根据题型对题目的选项进行处理
        handleQuestion(paperDTO);
        // 配置模板引擎，特别是对于列表的处理
        Configure config = Configure.builder()
                .bind("title", htmlRenderPolicy)
                .bind("content",htmlRenderPolicy)
                .useSpringEL(true)
                .build();

        Map<String, Object> data = paperDTO.toMap();

        log.warn("导出WordUtil.exportPaperDTO:{}", data);

        // 加载模板文件
        XWPFTemplate template = XWPFTemplate.compile("ocepgen-admin/src/main/resources/template/template.docx", config)
                .render(data);

        // 输出文件
        template.writeAndClose(Files.newOutputStream(Paths.get("output.docx")));

    }

    private static void handleQuestion(EpExamPaperDTO paperDTO) {
        int globalQuestionIndex = 0;
        for (ExamSubtitleDTO subtitle : paperDTO.getSubtitles()) {
            for (QbQuestionDTO question : subtitle.getQuestions()) {
                globalQuestionIndex++;
                question.setGlobalQuestionIndex(globalQuestionIndex);
                // 如果是判断题，添加一个选项，正确/错误
//                if (Objects.equals(question.getType(), QuestionTypeEnum.JUDGMENTAL.value())) {
//                    QbQuestionOptionDTO optionDTO = new QbQuestionOptionDTO();
//                    optionDTO.setContent("()");
//                    question.setOptions(Collections.singletonList(new QbQuestionOptionDTO()));
//                }
            }
        }
    }

    public static EpExamPaperDTO getPaperDTO() {
        EpExamPaperDTO examPaperDTO = new EpExamPaperDTO();
        examPaperDTO.setId(111L);
        examPaperDTO.setCourseName("<p>Java Web开发实用教程</p>");
        examPaperDTO.setType(0);
        examPaperDTO.setDifficulty(3F);
        examPaperDTO.setScore(100F);
        examPaperDTO.setRemark("好好好");
        examPaperDTO.setQuestionCount(10);
        examPaperDTO.setTitle("Java Web开发实用教程 期末考试");

        ExamSubtitleDTO subtitleDTO1 = new ExamSubtitleDTO();
        subtitleDTO1.setSort(0);
        subtitleDTO1.setExamSubtitleName("一、单选题");

        QbQuestionDTO questionDTO1 = new QbQuestionDTO();
        questionDTO1.setType(0);
        questionDTO1.setScore(null);
        questionDTO1.setContent("<p>在Servlet中，response.getWriter()返回的是</p> <img src='https://picx.zhimg.com/v2-64713eec9ed7f98b27c12599e14cecdf_r.jpg?source=1def8aca'/>");

        QbQuestionOptionDTO optionDTO1 = new QbQuestionOptionDTO();
        optionDTO1.setContent("<p>A、 JspWriter对象</p>");
        QbQuestionOptionDTO optionDTO2 = new QbQuestionOptionDTO();
        optionDTO2.setContent("B、PrintWriter对象");
        QbQuestionOptionDTO optionDTO3 = new QbQuestionOptionDTO();
        optionDTO3.setContent("C、 Out对象");
        QbQuestionOptionDTO optionDTO4 = new QbQuestionOptionDTO();
        optionDTO4.setContent("D、ResponseWriter对象");

        questionDTO1.setOptions(Arrays.asList(optionDTO1, optionDTO2, optionDTO3, optionDTO4));


        QbQuestionDTO questionDTO2 = new QbQuestionDTO();
        questionDTO2.setType(0);
        questionDTO2.setScore(5F);
        questionDTO2.setContent("编写一个Filter，需要（）");

        QbQuestionOptionDTO optionDTO5 = new QbQuestionOptionDTO();
        optionDTO5.setContent("A、继承Filter 类");
        QbQuestionOptionDTO optionDTO6 = new QbQuestionOptionDTO();
        optionDTO6.setContent("B、实现Filter 接口");
        QbQuestionOptionDTO optionDTO7 = new QbQuestionOptionDTO();
        optionDTO7.setContent("C、继承HttpFilter 类");
        QbQuestionOptionDTO optionDTO8 = new QbQuestionOptionDTO();
        optionDTO8.setContent("D、实现HttpFilter接口");

        questionDTO2.setOptions(Arrays.asList(optionDTO5, optionDTO6, optionDTO7, optionDTO8));


        subtitleDTO1.setQuestions(Arrays.asList(questionDTO1, questionDTO2));





        ExamSubtitleDTO subtitleDTO2 = new ExamSubtitleDTO();
        subtitleDTO2.setSort(1);
        subtitleDTO2.setExamSubtitleName("二、多选题");


        QbQuestionDTO questionDTO3 = new QbQuestionDTO();
        questionDTO3.setType(1);
        questionDTO3.setScore(5F);
        questionDTO3.setContent("编写一个Filter，需要（）");

        QbQuestionOptionDTO optionDTO9 = new QbQuestionOptionDTO();
        optionDTO9.setContent("<p>A、1</p>");
        QbQuestionOptionDTO optionDTO10 = new QbQuestionOptionDTO();
        optionDTO10.setContent("<p>B、2</p>");
        QbQuestionOptionDTO optionDTO11 = new QbQuestionOptionDTO();
        optionDTO11.setContent("C、继承HttpFilter 类");
        QbQuestionOptionDTO optionDTO12 = new QbQuestionOptionDTO();
        optionDTO12.setContent("D、实现HttpFilter接口");

        questionDTO3.setOptions(Arrays.asList(optionDTO5, optionDTO6, optionDTO7, optionDTO8));


        subtitleDTO2.setQuestions(Arrays.asList(questionDTO3));


        ExamSubtitleDTO subtitleDTO3 = new ExamSubtitleDTO();
        subtitleDTO3.setSort(3);
        subtitleDTO3.setExamSubtitleName("三、判断题");


        QbQuestionDTO questionDTO4 = new QbQuestionDTO();
        questionDTO4.setType(2);
        questionDTO4.setScore(5F);
        questionDTO4.setContent("Cookie 文件是存放在服务器端的。");

        subtitleDTO3.setQuestions(Arrays.asList(questionDTO4));





        ExamSubtitleDTO subtitleDTO4 = new ExamSubtitleDTO();
        subtitleDTO4.setSort(3);
        subtitleDTO4.setExamSubtitleName("四、填空题");


        QbQuestionDTO questionDTO5 = new QbQuestionDTO();
        questionDTO5.setType(3);
        questionDTO5.setScore(5F);
        questionDTO5.setContent("Cookie 文件是存放在服务器端的。");

        subtitleDTO4.setQuestions(Arrays.asList(questionDTO5));





        ExamSubtitleDTO subtitleDTO5 = new ExamSubtitleDTO();
        subtitleDTO5.setSort(4);
        subtitleDTO5.setExamSubtitleName("五、主观题");


        QbQuestionDTO questionDTO6 = new QbQuestionDTO();
        questionDTO6.setType(4);
        questionDTO6.setScore(10F);
        questionDTO6.setContent("Get和Post请求的区别。");

        subtitleDTO5.setQuestions(Arrays.asList(questionDTO6));




        examPaperDTO.setSubtitles(Arrays.asList(subtitleDTO1, subtitleDTO2, subtitleDTO3, subtitleDTO4, subtitleDTO5));

        return examPaperDTO;
    }

    @Test
    public void testTemplate() throws IOException{

    }

}
