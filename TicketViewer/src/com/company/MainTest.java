package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    @Test
    public void displayIndividualTicketDetails(){
        String [] arrOne = {"tagOne", "tagTwo"};
        Main.Ticket ticketOne = new Main.Ticket("11/01/2021", "Tester Ticket One", "Testing Tickets In Unit Test",
                "open", 1, arrOne);
        String result = ticketOne.displayIndividualTicketDetails();

        assertEquals(result, "\nTicket with Subject: 'Tester Ticket One' status: open" +
                "\nwith the tags: [tagOne, tagTwo] opened by: 1 on 11/01/2021\n");
    }


    @Test
    public void displayTicketDetails(){
        String [] arrOne = {"tagOne", "tagTwo"};
        Main.Ticket ticketOne = new Main.Ticket("11/01/2021", "Tester Ticket One", "Testing Tickets In Unit Test",
                "open", 1, arrOne);
        String result = ticketOne.displayTicketDetails();

        assertEquals(result, "Ticket with Subject: 'Tester Ticket One' opened by: 1 on 11/01/2021");
    }
}
