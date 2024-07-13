package io.ocepgen.modules.question.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import io.ocepgen.modules.question.dto.QbQuestionDTO;
import io.ocepgen.modules.question.service.QbQuestionService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author roxy
 */
@Slf4j //此类不能被Spring管理，每次使用时需要new
public class QuestionListener extends AnalysisEventListener<Map<Integer, String>> {
    /**
     * 每隔100条存储数据库，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    // 处理成功的记录数
    private Integer successCount;
    // 处理失败的记录数
    private Integer failCount;

    // 被Spring管理的业务处理类，new QuestionListener()时传进来
    private QbQuestionService questionService;
    public QuestionListener(QbQuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        List<QbQuestionDTO> questionDTOS = new ArrayList<>(cachedDataList.size());
        // 遍历map list，先构造成对象
        cachedDataList.forEach(question -> {
            // 存储数据库
        });
        log.info("存储数据库成功！");
    }
}
