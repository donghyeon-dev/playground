package com.autocat.playground.arrays;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        Scanner in = new Scanner(System.in);

        // First Array Count
        int days =  in.nextInt();
        int sumDay = in.nextInt();
        int[] profits = new int[days];
        for(int i=0;i<days; i++){
            profits[i] = in.nextInt();
        };


        // 첫번째 구간 계산
        int sum = 0;
        for(int i =0; i< sumDay; i++){
            sum += profits[i]; // 38
        }

        int leftIndex = 0;
        int rightIndex = leftIndex +sumDay -1; // 0+3-1=2

        int max = Integer.MIN_VALUE;
        while(rightIndex < days-sumDay){
            //    38 - 15 + 20 = 43, 43-11+25=57, 57-20+10=47, 47-25+20=52, 52-10+13=53,53-20+13=43,43-19+15=37
            sum = sum - profits[++leftIndex] + profits[++rightIndex];
            if(max < sum){
                max = sum; // 38->43->57->
            }
        }



            System.out.print(max);

    }
}