package com.example.parking.components;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.parking.components.data.ParkingData;
import com.example.parking.model.Parking;
import com.example.parking.model.ParkingBay;
import com.example.parking.model.ParkingBayRepository;
import com.example.parking.model.ParkingBuilder;
import com.example.parking.model.ParkingRepository;

@Component
public class ParkingService {
	
	@Autowired
	private ParkingRepository parkingRepository;

	@Autowired
	private ParkingBayRepository parkingBayRepository;
	
	public Parking createParking(ParkingData parkingData) {
		
		ParkingBuilder parkingBuilder = new ParkingBuilder().withSquareSize(parkingData.getSize());
		
		for(Integer i : parkingData.getPedestrianExits()) {
			parkingBuilder.withPedestrianExit(i);
		}
		
		for(Integer i : parkingData.getDisabledBays()) {
			parkingBuilder.withDisabledBay(i);
		}
		
		Parking parking = parkingBuilder.build();
		
		parkingRepository.save(parking);
		return parking;
	}
	
	public Optional<Parking> getParkingById(Long id) {
		return parkingRepository.findById(id);
	}
	
	public long getAvailableBays(Parking parking) {
		return parking.getBays().stream().filter(pb -> pb.isAvailable()).count();
	}
	
	private Integer getFirstAvailableBay(Long parkingId, char carType) {
		 Optional<Parking> parkingOpt = parkingRepository.findById(parkingId);
		 if(parkingOpt.isPresent()) {
			 Parking parking = parkingOpt.get();
			 Optional<ParkingBay> parkingBayOpt = parking.getBays().stream().filter(pb -> pb.isAvailable(carType)).sorted(Comparator.comparing(ParkingBay::getDistanceToExit).thenComparing(ParkingBay::getIndex)).findFirst();
			 if(parkingBayOpt.isPresent()) {
				 return parkingBayOpt.get().getIndex();
			 }
		 }
		 
		 return -1;
	}
	
	public Integer parkCar(Long parkingId, char carType) {
		Integer indexToPark = getFirstAvailableBay(parkingId, carType);
		if(indexToPark > 0) {
			ParkingBay parkingBay = parkingBayRepository.findByParkingIdAndIndex(parkingId, indexToPark);
			parkingBay.setParkedCar(carType);
			parkingBayRepository.saveAndFlush(parkingBay);
		}
		return indexToPark;
	}
	
	public boolean unparkCar(Long parkingId, Integer index) {
		ParkingBay parkingBay = parkingBayRepository.findByParkingIdAndIndex(parkingId, index);
		if(parkingBay.isAvailable()) {
			return false;
		}
		
		parkingBay.initializeParkedCar();
		parkingBayRepository.saveAndFlush(parkingBay);
		return true;
	}
	
	public String printParking(Parking parking) {
		StringBuffer strBuffParking = new StringBuffer();
		int totalSize = parking.getSize()*parking.getSize();
		boolean needReverse = false;
		for(int i=0; i<totalSize; i=i+parking.getSize()) {
			final Integer minIndex = i;
			final Integer maxIndex = i+parking.getSize()-1;
			StringBuffer strBuffLane = new StringBuffer();
			parking.getBays().stream().filter(pb -> (pb.getIndex() >=minIndex && pb.getIndex() <= maxIndex)).sorted(Comparator.comparing(ParkingBay::getIndex)).forEachOrdered(pb -> strBuffLane.append(printBay(parking.getSize(), pb)));
			
			if(needReverse) {
				strBuffLane.reverse();
				needReverse = false;
			}else {
				needReverse = true;
			}
			
			strBuffParking.append(strBuffLane.append("\\n"));
		}
		
		return strBuffParking.toString();
	}
	
	private char printBay(Integer parkingSize, ParkingBay parkingBay) {
		return parkingBay.getParkedCar();
	}

}