package com.company;
import java.util.Scanner;

public class Main {

    //db password and uer id passed as args
    public static void main(String[] args) {
        String dbUserid = args[0];
        String dbPwd = args[1];

        menu(dbUserid, dbPwd);

    }

    //function displays menu in console
    public static void menu(String dbUserid, String dbPwd){

        Scanner sc = new Scanner(System.in);
        PSQL psqlConn = new PSQL(dbUserid, dbPwd, "urls");
        while(true){
            System.out.println("Menu:");
            System.out.println("1. Create Small URL from Full URL, Press 1");
            System.out.println("2. Retrieve Full URL with ShortURL, Press 2");
            System.out.println("3. Quit Program, Press Q");
            String input = sc.nextLine();
            if(input.length()>1){
                System.out.println("Error: Input length is too long");
            } else {

                char inputChar = input.charAt(0);

                switch(inputChar){

                    case '1' :
                        System.out.println("Please type in the full url:");
                        String fullurl = sc.nextLine();
                        Long rowID = psqlConn.insertURL(fullurl);
                        String shortURL = B62EncDec.toBase62(rowID);
                        System.out.println("Small URL for "+ fullurl +" is "+ "www.smallurl.com/" + shortURL+ "\n");

                        break;
                    case '2':
                        System.out.println("Please type in the small url:");
                        String smallUrl = sc.nextLine();
                        //String prefix = smallUrl.substring(0, 17);
                        if(smallUrl.length()<18||  !smallUrl.substring(0, 17).equals("www.smallurl.com/")){
                            System.out.println("Invalid Small Url, please try again\n");
                        } else {
                            String suffix = smallUrl.substring(17, smallUrl.length());
                            long decodedRowId = B62EncDec.toBase10(suffix);
                            String fullUrl = psqlConn.retriveFullUrl(decodedRowId);
                            if(fullUrl.equals("-1")){
                                System.out.println("Error: Invalid Small URL, Please try again");
                            }else{
                                System.out.println("Full URL for "+ smallUrl +" is "+ fullUrl+  "\n");
                            }
                        }
                        break;
                    case 'Q':
                        System.out.println("Quitting Small URL");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Error: Incorrect input, please try again\n");
                        break;
                }
            }
        }


    }
}
