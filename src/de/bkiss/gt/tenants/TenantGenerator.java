
package de.bkiss.gt.tenants;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author boss
 */
public class TenantGenerator {
    
    
    
    private String[] firstNamesMale = {
        "Aaron", "Peter", "Gustav", "Hans", "Carl", "Olaf", "Bernd", "Robert",
        "Tim", "Jim", "Konrad", "Giovanni", "Alex", "Helmut", "Christian", "Ronny", 
        "Lutz", "Andreas", "Konstantin", "Juan", "Chris", "Felix", "Uwe", "Sigi", 
        "Boris", "Fabian", "Nils", "Sascha", "John", "Holger", "Patrick", "Sven", 
        "Arnold", "Steve", "Phillip", "Kain", "Joao", "Luis", "Ron", "Lukas" 
    };
    
    private String[] firstNamesFemale = {
        "Linda", "Maria", "Justina", "Anna", "Lisa", "Ann", "Lucia", "Petra", 
        "Stefania", "Antea", "Alessia", "Alicia", "Cathy", "Elisabeth", "Lia", "Uta", 
        "Pia", "Michaela", "Daria", "Klara", "Eva", "Susi", "Erna", "Betty", 
        "Julia", "Isolde", "Angela", "Karin", "Isabel", "Anne", "Inga", "Doreen", 
        "Wilma", "Esther", "Sophie", "Rita", "Tina", "Olga", "Paula", "Frieda"
    };
    
    private String[] lastNames = {
        "Addison", "Algar", "Alton", "Bailey", "Barrett", "Brenton", "Burton",
        "Callahan", "Carlyle", "Chad", "Clay", "Dane", "Dean", "Dwight", "Easton",
        "Farrell", "Floyd", "Gary", "Gill", "Hammond", "Harding", "Irvin", "Jackson",
        "Jarvis", "Kacey", "Knox", "Lark", "Lenard", "Mackenzie", "Mallory",
        "Morty", "Nash", "Orville", "Payton", "Perry", "Randall"
    };
    
    private String[] professionsLower = {
        "Worker", "Student", "Unemployed", "Painter", "Musician"
    };

    private String[] professionsMiddle = {
        "Teacher", "Accountant"
    };
    
    private String[] professionsUpper = {
        "Doctor", "Politician", "Dentist", "Broker", "Manager"
    };
    
    
    
    private Random rnd;
    
    
    public TenantGenerator(){
        this.rnd = new Random();
    }
    
    
    public Set<Tenant> generateTenants(int num){
        Set<Tenant> tenants = new HashSet<Tenant>();
        
        for (int i = 0; i < num; i++)
            tenants.add(new Tenant(
                    generateName(),
                    1000,
                    generateProfessionMiddle(),
                    50,
                    "imgPath"));
        
        return tenants;
    }
    
    
    private String generateName(){
        if (rnd.nextInt(10) < 5)
            return generateFirstNameMale();
        else
            return generateFirstNameFemale();
    }
    
    
    private String generateFirstNameMale(){
        return firstNamesMale[rnd.nextInt(firstNamesMale.length)];
    }
    
    
    private String generateFirstNameFemale(){
        return firstNamesFemale[rnd.nextInt(firstNamesFemale.length)];
    }
    
    
    private String generateLastName(){
        return lastNames[rnd.nextInt(lastNames.length)];
    }
    
    
    private String generateProfessionLower(){
        return professionsLower[rnd.nextInt(professionsLower.length)];
    }
    
    
    private String generateProfessionMiddle(){
        return professionsMiddle[rnd.nextInt(professionsMiddle.length)];
    }
    
    
    private String generateProfessionUpper(){
        return professionsUpper[rnd.nextInt(professionsUpper.length)];
    }
    
    
}
