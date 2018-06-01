package com.example.parking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.parking.components.ParkingService;
import com.example.parking.model.Parking;
import com.example.parking.model.ParkingBuilder;
import com.example.parking.model.ParkingRepository;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ParkingTest {
    private static final int FIRSTUPEDESTRIANUEXITUINDEX = 8;

    private Parking parking;
    
    @Autowired
    private ParkingService parkingService;
    
    @Autowired
    private ParkingRepository parkingRepository;

    @Before
    public void setUp() {
        parking = new ParkingBuilder().withSquareSize(5).withPedestrianExit(FIRSTUPEDESTRIANUEXITUINDEX).withPedestrianExit(12).withDisabledBay(5)
            .withDisabledBay(10).build();
        parkingRepository.save(parking);
    }

    @Test
    public void testGetAvailableBays() {
        assertEquals(23, parkingService.getAvailableBays(parking));
    }

    @Test
    public void testParkCarVehiculeTypeC() {
        assertEquals(Integer.valueOf(7), parkingService.parkCar(parking, 'C'));
        assertEquals(22, parkingService.getAvailableBays(parking));
    }

    @Test
    public void testParkCarVehiculeTypeM() {
        assertEquals(Integer.valueOf(7), parkingService.parkCar(parking, 'M'));
        assertEquals(22, parkingService.getAvailableBays(parking));
    }

    @Test
    public void testParkCarTwoVehicules() {
    	assertEquals(Integer.valueOf(7), parkingService.parkCar(parking, 'C'));
        assertEquals(22, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(9), parkingService.parkCar(parking, 'M'));
        assertEquals(21, parkingService.getAvailableBays(parking));
    }

    @Test
    public void testParkCarDisabled() {
        assertEquals(Integer.valueOf(10), parkingService.parkCar(parking, 'D'));
        assertEquals(22, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(5), parkingService.parkCar(parking, 'D'));
        assertEquals(21, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(-1), parkingService.parkCar(parking, 'D'));
        assertEquals(21, parkingService.getAvailableBays(parking));
    }

    @Test
    public void testUnparkCar() {
        final int firstCarBayIndex = parkingService.parkCar(parking, 'C');
        assertTrue(parkingService.unparkCar(parking, firstCarBayIndex));
        assertEquals(23, parkingService.getAvailableBays(parking));
        assertFalse(parkingService.unparkCar(parking, firstCarBayIndex));

        final int secondCarBayIndex = parkingService.parkCar(parking, 'D');
        assertTrue(parkingService.unparkCar(parking, secondCarBayIndex));
        assertEquals(23, parkingService.getAvailableBays(parking));
        assertFalse(parkingService.unparkCar(parking, secondCarBayIndex));

        assertFalse(parkingService.unparkCar(parking, FIRSTUPEDESTRIANUEXITUINDEX));
    }

    @Test
    public void testToString() {
        assertEquals("UUUUU\nU=UU@\n@U=UU\nUUUUU\nUUUUU", parkingService.printParking(parking));
    }

    @Test
    public void testCompleteSolution() {
        assertEquals(Integer.valueOf(7), parkingService.parkCar(parking, 'C'));
        assertEquals("UUUUU\nU=CU@\n@U=UU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(22, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(9), parkingService.parkCar(parking, 'C'));
        assertEquals("UUUUU\nC=CU@\n@U=UU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(21, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(11), parkingService.parkCar(parking, 'M'));
        assertEquals("UUUUU\nC=CU@\n@M=UU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(20, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(13), parkingService.parkCar(parking, 'M'));
        assertEquals("UUUUU\nC=CU@\n@M=MU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(19, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(10), parkingService.parkCar(parking, 'D'));
        assertEquals("UUUUU\nC=CU@\nDM=MU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(18, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(5), parkingService.parkCar(parking, 'D'));
        assertEquals("UUUUU\nC=CUD\nDM=MU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(17, parkingService.getAvailableBays(parking));

        assertEquals(Integer.valueOf(-1), parkingService.parkCar(parking, 'D'));
        assertEquals("UUUUU\nC=CUD\nDM=MU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(17, parkingService.getAvailableBays(parking));

        assertFalse(parkingService.unparkCar(parking, 3));
        assertEquals("UUUUU\nC=CUD\nDM=MU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(17, parkingService.getAvailableBays(parking));

        assertTrue(parkingService.unparkCar(parking, 13));
        assertEquals("UUUUU\nC=CUD\nDM=UU\nUUUUU\nUUUUU", parkingService.printParking(parking));
        assertEquals(18, parkingService.getAvailableBays(parking));
    }
}
