package io.ocepgen;

public class WeightTest {

    public static void main(String[] args) {
        double[] weight = {0.20,0.15,0.30,0.15,0.10,0.10,0.05,0.05,0.15,0.05,0.10,0.10,0.15,0.25,0.10,0.15,0.15
                ,0.10,0.10,0.10,0.40,0.50,0.25,0.25,0.50,0.50};


/*        double[] score = {68,
                59,
                51,
                65,
                91,
                86,
                34,
                84,
                79,
                83,
                63,
                83,
                36,
                91,
                75,
                82,
                11,
                16,
                82,
                82,
                18,
                76,
                78,
                38,
                13,
                81
                };*/
        double[] score = {
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100,
                100
        };

        double deyu = 0;
        double zhiyu = 0;
        double tiyu = 0;
        double meiyu = 0;
        double laoyu = 0;
        for (int i = 0; i < 6; i++) {
            deyu +=  weight[i] * score[i];
        }
        for (int i = 6; i < 15; i++) {
            zhiyu += weight[i] * score[i];
        }
        for (int i = 15; i < 21; i++) {
            tiyu += weight[i] * score[i];
        }
        for (int i = 21; i< 24; i++) {
            meiyu += weight[i] * score[i];
        }
        for (int i = 24; i < 26; i++) {
            laoyu += weight[i] * score[i];
        }
        System.out.println(weight.length);
        System.out.println(score.length);
        System.out.println(deyu);
        System.out.println(zhiyu);
        System.out.println(tiyu);
        System.out.println(meiyu);
        System.out.println(laoyu);
        deyu *= 0.20;
        zhiyu *= 0.40;
        tiyu *= 0.20;
        meiyu *= 0.10;
        laoyu *= 0.10;
        System.out.println(deyu+zhiyu+tiyu+meiyu+laoyu);

    }

}
