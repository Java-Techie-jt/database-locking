package com.javatechie.service;

import com.javatechie.entity.Seat;
import com.javatechie.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieTicketBookingService {

    @Autowired
    private SeatRepository seatRepository;


    @Transactional
    public Seat bookSeat(Long seatId) {
        //fetch the existing seat by id
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found with id " + seatId));

        System.out.println(Thread.currentThread().getName() + " fetched seat with version " + seat.getVersion());

        if (seat.isBooked()) {
            throw new RuntimeException("Seat already booked !");
        }
        //booking seat
        seat.setBooked(true);
        //version check will occurs here
        return seatRepository.save(seat);
    }

    @Transactional
    public void bookSeatWithPessimistic(Long seatId) {

        System.out.println(Thread.currentThread().getName() + " is attempting to fetch the seat");

        //fetch the seat with Pessimistic lock
        Seat seat = seatRepository.findByIdAndLock(seatId);

        System.out.println(Thread.currentThread().getName() + " acquired the lock for seat id " + seatId);

        if (seat.isBooked()) {
            System.out.println(Thread.currentThread().getName() + " failed Seat Id " + seatId + " is already booked ");
            throw new RuntimeException("Seat already booked !");
        }
        //booking seat
        System.out.println(Thread.currentThread().getName() + " booking the seat " + seatId);

        seat.setBooked(true);
        //version check will occurs here
        seatRepository.save(seat);
        System.out.println(Thread.currentThread().getName() + " successfully book the seat with ID " + seatId);
    }
}
