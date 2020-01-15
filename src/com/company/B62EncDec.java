package com.company;

public class B62EncDec {

    private static String base = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXZY";

    //function takes a number and encodes to a b62 string.
    public static String toBase62(long num){
        int b = 62;
        long r = num % b;
        String res = base.charAt((int) r )+"";
        num = num/b;

        while(num != 0){
            r = num%b;
            res = base.charAt((int) r)+res;
            num = num/b;
        }

        //System.out.println(res);

        return res;
    }

    ////function takes a b62 string and decodes back to number.
    public static long toBase10(String shortURL){
        int b = 62;
        int len = shortURL.length();
        long res = 0;

        for(int i = 0; i<len; i++){
            res = b * res + base.indexOf(shortURL.charAt(i));
        }

        //System.out.println(res);
        return res;
    }

}
