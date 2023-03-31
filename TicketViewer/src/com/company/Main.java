package com.company;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.*;
import org.json.*;

public class Main {

    /**
     * Ticket class to store ticket attributes
     */
    static class Ticket {
    private String created_at = "";
    private String subject = "";
    private String description = "";
    private String status = "";
    private int requester_id;
    private String[] tags;

    Ticket(){               // default constructor
        requester_id = 0;
        tags = new String[0];
    }


        // Constructor for unit tester
    Ticket(String created_at, String subject, String description,
           String status, int requester_id, String [] tags){
        this.created_at = created_at;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.requester_id = requester_id;
        this.tags = tags;
    }

    /**
     * Ticket constructor using JSONObject. Takes JSONObject and parses it
     */
    Ticket(JSONObject jsonObject){
        this.created_at = jsonObject.getString("created_at");
        this.subject = jsonObject.getString("subject");
        this.description = jsonObject.getString("description");
        this.status = jsonObject.getString("status");
        this.requester_id = jsonObject.getInt("requester_id");
        this.tags = Arrays.stream(jsonObject.getJSONArray("tags").toList().toArray())
                .map(Object::toString).toArray(String[]::new);
    }

    /**
     * Setters and Getters
     */
    void setcreated_at(String created_at) {
        this.created_at = created_at;
    }
    String getcreated_at(){ return this.created_at;};


    void setsubject(String subject) {
        this.subject = subject;
    }
    String getsubject(){ return this.subject;};


    void setdescription(String description) {
        this.description = description;
    }
    String getdescription(){ return this.description;};


    void setstatus(String status) {
        this.status = status;
    }
    String getstatus(){ return this.status;};


    void setrequester_id(int requester_id) {
        this.requester_id = requester_id;
    }
    int getrequester_id(){ return this.requester_id;};


    void settags(String[] tags) {
        this.tags = tags;
    }
    String [] gettags(){ return this.tags;};
    ;

    /**
     * displays basic ticket details.
     */
    String displayTicketDetails(){
        return "Ticket with Subject: '" + getsubject() + "' opened by: "
                + getrequester_id() + " on " + getcreated_at();
    }


    /**
     * displays advanced ticket details- includes tags and status.
     */
    String displayIndividualTicketDetails(){
        return "\nTicket with Subject: '" + getsubject() + "' status: " + getstatus()
                + "\nwith the tags: " + Arrays.toString(gettags()) + " opened by: " + getrequester_id()
                + " on " + getcreated_at() + "\n";
    }

    };

    /**
     * displays all tickets in a list AND allow for user to page through list of tickets.
     */
    static void viewTickets (List<Ticket> list){
        int index = 0;                           // to access elements in Ticket list. everytime index is used, it is incremented
        int pageCounter = 1;                     // keep track of pagenumber to know when to stop displaying tickets
        Scanner myScanner = new Scanner(System.in);
        String input = "";
        boolean again = true;                   // whether to run the loop again

        for (; index < 25; index++) {                                                // print first 25 tickets on the page
            String printer = list.get(index).displayTicketDetails();                 // after this, pageCounter is 1 and index is 24
            System.out.println(printer);
        }

        while (again) {
            System.out.println("\nPress 1 to page forwards");
            System.out.println("Press 2 to page backwards");
            System.out.println("Press 3 to return to the main menu");
            input = myScanner.nextLine();
            if (input.equals("1")) {
                pageCounter++;
                if(index >= list.size() - 1) {   // If user tries to page forward, stays on same page until user pages backwards
                    System.out.println("\nYou are on the last 25 tickets, you can't page forwards!\n");
                    pageCounter--;              // since cannot page forwards, keep pagecounter the same by decrementing
                    continue;                   // don't display tickets until user pages correctly
                }
            }
            if (input.equals("2")) {
                pageCounter--;
                index -= 50;                    // when paging backwards, start at correct index
                if(index < 0) {                 // If user tries to page backward, stays on same page until user pages forwards
                    System.out.println("\nYou are on the first 25 tickets, you can't page backwards!\n");
                    pageCounter++;              // since cannot page backwards, keep pagecounter the same by incrementing
                    index = 25;                 // when user pages forward, starts at correct index
                    continue;                   // don't display tickets until user pages correctly
                }
            }
            if (!(input.equals("1") || input.equals("2")))     // if user does not input 1 or 2, automatically return to main menu
                again = false;
            while (index < pageCounter * 25) {                  // display tickets after paging
                String printer = list.get(index).displayTicketDetails();
                System.out.println(printer);
                index++;
            }
        }
    }

    /**
     * displays a specific ticket with advanced details
     */
    static void viewATicket(List<Ticket> list){
        Scanner myScanner = new Scanner(System.in);
        String input = "";
        System.out.println("Enter ticket number: ");
        input = myScanner.nextLine();
        String ticketDetails = "";
        try {
            ticketDetails = list.get(Integer.parseInt(input) - 1).displayIndividualTicketDetails();
            System.out.println(ticketDetails);
        }
        catch (IndexOutOfBoundsException e){    // handles if user inputs a ticket number that is out of bounds
            System.out.println("You selected a number out of bounds so the ticket number was set to 1");
            ticketDetails = list.get(0).displayIndividualTicketDetails();
            System.out.println(ticketDetails);
        }
        catch (Exception e){                    // handles if user inputs an invalid ticket number
            System.out.println("You selected an invalid ticket number so the ticket number was set to 1");
            ticketDetails = list.get(0).displayIndividualTicketDetails();
            System.out.println(ticketDetails);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean again = true;
        Scanner myScanner = new Scanner(System.in);
        String input = "";
        List<Ticket> list = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();     // creates a client
        HttpRequest request = HttpRequest.newBuilder(       // creates a request
                URI.create("https://zzczendeskcodingchallenge7111.zendesk.com/api/v2/tickets")) // url
                .header("accept", "application/json")   // necessary headers
                .header("Authorization", "Basic aGFpZGVyYWxtYW5kZWVsMDZAZ21haWwuY29tOkhleTJoZXkh")  // authorization
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // JSON response
        if(response.statusCode() == 500)
            System.out.println("Response is invalid");  // invalid response from server
        if(response.statusCode() == 503)
            System.out.println("API unavailable");      // API unavailable
        String jsonString = response.body();                    // store response in string
        JSONObject obj = new JSONObject(jsonString);            // create JSON object to parse the jsonString
        JSONArray arr = obj.getJSONArray("tickets");        // create JSONArray that holds all the tickets
        for(int i = 0; i < arr.length(); i++){                  // loop through the array creating a Ticket object, storing in list
            list.add(new Ticket(arr.getJSONObject(i)));
        }


        System.out.println("Welcome to the ticket viewer\nType 'menu' to view options or 'quit' to exit");  // User Interface
        input = myScanner.nextLine();
        input = input.toUpperCase();
        if(!input.equals("MENU"))
            return;


        while(again){
            System.out.println("Select view options: ");
            System.out.println("Press 1 to view all tickets");
            System.out.println("Press 2 to view a ticket");
            System.out.println("Type 'quit' to exit");
            input = myScanner.nextLine();
            switch (input) {
                case "1" -> viewTickets(list);  // calls viewTickets function
                case "2" -> viewATicket(list);  // calls viewATicket function
                default -> again = false;
            }
        }
    }
}
