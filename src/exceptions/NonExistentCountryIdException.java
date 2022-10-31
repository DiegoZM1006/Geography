package exceptions;

public class NonExistentCountryIdException extends Exception{
    public NonExistentCountryIdException(){
        super("THERE IS NO COUNTRY WITH THE ENTERED ID");
    }
}
