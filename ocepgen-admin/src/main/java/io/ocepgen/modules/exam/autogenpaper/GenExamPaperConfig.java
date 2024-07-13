package io.ocepgen.modules.exam.autogenpaper;

import io.ocepgen.modules.exam.dto.ExamGenerateParamsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能组卷 遗传算法 配置类
 * @author Roxy
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenExamPaperConfig {

    /**
     * （1）群体大小：20~100
     *
     * （2）遗传算法的终止进化代数：100~500
     *
     * （3）交叉概率：0.4~0.99
     *
     * （4）变异概率：0.0001~0.1
     */
    private int populationSize = 30; //种群规模
    private double crossoverRate = 0.5; //交叉概率
    private double mutationRate = 0.2; //变异概率
    private int maxIteratorNum = 80; //最大迭代次数

    private boolean elitism = true; //开启精英主义, 精英主义保证最优秀的个体总是可以进入下一代

    private int tournamentNum = 5; //锦标赛选择算法，随机选取n个体进行竞争，选取最优的个体

    private double expectedFitness = 0.2; //期望适应度 越小越优
    //public static double DIFFICULTY_WEIGHT = 0.3; //难度在适应度中的权重
    //public static double SCORE_WEIGHT = 0.7; //期望分数在适应度中的权重

    public void setSmall(){

        populationSize = 40;
        maxIteratorNum = 100;
        mutationRate = 0.1;
        crossoverRate = 0.3;
        expectedFitness = 0.1;
    }
    public void setMedium(){

        populationSize = 50;
        maxIteratorNum = 120;
        mutationRate = 0.2;
        crossoverRate = 0.4;
        expectedFitness = 0.08;
    }
    public void setLarge(){

        populationSize = 60;
        maxIteratorNum = 150;
        mutationRate = 0.3;
        crossoverRate = 0.5;
        expectedFitness = 0.05;
    }

    /**
     * 根据实际题量 与 期望题量的占比，更改智能组卷的配置，题库题量越大，种群、迭代次数、变异、交叉概率越大
     */
    public static GenExamPaperConfig getConfig(int expectedCount,int realCount){
        GenExamPaperConfig config = new GenExamPaperConfig();
        //题目占比
        int percentage = (realCount / expectedCount) * 10;
        if(percentage > 80){
            config.setLarge();
        }else if(percentage > 40){
            config.setMedium();
        }else{
            config.setSmall();
        }
        return config;
    }

    public void setCustom(ExamGenerateParamsDTO configDto){

        populationSize = configDto.getPopulationSize();
        maxIteratorNum = configDto.getMaxIteratorNum();
        mutationRate = configDto.getMutationRate();
        crossoverRate = configDto.getCrossoverRate();
        expectedFitness = configDto.getExpectedFitness();
    }

    /**
     * 用户自定义的配置
     */
    public static GenExamPaperConfig customConfig(ExamGenerateParamsDTO configDto){
        GenExamPaperConfig config = new GenExamPaperConfig();
        config.setCustom(configDto);
        return config;
    }

}
