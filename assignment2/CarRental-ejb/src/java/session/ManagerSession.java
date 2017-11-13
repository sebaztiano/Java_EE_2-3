package session;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }
    
    public void addCarRentalCompany(String name, List<String> regions){
        CarRentalCompany company = new CarRentalCompany(name, regions, new LinkedList<Car>());
        em.persist(company);
    }
    
    public void addCarTypes(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed){
        CarType carType = new CarType(name,nbOfSeats,trunkSpace,rentalPricePerDay,smokingAllowed);
        em.persist(carType);
    }
    
    public void addCar(int uid, CarType type){
        Car car = new Car(uid,type);
        em.persist(car);
    }

    @Override
    public void addCarToCompany(String name, int id, String carTypeName) {
        CarType carType = em.find(CarType.class, carTypeName);
        Car car = new Car(id, carType);
        em.persist(car);
        CarRentalCompany company = em.find(CarRentalCompany.class,name);
        company.addCar(car);
        company.addCarType(carType);
    }

}