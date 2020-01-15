package com.company;
import java.util.Scanner;

public class Main {

    //db password and uer id passed as args
    public static void main(String[] args) {
        String dbUserid = args[0];
        String dbPwd = args[1];
        String dburl = args[2];

        menu(dbUserid, dbPwd, dburl);

    }

    //function displays menu in console
    public static void menu(String dbUserid, String dbPwd, String dburl){

        Scanner sc = new Scanner(System.in);
        PSQL psqlConn = new PSQL(dbUserid, dbPwd, "urls", dburl);
        System.out.print("Please enter your userid: ");
        int currUserId = sc.nextInt();
        sc.nextLine();
        String smallUrl = "";

        while(true){
            System.out.println("Menu:");
            System.out.println("1. Create Small URL from Full URL, Press 1");
            System.out.println("2. Retrieve Full URL with ShortURL, Press 2");
            System.out.println("3. Update Full URL with ShortURL, Press 3");
            System.out.println("4. Delete ShortURL Record, Press 3");
            System.out.println("5. Quit Program, Press Q");
            String input = sc.nextLine();
            if(input.length()>1){
                System.out.println("Error: Input length is too long");
            } else {

                char inputChar = input.charAt(0);

                switch(inputChar){

                    case '1' :
                        System.out.println("Please type in the full url:");
                        String fullurl = sc.nextLine();
                        Long rowID = psqlConn.insertURL(fullurl,currUserId);
                        String shortURL = B62EncDec.toBase62(rowID);
                        System.out.println("Small URL for "+ fullurl +" is "+ "www.smallurl.com/" + shortURL+ "\n");

                        break;
                    case '2':
                        System.out.println("Please type in the small url:");
                        smallUrl = sc.nextLine();
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
                    case '3':
                        System.out.println("To update the full url, please type in the small url:");
                        smallUrl = sc.nextLine();
                        System.out.println("Please type in the new full url:");
                        String newUrl = sc.nextLine();
                        //String prefix = smallUrl.substring(0, 17);
                        if(smallUrl.length()<18||  !smallUrl.substring(0, 17).equals("www.smallurl.com/")){
                            System.out.println("Invalid Small Url, please try again\n");
                        } else {
                            String suffix = smallUrl.substring(17, smallUrl.length());
                            long decodedRowId = B62EncDec.toBase10(suffix);
                            long rowid = psqlConn.updateURL(decodedRowId, newUrl, currUserId);
                            if(rowid != -1){
                                String newShortURL = B62EncDec.toBase62(rowid);
                                System.out.println("Update Succesful, Small URL for "+ newUrl +" is "+ "www.smallurl.com/" + newShortURL+ "\n");
                            } else {
                                System.out.println("Update Fail, no record found. Please check your user id or small url\n");
                            }

                        }
                        break;

                    case '4':
                        System.out.println("Please type in the small url to delete:");
                        smallUrl = sc.nextLine();

                        if(smallUrl.length()<18||  !smallUrl.substring(0, 17).equals("www.smallurl.com/")){
                            System.out.println("Invalid Small Url, please try again\n");
                        } else {
                            String suffix = smallUrl.substring(17, smallUrl.length());
                            long decodedRowId = B62EncDec.toBase10(suffix);

                            boolean removed = psqlConn.deleteFullURLRecord(decodedRowId, currUserId);
                            if(removed == false){
                                System.out.println("Error: Unable to delete, Invalid Small URL or userid, Please try again\n");
                            }else{
                                System.out.println("Record removed\n");
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
