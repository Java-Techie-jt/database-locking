package com.javatechie.service;

import com.javatechie.entity.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptimisticSeatBookingTestService {

    @Autowired
    private MovieTicketBookingService movieTicketBookingService;


    public void testOptimisticLocking(Long seatId) throws InterruptedException {
        // 2 thread

        Thread th1 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " is attempting to book the seat");
                Seat seat = movieTicketBookingService.bookSeat(seatId);
                System.out.println(Thread.currentThread().getName() + " successfully booked the seat with version " + seat.getVersion());
            } catch (Exception ex) {
                System.out.println(Thread.currentThread().getName() + " failed : " + ex.getMessage());
            }
        });

        Thread th2 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " is attempting to book the seat");
                Seat seat = movieTicketBookingService.bookSeat(seatId);
                System.out.println(Thread.currentThread().getName() + " successfully booked the seat with version " + seat.getVersion());
            } catch (Exception ex) {
                System.out.println(Thread.currentThread().getName() + " failed : " + ex.getMessage());
            }
        });

        th1.start();
        th2.start();
        th1.join();
        th2.join();
    }
}
